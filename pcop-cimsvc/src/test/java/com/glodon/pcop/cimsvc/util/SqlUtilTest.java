package com.glodon.pcop.cimsvc.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SqlUtilTest {

    @Test
    public void isContainSensiveKeyWords() {
        List<String> stringList = Arrays.asList("delete vertex from v", "udpate vertex from v set id = 0",
                "deleteTime=\"123\"");

        for (String str : stringList) {
            System.out.println("str=[ " + str + " ], flag=[ " + SqlUtil.isContainSensiveKeyWords(str) + " ]");
        }
    }
}