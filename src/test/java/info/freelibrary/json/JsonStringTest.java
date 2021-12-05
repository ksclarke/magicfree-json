
package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.freelibrary.util.StringUtils;

public class JsonStringTest extends AbstractTestBase {

    private JsonWriter myWriter;

    @BeforeEach
    public void setUp() {
        myWriter = new JsonWriter(new StringWriter());
    }

    @Test
    public void constructor_failsWithNull() {
        final String output = assertThrows(NullPointerException.class, () -> new JsonString(null)).getMessage();
        Assertions.assertTrue(output.contains(getI18n(MessageCodes.JSON_003)));
    }

    @Test
    public void write() throws IOException {
        final String name = getNames().next();

        new JsonString(name).write(myWriter);
        assertEquals(StringUtils.format("\"{}\"", name), myWriter.toString());
    }

    @Test
    public void write_escapesStrings() throws IOException {
        new JsonString("foo\\bar").write(myWriter);
        assertEquals("\"foo\\\\bar\"", myWriter.toString());
    }

    @Test
    public void isString() {
        assertTrue(new JsonString(getNames().next()).isString());
    }

    @Test
    public void asString() {
        final String name = getNames().next();
        assertEquals(name, new JsonString(name).asString());
    }

    @Test
    public void equals_trueForSameInstance() {
        final JsonString string = new JsonString(getNames().next());
        assertTrue(string.equals(string));
    }

    @Test
    public void equals_trueForEqualStrings() {
        final String name = getNames().next();
        assertTrue(new JsonString(name).equals(new JsonString(name)));
    }

    @Test
    public void equals_falseForDifferentStrings() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertFalse(new JsonString(EMPTY).equals(new JsonString(name1)));
        assertFalse(new JsonString(name1).equals(new JsonString(name2)));
    }

    @Test
    public void equals_falseForNull() {
        assertFalse(new JsonString(getNames().next()).equals(null));
    }

    @Test
    public void hashCode_equalsForEqualStrings() {
        final String name = getNames().next();
        assertTrue(new JsonString(name).hashCode() == new JsonString(name).hashCode());
    }

    @Test
    public void hashCode_differsForDifferentStrings() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertFalse(new JsonString(EMPTY).hashCode() == new JsonString(name1).hashCode());
        assertFalse(new JsonString(name1).hashCode() == new JsonString(name2).hashCode());
    }

}
