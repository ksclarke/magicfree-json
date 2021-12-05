
package info.freelibrary.json;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("serial") // use default serial UID
final class JsonNumber extends JsonValue {

    /**
     * A JSON number in string form.
     */
    private final String myValue;

    /**
     * Creates a new JSON number.
     *
     * @param aValue
     */
    JsonNumber(final String aValue) {
        myValue = Objects.requireNonNull(aValue, "string is null");
    }

    @Override
    public String toString() {
        return myValue;
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeNumber(myValue);

    }

    @Override
    public boolean isNumber() {
        return true;
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
    public float asFloat() {
        return Float.parseFloat(myValue);
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(myValue);
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

        return myValue.equals(((JsonNumber) aObject).myValue);
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return equals(aValue);
    }
}
