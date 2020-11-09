package com.glodon.pcop.cimsvc.service.spatial;

import com.alibaba.fastjson.JSON;
import com.glodon.pcop.cim.common.model.FileStructureBean;
import com.glodon.pcop.cim.common.util.CimConstants;
import com.glodon.pcop.cim.common.util.FileUtil;
import com.glodon.pcop.cim.common.util.ShpFileUtil;
import com.glodon.pcop.cim.common.util.ZipFileUtil;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataWarehouse.ExploreParameters;
import com.glodon.pcop.cim.engine.dataServiceEngine.util.factory.CimDataEngineComponentFactory;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.factory.ModelAPIComponentFactory;
import com.glodon.pcop.cimsvc.config.properties.OrientdbPropertyConfig;
import com.glodon.pcop.cimsvc.model.shp.ShpMappingImportBean;
import com.glodon.pcop.cimsvc.model.spatial.AreaInputParserUtil;
import com.glodon.pcop.cimsvc.model.spatial.BCQueryInput;
import com.glodon.pcop.cimsvc.model.spatial.BufferIntersectQueryInput;
import com.glodon.pcop.cimsvc.model.v2.CommonQueryConditionsBean;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import com.google.gson.Gson;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class SpatialService {
    private static final Logger log = LoggerFactory.getLogger(SpatialService.class);

    private static final String PREFIX = "SHP_";
    private static final String SUFFIX = "_CLASS";
    private static final String DECIMAL_FUNCTION = ".asDecimal()";

    @Value("${my.orientdb.poolSize}")
    private int poolSize = 10;

    @Autowired
    private OrientdbPropertyConfig orientConfig;

    private ExecutorService executorService;

    private OrientDB orientDB;
    private ODatabasePool pool;

    @PostConstruct
    public void init() {
        orientDB = new OrientDB(orientConfig.getLocation(), OrientDBConfig.defaultConfig());
        pool = new ODatabasePool(orientDB, orientConfig.getDiscoverSpace(), orientConfig.getAccount(),
                orientConfig.getPassword());
        log.info("thread pool size: {}", poolSize);
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    @PreDestroy
    public void destory() {
        if (pool != null) {
            pool.close();
        }
        if (orientDB != null) {
            orientDB.close();
        }
    }

    public void shpLoader(String objectTypeId, MultipartFile shpFile) {
        try {
            String originalName = shpFile.getOriginalFilename();
            log.info("original name: {}", originalName);
            log.info("original size: {}", shpFile.getSize());
            Path tmpDirectory = Paths.get("tmp", originalName.substring(0, originalName.lastIndexOf('.')));
            if (Files.exists(tmpDirectory)) {
                FileUtil.deleteFile(tmpDirectory, true);
            }
            Files.createDirectories(tmpDirectory);
            log.info("tmp directory: {}", tmpDirectory);
            Path tmpFile = tmpDirectory.resolve(originalName);
            log.info("tmp file: {}", tmpFile.toAbsolutePath());
            shpFile.transferTo(Files.createFile(tmpFile.toAbsolutePath()).toFile());

            ZipFileUtil.unzip(tmpFile.toString(), tmpDirectory.toString(), "GBK");

            Path shpPath = null;
            try (DirectoryStream<Path> entries =
                         Files.newDirectoryStream(tmpDirectory.resolve(originalName.substring(0,
                                 originalName.lastIndexOf('.'))))) {
                for (Path path : entries) {
                    if (path.toString().toLowerCase().endsWith("shp")) {
                        shpPath = path;
                    }
                }
            }

            if (shpPath != null) {
                String className = getClassNameByObjectTypeId(objectTypeId);
                log.debug("class name: {}", className);
                OrientDB orientDB = new OrientDB(orientConfig.getLocation(), OrientDBConfig.defaultConfig());
                ODatabaseSession db = orientDB.open(orientConfig.getDiscoverSpace(), orientConfig.getAccount(),
                        orientConfig.getPassword());
                try {
                    ShpLoader.createOClass(db, className, shpPath.toString());
                    ShpLoader.contentInsert(db, shpPath.toString(), className, objectTypeId);
                } finally {
                    db.close();
                }
            } else {
                log.error("no shp file found");
            }
        } catch (Exception e) {
            log.error("shp file loader filed", e);
        }
    }

    public List<String> bufferContainAnalysis(String objectTypeId, BCQueryInput queryConditions) throws InterruptedException {
        List<String> cimIDs = new ArrayList<>();
        List<BufferContainTask> taskList = new ArrayList<>();
        try (ODatabaseSession db = pool.acquire()) {
            OClass oClass = db.getClass(getClassNameByObjectTypeId(objectTypeId));
            if (oClass == null) {
                log.error("class of {} not found", getClassNameByObjectTypeId(objectTypeId));
                return cimIDs;
            }

            String statement = getQueryByScopeAndGeneralPropertiesSql(objectTypeId, queryConditions.getConditions(),
                    queryConditions.getWktArea());

            OResultSet rs = db.query(statement);

            while (rs.hasNext()) {
                OResult row = rs.next();
                if (queryConditions.getBufferInputs() != null && queryConditions.getBufferInputs().size() > 0) {
                    BufferContainTask task = new BufferContainTask(queryConditions.getBufferInputs(),
                            row.getProperty("tx_geom").toString(), row.getProperty("ID").toString(), pool);
                    taskList.add(task);
                } else {
                    log.info("no buffer conditions");
                    cimIDs.add(row.getProperty("ID"));
                }
            }
            rs.close();
        }

        List<Future<String>> futureList = executorService.invokeAll(taskList);
        for (Future<String> ft : futureList) {
            try {
                String rtId = ft.get();
                if (StringUtils.isNotBlank(rtId)) {
                    cimIDs.add(rtId);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cimIDs;
    }

    public List<String> bufferContainAnalysis(String objectTypeId, BufferIntersectQueryInput queryConditions) throws InterruptedException {
        List<String> cimIDs = new ArrayList<>();
        List<BufferContainTask> taskList = new ArrayList<>();
        try (ODatabaseSession db = pool.acquire()) {
            OClass oClass = db.getClass(getClassNameByObjectTypeId(objectTypeId));
            if (oClass == null) {
                log.error("class of {} not found", getClassNameByObjectTypeId(objectTypeId));
                return cimIDs;
            }

            String wktArea = "";
            if (queryConditions.getCustomArea() != null) {
                wktArea = AreaInputParserUtil.getWktArea(db, queryConditions.getCustomArea());
            }

            String statement = getQueryByScopeAndGeneralPropertiesSql(objectTypeId, queryConditions.getConditions(),
                    wktArea);
            OResultSet rs = db.query(statement);

            while (rs.hasNext()) {
                OResult row = rs.next();
                if (queryConditions.getBufferInputs() != null && queryConditions.getBufferInputs().size() > 0) {
                    BufferContainTask task = new BufferContainTask(queryConditions.getBufferInputs(),
                            row.getProperty("tx_geom").toString(), row.getProperty("ID").toString(), pool);
                    taskList.add(task);
                } else {
                    log.info("no buffer conditions");
                    cimIDs.add(row.getProperty("ID"));
                }
            }
            rs.close();
        }

        List<Future<String>> futureList = executorService.invokeAll(taskList);
        for (Future<String> ft : futureList) {
            try {
                String rtId = ft.get();
                if (StringUtils.isNotBlank(rtId)) {
                    cimIDs.add(rtId);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cimIDs;
    }

    /**
     * 根据输入的对象类型查询对应的存储几何数据的类型
     *
     * @param objectTypeId
     * @return
     */
    public static String getClassNameByObjectTypeId(String objectTypeId) {
        if (StringUtils.isNotBlank(objectTypeId)) {
            return PREFIX + objectTypeId + SUFFIX;
        } else {
            log.error("object type id is blank");
            return null;
        }
    }

    /**
     * 单实例，单对象类型缓冲区查询
     *
     * @param db
     * @param wktGeom
     * @param bufferInput
     * @return
     */
    private boolean filterBybuffer(ODatabaseSession db, String wktGeom, BCQueryInput.BufferInput bufferInput) {
        if (bufferInput.getDistance() < 0) {
            log.info("distance less than 0");
            return true;
        }
        String statement = getQueryByBufferSql(wktGeom, bufferInput);

        OResultSet oResultSet = db.query(statement);
        if (oResultSet.hasNext()) {
            oResultSet.close();
            return true;
        } else {
            oResultSet.close();
            return false;
        }
    }

    /**
     * 构造通过选定区域和普通属性查询实例的SQL
     *
     * @param objectTypeId
     * @param conditions
     * @param wktArea
     * @return
     */
    public String getQueryByScopeAndGeneralPropertiesSql(String objectTypeId,
                                                         List<CommonQueryConditionsBean> conditions, String wktArea) {//NOSONAR
        // general where conditions
        String whereCond = "";
        if (conditions != null && conditions.size() > 0) {
            StringBuffer sb = new StringBuffer();
            boolean firstFlag = true;
            for (CommonQueryConditionsBean cnd : conditions) {
                if (StringUtils.isNotBlank(cnd.getPropertyName())) {
                    if (firstFlag) {
                        sb.append(" WHERE ").append(parserCondition(cnd));
                        firstFlag = false;
                    } else {
                        if (ExploreParameters.FilteringLogic.AND.equals(cnd.getFilterLogical())) {
                            sb.append("AND ").append(parserCondition(cnd));
                        } else if (ExploreParameters.FilteringLogic.OR.equals(cnd.getFilterLogical())) {
                            sb.append("OR ").append(parserCondition(cnd));
                        } else {
                            log.error("not support filtering logic: {}", cnd.getFilterLogical());
                        }
                    }
                    sb.append(' ');
                } else {
                    log.info("general property key is blank");
                }
            }
            whereCond = sb.toString();
            log.debug("general where condition: {}", whereCond);
        } else {
            log.info("no general property query condition");
        }
        //filter by properties of their own
        String statement;
        if (StringUtils.isNotBlank(wktArea)) {
            statement = String.format("SELECT FROM ( SELECT ID, ST_Intersects(ST_GeomFromText('%s'), geom) AS GALF , " +
                            "ST_AsText(geom) AS tx_geom FROM %s %s ) WHERE GALF = true", wktArea,
                    getClassNameByObjectTypeId(objectTypeId), whereCond);
        } else {
            statement = String.format("SELECT ID, ST_AsText(geom) AS tx_geom FROM %s %s",
                    getClassNameByObjectTypeId(objectTypeId), whereCond);
        }

        log.info("query by general properties and scope sql: {}", statement);
        return statement;
    }

    /**
     * 构造缓冲区查询SQL
     *
     * @param wktGeom
     * @param bufferInput
     * @return
     */
    public String getQueryByBufferSql(String wktGeom, BCQueryInput.BufferInput bufferInput) {
        double distenceByDegree = 0.01D * (bufferInput.getDistance() / 1113D);
        log.info("distince by degree: {}", distenceByDegree);
        String statement = String.format("SELECT FROM ( SELECT ID, ST_Intersects(ST_Buffer(ST_GeomFromText('%s'), %s)" +
                        ", geom) AS GALF FROM %s ) WHERE GALF = true", wktGeom, distenceByDegree,
                getClassNameByObjectTypeId(bufferInput.getObjectTypeId()));
        log.info("query by buffer: {}", statement);
        return statement;
    }

    private static String parserCondition(CommonQueryConditionsBean conditionsBean) {//NOSONAR
        StringBuffer cond = new StringBuffer();

        if (conditionsBean != null && StringUtils.isNotBlank(conditionsBean.getPropertyName())) {
            String filterType = conditionsBean.getFilterType();
            switch (filterType) {
                case "BetweenFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName()).append(" BETWEEN ").append(conditionsBean.getFirstParam()).append(" and ").append(conditionsBean.getSecondParam());
                    break;
                case "EqualFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName()).append(" = ").append('\'').append(conditionsBean.getFirstParam()).append('\'');
                    break;
                case "GreaterThanFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName().trim()).append(DECIMAL_FUNCTION).append(" > ").append(conditionsBean.getFirstParam());
                    break;
                case "GreaterThanEqualFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName().trim()).append(DECIMAL_FUNCTION).append(" >= "
                    ).append(conditionsBean.getFirstParam());
                    break;
                case "LessThanFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName().trim()).append(DECIMAL_FUNCTION).append(" <").append(conditionsBean.getFirstParam());
                    break;
                case "LessThanEqualFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName().trim()).append(DECIMAL_FUNCTION).append(" <= "
                    ).append(conditionsBean.getFirstParam());
                    break;
                case "NotEqualFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName()).append(" <> ").append(conditionsBean.getFirstParam());
                    break;
                case "SimilarFilteringItem":
                    cond = cond.append(conditionsBean.getPropertyName()).append(" like ").append('\'').append(conditionsBean.getFirstParam()).append('\'');
                    break;
                case "InValueFilteringItem":
                    if (conditionsBean.getListParam() != null && conditionsBean.getListParam().size() > 0) {
                        cond = cond.append(conditionsBean.getPropertyName()).append(" in ").append('[');
                        boolean firstElement = true;
                        for (String parm : conditionsBean.getListParam()) {
                            if (firstElement) {
                                cond.append('\'').append(parm).append('\'');
                                firstElement = false;
                            } else {
                                cond.append(" ,").append('\'').append(parm).append('\'');
                            }
                        }
                        cond.append(']');
                    }
                    break;
                default:
                    log.error("not support filter type");
                    break;
            }
        }

        return cond.toString();
    }

    public Map<String, List<Map<String, Object>>> circleBufferBatch(String centerPoint, Double radius,
                                                                    List<String> objectTypeIds) {
        Map<String, List<Map<String, Object>>> res = new HashMap<>();
        for (String objId : objectTypeIds) {
            log.info("object type Id: {}", objId);
            if (StringUtils.isNotBlank(objId)) {
                res.put(objId, circleBuffer(centerPoint, radius, objId));
            }
        }

        return res;
    }

    public List<Map<String, Object>> circleBuffer(String centerPoint, Double radius, String objectTypeId) {
        String statement = getCircleBufferSql(centerPoint, radius, objectTypeId);
        // String statement = "SELECT * FROM (SELECT *, ST_Within(ST_GeomFromText(geom_txt),  ST_Buffer
        // (ST_GeomFromText" +
        //         "(\"POINT (12.4684635 41.8914114)\"), 0.004492362982929021)) as flag from " +
        //         "GLD_IH_FACT_yuanjk_shp_test_aaaaa where geom_txt.trim().length() > 0) WHERE flag=true";
        // String statement = centerPoint;

        List<Map<String, Object>> res = new ArrayList<>();
        try (ODatabaseSession db = pool.acquire()) {
            OResultSet rs = db.query(statement);
            while (rs.hasNext()) {
                Map<String, Object> rec = new HashMap<>();
                OResult row = rs.next();
                log.info("row to json: {}", row.toJSON());
                if (row.hasProperty(CimConstants.BaseDataSetKeys.ID)) {
                    rec.put(CimConstants.BaseDataSetKeys.ID, row.getProperty(CimConstants.BaseDataSetKeys.ID));
                }
                if (row.hasProperty(CimConstants.BaseDataSetKeys.NAME)) {
                    rec.put(CimConstants.BaseDataSetKeys.NAME, row.getProperty(CimConstants.BaseDataSetKeys.NAME));
                }
                if (row.hasProperty("geom_txt")) {
                    rec.put("geom_txt", row.getProperty("geom_txt"));
                }
                log.info("IIIIIDDDD: {}", rec);
                res.add(rec);
            }
            rs.close();
        }

        return res;
    }

    public String getCircleBufferSql(String centerPoint, Double radius, String objectTypeId) {
        String realObjectTypeId = "GLD_IH_FACT_" + objectTypeId.trim();
        double distenceByDegree = 0.01D * (radius / 1113D);

        String statement = String.format("SELECT ID, NAME, geom_txt FROM (" +
                "SELECT ID, NAME, geom_txt, ST_DWithin(ST_GeomFromText(geom_txt), ST_GeomFromText(\"%s\"), " +
                "%s) as flag from %s where geom_txt.trim().length() " +
                "> 0) WHERE flag=true", centerPoint, distenceByDegree, realObjectTypeId);
        log.info("circle buffer query: {}", statement);
        return statement;
    }

    public void mappingImport(String tenantId, String mappingInfo, MultipartFile shpFile) {
        try {
            String originalName = shpFile.getOriginalFilename();
            log.info("original name: {}", originalName);
            log.info("original size: {}", shpFile.getSize());
            Path tmpDirectory = Paths.get("tmp", originalName.substring(0, originalName.lastIndexOf('.')));
            if (Files.exists(tmpDirectory)) {
                FileUtil.deleteFile(tmpDirectory, true);
            }
            Files.createDirectories(tmpDirectory);
            log.info("tmp directory: {}", tmpDirectory);
            Path tmpFile = tmpDirectory.resolve(originalName);
            log.info("tmp file: {}", tmpFile.toAbsolutePath());
            shpFile.transferTo(Files.createFile(tmpFile.toAbsolutePath()).toFile());

            ZipFileUtil.unzip(tmpFile.toString(), tmpDirectory.toString(), "GBK");

            Path shpPath = null;
            try (DirectoryStream<Path> entries =
                         Files.newDirectoryStream(tmpDirectory.resolve(originalName.substring(0,
                                 originalName.lastIndexOf('.'))))) {
                for (Path path : entries) {
                    if (path.toString().toLowerCase().endsWith("shp")) {
                        shpPath = path;
                    }
                }
            }

            if (shpPath != null) {
                CimDataSpace cds = null;
                try {
                    cds = CimDataEngineComponentFactory.connectInfoDiscoverSpace(CimConstants.defauleSpaceName);
                    CIMModelCore modelCore = ModelAPIComponentFactory.getCIMModelCore(CimConstants.defauleSpaceName,
                            tenantId);
                    modelCore.setCimDataSpace(cds);

                    // ShpMappingImportBean mappingImport = new Gson().fromJson(mappingInfo, ShpMappingImportBean
                    // .class);
                    ShpMappingImportBean mappingImport = updateMappingInput(shpPath.toString(), mappingInfo);
                    if (mappingImport != null) {
                        log.info("objectTypeId={}, objectTypeName={}, dataSetId={}, dataSetName={}, " +
                                        "propertyMapping={}", mappingImport.getObjectTypeId(),
                                mappingImport.getObjectTypeName(), mappingImport.getDataSetId(),
                                mappingImport.getDataSetName(), mappingImport.getPropertyMapping());
                    } else {
                        log.error("mapping info deserialize failed");
                    }

                    InfoObjectDef objectDef = ShpLoader.createObjectTypeAndDataSet(tenantId, mappingImport, modelCore);
                    ShpLoader.shpMappingImport(tenantId, mappingImport, shpPath.toString(), objectDef);
                } finally {
                    if (cds != null) {
                        cds.closeSpace();
                    }
                }
            } else {
                log.error("no shp file found");
            }
        } catch (Exception e) {
            log.error("shp file loader filed", e);
        }
    }

    public ShpMappingImportBean updateMappingInput(String shpFile, String mappingInfo) throws IOException {
        ShpMappingImportBean mappingImport = new Gson().fromJson(mappingInfo, ShpMappingImportBean.class);
        Assert.hasText(mappingImport.getObjectTypeId(), "object type id is blank");
        if (StringUtils.isBlank(mappingImport.getDataSetId())) {
            String dataSetId = mappingImport.getObjectTypeId() + "DataSet__" + DateTimeUtils.currentTimeMillis();
            mappingImport.setDataSetId(dataSetId);
            mappingImport.setDataSetName(dataSetId);
        }

        if (mappingImport.getPropertyMapping() == null || mappingImport.getPropertyMapping().size() < 1) {
            FileStructureBean fileStructureBean = ShpFileUtil.getShpStructure(null, shpFile);
            Map<String, String> structure = fileStructureBean.getStructure();
            Assert.notEmpty(structure, "no filed in this shp file");
            Map<String, String> keyMapping = new HashMap<>();
            for (Map.Entry<String, String> entry : structure.entrySet()) {
                keyMapping.put(entry.getKey(), PinyinUtils.getPinYinWithoutSpecialChar(entry.getKey()));
            }
            mappingImport.setPropertyMapping(keyMapping);
        }
        log.info("mapping info: {}", JSON.toJSONString(mappingImport));
        return mappingImport;
    }

}
