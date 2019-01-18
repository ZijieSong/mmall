package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.deser.std.DateDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtil {

    private static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date getDateFromStr(String str) {
        return getDateFromStr(str, DEFAULT_PATTERN);
    }

    public static String getStrFromDate(Date date) {
        return getStrFromDate(date, DEFAULT_PATTERN);
    }

    public static Date getDateFromStr(String str, String pattern) {
        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(pattern);
        return dateTimeFormat.parseDateTime(str).toDate();
    }

    public static String getStrFromDate(Date date, String pattern) {
        if (date == null)
            return StringUtils.EMPTY;
        return new DateTime(date).toString(pattern);
    }

    public static void main(String[] args) {
        System.out.println(getStrFromDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}
