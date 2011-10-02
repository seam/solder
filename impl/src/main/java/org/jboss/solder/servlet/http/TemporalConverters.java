package org.jboss.solder.servlet.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TemporalConverters {
    public static final String[] DATE_TIME_PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};

    public static Date parseDate(String source) {
        for (String pattern : DATE_TIME_PATTERNS) {
            try {
                return new SimpleDateFormat(pattern).parse(source);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public static Calendar parseCalendar(String source) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseDate(source));
        return calendar;
    }
}
