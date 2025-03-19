// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 * Elements can be added using the <code>add(...)</code> methods which accept instances of {@link JsonValue}, strings,
 * primitive numbers, and boolean values. To replace an element of an array, use the <code>set(int, ...)</code> methods.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class also supports iterating over the elements
 * in document order using an {@link #iterator()} or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 * An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a <code>JsonArray</code>
 * instance concurrently, while at least one of these threads modifies the contents of this array, access to the
 * instance must be synchronized externally. Failure to do so may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
public final class JsonArray extends JsonValue implements Iterable<JsonValue> {

    /** The logger used by the {@code JsonArray} class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonArray.class, MessageCodes.BUNDLE);

    /** The {@code serialVersionUID} for the {@code JsonArray} class. */
    private static final long serialVersionUID = 8799105840124461744L;

    /** The {@code JsonArray}'s values. */
    private final List<JsonValue> myValues;

    /**
     * Creates a new empty JsonArray.
     */
    public JsonArray() {
        myValues = new ArrayList<>();
    }

    /**
     * Creates a new JsonArray with the contents of the supplied JsonArray.
     *
     * @param aJsonArray The JsonArray to get the initial contents from, must not be <code>null</code>
     */
    public JsonArray(final JsonArray aJsonArray) {
        this(aJsonArray, false);
    }

    /**
     * Creates a new unmodifiable JsonArray with the contents of the supplied JsonArray.
     *
     * @param aJsonArray
     * @param aUnmodifiableArray
     */
    private JsonArray(final JsonArray aJsonArray, final boolean aUnmodifiableArray) {
        Objects.requireNonNull(aJsonArray, LOGGER.getMessage(MessageCodes.JSON_017));

        if (aUnmodifiableArray) {
            myValues = Collections.unmodifiableList(aJsonArray.myValues);
        } else {
            myValues = new ArrayList<>(aJsonArray.myValues);
        }
    }

    /**
     * Appends the JSON representation of the specified <code>boolean</code> value to the end of this array.
     *
     * @param aValue the value to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final boolean aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    /**
     * Appends the JSON representation of the specified <code>double</code> value to the end of this array.
     *
     * @param aValue the value to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final double aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    /**
     * Appends the JSON representation of the specified <code>float</code> value to the end of this array.
     *
     * @param aValue the value to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final float aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    /**
     * Appends the JSON representation of the specified <code>int</code> value to the end of this array.
     *
     * @param aValue the value to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final int aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    /**
     * Appends the specified JSON value to the end of this array.
     *
     * @param aValue the JsonValue to add to the array, must not be <code>null</code>
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final JsonValue aValue) {
        myValues.add(Objects.requireNonNull(aValue, LOGGER.getMessage(MessageCodes.JSON_007)));
        return this;
    }

    /**
     * Appends the JSON representation of the specified <code>long</code> value to the end of this array.
     *
     * @param aValue the value to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final long aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    /**
     * Appends the JSON representation of the specified string to the end of this array.
     *
     * @param aValue the string to add to the array
     * @return the array itself, to enable method chaining
     */
    public JsonArray add(final String aValue) {
        myValues.add(Json.value(aValue));
        return this;
    }

    @Override
    public JsonArray asArray() {
        return this;
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return ValueUtils.equals(EMPTY, this, aValue, aConfig);
    }

    /**
     * Indicates whether a given object is "equal to" this JsonArray. An object is considered equal if it is also a
     * <code>JsonArray</code> and both arrays contain the same list of values.
     * <p>
     * If two JsonArrays are equal, they will also produce the same JSON output.
     * </p>
     *
     * @param aObject The object to be compared with this JsonArray
     * @return True if the specified object is equal to this JsonArray; else, false
     */
    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        return Objects.equals(myValues, ((JsonArray) aObject).myValues);
    }

    /**
     * Returns the value of the element at the specified position in this array.
     *
     * @param aIndex the index of the array element to return
     * @return the value of the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonValue get(final int aIndex) {
        return myValues.get(aIndex);
    }

    @Override
    public int hashCode() {
        return myValues.hashCode();
    }

    @Override
    public boolean isArray() {
        return true;
    }

    /**
     * Returns <code>true</code> if this array contains no elements.
     *
     * @return <code>true</code> if this array contains no elements
     */
    public boolean isEmpty() {
        return myValues.isEmpty();
    }

    /**
     * Returns an iterator over the values of this array in document order. The returned iterator cannot be used to
     * modify this array.
     *
     * @return an iterator over the values of this array
     */
    @Override
    public ListIterator<JsonValue> iterator() {
        final ListIterator<JsonValue> iterator = myValues.listIterator();

        return new ListIterator<>() {

            @Override
            public void add(final JsonValue aValue) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public boolean hasPrevious() {
                return iterator.hasPrevious();
            }

            @Override
            public JsonValue next() {
                return iterator.next();
            }

            @Override
            public int nextIndex() {
                return iterator.nextIndex();
            }

            @Override
            public JsonValue previous() {
                return iterator.previous();
            }

            @Override
            public int previousIndex() {
                return iterator.previousIndex();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(final JsonValue arg0) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Removes the element at the specified index from this array.
     *
     * @param aIndex the index of the element to remove
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray remove(final int aIndex) {
        myValues.remove(aIndex);
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * <code>boolean</code> value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final boolean aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * <code>double</code> value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final double aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * <code>float</code> value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final float aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * <code>int</code> value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final int aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the specified JSON value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position, must not be <code>null</code>
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final JsonValue aValue) {
        myValues.set(aIndex, Objects.requireNonNull(aValue, LOGGER.getMessage(MessageCodes.JSON_007)));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * <code>long</code> value.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the value to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final long aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Replaces the element at the specified position in this array with the JSON representation of the specified
     * string.
     *
     * @param aIndex the index of the array element to replace
     * @param aValue the string to be stored at the specified array position
     * @return the array itself, to enable method chaining
     * @throws IndexOutOfBoundsException if the index is out of range, i.e. <code>index &lt; 0</code> or
     *         <code>index &gt;= size</code>
     */
    public JsonArray set(final int aIndex, final String aValue) {
        myValues.set(aIndex, Json.value(aValue));
        return this;
    }

    /**
     * Returns the number of elements in this array.
     *
     * @return the number of elements in this array
     */
    public int size() {
        return myValues.size();
    }

    /**
     * Returns a list of the values in this array in document order. The returned list is backed by this array and will
     * reflect subsequent changes. It cannot be used to modify this array. Attempts to modify the returned list will
     * result in an exception.
     *
     * @return a list of the values in this array
     */
    public List<JsonValue> values() {
        return Collections.unmodifiableList(myValues);
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        final Iterator<JsonValue> iterator;

        Objects.requireNonNull(aWriter, LOGGER.getMessage(MessageCodes.JSON_009)).writeArrayOpen(myValues.size());
        iterator = iterator();

        if (iterator.hasNext()) {
            iterator.next().write(aWriter);

            while (iterator.hasNext()) {
                aWriter.writeArraySeparator();
                iterator.next().write(aWriter);
            }
        }

        aWriter.writeArrayClose(myValues.size());
    }

    /**
     * Returns an unmodifiable wrapper for the specified JsonArray. This method allows to provide read-only access to a
     * JsonArray.
     * <p>
     * The returned JsonArray is backed by the given array and reflects subsequent changes. Attempts to modify the
     * returned JsonArray result in an <code>UnsupportedOperationException</code>.
     * </p>
     *
     * @param aJsonArray the JsonArray for which an unmodifiable JsonArray is to be returned
     * @return an unmodifiable view of the specified JsonArray
     */
    public static JsonArray unmodifiableArray(final JsonArray aJsonArray) {
        return new JsonArray(aJsonArray, true);
    }
}
