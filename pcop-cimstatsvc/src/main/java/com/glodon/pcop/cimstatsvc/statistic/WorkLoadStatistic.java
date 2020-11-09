package com.glodon.pcop.cimstatsvc.statistic;

import com.glodon.pcop.cim.engine.dataServiceEngine.dataMart.Fact;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.InformationExplorer;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineInfoExploreException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.exception.CimDataEngineRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.external._IOT_REST.IOT_REST_MongoDBConnectionUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.mongodb.mongo3.MongoDbDataContext;
import org.apache.metamodel.schema.Column;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.glodon.pcop.cimstatsvc.dao.BaseExDao.saveToCim;
import static com.glodon.pcop.cimstatsvc.dao.DbExecute.dbName;

//指标信息临时统计
//单设备 单指标报警 每两小时统计一次
public class WorkLoadStatistic {

    public static void count() {
        List<String> cimIdList = new ArrayList<>();
        //只取默认环境
        String objectTypeName = "envmonitordevice";

        CimDataSpace cimDataSpace = CimDataEngineComponentFactory.connectInfoDiscoverSpace(dbName);
        InformationExplorer ie = cimDataSpace.getInformationExplorer();
        ExploreParameters ep = new ExploreParameters();
        ep.setType("envmonitordevice");
        try {
            List<Fact> envDeviceFactList = ie.discoverInheritFacts(ep);
            if (envDeviceFactList != null) {
                for (Fact currentFact : envDeviceFactList) {
                    if (objectTypeName.equals("envmonitordevice")) {
                        if (currentFact.hasProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME)) {
                            cimIdList.add(currentFact.getProperty(BusinessLogicConstant.ID_PROPERTY_TYPE_NAME).getPropertyValue().toString());
                        }
                    }
                }
            }

        } catch (CimDataEngineRuntimeException e) {
            e.printStackTrace();
        } catch (CimDataEngineInfoExploreException e) {
            e.printStackTrace();
        } finally {
            if(cimDataSpace != null){
                cimDataSpace.closeSpace();
            }
        }

        List<String> items = new ArrayList<>();
        items.add("pm25");
        items.add("pm10");
        items.add("temperature");
        items.add("windSpeed");
        items.add("noise");

        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date end = new Timestamp(cal.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 2);
        Date start = new Timestamp(cal.getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(end.getTime());

        List<Map<String, Object>> listMaps = new ArrayList<>();

        //统计失败次数
        Map<String, Object> dataMap = getFaultsStat(null, start, end, items);
        dataMap.put("statDay", day);
        dataMap.put("statTime", end);
        listMaps.add(dataMap);

        //统计各种指标
        for (int i = 0; i < cimIdList.size(); i++) {
            HashMap<String, List<Integer>> result = getStat(cimIdList, start, end, items);
            for (String key : result.keySet()) {
                List<Integer>  ss = result.get(key);
                for(int index = 0;index<ss.size();index++){
                    Map<String, Object> currentDataMap = new HashMap<>();
                    currentDataMap.put("deviceId", cimIdList.get(i));
                    currentDataMap.put("statDay", day);
                    currentDataMap.put("statTime", end);
                    currentDataMap.put("alarmItem", key);
                    currentDataMap.put("alarmLevel", index);
                    currentDataMap.put("count",ss.get(index));
                    listMaps.add(currentDataMap);
                }
            }
        }

        saveToCim("iotDeviceAlarmStatData", listMaps);
    }

    public static Map<String, Object>  getFaultsStat(List<String> ids, Date start, Date end, List<String> items) {
        String MONGODBV3_DATABASE_OBJ = "device_alarms";
        MongoClient mongoClient = IOT_REST_MongoDBConnectionUtil.getMongoClient();
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGODBV3_DATABASE_OBJ.toString());
        DataContext targetDataContext = new MongoDbDataContext(mongoDatabase);

        DataSet dataSet = targetDataContext.query()
                .from("deviceFaults").selectCount()
                .where("level").eq("FAULT")
                .and("time").greaterThan(localToUTC(start)).and("time").lessThan(localToUTC(end))
                .execute();
        Iterator<Row> rowItem = dataSet.iterator();
        int count = 0;
        while (rowItem.hasNext()) {
            Row currentRow = rowItem.next();
            count = Integer.valueOf(currentRow.getValue(0).toString());
        }
        Map<String, Object> currentDataMap = new HashMap<>();
        currentDataMap.put("deviceId", "all");
        currentDataMap.put("alarmItem", "fault");
        currentDataMap.put("alarmLevel", 1);
        currentDataMap.put("count",count);

        return  currentDataMap;

    }




    public static HashMap<String, List<Integer>> getStat(List<String> ids, Date start, Date end, List<String> items) {
        String MONGODBV3_DATABASE_OBJ = "device_workloads";
        MongoClient mongoClient = IOT_REST_MongoDBConnectionUtil.getMongoClient();

        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGODBV3_DATABASE_OBJ.toString());
        DataContext targetDataContext = new MongoDbDataContext(mongoDatabase);

        Column timeColumn = targetDataContext.getDefaultSchema().getTableByName("deviceWorkloads").getColumnByName("time");
        Column valueColumn = targetDataContext.getDefaultSchema().getTableByName("deviceWorkloads").getColumnByName("value");
        Column itemColumn = targetDataContext.getDefaultSchema().getTableByName("deviceWorkloads").getColumnByName("item");
        DataSet dataSet = targetDataContext.query()
                .from("deviceWorkloads").select("item", "value")
                .where("item").in(items)
                .and("cimId").in(ids)
                .and("time").greaterThan(localToUTC(start)).and("time").lessThan(localToUTC(end))
                .execute();

        Iterator<Row> rowItem = dataSet.iterator();

        HashMap<String, List<Integer>> result = new HashMap<>();
        List<Integer> pm25 =  Arrays.asList(0,0,0,0,0,0,0);
        List<Integer> pm10 =  Arrays.asList(0,0,0,0,0,0,0);
        List<Integer> temperature =  Arrays.asList(0,0,0,0);
        List<Integer> windSpeed =  Arrays.asList(0,0,0,0,0);
        List<Integer> noise =  Arrays.asList(0,0);


        while (rowItem.hasNext()) {
            Row currentRow = rowItem.next();
            String item = currentRow.getValue(itemColumn).toString();
            Double value = Double.parseDouble(currentRow.getValue(valueColumn).toString());
            int index = 0;
            if (item.equals("pm25") && items.contains(item)) {
                index = convertPM25Level(value);
                pm25.set(index,pm25.get(index) + 1);
            } else if (item.equals("pm10") && items.contains(item)) {
                index = convertPM10Level(value);
                pm10.set(index,pm10.get(index) + 1);
            } else if (item.equals("temperature") && items.contains(item)) {
                index = convertTemperatureLevel(value);
                temperature.set(index,temperature.get(index) + 1);
            } else if (item.equals("windSpeed") && items.contains(item)) {
                index = convertWindSpeedLevel(value);
                windSpeed.set(index,windSpeed.get(index) + 1);
            } else if (item.equals("noise") && items.contains(item)) {
                index = convertNoiseLevel(value);
                noise.set(index,noise.get(index) + 1);
            }
        }

        result.put("pm25",pm25);
        result.put("pm10",pm10);
        result.put("temperature",temperature);
        result.put("windSpeed",windSpeed);
        result.put("noise",noise);

        dataSet.close();
        return result;
    }


    public static Date localToUTC(Date localDate) {
        long localTimeInMillis = localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate = new Date(calendar.getTimeInMillis());
        return utcDate;
    }


    public static int convertPM25Level(double value) {
        if (value <= 35) {
            return 0;
        }
        if (value <= 75) {
            return 1;
        }
        if (value <= 115) {
            return 2;
        }
        if (value <= 150) {
            return 3;
        }
        if (value <= 250) {
            return 4;
        }
        if (value <= 350) {
            return 5;
        }
        if (value > 350) {
            return 6;
        }
        return 0;
    }

    public static int convertTemperatureLevel(double value) {
        if (value <= -15) {
            return 2;
        }
        if (value <= -10) {
            return 1;
        }
        if (value <= 34) {
            return 0;
        }
        if (value <= 37) {
            return 1;
        }
        if (value <= 40) {
            return 2;
        }
        if (value > 40) {
            return 3;
        }
        return 0;
    }

    public static int convertNoiseLevel(double value) {
        if (value >= 50) {
            return 1;
        }
        return 0;
    }

    public static int convertPM10Level(double value) {
        if (value <= 50) {
            return 0;
        }
        if (value <= 150) {
            return 1;
        }
        if (value <= 250) {
            return 2;
        }
        if (value <= 350) {
            return 3;
        }
        if (value <= 420) {
            return 4;
        }
        if (value > 420) {
            return 5;
        }
        return 1;
    }

    public static int convertWindSpeedLevel(double value) {
        if (value < 10.8) {
            return 0;
        }
        if (value <= 17.1) {
            return 1;
        }
        if (value <= 24.4) {
            return 2;
        }
        if (value <= 32.6) {
            return 3;
        }
        if (value >= 32.6) {
            return 4;
        }
        return 0;
    }
}
