
package info.freelibrary.json;

import static info.freelibrary.json.Json.parse;
import static info.freelibrary.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

public class JsonParserTest extends AbstractTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParserTest.class, MessageCodes.BUNDLE);

    private static final boolean LATEST_LTS_JDK = System.getProperty("java.specification.version").compareTo("17") >= 0;

    private TestHandler myHandler;

    private JsonParser myParser;

    private String myLog;

    @BeforeEach
    public void setUp() {
        myHandler = new TestHandler();
        myParser = new JsonParser(myHandler);
    }

    @Test
    @Disabled
    public void snapshot_reset() throws IOException {
        myHandler = new SnapshotHandler();
        myParser = new JsonParser(myHandler);

        myParser.parse(new JsonReader(new File("src/test/resources/sample-1.json")));
    }

    @Test
    public void constructor_rejectsNullHandler() {
        assertThrows(NullPointerException.class, () -> new JsonParser((JsonHandler<?, ?>) null));
    }

    @Test
    public void parse_string_rejectsNull() {
        assertThrows(NullPointerException.class, () -> myParser.parse((String) null));
    }

    @Test
    public void parse_reader_rejectsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> myParser.parse(new JsonReader((File) null)));
    }

    @Test
    public void parse_reader_rejectsNegativeBufferSize() throws IOException {
        final JsonReader reader = new JsonReader(new File("src/test/resources/sample-1.json"));
        assertThrows(IllegalArgumentException.class, () -> myParser.parse(reader, -1));
    }

    @Test
    public void parse_string_rejectsEmpty() {
        assertParseException(0, "Unexpected end of input", EMPTY);
    }

    @Test
    public void parse_reader_rejectsEmpty() {
        final ParseException details = assertThrows(ParseException.class, () -> myParser.parse(EMPTY));

        assertEquals(0, details.getLocation().getOffset());
        assertTrue(details.getMessage().startsWith(getI18n(MessageCodes.JSON_008)));
    }

    @Test
    public void parse_null() {
        myParser.parse("null");
        assertEquals(join("startNull 0", "endNull 4"), myHandler.getLog());
    }

    @Test
    public void parse_true() {
        myParser.parse("true");
        assertEquals(join("startBoolean 0", "endBoolean true 4"), myHandler.getLog());
    }

    @Test
    public void parse_false() {
        myParser.parse("false");
        assertEquals(join("startBoolean 0", "endBoolean false 5"), myHandler.getLog());
    }

    @Test
    public void parse_string() {
        myParser.parse("\"foo\"");
        assertEquals(join("startString 0", "endString foo 5"), myHandler.getLog());
    }

    @Test
    public void parse_string_empty() {
        myParser.parse("\"\"");
        assertEquals(join("startString 0", "endString  2"), myHandler.getLog());
    }

    @Test
    public void parse_number() {
        myParser.parse("23");
        assertEquals(join("startNumber 0", "endNumber 23 2"), myHandler.getLog());
    }

    @Test
    public void parse_number_negative() {
        myParser.parse("-23");
        assertEquals(join("startNumber 0", "endNumber -23 3"), myHandler.getLog());
    }

    @Test
    public void parse_number_negative_exponent() {
        myParser.parse("-2.3e-12");
        assertEquals(join("startNumber 0", "endNumber -2.3e-12 8"), myHandler.getLog());
    }

    @Test
    public void parse_array() {
        myParser.parse("[23]");
        assertEquals(join("startArray 0", "startArrayValue a1 1", "startNumber 1", "endNumber 23 3",
                "endArrayValue a1 3", "endArray a1 4"), myHandler.getLog());
    }

    @Test
    public void parse_array_empty() {
        myParser.parse("[]");
        assertEquals(join("startArray 0", "endArray a1 2"), myHandler.getLog());
    }

    @Test
    public void parse_object() {
        myParser.parse("{\"foo\": 23}");
        assertEquals(
                join("startObject 0", "startObjectName o1 1", "endObjectName o1 foo 6", "startObjectValue o1 foo 8",
                        "startNumber 8", "endNumber 23 10", "endObjectValue o1 foo 10", "endObject o1 11"),
                myHandler.getLog());
    }

    @Test
    public void parse_object_empty() {
        myParser.parse("{}");
        assertEquals(join("startObject 0", "endObject o1 2"), myHandler.getLog());
    }

    @Test
    public void parse_stripsPadding() {
        assertEquals(new JsonArray(), parse(" [ ] "));
    }

    @Test
    public void parse_ignoresAllWhiteSpace() {
        assertEquals(new JsonArray(), parse("\t\r\n [\t\r\n ]\t\r\n "));
    }

    @Test
    public void parse_failsWithUnterminatedString() {
        assertParseException(5, "Unexpected end of input", "[\"foo");
    }

    @Test
    public void parse_lineAndColumn_onFirstLine() {
        myParser.parse("[]");
        assertEquals("line 1, column 3", myHandler.myLastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterLF() {
        myParser.parse("[\n]");
        assertEquals("line 2, column 2", myHandler.myLastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterCRLF() {
        myParser.parse("[\r\n]");
        assertEquals("line 2, column 2", myHandler.myLastLocation.toString());
    }

    @Test
    public void parse_lineAndColumn_afterCR() {
        myParser.parse("[\r]");
        assertEquals("line 1, column 4", myHandler.myLastLocation.toString());
    }

    @Test
    public void parse_handlesInputsThatExceedBufferSize() throws IOException {
        final String input = "[ 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47 ]";
        final DefaultHandler defHandler = new DefaultHandler();

        myParser = new JsonParser(defHandler);
        myParser.parse(new JsonReader(input), 3);

        assertEquals("[2,3,5,7,11,13,17,19,23,29,31,37,41,43,47]", defHandler.getResult().toString());
    }

    @Test
    public void parse_handlesStringsThatExceedBufferSize() throws IOException {
        final String input = "[ \"lorem ipsum dolor sit amet\" ]";
        final DefaultHandler defHandler = new DefaultHandler();

        myParser = new JsonParser(defHandler);
        myParser.parse(new JsonReader(input), 3);

        assertEquals("[\"lorem ipsum dolor sit amet\"]", defHandler.getResult().toString());
    }

    @Test
    public void parse_handlesNumbersThatExceedBufferSize() throws IOException {
        final DefaultHandler defHandler = new DefaultHandler();
        final String input = "[ 3.141592653589 ]";

        myParser = new JsonParser(defHandler);
        myParser.parse(new JsonReader(input), 3);

        assertEquals("[3.141592653589]", defHandler.getResult().toString());
    }

    @Test
    public void parse_handlesPositionsCorrectlyWhenInputExceedsBufferSize() {
        final JsonReader reader = new JsonReader("{\n  \"a\": 23,\n  \"b\": 42,\n}");
        final ParseException details = assertThrows(ParseException.class, () -> myParser.parse(reader, 3));

        assertEquals(new Location(24, 4, 1, 1), details.getLocation());
    }

    @Test
    public void parse_failsOnTooDeeplyNestedArray() {
        final String input;

        JsonArray array = new JsonArray();

        for (int index = 0; index < 1001; index++) {
            array = new JsonArray().add(array);
        }

        input = array.toString();
        myLog = assertThrows(ParseException.class, () -> myParser.parse(input)).getMessage();
        assertEquals("Nesting too deep at line 1, column 1002", myLog);
    }

    /**
     * Tests that parse will fail on a deeply nested object on JDK >= 17. This tests fails with the pre-configured test
     * number (i.e. 1001) on earlier JDK versions (throwing a StackOverflowError), so we only test on a current LTS JDK.
     * <p>
     * The test may pass with 940 levels on JDK 11 but, unless there is a desire for the information, we're not going to
     * go and test every old JDK version to find out the point at which it fails for each.
     */
    @Test
    public void parse_failsOnTooDeeplyNestedObject() {
        assumeTrue(LATEST_LTS_JDK); // Will skip this test if the JDK isn't the latest LTS release (or greater)

        final String name = getNames().next().substring(0, 3);
        final String input;

        JsonObject object = new JsonObject();

        for (int index = 0; index < 1001; index++) {
            object = new JsonObject().add(name, object);
        }

        input = object.toString();

        myLog = assertThrows(ParseException.class, () -> myParser.parse(input)).getMessage();
        assertEquals("Nesting too deep at line 1, column 7002", myLog);
    }

    @Test
    public void parse_failsOnTooDeeplyNestedMixedObject() {
        final String name = getNames().next().substring(0, 3);
        final String input;

        JsonValue value = new JsonObject();

        for (int index = 0; index < 1001; index++) {
            value = index % 2 == 0 ? new JsonArray().add(value) : new JsonObject().add(name, value);
        }

        input = value.toString();
        myLog = assertThrows(ParseException.class, () -> myParser.parse(input)).getMessage();
        assertEquals("Nesting too deep at line 1, column 4002", myLog);
    }

    @Test
    public void parse_doesNotFailWithManyArrays() {
        final JsonArray array = new JsonArray();

        for (int index = 0; index < 1001; index++) {
            array.add(new JsonArray().add(7));
        }

        assertTrue(parse(array.toString()).isArray());
    }

    @Test
    public void parse_doesNotFailWithManyEmptyArrays() {
        final JsonArray array = new JsonArray();

        for (int index = 0; index < 1001; index++) {
            array.add(new JsonArray());
        }

        assertTrue(parse(array.toString()).isArray());
    }

    @Test
    public void parse_doesNotFailWithManyObjects() {
        final JsonArray array = new JsonArray();

        for (int index = 0; index < 1001; index++) {
            array.add(new JsonObject().add(getNames().next(), 7));
        }

        assertTrue(parse(array.toString()).isArray());
    }

    @Test
    public void parse_doesNotFailWithManyEmptyObjects() {
        final JsonArray array = new JsonArray();

        for (int index = 0; index < 1001; index++) {
            array.add(new JsonObject());
        }

        assertTrue(parse(array.toString()).isArray());
    }

    @Test
    public void parse_canBeCalledTwice() {
        myParser.parse("[23]");
        myParser.parse("[42]");

        assertEquals(join(
                // First run
                "startArray 0", "startArrayValue a1 1", "startNumber 1", "endNumber 23 3", "endArrayValue a1 3",
                "endArray a1 4",
                // Second run
                "startArray 0", "startArrayValue a2 1", "startNumber 1", "endNumber 42 3", "endArrayValue a2 3",
                "endArray a2 4"), myHandler.getLog());
    }

    @Test
    public void arrays_empty() {
        assertEquals("[]", parse("[]").toString());
    }

    @Test
    public void arrays_singleValue() {
        assertEquals("[23]", parse("[23]").toString());
    }

    @Test
    public void arrays_multipleValues() {
        assertEquals("[23,42]", parse("[23,42]").toString());
    }

    @Test
    public void arrays_withWhitespaces() {
        assertEquals("[23,42]", parse("[ 23 , 42 ]").toString());
    }

    @Test
    public void arrays_nested() {
        assertEquals("[[23]]", parse("[[23]]").toString());
        assertEquals("[[[]]]", parse("[[[]]]").toString());
        assertEquals("[[23],42]", parse("[[23],42]").toString());
        assertEquals("[[23],[42]]", parse("[[23],[42]]").toString());
        assertEquals("[[23],[42]]", parse("[[23],[42]]").toString());
        assertEquals("[{\"foo\":[23]},{\"bar\":[42]}]", parse("[{\"foo\":[23]},{\"bar\":[42]}]").toString());
    }

    @Test
    public void arrays_illegalSyntax() {
        assertParseException(1, "Expected value", "[,]");
        assertParseException(4, "Expected ',' or ']'", "[23 42]");
        assertParseException(4, "Expected value", "[23,]");
    }

    @Test
    public void arrays_incomplete() {
        assertParseException(1, "Unexpected end of input", "[");
        assertParseException(2, "Unexpected end of input", "[ ");
        assertParseException(3, "Unexpected end of input", "[23");
        assertParseException(4, "Unexpected end of input", "[23 ");
        assertParseException(4, "Unexpected end of input", "[23,");
        assertParseException(5, "Unexpected end of input", "[23, ");
    }

    @Test
    public void objects_empty() {
        assertEquals("{}", parse("{}").toString());
    }

    @Test
    public void objects_singleValue() {
        assertEquals("{\"foo\":23}", parse("{\"foo\":23}").toString());
    }

    @Test
    public void objects_multipleValues() {
        assertEquals("{\"foo\":23,\"bar\":42}", parse("{\"foo\":23,\"bar\":42}").toString());
    }

    @Test
    public void objects_whitespace() {
        assertEquals("{\"foo\":23,\"bar\":42}", parse("{ \"foo\" : 23, \"bar\" : 42 }").toString());
    }

    @Test
    public void objects_nested() {
        assertEquals("{\"foo\":{}}", parse("{\"foo\":{}}").toString());
        assertEquals("{\"foo\":{\"bar\":42}}", parse("{\"foo\":{\"bar\": 42}}").toString());
        assertEquals("{\"foo\":{\"bar\":{\"baz\":42}}}", parse("{\"foo\":{\"bar\": {\"baz\": 42}}}").toString());
        assertEquals("{\"foo\":[{\"bar\":{\"baz\":[[42]]}}]}",
                parse("{\"foo\":[{\"bar\": {\"baz\": [[42]]}}]}").toString());
    }

    @Test
    public void objects_illegalSyntax() {
        assertParseException(1, "Expected name", "{,}");
        assertParseException(1, "Expected name", "{:}");
        assertParseException(1, "Expected name", "{23}");
        assertParseException(4, "Expected ':'", "{\"a\"}");
        assertParseException(5, "Expected ':'", "{\"a\" \"b\"}");
        assertParseException(5, "Expected value", "{\"a\":}");
        assertParseException(8, "Expected name", "{\"a\":23,}");
        assertParseException(8, "Expected name", "{\"a\":23,42");
    }

    @Test
    public void objects_incomplete() {
        assertParseException(1, "Unexpected end of input", "{");
        assertParseException(2, "Unexpected end of input", "{ ");
        assertParseException(2, "Unexpected end of input", "{\"");
        assertParseException(4, "Unexpected end of input", "{\"a\"");
        assertParseException(5, "Unexpected end of input", "{\"a\" ");
        assertParseException(5, "Unexpected end of input", "{\"a\":");
        assertParseException(6, "Unexpected end of input", "{\"a\": ");
        assertParseException(7, "Unexpected end of input", "{\"a\":23");
        assertParseException(8, "Unexpected end of input", "{\"a\":23 ");
        assertParseException(8, "Unexpected end of input", "{\"a\":23,");
        assertParseException(9, "Unexpected end of input", "{\"a\":23, ");
    }

    @Test
    public void strings_emptyString_isAccepted() {
        assertEquals("", parse("\"\"").asString());
    }

    @Test
    public void strings_asciiCharacters_areAccepted() {
        assertEquals(" ", parse("\" \"").asString());
        assertEquals("a", parse("\"a\"").asString());
        assertEquals("foo", parse("\"foo\"").asString());
        assertEquals("A2-D2", parse("\"A2-D2\"").asString());
        assertEquals("\u007f", parse("\"\u007f\"").asString());
    }

    @Test
    public void strings_nonAsciiCharacters_areAccepted() {
        assertEquals("Русский", parse("\"Русский\"").asString());
        assertEquals("العربية", parse("\"العربية\"").asString());
        assertEquals("日本語", parse("\"日本語\"").asString());
    }

    @Test
    public void strings_controlCharacters_areRejected() {
        // JSON string must not contain characters < 0x20
        assertParseException(3, "Expected valid string character", "\"--\n--\"");
        assertParseException(3, "Expected valid string character", "\"--\r\n--\"");
        assertParseException(3, "Expected valid string character", "\"--\t--\"");
        assertParseException(3, "Expected valid string character", "\"--\u0000--\"");
        assertParseException(3, "Expected valid string character", "\"--\u001f--\"");
    }

    @Test
    public void strings_validEscapes_areAccepted() {
        // valid escapes are \" \\ \/ \b \f \n \r \t and unicode escapes
        assertEquals(" \" ", parse("\" \\\" \"").asString());
        assertEquals(" \\ ", parse("\" \\\\ \"").asString());
        assertEquals(" / ", parse("\" \\/ \"").asString());
        assertEquals(" \u0008 ", parse("\" \\b \"").asString());
        assertEquals(" \u000c ", parse("\" \\f \"").asString());
        assertEquals(" \r ", parse("\" \\r \"").asString());
        assertEquals(" \n ", parse("\" \\n \"").asString());
        assertEquals(" \t ", parse("\" \\t \"").asString());
    }

    @Test
    public void strings_escape_atStart() {
        assertEquals("\\x", parse("\"\\\\x\"").asString());
    }

    @Test
    public void strings_escape_atEnd() {
        assertEquals("x\\", parse("\"x\\\\\"").asString());
    }

    @Test
    public void strings_illegalEscapes_areRejected() {
        assertParseException(2, "Expected valid escape sequence", "\"\\a\"");
        assertParseException(2, "Expected valid escape sequence", "\"\\x\"");
        assertParseException(2, "Expected valid escape sequence", "\"\\000\"");
    }

    @Test
    public void strings_validUnicodeEscapes_areAccepted() {
        assertEquals("\u0021", parse("\"\\u0021\"").asString());
        assertEquals("\u4711", parse("\"\\u4711\"").asString());
        assertEquals("\uffff", parse("\"\\uffff\"").asString());
        assertEquals("\uabcdx", parse("\"\\uabcdx\"").asString());
    }

    @Test
    public void strings_illegalUnicodeEscapes_areRejected() {
        assertParseException(3, "Expected hexadecimal digit", "\"\\u \"");
        assertParseException(3, "Expected hexadecimal digit", "\"\\ux\"");
        assertParseException(5, "Expected hexadecimal digit", "\"\\u20 \"");
        assertParseException(6, "Expected hexadecimal digit", "\"\\u000x\"");
    }

    @Test
    public void strings_incompleteStrings_areRejected() {
        assertParseException(1, "Unexpected end of input", "\"");
        assertParseException(4, "Unexpected end of input", "\"foo");
        assertParseException(5, "Unexpected end of input", "\"foo\\");
        assertParseException(6, "Unexpected end of input", "\"foo\\n");
        assertParseException(6, "Unexpected end of input", "\"foo\\u");
        assertParseException(7, "Unexpected end of input", "\"foo\\u0");
        assertParseException(9, "Unexpected end of input", "\"foo\\u000");
        assertParseException(10, "Unexpected end of input", "\"foo\\u0000");
    }

    @Test
    public void numbers_integer() {
        assertEquals(new JsonNumber("0"), parse("0"));
        assertEquals(new JsonNumber("-0"), parse("-0"));
        assertEquals(new JsonNumber("1"), parse("1"));
        assertEquals(new JsonNumber("-1"), parse("-1"));
        assertEquals(new JsonNumber("23"), parse("23"));
        assertEquals(new JsonNumber("-23"), parse("-23"));
        assertEquals(new JsonNumber("1234567890"), parse("1234567890"));
        assertEquals(new JsonNumber("123456789012345678901234567890"), parse("123456789012345678901234567890"));
    }

    @Test
    public void numbers_minusZero() {
        // allowed by JSON, allowed by Java
        final JsonValue value = parse("-0");

        assertEquals(0, value.asInt());
        assertEquals(0l, value.asLong());
        assertEquals(0f, value.asFloat(), 0.00000000001f);
        assertEquals(0d, value.asDouble(), 0.00000000001d);
    }

    @Test
    public void numbers_decimal() {
        assertEquals(new JsonNumber("0.23"), parse("0.23"));
        assertEquals(new JsonNumber("-0.23"), parse("-0.23"));
        assertEquals(new JsonNumber("1234567890.12345678901234567890"), parse("1234567890.12345678901234567890"));
    }

    @Test
    public void numbers_withExponent() {
        assertEquals(new JsonNumber("0.1e9"), parse("0.1e9"));
        assertEquals(new JsonNumber("0.1e9"), parse("0.1e9"));
        assertEquals(new JsonNumber("0.1E9"), parse("0.1E9"));
        assertEquals(new JsonNumber("-0.23e9"), parse("-0.23e9"));
        assertEquals(new JsonNumber("0.23e9"), parse("0.23e9"));
        assertEquals(new JsonNumber("0.23e+9"), parse("0.23e+9"));
        assertEquals(new JsonNumber("0.23e-9"), parse("0.23e-9"));
    }

    @Test
    public void numbers_withInvalidFormat() {
        assertParseException(0, "Expected value", "+1");
        assertParseException(0, "Expected value", ".1");
        assertParseException(1, "Unexpected character", "02");
        assertParseException(2, "Unexpected character", "-02");
        assertParseException(1, "Expected digit", "-x");
        assertParseException(2, "Expected digit", "1.x");
        assertParseException(2, "Expected digit", "1ex");
        assertParseException(3, "Unexpected character", "1e1x");
    }

    @Test
    public void numbers_incomplete() {
        assertParseException(1, "Unexpected end of input", "-");
        assertParseException(2, "Unexpected end of input", "1.");
        assertParseException(4, "Unexpected end of input", "1.0e");
        assertParseException(5, "Unexpected end of input", "1.0e-");
    }

    @Test
    public void null_complete() {
        assertEquals(Json.NULL, parse("null"));
    }

    @Test
    public void null_incomplete() {
        assertParseException(1, "Unexpected end of input", "n");
        assertParseException(2, "Unexpected end of input", "nu");
        assertParseException(3, "Unexpected end of input", "nul");
    }

    @Test
    public void null_withIllegalCharacter() {
        assertParseException(1, "Expected 'u'", "nx");
        assertParseException(2, "Expected 'l'", "nux");
        assertParseException(3, "Expected 'l'", "nulx");
        assertParseException(4, "Unexpected character", "nullx");
    }

    @Test
    public void true_complete() {
        assertSame(Json.TRUE, parse("true"));
    }

    @Test
    public void true_incomplete() {
        assertParseException(1, "Unexpected end of input", "t");
        assertParseException(2, "Unexpected end of input", "tr");
        assertParseException(3, "Unexpected end of input", "tru");
    }

    @Test
    public void true_withIllegalCharacter() {
        assertParseException(1, "Expected 'r'", "tx");
        assertParseException(2, "Expected 'u'", "trx");
        assertParseException(3, "Expected 'e'", "trux");
        assertParseException(4, "Unexpected character", "truex");
    }

    @Test
    public void false_complete() {
        assertSame(Json.FALSE, parse("false"));
    }

    @Test
    public void false_incomplete() {
        assertParseException(1, "Unexpected end of input", "f");
        assertParseException(2, "Unexpected end of input", "fa");
        assertParseException(3, "Unexpected end of input", "fal");
        assertParseException(4, "Unexpected end of input", "fals");
    }

    @Test
    public void false_withIllegalCharacter() {
        assertParseException(1, "Expected 'a'", "fx");
        assertParseException(2, "Expected 'l'", "fax");
        assertParseException(3, "Expected 's'", "falx");
        assertParseException(4, "Expected 'e'", "falsx");
        assertParseException(5, "Unexpected character", "falsex");
    }

    private void assertParseException(final int offset, final String message, final String json) {
        final ParseException details = assertThrows(ParseException.class, () -> myParser.parse(json));

        assertEquals(offset, details.getLocation().getOffset());
        assertTrue(details.getMessage().startsWith(message));
    }

    private static String join(final String... aStringArray) {
        final StringBuilder builder = new StringBuilder();

        for (final String string : aStringArray) {
            builder.append(string).append('\n');
        }

        return builder.toString();
    }

    static class InterruptedHandler extends DefaultHandler {

        JsonParser myInterruptedHandlerParser;

        @Override
        public void setJsonParser(final JsonParser aParser) {
            myInterruptedHandlerParser = aParser;
        }

    }

    static class SnapshotHandler extends TestHandler {

        private boolean hasDuration;

        @Override
        public void endPropertyName(final Object aObject, final String aName) {
            if ("duration".equals(aName) && !hasDuration) {
                final Location location = myTestHandlerParser.getLocation();

                try {
                    myTestHandlerParser.mark();
                    LOGGER.debug("Duration at: {} [{}]", location, location.getOffset());
                } catch (final IOException details) {
                    throw new ParseException(myTestHandlerParser.getLocation(), details.getMessage());
                }

                hasDuration = true;
            } else if ("motivation".equals(aName)) {
                final Location location = myTestHandlerParser.getLocation();

                LOGGER.debug("Duration at: {} [{}]", location, location.getOffset());
                myTestHandlerParser.reset();
            }

            super.record("endObjectName", aObject, aName);
        }
    }

    static class TestHandler implements JsonHandler<Object, Object> {

        final StringBuilder myLog = new StringBuilder();

        Location myLastLocation;

        JsonParser myTestHandlerParser;

        int mySequence = 0;

        @Override
        public void setJsonParser(final JsonParser aParser) {
            myTestHandlerParser = aParser;
        }

        @Override
        public void startNull() {
            record("startNull");
        }

        @Override
        public void endNull() {
            record("endNull");
        }

        @Override
        public void startBoolean() {
            record("startBoolean");
        }

        @Override
        public void endBoolean(final boolean value) {
            record("endBoolean", Boolean.valueOf(value));
        }

        @Override
        public void startString() {
            record("startString");
        }

        @Override
        public void endString(final String string) {
            record("endString", string);
        }

        @Override
        public void startNumber() {
            record("startNumber");
        }

        @Override
        public void endNumber(final String string) {
            record("endNumber", string);
        }

        @Override
        public Object startArray() {
            record("startArray");
            return "a" + ++mySequence;
        }

        @Override
        public void endArray(final Object array) {
            record("endArray", array);
        }

        @Override
        public void startArrayValue(final Object array) {
            record("startArrayValue", array);
        }

        @Override
        public void endArrayValue(final Object array) {
            record("endArrayValue", array);
        }

        @Override
        public Object startJsonObject() {
            record("startObject");
            return "o" + ++mySequence;
        }

        @Override
        public void endJsonObject(final Object object) {
            record("endObject", object);
        }

        @Override
        public void startPropertyName(final Object object) {
            record("startObjectName", object);
        }

        @Override
        public void endPropertyName(final Object object, final String name) {
            record("endObjectName", object, name);
        }

        @Override
        public void startPropertyValue(final Object object, final String name) {
            record("startObjectValue", object, name);
        }

        @Override
        public void endPropertyValue(final Object object, final String name) {
            record("endObjectValue", object, name);
        }

        private void record(final String aEvent, final Object... aArgsArray) {
            myLastLocation = myTestHandlerParser.getLocation();
            myLog.append(aEvent);

            for (final Object arg : aArgsArray) {
                myLog.append(' ').append(arg);
            }

            myLog.append(' ').append(myLastLocation.getOffset()).append('\n');
        }

        String getLog() {
            return myLog.toString();
        }
    }
}
