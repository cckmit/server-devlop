package com.glodon.pcop.cimsvc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtil {
    private static Logger log = LoggerFactory.getLogger(SqlUtil.class);


    public static boolean isContainSensiveKeyWords(String rawCondistion) {
        boolean flag = false;

        String[] tokenList = rawCondistion.toUpperCase().split("\\s");
        for (int i = 0; i < tokenList.length; i++) {
            if (tokenList[i].equals("DELETE") || tokenList[i].equals("UPDATE")) {
                flag = true;
                break;
            }
        }

        return flag;
    }

}
