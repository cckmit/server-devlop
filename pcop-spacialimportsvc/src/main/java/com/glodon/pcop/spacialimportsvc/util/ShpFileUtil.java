package com.glodon.pcop.spacialimportsvc.util;

import com.glodon.pcop.cim.common.model.FileStructureBean;
import com.glodon.sde.fileParser.CreateFileParser;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShpFileUtil {

    private static final Logger log = LoggerFactory.getLogger(ShpFileUtil.class);

    public static Map<String, String> dataTypeMap = new HashMap<>();
    private static final String DEFAULT_DATA_TYPE = "STRING";
    public static final String FID = "feature_id";
    public static final String THE_GEOM = "the_geom";
    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String defaultCharSet = "utf8";

    static {
        dataTypeMap.put("String", DEFAULT_DATA_TYPE);
        dataTypeMap.put("Integer", "INT");
        dataTypeMap.put("Double", "DOUBLE");
        dataTypeMap.put("Long", "LONG");
        dataTypeMap.put("Date", "DATE");
        dataTypeMap.put("Boolean", "BOOLEAN");
    }

    public static FileStructureBean getShpStructure(String fileId, String shpFile, String... charSet) throws IOException {
        FileStructureBean structureBean = new FileStructureBean();
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        Charset cs = Charset.forName(defaultCharSet);
        if (charSet.length > 0 && StringUtils.isNotBlank(charSet[0])) {
            cs = Charset.forName(charSet[0]);
        }
        ((ShapefileDataStore) dataStore).setCharset(cs);

        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();

        CoordinateReferenceSystem referenceSystem = geometryDescriptor.getCoordinateReferenceSystem();
        log.info("CRS of {} is: \n{}", shpFile, referenceSystem.toWKT());

        int totalCount = featureSource.getCount(Query.ALL);
        log.info("features count: {}", totalCount);

        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        Map<String, String> dataTypes = new HashMap<>();
        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            String propertyType = attributeDescriptor.getType().getBinding().getSimpleName();
            String propertyName = attributeDescriptor.getName().toString();
            if (StringUtils.isNotBlank(propertyType)) {
                dataTypes.put(propertyName, dataTypeMap.get(propertyType));
            } else {
                log.error("property type of {} is not support currently, instaead with {}", propertyType, DEFAULT_DATA_TYPE);
                dataTypes.put(propertyName, DEFAULT_DATA_TYPE);
            }
        }
        log.info("property name and type mapping: {}", dataTypes);
        structureBean.setFileId(fileId);
        structureBean.setTotalCount(totalCount);
        structureBean.setStructure(dataTypes);

        return structureBean;
    }

    public static List<Map<String, Object>> readShpContent(String shpFile, String... charSet) throws IOException {//NOSONAR
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        Charset cs = Charset.forName(defaultCharSet);
        if (charSet.length > 0 && StringUtils.isNotBlank(charSet[0])) {
            cs = Charset.forName(charSet[0]);
        }
        ((ShapefileDataStore) dataStore).setCharset(cs);

        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(Integer.MAX_VALUE);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        List<Map<String, Object>> contents = new ArrayList<>();
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                Map<String, Object> row = new HashMap<>();
                SimpleFeature feature = features.next();
                // String column = String.format(" %s = \'%s\' ", FID, feature.getID());
                //add geometry property
                // Geometry geometry = (Geometry) feature.getDefaultGeometry();
                // builder.append(", " + THE_GEOM + " = St_GeomFromText(\"").append((new WKTWriter2()).writeFormatted(geometry)).append("\") ");
                row.put(THE_GEOM, "");
                //add other properties
                for (Property property : feature.getProperties()) {
                    String propertyName = property.getName().toString();
                    String propertyValueStr = "";
                    if (THE_GEOM.toLowerCase().equals(propertyName.toLowerCase())) {
                        continue;
                    }
                    Object propertyValue = property.getValue();
                    if (propertyValue != null) {
                        Class aClass = property.getType().getBinding();
                        if (Date.class.equals(aClass)) {
                            propertyValueStr = defaultDateFormat.format(property.getValue());
                        } else {
                            propertyValueStr = property.getValue().toString();
                        }
                    }
                    row.put(propertyName, propertyValueStr);
                }
                contents.add(row);
            }
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
        return contents;
    }

    public static List<Map<String, Object>> readShpContentV1(String shpFile) {
        List<Map<String, Object>> contents = new ArrayList<>();
        try {
            log.info("Shp file local path: {}", shpFile);
            System.load("/usr/local/lib/libjni.so");
            CreateFileParser parser = new CreateFileParser();
            parser.Load(shpFile);
            Long count = parser.getRecordCount();
            log.info("Shp file record count {}", count);
            Map<String, String> structureMap = parser.getStruct();
            log.info("Shp file record structure {}", structureMap);
            for (int i = 0; i < count; i++) {
                try {
                    Map<String, Object> row_map = parser.getNextRow();
                    contents.add(row_map);
                    if (i < 10) {
                        log.info("Shp file content line {}: {}", i, row_map);
                    }
                } catch (Exception e) {
                    log.error("Shp record cast failed：lineNumber={}", i);
                }
            }
        } catch (Exception e) {
            log.error("Shp file import failed: filePath={}", shpFile);
            e.printStackTrace();
        }
        return contents;
    }


    public static void main(String[] args) {
        // String shpPathStr = "G:\\data\\geospatial_data\\ne_10m_admin_1_states_provinces\\ne_10m_admin_1_states_provinces.shp";
        // String shpPathStr = "D:\\data\\spatial_data\\ne_10m_rivers_lake_centerlines\\ne_10m_rivers_lake_centerlines.shp";
        String shpPathStr = "G:\\data\\geospatial_data\\规划试验数据-宗地层\\宗地层数据.shp";
        try {
            getShpStructure("", shpPathStr);
            List<Map<String, Object>> contents = readShpContent(shpPathStr);
            if (contents != null) {
                System.out.println(contents.get(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
