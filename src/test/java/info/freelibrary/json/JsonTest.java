
package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests the Json class.
 */
public class JsonTest extends AbstractTestBase {

    /**
     * Log output for a given test.
     */
    private String myLog;

    /**
     * Tests the literal constants defined in Json.
     */
    @Test
    public void literalConstants() {
        assertTrue(Json.NULL.isNull());
        assertTrue(Json.TRUE.isTrue());
        assertTrue(Json.FALSE.isFalse());
    }

    @Test
    public void value_int() {
        assertEquals("0", Json.value(0).toString());
        assertEquals("23", Json.value(23).toString());
        assertEquals("-1", Json.value(-1).toString());
        assertEquals("2147483647", Json.value(Integer.MAX_VALUE).toString());
        assertEquals("-2147483648", Json.value(Integer.MIN_VALUE).toString());
    }

    @Test
    public void value_long() {
        assertEquals("0", Json.value(0l).toString());
        assertEquals("9223372036854775807", Json.value(Long.MAX_VALUE).toString());
        assertEquals("-9223372036854775808", Json.value(Long.MIN_VALUE).toString());
    }

    @Test
    public void value_float() {
        assertEquals("23.5", Json.value(23.5f).toString());
        assertEquals("-3.1416", Json.value(-3.1416f).toString());
        assertEquals("1.23E-6", Json.value(0.00000123f).toString());
        assertEquals("-1.23E7", Json.value(-12300000f).toString());
    }

    @Test
    public void value_float_cutsOffPointZero() {
        assertEquals("0", Json.value(0f).toString());
        assertEquals("-1", Json.value(-1f).toString());
        assertEquals("10", Json.value(10f).toString());
    }

    @Test
    public void value_float_failsWithInfinity() {
        myLog = assertThrows(IllegalArgumentException.class, () -> Json.value(Float.POSITIVE_INFINITY)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_001)));
    }

    @Test
    public void value_float_failsWithNaN() {
        myLog = assertThrows(IllegalArgumentException.class, () -> Json.value(Float.NaN)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_001)));
    }

    @Test
    public void value_double() {
        assertEquals("23.5", Json.value(23.5d).toString());
        assertEquals("3.1416", Json.value(3.1416d).toString());
        assertEquals("1.23E-6", Json.value(0.00000123d).toString());
        assertEquals("1.7976931348623157E308", Json.value(1.7976931348623157E308d).toString());
    }

    @Test
    public void value_double_cutsOffPointZero() {
        assertEquals("0", Json.value(0d).toString());
        assertEquals("-1", Json.value(-1d).toString());
        assertEquals("10", Json.value(10d).toString());
    }

    @Test
    public void value_double_failsWithInfinity() {
        myLog = assertThrows(IllegalArgumentException.class, () -> Json.value(Double.POSITIVE_INFINITY)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_001)));
    }

    @Test
    public void value_double_failsWithNaN() {
        myLog = assertThrows(IllegalArgumentException.class, () -> Json.value(Double.NaN)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_001)));
    }

    @Test
    public void value_boolean() {
        assertSame(Json.TRUE, Json.value(true));
        assertSame(Json.FALSE, Json.value(false));
    }

    @Test
    public void value_string() {
        assertEquals(EMPTY, Json.value(EMPTY).asString());
        assertEquals("Hello", Json.value("Hello").asString());
        assertEquals("\"Hello\"", Json.value("\"Hello\"").asString());
    }

    @Test
    public void value_string_toleratesNull() {
        assertSame(Json.NULL, Json.value(null));
    }

    @Test
    public void array() {
        assertEquals(new JsonArray(), Json.array());
    }

    @Test
    public void array_int() {
        assertEquals(new JsonArray().add(23), Json.array(23));
        assertEquals(new JsonArray().add(23).add(42), Json.array(23, 42));
    }

    @Test
    public void array_int_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((int[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void array_long() {
        assertEquals(new JsonArray().add(23l), Json.array(23l));
        assertEquals(new JsonArray().add(23l).add(42l), Json.array(23l, 42l));
    }

    @Test
    public void array_long_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((long[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void array_float() {
        assertEquals(new JsonArray().add(3.14f), Json.array(3.14f));
        assertEquals(new JsonArray().add(3.14f).add(1.41f), Json.array(3.14f, 1.41f));
    }

    @Test
    public void array_float_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((float[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void array_double() {
        assertEquals(new JsonArray().add(3.14d), Json.array(3.14d));
        assertEquals(new JsonArray().add(3.14d).add(1.41d), Json.array(3.14d, 1.41d));
    }

    @Test
    public void array_double_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((double[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void array_boolean() {
        assertEquals(new JsonArray().add(true), Json.array(true));
        assertEquals(new JsonArray().add(true).add(false), Json.array(true, false));
    }

    @Test
    public void array_boolean_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((boolean[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void array_string() {
        assertEquals(new JsonArray().add("foo"), Json.array("foo"));
        assertEquals(new JsonArray().add("foo").add("bar"), Json.array("foo", "bar"));
    }

    @Test
    public void array_string_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.array((String[]) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_002)));
    }

    @Test
    public void object() {
        assertEquals(new JsonObject(), Json.object());
    }

    @Test
    public void parse_string() {
        assertEquals(Json.value(24), Json.parse("24"));
    }

    @Test
    public void parse_string_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.parse((String) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_003)));
    }

    @Test
    public void parse_reader() throws IOException {
        assertEquals(Json.value(23), Json.parse("23"));
    }

    @Test
    public void parse_file_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> Json.parse(new JsonReader((File) null))).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_004)));
    }

}
