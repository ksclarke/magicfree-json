
package info.freelibrary.json;

import java.io.IOException;

@SuppressWarnings("serial") // use default serial UID
final class JsonString extends JsonValue {

    /** The JSON string's value. */
    private final String myValue;

    /**
     * Creates a new JSON string.
     *
     * @param aString A JSON string
     */
    JsonString(final String aString) {
        if (aString == null) {
            throw new NullPointerException("string is null");
        }

        myValue = aString;
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeString(myValue);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String asString() {
        return myValue;
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        return myValue.equals(((JsonString) aObject).myValue);
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return equals(aValue);
    }
}
