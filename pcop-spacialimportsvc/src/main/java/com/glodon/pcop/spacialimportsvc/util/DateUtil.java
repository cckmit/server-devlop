package com.glodon.pcop.spacialimportsvc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 获取当前日期：yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentDateReadable() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date());
    }

    /**
     * 获取当前日期：yyyyMMddHHmmssSSS
     *
     * @return
     */
    public static String getCurrentDateMs() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return df.format(new Date());
    }

    /**
     * 获取当前日期：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }


}
