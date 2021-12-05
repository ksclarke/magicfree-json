
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import info.freelibrary.util.StringUtils;

/**
 * Tests the JsonReader.
 */
class JsonReaderTest {

    /**
     * A JSON test fixture.
     */
    private static final String JSON_FILE = "src/test/resources/sample-{}.json";

    /**
     * Tests reading from a JSON file.
     *
     * @throws IOException If there is trouble reading from the JSON file
     */
    @Test
    final void constructor_readFile() throws IOException {
        final DefaultHandler handler = new DefaultHandler();
        final JsonParser parser = new JsonParser(handler);

        parser.parse(new JsonReader(new File(StringUtils.format(JSON_FILE, 1))));
        assertEquals(697, handler.getResult().toString().length());
    }

    /**
     * Tests reading from a URL referencing a JSON file.
     *
     * @throws IOException If there is trouble reading from the JSON file at the end of the URL
     */
    @Test
    final void constructor_readURL() throws IOException {
        final File file = new File(StringUtils.format(JSON_FILE, 1));
        final DefaultHandler handler = new DefaultHandler();
        final JsonParser parser = new JsonParser(handler);
        final JsonValue expected;

        parser.parse(new JsonReader(file));
        expected = handler.getResult();
        parser.parse(new JsonReader(file.toURI().toURL()));
        assertEquals(expected, handler.getResult());
    }

    /**
     * Tests reading from a URL referencing a JSON file.
     *
     * @throws IOException If there is trouble reading from the JSON file at the end of the URL
     */
    @Test
    final void constructor_readURLCharset() throws IOException {
        final File file = new File(StringUtils.format(JSON_FILE, 3));
        final DefaultHandler handler = new DefaultHandler();
        final JsonParser parser = new JsonParser(handler);
        final JsonValue expected;

        parser.parse(new JsonReader(file));
        expected = handler.getResult();
        parser.parse(new JsonReader(file.toURI().toURL()));
        assertEquals(expected, handler.getResult());
    }
}
