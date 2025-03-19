// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * A JSON string value.
 */
final class JsonString extends JsonValue {

    /** The logger that's used by the {@code JsonString} class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonString.class, MessageCodes.BUNDLE);

    /** The {@code serialVersionUID} for the {@code JsonString} class. */
    private static final long serialVersionUID = -3533756460153728263L;

    /** The JSON string's value. */
    private final String myValue;

    /**
     * Creates a new JSON string.
     *
     * @param aString A JSON string
     */
    JsonString(final String aString) {
        Objects.requireNonNull(aString, LOGGER.getMessage(MessageCodes.JSON_003));
        myValue = aString;
    }

    @Override
    public String asString() {
        return myValue;
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

        return Objects.equals(myValue, ((JsonString) aObject).myValue);
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeString(myValue);
    }
}
