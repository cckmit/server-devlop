package com.glodon.pcop.cimsvc.util;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void getCurrentDateReadable() {
    }

    @Test
    public void getCurrentDateMs() {
    }

    @Test
    public void getCurrentDate() {
        DateTime currentDateTime = DateTime.now();
        Date date = new Date();

        System.out.println("joda date time: " + currentDateTime.getMillis());

        System.out.println("java date time: " + date.getTime());
    }


}