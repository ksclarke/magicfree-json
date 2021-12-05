
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonNumberTest extends AbstractTestBase {

    private JsonWriter myWriter;

    @BeforeEach
    public void setUp() {
        myWriter = new JsonWriter(new StringWriter());
    }

    @Test
    public void constructor_failsWithNull() {
        final String output = assertThrows(NullPointerException.class, () -> new JsonNumber(null)).getMessage();
        Assertions.assertTrue(output.contains(getI18n(MessageCodes.JSON_003)));
    }

    @Test
    public void write() throws IOException {
        new JsonNumber("23").write(myWriter);
        assertEquals("23", myWriter.toString());
    }

    @Test
    public void toString_returnsInputString() {
        assertEquals("foo", new JsonNumber("foo").toString());
    }

    @Test
    public void isNumber() {
        assertTrue(new JsonNumber("23").isNumber());
    }

    @Test
    public void asInt() {
        assertEquals(23, new JsonNumber("23").asInt());
    }

    @Test
    public void asInt_failsWithExceedingValues() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("10000000000").asInt());
    }

    @Test
    public void asInt_failsWithExponent() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("1e5").asInt());
    }

    @Test
    public void asInt_failsWithFractional() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("23.5").asInt());
    }

    @Test
    public void asLong() {
        assertEquals(23l, new JsonNumber("23").asLong());
    }

    @Test
    public void asLong_failsWithExceedingValues() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("10000000000000000000").asLong());
    }

    @Test
    public void asLong_failsWithExponent() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("1e5").asLong());
    }

    @Test
    public void asLong_failsWithFractional() {
        assertThrows(NumberFormatException.class, () -> new JsonNumber("23.5").asLong());
    }

    @Test
    public void asFloat() {
        assertEquals(23.05f, new JsonNumber("23.05").asFloat(), 0.0001f);
    }

    @Test
    public void asFloat_returnsInfinityForExceedingValues() {
        assertEquals(Float.POSITIVE_INFINITY, new JsonNumber("1e50").asFloat(), 0.00000000001f);
        assertEquals(Float.NEGATIVE_INFINITY, new JsonNumber("-1e50").asFloat(), 0.00000000001f);
    }

    @Test
    public void asDouble() {
        assertEquals(23.05, new JsonNumber("23.05").asDouble(), 0.00000000001d);
    }

    @Test
    public void asDouble_returnsInfinityForExceedingValues() {
        assertEquals(Double.POSITIVE_INFINITY, new JsonNumber("1e500").asDouble(), 0.00000000001d);
        assertEquals(Double.NEGATIVE_INFINITY, new JsonNumber("-1e500").asDouble(), 0.00000000001d);
    }

    @Test
    public void equals_trueForSameInstance() {
        final JsonNumber number = new JsonNumber("23");
        assertTrue(number.equals(number));
    }

    @Test
    public void equals_trueForEqualNumberStrings() {
        assertTrue(new JsonNumber("23").equals(new JsonNumber("23")));
    }

    @Test
    public void equals_falseForDifferentNumberStrings() {
        assertFalse(new JsonNumber("23").equals(new JsonNumber("42")));
        assertFalse(new JsonNumber("1e+5").equals(new JsonNumber("1e5")));
    }

    @Test
    public void equals_falseForNull() {
        assertFalse(new JsonNumber("23").equals(null));
    }

    @Test
    public void hashCode_equalsForEqualStrings() {
        assertTrue(new JsonNumber("23").hashCode() == new JsonNumber("23").hashCode());
    }

    @Test
    public void hashCode_differsForDifferentStrings() {
        assertFalse(new JsonNumber("23").hashCode() == new JsonNumber("42").hashCode());
    }

}
