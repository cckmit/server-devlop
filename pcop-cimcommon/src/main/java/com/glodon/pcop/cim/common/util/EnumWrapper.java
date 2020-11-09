package com.glodon.pcop.cim.common.util;

/**
 * @author yuanjk(yuanjk @ glodon.com), 2018-07-16 17:47:30
 */
public class EnumWrapper {

    /**
     * @author yuanjk(yuanjk @ glodon.com), 2018-07-18 08:42:01 请求返回码
     */
    public static enum CodeAndMsg {

        E05000200("Success"),
        E05010500("Cim service error!"),
        E05010501("CimDataEngineRuntimeException"),
        E05010502("CimDataEngineInfoExploreException"),
        E05010503("DataServiceModelRuntimeException"),
        E05010504("CimDataEngineDataMartException"),
        E05010505("unknown error"),
        E05010506("cim data engine error"),
        E05010002("Info discovery engine exception."),
        E05020001("Create Minio Client failed"),
        E05040402("Entity delete failed"),
        E05040403("Entity update failed"),
        E05040404("Entity not found"),
        E05040405("Entity not belongs to tenant"),
        E05040406("Entity add filed"),
        E05040001("Query condition parse failed"),
        E05040002("No matching property type found"),
        E05040003("行业分类未找到"),
        E05040004("Property type is not defined"),
        E05040005("Property Mapping error, ID must be mapped."),
        E05040006("input is not enough"),
        E05040007("input error"),
        E05040008("object type is already exists"),
        E05040009("data space is already exists"),
        E05050001("gis server error"),
        E05050002("douplicate names"),
        E05060001("shp文件解析失败"),
        E05060002("Minio file not found"),
        E05060003("Cannot get pre signed url"),
        E05060004("minio internal error"),
        E05070001("Data Engine Error"),
        E05080001("bim face response error"),
        E05090001("tree node add filed"),
        E05080002("bim face processing"),
        E05080003("bim face response error"),
        E05080004("signature not true"),
        E14000200("job service success"),
        E17020500("special import service error"),
        E17000200("Success"),
        E17060001("Get shp file construct error");


        private String msg;

        private CodeAndMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * @author yuanjk(yuanjk @ glodon.com), 2018-07-18 08:41:26 MySQL序列名称
     */
    public static enum SequenceName {

        DATASETID("datasetid"),
        INDUSTRYID("industryid"),
        OBJECTTYPEID("objecttypeid"),
        TABLENAMEID("tablenameid");

        private String seqName;

        private SequenceName(String seqName) {
            this.seqName = seqName;
        }

        public String getSeqName() {
            return seqName;
        }
    }


    /**
     * 导入文件类型
     */
    public enum IMPORT_FILE_TYPE {
        SHP, XLS, RVT, OBJ
    }

    /**
     * 属性集结构
     */
    public enum DATA_SET_STRUCTURE {
        SINGLE, COLLECTION
    }

    /**
     * 属性集类型
     */
    public enum DATA_SET_TYPE {
        OBJECT, INSTANCE
    }

    /**
     * 属性集数据类型
     */
    public enum DATA_SET_DATA_TYPE {
        SHP, OBJ, 通用属性集 //NOSONAR
    }


}
