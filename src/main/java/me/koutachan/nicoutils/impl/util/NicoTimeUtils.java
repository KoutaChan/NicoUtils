package me.koutachan.nicoutils.impl.util;

import org.glassfish.grizzly.http.util.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NicoTimeUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX");

    public static Date toDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new IllegalStateException();
        }
    }

    public static String toString(Date date) {
        return dateFormat.format(date);
    }
}
