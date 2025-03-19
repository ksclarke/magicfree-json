// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.util.Objects;

/**
 * The default implementation of JsonHandler that returns JSON constructs in the form of a JsonValue.
 */
public class DefaultHandler implements JsonHandler<JsonObject, JsonArray> {

    /** A JSON value. */
    private JsonValue myValue;

    @Override
    public void endArray(final JsonArray aJsonArray) {
        myValue = aJsonArray;
    }

    @Override
    public void endArrayValue(final JsonArray aJsonArray) {
        aJsonArray.add(myValue);
    }

    @Override
    public void endBoolean(final boolean aBool) {
        myValue = aBool ? Json.TRUE : Json.FALSE;
    }

    @Override
    public void endJsonObject(final JsonObject aJsonObject) {
        myValue = aJsonObject;
    }

    @Override
    public void endNull() {
        myValue = Json.NULL;
    }

    @Override
    public void endNumber(final String aString) {
        myValue = new JsonNumber(aString);
    }

    @Override
    public void endPropertyValue(final JsonObject aJsonObject, final String aName) {
        aJsonObject.add(aName, myValue);
    }

    @Override
    public void endString(final String aString) {
        myValue = new JsonString(aString);
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        return Objects.equals(myValue, ((DefaultHandler) aObject).myValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myValue);
    }

    @Override
    public void setJsonParser(final JsonParser aParser) {
        // This is intentionally left empty; it's not needed for our DefaultHandler.
    }

    @Override
    public JsonArray startArray() {
        return new JsonArray();
    }

    @Override
    public JsonObject startJsonObject() {
        return new JsonObject();
    }

    /**
     * Gets the result of the JSON parsing.
     *
     * @return The result of the JSON parsing
     */
    JsonValue getResult() {
        return myValue;
    }

}
