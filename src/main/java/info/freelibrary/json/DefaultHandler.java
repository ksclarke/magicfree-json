
package info.freelibrary.json;

/**
 * The default implementation of JsonHandler that returns JSON constructs in the form of a JsonValue.
 */
public class DefaultHandler implements JsonHandler<JsonObject, JsonArray> {

    /**
     * A JSON value.
     */
    private JsonValue myValue;

    /**
     * Gets the result of the JSON parsing.
     *
     * @return The result of the JSON parsing
     */
    JsonValue getResult() {
        return myValue;
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

    @Override
    public void endNull() {
        myValue = Json.NULL;
    }

    @Override
    public void endBoolean(final boolean aBool) {
        myValue = aBool ? Json.TRUE : Json.FALSE;
    }

    @Override
    public void endString(final String aString) {
        myValue = new JsonString(aString);
    }

    @Override
    public void endNumber(final String aString) {
        myValue = new JsonNumber(aString);
    }

    @Override
    public void endArray(final JsonArray aJsonArray) {
        myValue = aJsonArray;
    }

    @Override
    public void endJsonObject(final JsonObject aJsonObject) {
        myValue = aJsonObject;
    }

    @Override
    public void endArrayValue(final JsonArray aJsonArray) {
        aJsonArray.add(myValue);
    }

    @Override
    public void endPropertyValue(final JsonObject aJsonObject, final String aName) {
        aJsonObject.add(aName, myValue);
    }

}