package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText().trim();

        if (dateStr.endsWith("Z")) {
            dateStr = dateStr.replace("Z", "+0000");
        }

        try {
            return new Timestamp(ConfigSerializer.SDF_TIMESTAMP.parse(dateStr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Format invalid to Timestamp: " + dateStr, e);
        }
    }

}
