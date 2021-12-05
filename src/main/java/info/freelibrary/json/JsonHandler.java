
package info.freelibrary.json;

/**
 * A handler for parser events. Implementors of this interface can be given to a {@link JsonParser}. The parser will
 * then call the methods of the given handler while reading the input.
 * <p>
 * Implementations can use <code>getLocation()</code> to access the current character position of the parser at any
 * point. The <code>start*</code> methods will be called while the location points to the first character of the parsed
 * element. The <code>end*</code> methods will be called while the location points to the character position that
 * directly follows the last character of the parsed element. Example:
 * </p>
 *
 * <pre>
 * ["lorem ipsum"]
 *  ^            ^
 *  startString  endString
 * </pre>
 * <p>
 * Implementations that build an object representation of the parsed JSON can return arbitrary handler objects for JSON
 * arrays and JSON objects in {@link #startArray()} and {@link #startJsonObject()}. These handler objects will then be
 * provided in all subsequent parser events for this particular array or object. They can be used to keep track the
 * elements of a JSON array or object.
 * </p>
 *
 * @param <O> The type of handler used for JSON objects
 * @param <I> The type of handler used for JSON arrays
 * @see JsonParser
 */
public interface JsonHandler<O, I> {

    /**
     * Sets the handler's JSON object.
     *
     * @param aObject A JSON object
     */
    default void setObject(final O aObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the handler's JSON object.
     *
     * @return A JSON object
     */
    default O getObject() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the handler's JSON array iterator.
     *
     * @param aIterable A JSON array iterator
     */
    default void setIterable(final I aIterable) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the handler's JSON array.
     *
     * @return Gets the JSON array iterator
     */
    default I getIterable() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indicates the end of an array in the JSON input. This method will be called after reading the closing square
     * bracket character (<code>']'</code>).
     *
     * @param aArray the array handler returned from {@link #startArray()}, or <code>null</code> if not provided
     */
    default void endArray(final I aArray) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of an array element in the JSON input. This method will be called after reading the last
     * character of the element value, just after the <code>end</code> method for the specific element type (like
     * {@link #endString(String) endString()}, {@link #endNumber(String) endNumber()}, etc.).
     *
     * @param aArray the array handler returned from {@link #startArray()}, or <code>null</code> if not provided
     */
    default void endArrayValue(final I aArray) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of a boolean literal (<code>true</code> or <code>false</code>) in the JSON input. This method
     * will be called after reading the last character of the literal.
     *
     * @param aValue the parsed boolean value
     */
    default void endBoolean(final boolean aValue) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of a <code>null</code> literal in the JSON input. This method will be called after reading the
     * last character of the literal.
     */
    default void endNull() {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of a number in the JSON input. This method will be called after reading the last character of
     * the number.
     *
     * @param aString The parsed number string
     */
    default void endNumber(final String aString) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of an object in the JSON input. This method will be called after reading the closing curly
     * bracket character (<code>'}'</code>).
     *
     * @param aObject the object handler returned from {@link #startJsonObject()}, or null if not provided
     */
    default void endJsonObject(final O aObject) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of an object property name in the JSON input. This method will be called after reading the
     * closing quote character (<code>'"'</code>) of the property name.
     *
     * @param aObject The object handler returned from {@link #startJsonObject()}, or null if not provided
     * @param aName The parsed property name
     */
    default void endPropertyName(final O aObject, final String aName) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of an object property value in the JSON input. This method will be called after reading the
     * last character of the property value, just after the <code>end</code> method for the specific property type (like
     * {@link #endString(String) endString()}, {@link #endNumber(String) endNumber()}, etc.).
     *
     * @param aObject The object handler returned from {@link #startJsonObject()}, or null if not provided
     * @param aName The parsed property name
     */
    default void endPropertyValue(final O aObject, final String aName) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the end of a string in the JSON input. This method will be called after reading the closing double
     * quote character (<code>'&quot;'</code>).
     *
     * @param aString the parsed string
     */
    default void endString(final String aString) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of an array in the JSON input. This method will be called when reading the opening square
     * bracket character (<code>'['</code>).
     * <p>
     * This method may return an object to handle subsequent parser events for this array. This array handler will then
     * be provided in all calls to {@link #startArrayValue(Object) startArrayValue()}, {@link #endArrayValue(Object)
     * endArrayValue()}, and {@link #endArray(Object) endArray()} for this array. If this method is not implemented by a
     * handler, it returns a null by default. This can be passed to unimplemented start/end handler methods without
     * incident.
     * </p>
     *
     * @return a handler for this array, or <code>null</code> if not needed
     */
    default I startArray() {
        return null;
    }

    /**
     * Indicates the beginning of an array element in the JSON input. This method will be called when reading the first
     * character of the element, just before the call to the <code>start</code> method for the specific element type
     * ({@link #startString()}, {@link #startNumber()}, etc.).
     *
     * @param aArray The array handler returned from {@link #startArray()}, or <code>null</code> if not provided
     */
    default void startArrayValue(final I aArray) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of a boolean literal (<code>true</code> or <code>false</code>) in the JSON input. This
     * method will be called when reading the first character of the literal.
     */
    default void startBoolean() {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of a <code>null</code> literal in the JSON input. This method will be called when reading
     * the first character of the literal.
     */
    default void startNull() {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of a number in the JSON input. This method will be called when reading the first
     * character of the number.
     */
    default void startNumber() {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of an object in the JSON input. This method will be called when reading the opening curly
     * bracket character (<code>'{'</code>).
     * <p>
     * This method may return an object to handle subsequent parser events for this object. This object handler will be
     * provided in all calls to {@link #startPropertyName(Object) startObjectName()},
     * {@link #endPropertyName(Object, String) endObjectName()}, {@link #startPropertyValue(Object, String)
     * startObjectValue()}, {@link #endPropertyValue(Object, String) endObjectValue()}, and
     * {@link #endJsonObject(Object) endObject()} for this object. If this method is not implemented by a handler, it
     * returns a null by default. This can be passed to unimplemented start/end handler methods without incident.
     * </p>
     *
     * @return a handler for this object, or <code>null</code> if not needed
     */
    default O startJsonObject() {
        return null;
    }

    /**
     * Indicates the beginning of the name of an object property in the JSON input. This method will be called when
     * reading the opening quote character ('&quot;') of the property name.
     *
     * @param aObject the object handler returned from {@link #startJsonObject()}, or <code>null</code> if not provided
     */
    default void startPropertyName(final O aObject) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of the name of an object property in the JSON input. This method will be called when
     * reading the opening quote character ('&quot;') of the property name.
     *
     * @param aObject The object handler returned from {@link #startJsonObject()}, or <code>null</code> if not provided
     * @param aName The property name
     */
    default void startPropertyValue(final O aObject, final String aName) {
        // This is intentionally left empty.
    }

    /**
     * Indicates the beginning of a string in the JSON input. This method will be called when reading the opening double
     * quote character (<code>'&quot;'</code>).
     */
    default void startString() {
        // This is intentionally left empty.
    }

    /**
     * Sets the parser that the handler is using so that the handler has access to the parser's location. This is set
     * automatically by the JsonParser when it is created with a JsonHandler.
     * <p>
     * Implementations should throw an IllegalStateException if this method is called more than once.
     * </p>
     *
     * @param aParser A JsonParser being used by this handler
     * @throws IllegalStateException If the handler's parser has already been set
     * @throws UnsupportedOperationException If the handler doesn't implement this method
     */
    default void setJsonParser(final JsonParser aParser) {
        throw new UnsupportedOperationException();
    }

}
