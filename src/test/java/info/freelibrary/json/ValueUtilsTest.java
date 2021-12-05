
package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import info.freelibrary.util.StringUtils;

/**
 * Tests of ValueUtils.
 */
class ValueUtilsTest {

    /**
     * An integer/string value for use in testing.
     */
    private static final String FOUR = "4";

    private static final String FILE_TEMPLATE = "src/test/resources/sample-{}.json";

    /**
     * Test the object equality comparison.
     */
    @Test
    final void testEqualsObject() {
        final JsonString jsonString = new JsonString(UUID.randomUUID().toString());
        assertTrue(ValueUtils.equals(EMPTY, jsonString, jsonString, new JsonOptions()));
    }

    /**
     * Tests an equality comparison when arrays are set to be collapsible.
     *
     * @throws IOException If there is trouble reading the test fixtures
     */
    @Test
    final void testCollapsibleArraysSet() throws IOException {
        final JsonValue uncollapsed = Json.parse(new FileReader(StringUtils.format(FILE_TEMPLATE, 4)));
        final JsonValue collapsed = Json.parse(new FileReader(StringUtils.format(FILE_TEMPLATE, 6)));
        final JsonOptions opts = new JsonOptions().setCollapsibleArrays(Arrays.asList("@context"));

        assertTrue(ValueUtils.equals(EMPTY, uncollapsed, collapsed, opts));
    }

    /**
     * Tests an equality comparison when arrays are not set to be collapsible.
     *
     * @throws IOException If there is trouble reading the test fixtures
     */
    @Test
    final void testCollapsibleArraysNotSet() throws IOException {
        final JsonValue uncollapsed = Json.parse(new FileReader(StringUtils.format(FILE_TEMPLATE, 4)));
        final JsonValue collapsed = Json.parse(new FileReader(StringUtils.format(FILE_TEMPLATE, 6)));

        assertFalse(ValueUtils.equals(EMPTY, uncollapsed, collapsed, new JsonOptions()));
    }

    /**
     * Test equality with first null.
     */
    @Test
    final void testEqualsFirstNull() {
        final JsonString jsonString = new JsonString(UUID.randomUUID().toString());
        assertFalse(ValueUtils.equals(EMPTY, null, jsonString, new JsonOptions()));
    }

    /**
     * Test equality with second null.
     */
    @Test
    final void testEqualsSecondNull() {
        final JsonString jsonString = new JsonString(UUID.randomUUID().toString());
        assertFalse(ValueUtils.equals(EMPTY, jsonString, null, new JsonOptions()));
    }

    /**
     * Tests equality with two different classes.
     */
    @Test
    final void testEqualsDifferentClasses() {
        assertFalse(ValueUtils.equals(EMPTY, new JsonNumber(FOUR), new JsonString(FOUR), new JsonOptions()));
    }
}
