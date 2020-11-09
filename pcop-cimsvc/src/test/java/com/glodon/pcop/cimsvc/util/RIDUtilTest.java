package com.glodon.pcop.cimsvc.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RIDUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void isAvailableOrientdbRid() {
        String[] sts = {"#1:1", "#:1", "#1:", "#a:1", "#1:a", "1#1:2", "#1:2a"};
        for (String st : sts) {
            System.out.println(st + "=" + RIDUtil.isAvailableOrientdbRid(st));
        }
    }

    @Test
    public void ridToString() {
    }
}