package com.glodon.pcop.cimsvc.util;

import java.util.regex.Pattern;

public class RIDUtil {

    public static final String ORIENTDB_RID_STR = "#\\d+:\\d+";

    public static boolean isAvailableOrientdbRid(String rid) {
        return Pattern.matches(ORIENTDB_RID_STR, rid);
    }

    public static String ridToString(String rid) {
        return rid.replace("#", "C").replace(":", "P");
    }
}
