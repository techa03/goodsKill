package com.goodskill.web.util;

import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class String2DateUtilTest {

    @Test
    public void testConvertWithValidDateString() {
        String2DateUtil converter = new String2DateUtil();
        String dateString = "2020-03-11";
        Date expectedDate = getDate(2020, 3, 11);

        Date actualDate = converter.convert(dateString);

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testConvertWithValidDateTimeString() {
        String2DateUtil converter = new String2DateUtil();
        String dateTimeString = "2020-03-11 12:30:45";
        Date expectedDateTime = getDateTime(2020, 3, 11, 12, 30, 45);

        Date actualDateTime = converter.convert(dateTimeString);

        assertEquals(expectedDateTime, actualDateTime);
    }

//    @Test
//    public void testConvertWithInvalidDateString() {
//        String2DateUtil converter = new String2DateUtil();
//        String invalidDateString = "2020-13-11";
//
//        Date actualDate = converter.convert(invalidDateString);
//
//        assertNull(actualDate);
//    }
//
//    @Test
//    public void testConvertWithInvalidDateTimeString() {
//        String2DateUtil converter = new String2DateUtil();
//        String invalidDateTimeString = "2020-03-11 25:30:45";
//
//        Date actualDateTime = converter.convert(invalidDateTimeString);
//
//        assertNull(actualDateTime);
//    }

    private Date getDate(int year, int month, int day) {
        // Create a Date object with the specified year, month, and day
        return DateUtil.parse(year + "-" + month + "-" + day);
    }

    private Date getDateTime(int year, int month, int day, int hour, int minute, int second) {
        // Create a Date object with the specified year, month, day, hour, minute, and second
        return DateUtil.parse(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
    }
}
