package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Date;

public class DateSqlSerializer extends StdSerializer<Date> {

    public DateSqlSerializer() {
        super(Date.class);
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(ConfigSerializer.SDF_DATE.format(date));
    }
}