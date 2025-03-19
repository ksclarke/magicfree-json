// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * A JSON literal.
 */
class JsonLiteral extends JsonValue {

    /** The logger for the {@code JsonLiteral} class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonLiteral.class, MessageCodes.BUNDLE);

    /** The {@code serialVersionUID} for the {@code JsonLiteral} class. */
    private static final long serialVersionUID = -3556416152434981507L;

    /** Whether this literal is FALSE. */
    private final boolean isFalse;

    /** Whether this literal is a NULL. */
    private final boolean isNull;

    /** Whether this literal is TRUE. */
    private final boolean isTrue;

    /** The value of this JSON literal. */
    private final String myValue;

    /**
     * Creates a new JSON literal from the supplied string value.
     *
     * @param aValue A literal value
     */
    JsonLiteral(final String aValue) {
        Objects.requireNonNull(aValue, LOGGER.getMessage(MessageCodes.JSON_023));

        myValue = aValue.toLowerCase(Locale.US);

        isNull = "null".equals(myValue);
        isTrue = "true".equals(myValue);
        isFalse = "false".equals(myValue);
    }

    @Override
    public boolean asBoolean() {
        return isNull ? super.asBoolean() : isTrue;
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

        return Objects.equals(myValue, ((JsonLiteral) aObject).myValue);
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public boolean isBoolean() {
        return isTrue || isFalse;
    }

    @Override
    public boolean isFalse() {
        return isFalse;
    }

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public boolean isTrue() {
        return isTrue;
    }

    @Override
    public String toString() {
        return myValue;
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeLiteral(myValue);
    }
}
