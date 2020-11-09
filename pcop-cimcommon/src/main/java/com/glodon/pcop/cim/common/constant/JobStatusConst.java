package com.glodon.pcop.cim.common.constant;

public class JobStatusConst {
//    public static final String PCOP_ANALYTIC_STORAGE = "pcopAnalyticStorage";
    public static final String BIM_MODEL_TRANSLATE = "BIMModelTranslate";
    public static final String PCOP_ANALYTIC_STORAGE_JOBNAME_PREFIX = "FileDataImport";
    public static final String PCOP_ANALYTIC_STORAGE_ID = "ID";
    public static final String PCOP_ANALYTIC_STORAGE_FILENAME = "FILENAME";
    public static final String PCOP_ANALYTIC_STORAGE_JOBTYPE = "JOBTYPE";

    public enum PcopAnalyticStorageJobTypeEnum {
        MODEL_TRANSLATE, //模型转换
        PARSE_IMPORT, //解析入库
        MODEL_PUBLISH;//模型发布
    }

}
