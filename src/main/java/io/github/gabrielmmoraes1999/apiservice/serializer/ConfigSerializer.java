package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class ConfigSerializer {

    protected static SimpleModule SIMPLE_MODULE = new SimpleModule();
    protected static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    protected static SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    protected static DateTimeFormatter DTF_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected static TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static void init() {
        SDF_TIMESTAMP.setTimeZone(TIME_ZONE);

        SIMPLE_MODULE.addSerializer(java.sql.Date.class, new DateSqlSerializer());
        SIMPLE_MODULE.addSerializer(java.time.LocalDateTime.class, new LocalDateTimeSerializer());
        SIMPLE_MODULE.addSerializer(java.sql.Timestamp.class, new TimestampSerializer());

        SIMPLE_MODULE.addDeserializer(java.sql.Date.class, new DateSqlDeserializer());
        SIMPLE_MODULE.addDeserializer(java.time.LocalDateTime.class, new LocalDateTimeDeserializer());
        SIMPLE_MODULE.addDeserializer(java.sql.Timestamp.class, new TimestampDeserializer());
    }

    public static SimpleModule getSimpleModule() {
        return ConfigSerializer.SIMPLE_MODULE;
    }

}
