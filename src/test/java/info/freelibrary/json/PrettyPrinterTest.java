
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

/**
 * Tests of the {@link PrettyWriter} JSON writer.
 */
public class PrettyPrinterTest extends AbstractTestBase {

    @Test
    public void testIndentWithSpaces_array() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter());
        new JsonArray().add(23).add(42).writeTo(output);
        assertEquals("[\n  23,\n  42\n]", output.toString());
    }

    @Test
    public void testIndentWithSpaces_emptyArray() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter());
        new JsonArray().writeTo(output);
        assertEquals("[  ]", output.toString());
    }

    @Test
    public void testIndentWithSpaces_emptyObject() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter());
        new JsonObject().writeTo(output);
        assertEquals("{\n  \n}", output.toString());
    }

    @Test
    public void testIndentWithSpaces_nestedArray() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter());
        new JsonArray().add(23).add(new JsonArray().add(42)).writeTo(output);
        assertEquals("[\n  23,\n  [ 42 ]\n]", output.toString());
    }

    @Test
    public void testIndentWithSpaces_nestedObject() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), ' ', 2);
        new JsonObject().add("a", 23).add("b", new JsonObject().add("c", 42)).writeTo(output);
        assertEquals("{\n  \"a\" : 23,\n  \"b\" : {\n    \"c\" : 42\n  }\n}", output.toString());
    }

    @Test
    public void testIndentWithSpaces_object() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), new char[] { ' ', ' ' });
        new JsonObject().add("a", 23).add("b", 42).writeTo(output);
        assertEquals("{\n  \"a\" : 23,\n  \"b\" : 42\n}", output.toString());
    }

    @Test
    public void testIndentWithSpaces_one() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), new char[] { ' ' });
        new JsonArray().add(23).add(42).writeTo(output);
        assertEquals("[\n 23,\n 42\n]", output.toString());
    }

    @Test
    public void testIndentWithTabs() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), '\t');
        new JsonArray().add(23).add(42).writeTo(output);
        assertEquals("[\n\t23,\n\t42\n]", output.toString());
    }

    @Test
    public void testSingleLine_nestedArray() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), new char[] {});
        new JsonArray().add(23).add(new JsonArray().add(42)).writeTo(output);
        assertEquals("[23, [ 42 ]]", output.toString());
    }

    @Test
    public void testSingleLine_nestedObject() throws IOException {
        final PrettyWriter output = new PrettyWriter(new StringWriter(), new char[] {});
        new JsonObject().add("a", 23).add("b", new JsonObject().add("c", 42)).writeTo(output);
        assertEquals("{\"a\" : 23, \"b\" : {\"c\" : 42}}", output.toString());
    }
}
