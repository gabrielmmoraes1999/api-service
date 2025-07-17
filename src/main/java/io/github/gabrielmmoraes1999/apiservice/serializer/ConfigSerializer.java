package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.TimeZone;

public class ConfigSerializer {

    protected static ZoneOffset ZONE_OFF_SET = ZoneOffset.of("Z");
    protected static SimpleModule SIMPLE_MODULE = new SimpleModule();
    protected static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    protected static DateTimeFormatter DTF_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    protected static DateTimeFormatter DTF_TIMESTAMP = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 3, 9, true)
            .optionalEnd()
            .appendPattern("XXX")
            .toFormatter();

    public static void init() {
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

    public static void setTimeZone(TimeZone zone) {
        ZONE_OFF_SET = ZonedDateTime.now(zone.toZoneId()).getOffset();
    }

}
