// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * A JSON number implementation of {@code JsonValue}.
 */
final class JsonNumber extends JsonValue {

    /** The logger for the {@code JsonNumber} class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonNumber.class, MessageCodes.BUNDLE);

    /** The {@code serialVersionUID} for the {@JsonNumber} class. */
    private static final long serialVersionUID = -2585433664630099643L;

    /** A JSON number in string form. */
    private final String myValue;

    /**
     * Creates a new JSON number.
     *
     * @param aValue A numeric value as a string
     */
    JsonNumber(final String aValue) {
        myValue = Objects.requireNonNull(aValue, LOGGER.getMessage(MessageCodes.JSON_003));
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(myValue);
    }

    @Override
    public float asFloat() {
        return Float.parseFloat(myValue);
    }

    @Override
    public int asInt() {
        return Integer.parseInt(myValue, 10);
    }

    @Override
    public long asLong() {
        return Long.parseLong(myValue, 10);
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return equals(aValue);
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        return Objects.equals(myValue, ((JsonNumber) aObject).myValue);
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public String toString() {
        return myValue;
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeNumber(myValue);

    }
}
