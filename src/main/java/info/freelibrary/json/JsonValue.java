
package info.freelibrary.json;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Objects;

/**
 * Represents a JSON value. This can be a JSON <strong>object</strong>, an <strong> array</strong>, a
 * <strong>number</strong>, a <strong>string</strong>, or one of the literals <strong>true</strong>,
 * <strong>false</strong>, and <strong>null</strong>.
 * <p>
 * The literals <strong>true</strong>, <strong>false</strong>, and <strong>null</strong> are represented by the
 * constants {@link Json#TRUE}, {@link Json#FALSE}, and {@link Json#NULL}.
 * </p>
 * <p>
 * JSON <strong>objects</strong> and <strong>arrays</strong> are represented by the subtypes {@link JsonObject} and
 * {@link JsonArray}. Instances of these types can be created using the public constructors of these classes.
 * </p>
 * <p>
 * Instances that represent JSON <strong>numbers</strong>, <strong>strings</strong> and <strong>boolean</strong> values
 * can be created using the static factory methods {@link Json#value(String)}, {@link Json#value(long)},
 * {@link Json#value(double)}, etc.
 * </p>
 * <p>
 * In order to find out whether an instance of this class is of a certain type, the methods {@link #isObject()},
 * {@link #isArray()}, {@link #isString()}, {@link #isNumber()} etc. can be used.
 * </p>
 * <p>
 * If the type of a JSON value is known, the methods {@link #asObject()}, {@link #asArray()}, {@link #asString()},
 * {@link #asInt()}, etc. can be used to get this value directly in the appropriate target type.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public abstract class JsonValue implements Serializable {

    /**
     * Creates a new JSON value.
     */
    JsonValue() {
        // Prevent subclasses outside of this package
    }

    /**
     * Detects whether this value represents a JSON object. If this is the case, this value is an instance of
     * {@link JsonObject}.
     *
     * @return <code>true</code> if this value is an instance of JsonObject
     */
    public boolean isObject() {
        return false;
    }

    /**
     * Detects whether this value represents a JSON array. If this is the case, this value is an instance of
     * {@link JsonArray}.
     *
     * @return <code>true</code> if this value is an instance of JsonArray
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Detects whether this value represents a JSON number.
     *
     * @return <code>true</code> if this value represents a JSON number
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * Detects whether this value represents a JSON string.
     *
     * @return <code>true</code> if this value represents a JSON string
     */
    public boolean isString() {
        return false;
    }

    /**
     * Detects whether this value represents a boolean value.
     *
     * @return <code>true</code> if this value represents either the JSON literal <code>true</code> or
     *         <code>false</code>
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * Detects whether this value represents the JSON literal <code>true</code>.
     *
     * @return <code>true</code> if this value represents the JSON literal <code>true</code>
     */
    public boolean isTrue() {
        return false;
    }

    /**
     * Detects whether this value represents the JSON literal <code>false</code>.
     *
     * @return <code>true</code> if this value represents the JSON literal <code>false</code>
     */
    public boolean isFalse() {
        return false;
    }

    /**
     * Detects whether this value represents the JSON literal <code>null</code>.
     *
     * @return <code>true</code> if this value represents the JSON literal <code>null</code>
     */
    public boolean isNull() {
        return false;
    }

    /**
     * Returns this JSON value as {@link JsonObject}, assuming that this value represents a JSON object. If this is not
     * the case, an exception is thrown.
     *
     * @return a JSONObject for this value
     * @throws UnsupportedOperationException if this value is not a JSON object
     */
    public JsonObject asObject() {
        throw new UnsupportedOperationException("Not an object: " + toString());
    }

    /**
     * Returns this JSON value as {@link JsonArray}, assuming that this value represents a JSON array. If this is not
     * the case, an exception is thrown.
     *
     * @return a JSONArray for this value
     * @throws UnsupportedOperationException if this value is not a JSON array
     */
    public JsonArray asArray() {
        throw new UnsupportedOperationException("Not an array: " + toString());
    }

    /**
     * Returns this JSON value as an <code>int</code> value, assuming that this value represents a JSON number that can
     * be interpreted as Java <code>int</code>. If this is not the case, an exception is thrown.
     * <p>
     * To be interpreted as Java <code>int</code>, the JSON number must neither contain an exponent nor a fraction part.
     * Moreover, the number must be in the <code>Integer</code> range.
     * </p>
     *
     * @return this value as <code>int</code>
     * @throws UnsupportedOperationException if this value is not a JSON number
     * @throws NumberFormatException if this JSON number can not be interpreted as <code>int</code> value
     */
    public int asInt() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Returns this JSON value as a <code>long</code> value, assuming that this value represents a JSON number that can
     * be interpreted as Java <code>long</code>. If this is not the case, an exception is thrown.
     * <p>
     * To be interpreted as Java <code>long</code>, the JSON number must neither contain an exponent nor a fraction
     * part. Moreover, the number must be in the <code>Long</code> range.
     * </p>
     *
     * @return this value as <code>long</code>
     * @throws UnsupportedOperationException if this value is not a JSON number
     * @throws NumberFormatException if this JSON number can not be interpreted as <code>long</code> value
     */
    public long asLong() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Returns this JSON value as a <code>float</code> value, assuming that this value represents a JSON number. If this
     * is not the case, an exception is thrown.
     * <p>
     * If the JSON number is out of the <code>Float</code> range, {@link Float#POSITIVE_INFINITY} or
     * {@link Float#NEGATIVE_INFINITY} is returned.
     * </p>
     *
     * @return this value as <code>float</code>
     * @throws UnsupportedOperationException if this value is not a JSON number
     */
    public float asFloat() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Returns this JSON value as a <code>double</code> value, assuming that this value represents a JSON number. If
     * this is not the case, an exception is thrown.
     * <p>
     * If the JSON number is out of the <code>Double</code> range, {@link Double#POSITIVE_INFINITY} or
     * {@link Double#NEGATIVE_INFINITY} is returned.
     * </p>
     *
     * @return this value as <code>double</code>
     * @throws UnsupportedOperationException if this value is not a JSON number
     */
    public double asDouble() {
        throw new UnsupportedOperationException("Not a number: " + toString());
    }

    /**
     * Returns this JSON value as String, assuming that this value represents a JSON string. If this is not the case, an
     * exception is thrown.
     *
     * @return the string represented by this value
     * @throws UnsupportedOperationException if this value is not a JSON string
     */
    public String asString() {
        throw new UnsupportedOperationException("Not a string: " + toString());
    }

    /**
     * Returns this JSON value as a <code>boolean</code> value, assuming that this value is either <code>true</code> or
     * <code>false</code>. If this is not the case, an exception is thrown.
     *
     * @return this value as <code>boolean</code>
     * @throws UnsupportedOperationException if this value is neither <code>true</code> or <code>false</code>
     */
    public boolean asBoolean() {
        throw new UnsupportedOperationException("Not a boolean: " + toString());
    }

    /**
     * Writes the JSON representation of this value to the given writer using the given formatting.
     * <p>
     * Writing performance can be improved by using a {@link java.io.BufferedWriter BufferedWriter}.
     * </p>
     *
     * @param aWriter The writer to which to write this value
     * @throws IOException If an I/O error occurs in the writer
     */
    public void writeTo(final JsonWriter aWriter) throws IOException {
        write(Objects.requireNonNull(aWriter, "writer is null"));
    }

    /**
     * Outputs a string according to the supplied JSON options.
     *
     * @param aConfig A serialization configuration
     * @return A JSON string
     */
    public String toString(final JsonOptions aConfig) {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter writer;

        if (aConfig.ignoreOrder()) {
            writer = new SortingWriter(stringWriter);
        } else if (aConfig.isFormatted()) {
            writer = new PrettyWriter(stringWriter);
        } else {
            writer = new JsonWriter(stringWriter);
        }

        try {
            writeTo(writer);
        } catch (final IOException details) {
            throw new RuntimeException(details);
        }

        return writer.toString();
    }

    /**
     * Returns the JSON string for this value.
     *
     * @return a JSON string serialization of this value
     */
    @Override
    public String toString() {
        final JsonWriter writer = new JsonWriter(new StringWriter());

        try {
            writeTo(writer);
        } catch (final IOException details) {
            throw new RuntimeException(details); // Runtime b/c StringWriter should not throw IOException(s)
        }

        return writer.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one according to the contract specified in
     * {@link Object#equals(Object)}.
     * <p>
     * By default, JSON values are considered equal if and only if they represent the same JSON value. As a result, two
     * given JsonObjects may be different even though they contain the same set of names with the same values, but in a
     * different order.
     * </p>
     *
     * @param aObject the reference object with which to compare
     * @return true if this object is the same as the object argument; false otherwise
     */
    @Override
    public boolean equals(final Object aObject) {
        throw new UnsupportedOperationException("Subclasses of JsonValue should be compared");
    }

    /**
     * Indicates whether some other object is "equal to" this one according to the rules passed in through the supplied
     * {@link JsonOptions}.
     *
     * @param aJsonValue An object to compare
     * @param aConfig Options to use when comparing JSON values
     * @return True if the supplied object is "equal to" the current JSON value; else, false
     */
    public boolean equals(final JsonValue aJsonValue, final JsonOptions aConfig) {
        throw new UnsupportedOperationException("Subclasses of JsonValue should be compared");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Subclasses of JsonValue should be hashed");
    }

    /**
     * Writes this JsonValue to the supplied writer.
     *
     * @param aWriter A writer to write the JsonValue to
     * @throws IOException If there is trouble writing the value
     */
    abstract void write(JsonWriter aWriter) throws IOException;

}
