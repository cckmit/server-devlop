package com.glodon.pcop.cimstatsvc.constant;

//定义常量
public class ObjectTypeIdConstant {
    //项目
    public static final String PROJECT = "projectV1"; //项目

    //分包商与分包队伍
    public static final String SUB_CONSTRATOR = "subConstrator";//分包商
    public static final String SUB_CONSTRATOR_TEAM = "subConstratorTeam";  //fenbao
    //质量
    public static final String QUALITY_PROBLEM_TYPE = "qualityProblemType"; //	质量问题库
    public static final String QUALITY_PROBLEM = "qualityProblem"; //质量检查问题
    public static final String PROBLEM_RECTIFICATION = "problemRectification"; //质量检查问题-整改（详情）
    public static final String PROBLEM_REVIEW = "problemReview"; //质量检查问题-复查（详情）
    public static final String QUALITY_PROBLEM_SUM = "qualityProblemSum"; //	质量问题数量
    public static final String QUALITY_PROBLEM_BY_DUTYPERSON = "qualityProblemByDutyperson"; //质量问题按责任人（整改人）分析
    public static final String QUALITY_PROBLEM_BY_SUBCONSTRATOR = "qualityProblemBySubconstrator"; //	质量问题按分包商（责任单位-队伍）分析
    public static final String QUALITY_PROBLEM_TREND_BY_TYPE = "qualityProblemTrendByType"; //质量问题按类型分析
    public static final String QUALITY_PROBLEM_TREND = "qualityProblemTrend"; //质量问题趋势分析

    //安全
    public static final String SAFETY_PROBLEM_TYPE = "safetyProblemType"; //安全问题库
    public static final String SAFETY_PROULEM = "safetyProblem"; //安全检查问题
    public static final String SAFETY_PROBLEM_RECTIFICATION = "safetyProblemRectification"; //安全检查问题-整改（详情）
    public static final String SAFETY_PROBLEM_REVIEW = "safetyProblemReview"; //安全检查问题-复查（详情）
    public static final String SAFETY_PROBLEM_SUM = "safetyProblemSum"; //	安全隐患总数
    public static final String SAFETY_PROBLEM_BY_DUTYPERSON = "safetyProblemByDutyperson"; //安全隐患按责任人（整改人）分析
    public static final String SAFETY_PROBLEM_BY_SUBCONSTRATOR = "safetyProblemBySubconstrator"; //安全隐患按分包商（责任单位-队伍）分析
    public static final String SAFETY_PROBLEM_TREND_BY_TYPE = "safetyProblemTrendByType"; //安全隐患按类型分析
    public static final String SAFETY_PROBLEM_TREND = "safetyProblemTrend"; //	安全隐患趋势分析

    //人员-劳务
    public static final String LABOR_OVER_ALL = "laborRealtimeAnalyse";  //劳务在场和实时人数
    public static final String LABOR_REALTIME_ANALYSE_WORK_TEAM = "laborRealtimeAnalyseWorkTeam"; //劳务实时在场汇总按队伍
    public static final String LABOR_REALTIME_ANALYSE_WORK_TYPE = "laborRealtimeAnalyseWorkType"; //劳务实时在场汇总按工种
    public static final String LABOR_COUNT_BY_MONTH = "laborCountByMonth"; //劳务人员按月统计
    public static final String LABOR_INOUT_SUMMARY = "laborApproachexitSummary"; //劳务进退场摘要
    public static final String LABOR_PLAN_SUMMARY = "laborPlanSummary"; //劳务需求计划
    public static final String LABOR_PLAN_DETAIL = "laborPlanDetail"; //劳务需求计划详情
    public static final String LABOR_ATTEND_SUMMARY = "LaborAttendSummary";//劳务人员考勤概述
    public static final String LABOR_ATTEND_WORKTYPE = "laborAttendWorkType"; //	劳务人员工种考勤详情
    //人员-管理
    public static final String ADMIN_ATTEND_SUMMARY = "adminAttendSummary"; //	考勤概要(管理人员)
    public static final String ADMIN_ATTEND_DETAIL = "adminAttendDetail"; //	考勤详情(管理人员)
    public static final String MANAGER_ATTEND_SUMMARY_COUNT_BY_MONTH = "managerAttendSummaryCountByMonth";//管理人员考勤概要月统计
    public static final String MANAGER_ATTEND_DETAIL_COUNT_BY_MONTH = "managerAttendDetailCountByMonth";//管理人员考勤详细月统计

    public static final String DATA_STATISTICS = "DATA_STATISTICS";
    public static final String BASE_FILE_METADATA_INFO = "BASE_FILE_METADATA_INFO";
    //sys
    public static final String FETCH_CFG = "projectDataFetchCfg";


}

