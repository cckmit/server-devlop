package com.glodon.pcop.cim.common.util;

import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CimConstants {
    //bim 文件类型
    public static Set<String> BIM_FILE_TYPES = new HashSet<>();
    //office 文件类型
    public static Set<String> OFFICE_FILE_TYPES = new HashSet<>();
    //导入文件类型
    public static Map<String, String> FILE_TYPES_MAPPING = new HashMap<>();

    public static Set<String> BASE_DATASET_KEYS_SET = new HashSet<>();

    public static final String OBJECT_TYPE_AND_TAG_RELATION_TYPE_DEFAULT = "OBJECT_TYPE_AND_TAG_RELATION_TYPE";

    public static final long MAX_OFFICE_FILE_SIZE = 100 * 1024 * 1024;//100M max office file size

    public final static String DataClassify_DIMENSION = "DIMENSION";
    public final static String DataClassify_FACT = "FACT";


    static {

        FILE_TYPES_MAPPING.put("3DS", "3ds文件");
        FILE_TYPES_MAPPING.put("BMV", "bmv文件");
        FILE_TYPES_MAPPING.put("DAE", "dae文件");
        FILE_TYPES_MAPPING.put("DGN", "dgn文件");
        FILE_TYPES_MAPPING.put("DOC", "Microsoft Word 97-2003 文档");
        FILE_TYPES_MAPPING.put("DOCX", "Microsoft Word 文档");
        FILE_TYPES_MAPPING.put("DWG", "dwg文件");
        FILE_TYPES_MAPPING.put("DXF", "dxf文件");
        FILE_TYPES_MAPPING.put("IFC", "ifc文件");
        FILE_TYPES_MAPPING.put("IGMS", "igms文件");
        FILE_TYPES_MAPPING.put("JPG", "jpg文件");
        FILE_TYPES_MAPPING.put("MAX", "max文件");
        FILE_TYPES_MAPPING.put("MDB", "Microsoft Access Database 文件");
        FILE_TYPES_MAPPING.put("MP4", "mp4文件");
        FILE_TYPES_MAPPING.put("MPP", "Microsoft Project 文档");
        FILE_TYPES_MAPPING.put("OBJ", "obj文件");
        FILE_TYPES_MAPPING.put("PDF", "pdf文件");
        FILE_TYPES_MAPPING.put("PLY", "ply文件");
        FILE_TYPES_MAPPING.put("PNG", "png文件");
        FILE_TYPES_MAPPING.put("PPTX", "Microsoft PowerPoint 演示文稿");
        FILE_TYPES_MAPPING.put("RAR", "rar文件");
        FILE_TYPES_MAPPING.put("RFA", "rfa文件");
        FILE_TYPES_MAPPING.put("RVT", "rvt文件");
        FILE_TYPES_MAPPING.put("SKP", "skp文件");
        FILE_TYPES_MAPPING.put("STL", "stl文件");
        FILE_TYPES_MAPPING.put("TIFF", "tiff文件");
        FILE_TYPES_MAPPING.put("TXT", "文本文档");
        FILE_TYPES_MAPPING.put("WMV", "wmv文件");
        FILE_TYPES_MAPPING.put("XLS", "Microsoft Excel 97-2003 工作表");
        FILE_TYPES_MAPPING.put("XLSX", "Microsoft Excel 工作表");
        FILE_TYPES_MAPPING.put("ZIP", "zip文件");

        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.ID);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.NAME);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.OBJECT_TYPE_ID);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.IS_URBAN_PARTS);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.COMMENT);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.SCALING);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.ANGLE);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.OBJECT_TYPE_ID_V1);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.CREATOR);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.CREATE_TIME);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.UPDATE_TIME);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.X);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.Y);
        BASE_DATASET_KEYS_SET.add(BaseDataSetKeys.Z);

    }

    public static final String EXTERNAL_DATA_SET_TOTAL_COUNT = "EXTERNAL_DATA_SET_TOTAL_COUNT";

    public static final String TREE_PARENT_CHILD_RELATION_TYPE = "TREE_PARENT_CHILD_RELATION";


    /**
     * 默认OrientDb存储库
     */
    public static String defauleSpaceName = "test";
    public static String defalutTenantId = "2";

    public static final String UPDATE_DATETIME = "updateDateTime";
    public static final String CREATE_DATETIME = "createDateTime";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CREATE_TIME = "createTime";
    public static final String IS_DISABLED = "isDisabled";
    public static final String CREATOR = "creator";
    public static final String UPDATOR = "updator";
    public static final String COMMENT = "comment";
    public static final String INDUSTRY_NAME = "industryName";
    public static final String OBJECT_TYPE_ID = "objectTypeId";


    /**
     * 每个属性集必须具有的属性
     */
    public static final String ID_PROPERTY_TYPE_NAME = "ID";
    public static final String NAME_PROPERTY_TYPE_NAME = "NAME";

    /**
     * BASE64图片前缀
     */
    public static final String BASE64_IMG_PREFIX = "data:image/gif;base64,";


    /**
     * 属性类型定义中的属性key
     */
    public static final String PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_NAME = "CIM_BUILDIN_PROPERTYTYPE_NAME ";
    public static final String PROPERTY_TYPE_CIM_BUILDIN_PROPERTYTYPE_DATACLASSIFY =
            "CIM_BUILDIN_PROPERTYTYPE_DATACLASSIFY ";


    /**
     * 属性类型
     */
    public static class SupportPropertyTypes {
        public static final String BOOLEAN = "BOOLEAN";
        public static final String INT = "INT";
        public static final String SHORT = "SHORT";
        public static final String LONG = "LONG";
        public static final String FLOAT = "FLOAT";
        public static final String DOUBLE = "DOUBLE";
        public static final String DATE = "DATE";
        public static final String STRING = "STRING";
        public static final String BYTE = "BYTE";
        public static final String BINARY = "BINARY";
    }

    public static final String PROPERTY_PRIMAY_KEY_SEPARTOR = "|";
    public static final String INSTANCE_RID = "instanceRid";
    public static final String INPUT_INSTANCE_RID = "instance_rid";

    public static class BaseFileInfoKeys {
        public static String BaseFileObjectTypeName = "BASE_FILE_METADATA_INFO";
        public static final String FILE_DATA_ID = "fileDataId";
        public static final String FILE_DATA_NAME = "fileDataName";
        public static final String FILE_SIZE = "fileSize";
        public static final String CREATOR = CimConstants.CREATOR;
        public static final String UPDATOR = CimConstants.UPDATOR;
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;
        public static final String FILE_DATA_TYPE = "fileDataType";
        public static final String SRC_FILE_NAME = "srcFileName";
        public static final String MINIO_OBJECT_NAME = "minioObjectName";
        public static final String INDUSTRY_NAME = CimConstants.INDUSTRY_NAME;
        public static final String INDUSTRY_RID = "indusrtyRid";
        public static final String TRANSLATE_STATUS = "translateStatus";
        public static final String TRANSLATE_TASK_ID = "translateTaskId";

        public static final String BUCKET_NAME = "bucketName";
        public static final String OBJECT_NAME = "objectName";
        public static final String FILE_ID = "fileId";
        public static final String DATA_BAG_ID = "databagId";
        public static final String FILE_TYPE = "fileType";
        public static final String FILE_CONTENT_TYPE = "fileContentType";
        public static final String RESULT_STATUS = "status";
        public static final String PREVIEW_PATH = "previewPath";
        public static final String PREVIEW_TYPE = "previewType";
    }

    public static class BaseFileDownloadInfoKeys {
        public static String baseFileDownloadObjectName = "BASE_FILE_DOWNLOAD_METADATA_INFO";
        public static final String FILE_NAME = "fileName";
        public static final String FILE_SIZE = "fileSize";
        public static final String CREATOR = CimConstants.CREATOR;
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
    }

    public static class IndustryTypeNodeKeys {
        public static String INDUSTRY_ID = "industryId";
        public static String INDUSTRY_NAME = CimConstants.INDUSTRY_NAME;
        public static String PARENT_INDUSTRY_RID = "parentIndustryRid";
        public static String CREATE_TIME = CimConstants.CREATE_TIME;
        public static String UPDATE_TIME = CimConstants.UPDATE_TIME;

        public static String INDUSTRY_TYPE_NAME = "industryTypeName";
        public static String INDUSTRY_TYPE_DESC = "industryTypeDesc";
        public static String CREATOR_ID = CimConstants.CREATOR;
        public static String UPDATOR_ID = CimConstants.UPDATOR;
    }

    public static class ObjectTypeNodeKeys {
        public static final String OBJECT_NAME = "objectName";
        public static final String DISPLAY_NAME = "displayName";

        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;

        public static final String CREATOR_ID = CimConstants.CREATOR;
        public static final String UPDATOR_ID = CimConstants.UPDATOR;
        public static final String COMMENT = CimConstants.COMMENT;

        public static final String INFO_OBJECT_TYPE_NAME = "infoObjectTypeName";
        public static final String INFO_OBJECT_TYPE_DESC = "infoObjectTypeDesc";
        public static final String UPDATE_DATE_TIME = "updateDateTime";
        public static final String CREATE_DATE_TIME = "createDateTime";
    }

    public static class InstanceNodeKeys {
        public static final String ID = "ID";
        public static final String NAME = "NAME";
        public static final String OBJECT_NAME = "objectName";

        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;

        public static final String CREATOR_ID = CimConstants.CREATOR;
        public static final String UPDATOR_ID = CimConstants.UPDATOR;
        public static final String COMMENT = CimConstants.COMMENT;
    }

    public static class BaseDataSetKeys {
        public static String ID = "ID";
        public static String NAME = "NAME";
        public static String OBJECT_TYPE_ID = CimConstants.OBJECT_TYPE_ID;
        public static String IS_URBAN_PARTS = "isUrbanParts";
        public static String COMMENT = CimConstants.COMMENT;
        public static String SCALING = "scaling";
        public static String ANGLE = "angle";
        public static String OBJECT_TYPE_ID_V1 = "objecttypeID";
        public static String CREATOR = CimConstants.CREATOR;
        public static String CREATE_TIME = "createtime";//NOSONAR
        public static String UPDATE_TIME = "updatetime";//NOSONAR
        public static String X = "x";
        public static String Y = "y";
        public static String Z = "z";
    }

    public static class FileContentTypes {
        public static final String SHP = "SHP";
        public static final String GDB = "GDB";
        public static final String MDB = "MDB";
        public static final String XLS = "XLS";
        public static final String XLSX = "XLSX";
    }

    public static class FileImportProperties {
        public static final String DATA_IMPORT_MAPPING_INFO_TYPE_NAME = "DATA_IMPORT_MAPPING_INFO";

        public static final String TASK_ID = "taskId";
        public static final String TENANT_ID = "tenantId";
        public static final String OBJECT_TYPE_ID = CimConstants.OBJECT_TYPE_ID;
        public static final String DATA_SET_ID = "dataSetId";
        public static final String DATA_SET_NAME = "DataSetName";
        public static final String PROPERTIES_MAPPING = "propertiesMapping";

        public static final String FILE_DATA_ID = "fileDataId";
        public static final String SINGLE_FILE_NAME = "singleFileName";
        public static final String COMMENT = CimConstants.COMMENT;
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;
        public static final String FILE_CONTENT_TYPE = "fileContentType";
    }

    public static class ContentTreeObjectType {
        public static final String CONTENT_TREE_OBJECT = "DATA_MANAGER_CONTENT_TREE";
        public static final String CONTENT_TREE_RELATION = "DATA_MANAGER_CONTENT_TREE_RELATION";
        // public static final String CONTENT_TREE_RELATION = "DATA_MANAGER_CONTENT_TREE_RELATION";

        public static final String DISPLAY_NAME = "displayName";
        public static final String COMMENT = CimConstants.COMMENT;
        public static final String NODE_TYPE = "nodeType";
        public static final String CREATE_TIME = "createtime";//NOSONAR
        public static final String UPDATE_TIME = "updatetime";//NOSONAR
        public static final String TENANT_ID = "tenantId";
        public static final String SUBMISSION_UNIT = "SubmissionUnit";

        public static final String NODE_TYPE_INDUSTRY = "INDUSTRY";
        public static final String NODE_TYPE_OBJECT = "OBJECT";
        public static final String NODE_TYPE_FILE = "FILE";
        public static final String NODE_TYPE_INSTANCE = "INSTANCE";

    }

    /**
     * 树节点基本信息
     */
    public static class TreeNodeBaseInfo {
        public static final String TREE_NODE_INFO_DATA_SET = "TreeNodeInfoDataSet";
        public static final String ID = "ID";
        public static final String NAME = "NAME";
        public static final String NODE_TYPE = "nodeType";
        public static final String PARENT_ID = "parentID";
        public static final String RELATION_TYPE = "relationType";
        public static final String REF_OBJECT_TYPE = "refObjectType";

        public static final String INDUSTRY_RID ="industryRid";
        public static final String OBJECT_TYPE_RID ="objectTypeRid";
        public static final String DATASET_RID ="datasetRid";
        public static final String RELATIONSHIP_RID ="relationShipRid";
        public static final String REF_CIM_ID = "refCimId";
        public static final String INSTANCE_RID = "instanceRid";
        public static final String FILTER = "filter";
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;
        public static final String TREE_DEF_ID = "treeDefId";
        public static final String LEVEL = "level";
        public static final String IDX = "idx";
        public static final Integer ROOT_LEVEL = 0;
    }

    /**
     * 树附加信息
     */
    public static class TreeAttachedInfo{
        public static final String CIM_BIM_VIS = "CIM_BIM_VIS";
        public static final String COMMON_VISUAL_REPRESENTATION_OF = "COMMON_VisualRepresentationOf";
    }

    public static class TreeDefinitionProperties {
        public static final String TREE_DEFINITION_OBJECT_TYPE = "TREE_DEFINITION";
        public static final String ID = "ID";
        public static final String NAME = "NAME";
        public static final String COMMENT = CimConstants.COMMENT;
        public static final String TREE_NODE_OBJECT = "treeNodeObject";
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;

    }

    public static class DataManagerTreeProperties {
        public static final String DATA_MANAGER_CONTENT_TREE = "DATA_MANAGER_CONTENT_TREE";
        public static final String DATA_NAME = "dataName";
        public static final String SUBMISSION_UNIT = "submissionUnit";
        public static final String CONTACT = "contact";
        public static final String CONTACT_NUMBER = "contactNumber";
        public static final String SRC_FILE_NAME = "srcFileName";
        public static final String FILE_TYPE = "fileType";
    }

    public static class UserIdAndDataPermissionProperties {
        public static final String USERID_AND_DATA_PERMISSION_MAPPING = "USERID_AND_DATA_PERMISSION_MAPPING";
        public static final String USER_ID = "userId";
        public static final String DATA_PERMISSSION_ID = "dataPermissionId";
        public static final String DATA_PERMISSSION_NAME = "dataPermissionName";
    }

    public static class DataPermissionSchemaProperties {
        public static final String DATA_PERMISSION_SCHEMA = "DATA_PERMISSION_SCHEMA";
        public static final String DATA_PERMISSION_SCHEMA_TREE = "DATA_PERMISSION_SCHEMA_TREE";
        public static final String READ_PERMISSION = "readPermission";
        public static final String WRITE_PERMISSION = "writePermission";
        public static final String DELETE_PERMISSION = "deletePermission";
    }

    public static class UserAndDataPermissionSchemaMapping {
        public static final String USERID_AND_DATA_PERMISSION_MAPPING = "USERID_AND_DATA_PERMISSION_MAPPING";
        public static final String USER_ID = "userId";
        public static final String DATA_PERMISSION_ID = "dataPermissionId";
    }

    public static class BimFileTranslateStatus {
        public static final String BIM_FILE_TRANSLATE_STATUS = "BIM_FILE_TRANSLATE_STATUS";
        public static final String BUCKET = "bucket";
        public static final String FILE_NAME = "fileName";//NOSONAR
        public static final String SRC_FILE_NAME = "srcFileName";//NOSONAR
        public static final String STATUS = "status";
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;
    }

    /**
     * 通用属性名称
     */
    public static class GeneralProperties {
        public static final String ID = "ID";
        public static final String NAME = "NAME";
        public static final String COMMENT = CimConstants.COMMENT;
        public static final String CREATOR_ID = CimConstants.CREATOR;
        public static final String UPDATOR_ID = CimConstants.UPDATOR;
        public static final String CREATE_TIME = CimConstants.CREATE_TIME;
        public static final String UPDATE_TIME = CimConstants.UPDATE_TIME;

    }

    /**
     * orientdb element 保留关键字
     */
    public static class OrientDBReservedKeys {
        public static final String ID = "id";
        public static final String ID_ = "id_";
    }


    public static final String JOB_END_IDENTIFIER = DigestUtils.md5DigestAsHex(("www.glodon.com" + "/&%5123***&&%%$$" +
            "#@").getBytes());

}
