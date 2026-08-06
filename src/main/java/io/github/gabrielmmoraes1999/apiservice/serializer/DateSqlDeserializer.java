package io.github.gabrielmmoraes1999.apiservice.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateSqlDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText().trim();

        try {
            return Date.valueOf(LocalDate.parse(dateStr, ConfigSerializer.DTF_DATE));
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Format invalid to Date: " + dateStr, e);
        }
    }
}
