package io.github.gabrielmmoraes1999.apiservice.serializer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class ConfigSerializer {

    protected static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    protected static SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    protected static DateTimeFormatter DTF_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected static TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static void init() {
        SDF_TIMESTAMP.setTimeZone(TIME_ZONE);
    }
}
