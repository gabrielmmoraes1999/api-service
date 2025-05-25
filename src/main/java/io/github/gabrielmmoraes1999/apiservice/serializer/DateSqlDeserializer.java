package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;

public class DateSqlDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText().trim();

        try {
            return new Date(ConfigSerializer.SDF_TIMESTAMP.parse(dateStr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Format invalid to Date: " + dateStr, e);
        }
    }
}
