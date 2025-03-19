
package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link JsonWriter}.
 */
public class JsonWriterTest extends AbstractTestBase {

    /** A writer to test. */
    private JsonWriter myWriter;

    @Test
    public void escapesControlCharacters() throws IOException {
        myWriter.writeString(string((char) 1, (char) 8, (char) 15, (char) 16, (char) 31));
        assertEquals("\"\\u0001\\u0008\\u000f\\u0010\\u001f\"", myWriter.toString());
    }

    @Test
    public void escapesEscapeCharacter() throws IOException {
        myWriter.writeString(string('f', 'o', 'o', (char) 27, 'b', 'a', 'r'));
        assertEquals("\"foo\\u001bbar\"", myWriter.toString());
    }

    @Test
    public void escapesEscapedQuotes() throws IOException {
        myWriter.writeString("foo\\\"bar");
        assertEquals("\"foo\\\\\\\"bar\"", myWriter.toString());
    }

    @Test
    public void escapesFirstChar() throws IOException {
        myWriter.writeString(string('\\', 'x'));
        assertEquals("\"\\\\x\"", myWriter.toString());
    }

    @Test
    public void escapesLastChar() throws IOException {
        myWriter.writeString(string('x', '\\'));
        assertEquals("\"x\\\\\"", myWriter.toString());
    }

    @Test
    public void escapesNewLine() throws IOException {
        myWriter.writeString("foo\nbar");
        assertEquals("\"foo\\nbar\"", myWriter.toString());
    }

    @Test
    public void escapesQuotes() throws IOException {
        myWriter.writeString("a\"b");
        assertEquals("\"a\\\"b\"", myWriter.toString());
    }

    @Test
    public void escapesSpecialCharacters() throws IOException {
        myWriter.writeString("foo\u2028bar\u2029");
        assertEquals("\"foo\\u2028bar\\u2029\"", myWriter.toString());
    }

    @Test
    public void escapesTabs() throws IOException {
        myWriter.writeString("foo\tbar");
        assertEquals("\"foo\\tbar\"", myWriter.toString());
    }

    @Test
    public void escapesWindowsNewLine() throws IOException {
        myWriter.writeString("foo\r\nbar");
        assertEquals("\"foo\\r\\nbar\"", myWriter.toString());
    }

    @Test
    public void escapesZeroCharacter() throws IOException {
        myWriter.writeString(string('f', 'o', 'o', (char) 0, 'b', 'a', 'r'));
        assertEquals("\"foo\\u0000bar\"", myWriter.toString());
    }

    @BeforeEach
    public void setUp() {
        myWriter = new JsonWriter(new StringWriter());
    }

    @Test
    public void writeArrayParts() throws IOException {
        myWriter.writeArrayOpen(0);
        myWriter.writeArraySeparator();
        myWriter.writeArrayClose(0);

        assertEquals("[,]", myWriter.toString());
    }

    @Test
    public void writeLiteral() throws IOException {
        final String name = getNames().next();
        myWriter.writeLiteral(name);
        assertEquals(name, myWriter.toString());
    }

    @Test
    public void writeMemberName_empty() throws IOException {
        myWriter.writeMemberName(EMPTY);
        assertEquals("\"\"", myWriter.toString());
    }

    @Test
    public void writeMemberName_escapesBackslashes() throws IOException {
        myWriter.writeMemberName("foo\\bar");
        assertEquals("\"foo\\\\bar\"", myWriter.toString());
    }

    @Test
    public void writeNumber() throws IOException {
        myWriter.writeNumber("23");
        assertEquals("23", myWriter.toString());
    }

    @Test
    public void writeObjectParts() throws IOException {
        myWriter.writeObjectOpen();
        myWriter.writeMemberSeparator();
        myWriter.writeObjectSeparator();
        myWriter.writeObjectClose();

        assertEquals("{:,}", myWriter.toString());
    }

    @Test
    public void writeSting_escapesBackslashes() throws IOException {
        myWriter.writeString("foo\\bar");
        assertEquals("\"foo\\\\bar\"", myWriter.toString());
    }

    @Test
    public void writeString_empty() throws IOException {
        myWriter.writeString(EMPTY);
        assertEquals("\"\"", myWriter.toString());
    }

    private static String string(final char... aCharsArray) {
        return String.valueOf(aCharsArray);
    }

}
