package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;

public class TimestampSerializer extends StdSerializer<Timestamp> {

    public TimestampSerializer() {
        super(Timestamp.class);
    }

    @Override
    public void serialize(Timestamp timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(
                timestamp.toInstant()
                .atOffset(ConfigSerializer.ZONE_OFF_SET)
                .format(ConfigSerializer.DTF_TIMESTAMP)
        );
    }

}
