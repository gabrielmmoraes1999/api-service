package io.github.gabrielmmoraes1999.apiservice.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONObjectNumberLimitTest {

    @Test
    void limitIsAFewHundredCharacters() {
        assertEquals(512, JSONObject.MAX_NUMBER_LENGTH);
    }

    @Test
    void normalNumbersStillParse() {
        JSONObject object = new JSONObject("{\"i\":42,\"d\":3.14,\"n\":-7,\"e\":1.5e2}");

        assertEquals(42, object.getInt("i"));
        assertEquals(3.14d, object.getDouble("d"), 0.000001d);
        assertEquals(-7, object.getInt("n"));
        assertEquals(150d, object.getDouble("e"), 0.000001d);
    }

    @Test
    void integerAtTheLimitStillParses() {
        String digits = "1".repeat(JSONObject.MAX_NUMBER_LENGTH);

        JSONObject object = new JSONObject("{\"n\":" + digits + "}");

        assertEquals(new BigInteger(digits), object.getBigInteger("n"));
    }

    @Test
    void oversizedIntegerIsRejected() {
        String digits = "1".repeat(JSONObject.MAX_NUMBER_LENGTH + 1);

        assertThrows(JSONException.class, () -> new JSONObject("{\"n\":" + digits + "}"));
    }

    @Test
    void decimalAtTheLimitStillParses() {
        String decimal = "1." + "2".repeat(JSONObject.MAX_NUMBER_LENGTH - 2);

        JSONObject object = new JSONObject("{\"n\":" + decimal + "}");

        assertEquals(new BigDecimal(decimal), object.getBigDecimal("n"));
    }

    @Test
    void oversizedDecimalIsRejected() {
        String decimal = "1." + "2".repeat(JSONObject.MAX_NUMBER_LENGTH);

        assertThrows(JSONException.class, () -> new JSONObject("{\"n\":" + decimal + "}"));
    }

}
