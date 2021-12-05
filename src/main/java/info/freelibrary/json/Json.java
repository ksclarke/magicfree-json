
package info.freelibrary.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * This class serves as the entry point to the minimal-json API.
 * <p>
 * To <strong>parse</strong> a given JSON input, use the <code>parse()</code> methods like in this example:
 * </p>
 *
 * <pre>
 *
 * JsonObject object = Json.parse(string).asObject();
 * </pre>
 * <p>
 * To <strong>create</strong> a JSON data structure to be serialized, use the methods <code>value()</code>,
 * <code>array()</code>, and <code>object()</code>. For example, the following snippet will produce the JSON string
 * <em>{"foo": 23, "bar": true}</em>:
 * </p>
 *
 * <pre>
 *
 * String string = Json.object().add("foo", 23).add("bar", true).toString();
 * </pre>
 * <p>
 * To create a JSON array from a given Java array, you can use one of the <code>array()</code> methods with varargs
 * parameters:
 * </p>
 *
 * <pre>
 * String[] names = ...
 * JsonArray array = Json.array(names);
 * </pre>
 */
public final class Json {

    /**
     * Represents the JSON literal <code>null</code>.
     */
    public static final JsonValue NULL = new JsonLiteral("null");

    /**
     * Represents the JSON literal <code>true</code>.
     */
    public static final JsonValue TRUE = new JsonLiteral("true");

    /**
     * Represents the JSON literal <code>false</code>.
     */
    public static final JsonValue FALSE = new JsonLiteral("false");

    /**
     * A logger for the Json class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Json.class, MessageCodes.BUNDLE);

    /**
     * Creates a new Json instance.
     */
    private Json() {
        // This is intentionally left empty
    }

    /**
     * Returns a JsonValue instance that represents the given <code>int</code> value.
     *
     * @param aValue The value to get a JSON representation for
     * @return A JSON value that represents the given value
     */
    public static JsonValue value(final int aValue) {
        return new JsonNumber(Integer.toString(aValue, 10));
    }

    /**
     * Returns a JsonValue instance that represents the given <code>long</code> value.
     *
     * @param aValue The value to get a JSON representation for
     * @return A JSON value that represents the given value
     */
    public static JsonValue value(final long aValue) {
        return new JsonNumber(Long.toString(aValue, 10));
    }

    /**
     * Returns a JsonValue instance that represents the given <code>float</code> value.
     *
     * @param aValue The value to get a JSON representation for
     * @return A JSON value that represents the given value
     */
    public static JsonValue value(final float aValue) {
        if (Float.isInfinite(aValue) || Float.isNaN(aValue)) {
            throw new IllegalArgumentException(LOGGER.getMessage(MessageCodes.JSON_018));
        }

        return new JsonNumber(cutOffPointZero(Float.toString(aValue)));
    }

    /**
     * Returns a JsonValue instance that represents the given <code>double</code> value.
     *
     * @param aValue The value to get a JSON representation for
     * @return A JSON value that represents the given value
     */
    public static JsonValue value(final double aValue) {
        if (Double.isInfinite(aValue) || Double.isNaN(aValue)) {
            throw new IllegalArgumentException(LOGGER.getMessage(MessageCodes.JSON_018));
        }

        return new JsonNumber(cutOffPointZero(Double.toString(aValue)));
    }

    /**
     * Returns a JsonValue instance that represents the given string.
     *
     * @param aString The string to get a JSON representation for
     * @return A JSON value that represents the given string
     */
    public static JsonValue value(final String aString) {
        return aString == null ? NULL : new JsonString(aString);
    }

    /**
     * Returns a JsonValue instance that represents the given <code>boolean</code> value.
     *
     * @param aValue The value to get a JSON representation for
     * @return A JSON value that represents the given value
     */
    public static JsonValue value(final boolean aValue) {
        return aValue ? TRUE : FALSE;
    }

    /**
     * Creates a new empty JsonArray. This is equivalent to creating a new JsonArray using the constructor.
     *
     * @return A new empty JSON array
     */
    public static JsonArray array() {
        return new JsonArray();
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given <code>int</code> values.
     *
     * @param aValues The values to be included in the new JSON array
     * @return A new JSON array that contains the given values
     */
    public static JsonArray array(final int... aValues) {
        final JsonArray array = new JsonArray();

        for (final int value : Objects.requireNonNull(aValues, LOGGER.getMessage(MessageCodes.JSON_002))) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given <code>long</code> values.
     *
     * @param aValues The values to be included in the new JSON array
     * @return A new JSON array that contains the given values
     */
    public static JsonArray array(final long... aValues) {
        final JsonArray array = new JsonArray();

        for (final long value : Objects.requireNonNull(aValues, LOGGER.getMessage(MessageCodes.JSON_002))) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given <code>float</code> values.
     *
     * @param values the values to be included in the new JSON array
     * @return a new JSON array that contains the given values
     */
    public static JsonArray array(final float... values) {
        final JsonArray array = new JsonArray();

        for (final float value : Objects.requireNonNull(values, "values is null")) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given <code>double</code> values.
     *
     * @param values the values to be included in the new JSON array
     * @return a new JSON array that contains the given values
     */
    public static JsonArray array(final double... values) {
        final JsonArray array = new JsonArray();

        for (final double value : Objects.requireNonNull(values, "values is null")) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given <code>boolean</code> values.
     *
     * @param values the values to be included in the new JSON array
     * @return a new JSON array that contains the given values
     */
    public static JsonArray array(final boolean... values) {
        final JsonArray array = new JsonArray();

        for (final boolean value : Objects.requireNonNull(values, "values is null")) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new JsonArray that contains the JSON representations of the given strings.
     *
     * @param aStringsArray the strings to be included in the new JSON array
     * @return a new JSON array that contains the given strings
     */
    public static JsonArray array(final String... aStringsArray) {
        final JsonArray array = new JsonArray();

        for (final String value : Objects.requireNonNull(aStringsArray, "values is null")) {
            array.add(value);
        }

        return array;
    }

    /**
     * Creates a new empty JsonObject. This is equivalent to creating a new JsonObject using the constructor.
     *
     * @return a new empty JSON object
     */
    public static JsonObject object() {
        return new JsonObject();
    }

    /**
     * Parses the given input string as JSON. The input must contain a valid JSON value, optionally padded with
     * whitespace.
     *
     * @param aString the input string, must be valid JSON
     * @return a value that represents the parsed JSON
     * @throws ParseException if the input is not valid JSON
     */
    public static JsonValue parse(final String aString) {
        final DefaultHandler handler = new DefaultHandler();
        new JsonParser(handler).parse(Objects.requireNonNull(aString, "string is null"));
        return handler.getResult();
    }

    /**
     * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
     * optionally padded with whitespace.
     *
     * @param aReader The reader from which to parse the JSON value
     * @return A value that represents the parsed JSON
     * @throws IOException If an I/O error occurs while reading the JSON
     * @throws ParseException If the input is not valid JSON
     */
    public static JsonValue parse(final Reader aReader) throws IOException {
        final DefaultHandler handler = new DefaultHandler();
        new JsonParser(handler).parse(Objects.requireNonNull(aReader, "reader is null"));
        return handler.getResult();
    }

    /**
     * Strip the point zero from strings that are numbers.
     *
     * @param aString A numeric string
     * @return A numeric string expressed just as a whole number
     */
    private static String cutOffPointZero(final String aString) {
        if (aString.endsWith(".0")) {
            return aString.substring(0, aString.length() - 2);
        }

        return aString;
    }

}
