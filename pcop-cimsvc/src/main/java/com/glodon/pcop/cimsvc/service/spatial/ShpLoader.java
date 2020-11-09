package com.glodon.pcop.cimsvc.service.spatial;

import com.glodon.pcop.cim.common.model.entity.PropertyEntity;
import com.glodon.pcop.cim.engine.dataServiceEngine.dataServiceBureau.CimDataSpace;
import com.glodon.pcop.cim.engine.dataServiceFeature.BusinessLogicConstant;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.DatasetVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.InfoObjectTypeVO;
import com.glodon.pcop.cim.engine.dataServiceFeature.vo.PropertyTypeVO;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.CIMModelCore;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.DatasetDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.InfoObjectDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDef;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.model.PropertyTypeDefs;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.transferVO.InfoObjectValue;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceModelRuntimeException;
import com.glodon.pcop.cim.engine.dataServiceModelAPI.util.exception.DataServiceUserException;
import com.glodon.pcop.cimsvc.model.shp.ShpMappingImportBean;
import com.glodon.pcop.cimsvc.util.PinyinUtils;
import com.microsoft.schemas.office.x2006.encryption.STSaltSize;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ShpLoader {

    private static Logger log = LoggerFactory.getLogger(ShpLoader.class);

    public static final String FID = "ID";
    public static final String THE_GEOM = "geom";
    private static final String CREATE_PROPERTY_CMD_TEMPLATE = "CREATE PROPERTY %s %s";
    private static final String CREATE_SPATIAL_INDEX_CMD_TEMPLATE = "CREATE INDEX %s ON %s(%s) SPATIAL ENGINE LUCENE";

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static boolean createOClass(ODatabaseSession database, String className, String shpFile) throws IOException {
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("UTF-8"));
        SimpleFeatureType featureType = featureSource.getSchema();

        List<AttributeDescriptor> attributeDescriptorList = featureType.getAttributeDescriptors();
        StringBuilder stringBuilder = new StringBuilder();
        //add new class
        OSchema oSchema = database.getMetadata().getSchema();
        if (oSchema.existsClass(className)) {
            log.error("OClass of {} is already exists", className);
            return false;
        }

        OClass oClass = database.createClass(className);
        stringBuilder.append(className).append(": \n");
        oClass.createProperty(FID, OType.STRING);
        stringBuilder.append("\t").append(FID).append("\t").append(OType.STRING).append("\n");
        GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        if (geometryDescriptor == null) {
            log.error("no geometry descriptor exists");
            return false;
        }
        String geoEmbeddedClassName = "O" + geometryDescriptor.getType().getName();
        OClass geoOClass = oSchema.getClass(geoEmbeddedClassName);
        if (geoOClass == null) {
            log.error("embedded OClass of {} not exists", geoEmbeddedClassName);
            return false;
        }
        oClass.createProperty(THE_GEOM, OType.EMBEDDED, geoOClass);
        stringBuilder.append("\t").append(THE_GEOM).append("\t").append(OType.EMBEDDED).append("\t").append(geoEmbeddedClassName).append("\n");

        for (AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
            OType oType = OType.getTypeByClass(attributeDescriptor.getType().getBinding());
            String propertyName = attributeDescriptor.getName().toString();
            System.out.println("property name: " + propertyName);
            if (propertyName.toLowerCase().equals(FID.toLowerCase()) || propertyName.toLowerCase().equals(THE_GEOM.toLowerCase())) {
                continue;
            }
            if (oType == null) {
                log.error("Can not get OType of this property, name={}, class={}", attributeDescriptor.getName(),
                        attributeDescriptor.getType().getBinding());
            } else {
                propertyName = PinyinUtils.getPinYinWithoutSpecialChar(propertyName);
                stringBuilder.append("\t").append(propertyName).append("\t").append(oType).append("\n");
                oClass.createProperty(propertyName, oType);
            }
        }
        log.info("OClass " + stringBuilder);
        return true;
    }

    public static void contentInsert(ODatabaseSession database, String shpFile, String className,
                                     String objectTypeId) throws IOException {//NOSONAR
        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(Integer.MAX_VALUE);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            int rowNum = 0;
            while (features.hasNext()) {
                StringBuilder builder = new StringBuilder();
                builder.append("INSERT INTO ").append(className).append(" SET ");
                SimpleFeature feature = features.next();
                //add feature id property
                // String column = String.format(" %s = \'%s\' ", FID, objectTypeId + '_' + feature.getID().substring
                // (feature.getID().lastIndexOf('.') + 1));
                String column = String.format(" %s = \'%s\' ", FID, objectTypeId + '_' + rowNum++);
                builder.append(column);
                //add geometry property
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                builder.append(", " + THE_GEOM + " = St_GeomFromText(\"").append((new WKTWriter2()).writeFormatted(geometry).replaceAll("\n", "")).append("\") ");
                //非空间类型
                //add other properties
                for (Property property : feature.getProperties()) {
                    String propertyName = property.getName().toString();
                    if (THE_GEOM.toLowerCase().equals(propertyName.toLowerCase())) {
                        continue;
                    }
                    propertyName = PinyinUtils.getPinYinWithoutSpecialChar(propertyName);
                    Object propertyValue = property.getValue();
                    if (propertyValue != null) {
                        String propertyValueStr;
                        Class aClass = property.getType().getBinding();
                        if (Date.class.equals(aClass)) {
                            propertyValueStr = defaultDateFormat.format(property.getValue());
                        } else {
                            propertyValueStr = property.getValue().toString();
                        }
                        if (propertyValueStr != null && !propertyValueStr.trim().equals("")) {
                            column = String.format(" , %s = \'%s\'", propertyName, propertyValueStr.replace("'", " "));
                            builder.append(column);
                        }
                    }
                }
                // System.out.println(builder.toString());
                try {
                    // database.command(new OCommandSQL(builder.toString())).execute();
                    database.command(builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("insert error: " + builder);
                }
            }
            String createIndexCmd = String.format(CREATE_SPATIAL_INDEX_CMD_TEMPLATE, className + "." + THE_GEOM,
                    className, THE_GEOM);
            System.out.println(createIndexCmd);
            // database.command(new OCommandSQL(createIndexCmd));
            database.command(createIndexCmd);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
    }

    public static InfoObjectDef createObjectTypeAndDataSet(String tenantId, ShpMappingImportBean mappingImport,
                                                           CIMModelCore modelCore) throws DataServiceModelRuntimeException {
        InfoObjectDefs objectDefs = modelCore.getInfoObjectDefs();
        InfoObjectDef objectDef;
        CimDataSpace cds = modelCore.getCimDataSpace();
        if (cds.hasInheritFactType(mappingImport.getObjectTypeId())) {
            objectDef = objectDefs.getInfoObjectDef(mappingImport.getObjectTypeId());
        } else {
            cds.flushUncommitedData();
            InfoObjectTypeVO objectTypeVO = new InfoObjectTypeVO();
            objectTypeVO.setObjectId(mappingImport.getObjectTypeId());
            objectTypeVO.setObjectName(mappingImport.getObjectTypeName());
            objectTypeVO.setTenantId(tenantId);
            objectDef = objectDefs.addRootInfoObjectDef(objectTypeVO);
            log.info("add object type: {}", mappingImport.getObjectTypeId());
        }

        if (objectDef != null) {
            DatasetDef datasetDef = objectDef.getDatasetDef(mappingImport.getDataSetId());
            if (datasetDef == null) {
                log.info("add data set: {}", mappingImport.getDataSetId());
                DatasetDefs datasetDefs = modelCore.getDatasetDefs();
                DatasetVO datasetVO = new DatasetVO();
                datasetVO.setDatasetName(mappingImport.getDataSetId().trim());
                datasetVO.setDatasetDesc(mappingImport.getDataSetName());
                datasetVO.setDatasetStructure(DatasetDef.DatasetStructure.SINGLE);
                datasetVO.setDataSetType(BusinessLogicConstant.DatasetType.INSTANCE);
                datasetVO.setDatasetClassify("通用属性集");
                datasetVO.setInheritDataset(false);
                datasetVO.setHasDescendant(false);

                datasetDef = datasetDefs.addDatasetDef(datasetVO);
                objectDef.linkDatasetDef(datasetDef.getDatasetRID());

                PropertyTypeDefs propertyTypeDefs = modelCore.getPropertyTypeDefs();
                for (Map.Entry<String, String> entry : mappingImport.getPropertyMapping().entrySet()) {
                    log.info("add property: {}", entry.getValue());
                    PropertyTypeVO propertyTypeVO = new PropertyTypeVO();
                    propertyTypeVO.setPropertyTypeName(entry.getValue().trim());
                    propertyTypeVO.setPropertyTypeDesc(entry.getValue().trim());
                    propertyTypeVO.setPropertyFieldDataClassify(PropertyEntity.DataTypes.STRING.toString());

                    PropertyTypeDef propertyTypeDef = propertyTypeDefs.addPropertyTypeDef(propertyTypeVO);
                    datasetDef.addPropertyTypeDef(propertyTypeDef.getPropertyTypeRID());
                }
            }
        }
        return objectDef;
    }

    public static void shpMappingImport(String tenantId, ShpMappingImportBean mappingImport, String shpFile,
                                        InfoObjectDef objectDef) throws IOException, DataServiceUserException {
        Map<String, String> proMapping = mappingImport.getPropertyMapping();

        File file = new File(shpFile);
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        ((ShapefileDataStore) dataStore).setCharset(Charset.forName("UTF-8"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();
        SimpleFeatureType featureType = featureSource.getSchema();
        Query query = new Query(featureType.getTypeName());
        query.setMaxFeatures(Integer.MAX_VALUE);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures(query);

        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            int rowNum = 0;
            while (features.hasNext()) {
                InfoObjectValue objectValue = new InfoObjectValue();
                Map<String, Object> oneRow = new HashMap<>();
                oneRow.put(FID, mappingImport.getObjectTypeId() + '_' + rowNum++);
                SimpleFeature feature = features.next();
                //add other properties
                for (Property property : feature.getProperties()) {
                    String propertyName = property.getName().toString();
                    if (THE_GEOM.toLowerCase().equals(propertyName.toLowerCase())) {
                        continue;
                    }
                    // propertyName = PinyinUtils.getPinYinWithoutSpecialChar(propertyName);
                    Object propertyValue = property.getValue();
                    if (propertyValue != null) {
                        String propertyValueStr;
                        Class aClass = property.getType().getBinding();
                        if (Date.class.equals(aClass)) {
                            propertyValueStr = defaultDateFormat.format(property.getValue());
                        } else {
                            propertyValueStr = property.getValue().toString();
                        }
                        oneRow.put(proMapping.get(propertyName), propertyValueStr);
                    }
                }
                objectValue.setBaseDatasetPropertiesValue(oneRow);
                objectDef.newObject(objectValue, false);
            }
            log.info("add orw number: {}", ++rowNum);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }

    }


    public static void main(String[] args) {
        OrientDB orientDB = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
        ODatabaseSession db = orientDB.open("spatial", "root", "wyc");
        String shpPathStr = "G:\\data\\geospatial_data\\查询测试数据\\WGS84\\电力工程电网隧道.shp";

        try {
            String className = "XHC_KJCX_DLSD_V2";
            createOClass(db, className, shpPathStr);
            contentInsert(db, shpPathStr, className, "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
