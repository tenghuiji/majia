package com.majia.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class TimeUtilsTest {
    @Test
    public void toUtilDateFromStrDateByFormat() {
        TimeUtils timeUtils = new TimeUtils();
        Date date=new Date();
        try {
            date = timeUtils.toUtilDateFromStrDateByFormat("2020-2-1","yyyy-mm-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        calendar.set(2020, 1, 1);
        Date compareDate = calendar.getTime();
        assertEquals(compareDate.compareTo(date),true);
    }

    @Test
    public void toSqlDateFromStrDate() {
    }

    @Test
    public void toStrDateFromUtilDateByFormat() {
    }

    @Test
    public void toCalendarFromUtilDate() {
    }

    @Test
    public void toSqlDateFromUtilDate() {
    }

    @Test
    public void toSqlTimeFromUtilDate() {
    }

    @Test
    public void toSqlTimestampFromUtilDate() {
    }

    @Test
    public void toUtilDateFromSqlDate() {
    }

    @Test
    public void getYearOfDate() {
    }

    @Test
    public void getMonthOfDate() {
    }

    @Test
    public void getDayOfDate() {
    }

    @Test
    public void getHourOfDate() {
    }

    @Test
    public void getMinuteOfDate() {
    }

    @Test
    public void getSecondOfDate() {
    }

    @Test
    public void getMillisOfDate() {
    }

    @Test
    public void getNowOfDateByFormat() {
    }

    @Test
    public void getSystemOfDateByFormat() {
    }

    @Test
    public void getDayOfMonth() {
    }

    @Test
    public void getDateOfMonthBegin() {
    }

    @Test
    public void getDateOfMonthEnd() {
    }

    @Test
    public void convertByTimezone() {
    }

    @Test
    public void testConvertByTimezone() {
    }

    @Test
    public void testConvertByTimezone1() {
    }

    @Test
    public void testConvertByTimezone2() {
    }
}
