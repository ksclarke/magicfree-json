
package info.freelibrary.json;

import static info.freelibrary.util.Constants.EMPTY;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import info.freelibrary.json.JsonObject.Property;

/**
 * Represents a JSON object, a set of name/value pairs, where the names are strings and the values are JSON values.
 * <p>
 * Properties can be added using the <code>add(String, ...)</code> methods which accept instances of {@link JsonValue},
 * strings, primitive numbers, and boolean values. To modify certain values of an object, use the
 * <code>set(String, ...)</code> methods. Please note that the <code>add</code> methods are faster than <code>set</code>
 * as they do not search for existing properties. On the other hand, the <code>add</code> methods do not prevent adding
 * multiple properties with the same name. Duplicate names are discouraged but not prohibited by JSON.
 * </p>
 * <p>
 * Properties can be accessed by their name using {@link #get(String)}. A list of all names can be obtained from the
 * method {@link #names()}. This class also supports iterating over the properties in document order using an
 * {@link #iterator()} or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (Property property : jsonObject) {
 *   String name = property.getName();
 *   JsonValue value = property.getValue();
 *   ...
 * }
 * </pre>
 * <p>
 * Even though JSON objects are unordered by definition, instances of this class preserve the order of properties to
 * allow processing in document order and to guarantee a predictable output.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a <code>JsonObject</code>
 * instance concurrently, while at least one of these threads modifies the contents of this object, access to the
 * instance must be synchronized externally. Failure to do so may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
public final class JsonObject extends JsonValue implements Iterable<Property> {

    /**
     * The <code>serialVersionUID</code> for JsonObject.
     */
    private static final long serialVersionUID = -7137647142842370219L;

    /**
     * A list of property names.
     */
    private final List<String> myNames;

    /**
     * A list of property values.
     */
    private final List<JsonValue> myValues;

    /**
     * A hash code table.
     */
    private transient HashCodeTable myTable;

    /**
     * Creates a new empty JsonObject.
     */
    public JsonObject() {
        myNames = new ArrayList<>();
        myValues = new ArrayList<>();
        myTable = new HashCodeTable();
    }

    /**
     * Creates a new JsonObject, initialized with the contents of the specified JSON object.
     *
     * @param aObject the JSON object to get the initial contents from, must not be <code>null</code>
     */
    public JsonObject(final JsonObject aObject) {
        this(aObject, false);
    }

    /**
     * Creates a new JsonObject that can be unmodifiable, if desired.
     *
     * @param aObject A JSON object
     * @param aUnmodifiable Whether the newly created JsonObject should be unmodifiable
     */
    private JsonObject(final JsonObject aObject, final boolean aUnmodifiable) {
        Objects.requireNonNull(aObject, "object is null");

        if (aUnmodifiable) {
            myNames = Collections.unmodifiableList(aObject.myNames);
            myValues = Collections.unmodifiableList(aObject.myValues);
        } else {
            myNames = new ArrayList<>(aObject.myNames);
            myValues = new ArrayList<>(aObject.myValues);
        }

        myTable = new HashCodeTable();
        updateHashIndex();
    }

    /**
     * Returns an unmodifiable JsonObject for the specified one. This method allows read-only access to a JsonObject.
     * <p>
     * The returned JsonObject is backed by the given object and reflect changes that happen to it. Attempts to modify
     * the returned JsonObject result in an <code>UnsupportedOperationException</code>.
     * </p>
     *
     * @param aObject the JsonObject for which an unmodifiable JsonObject is to be returned
     * @return an unmodifiable view of the specified JsonObject
     */
    public static JsonObject unmodifiableObject(final JsonObject aObject) {
        return new JsonObject(aObject, true);
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified <code>int</code> value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final int aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified <code>long</code> value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final long aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified <code>float</code> value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final float aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified <code>double</code> value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final double aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified <code>boolean</code> value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final boolean aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the JSON representation of the
     * specified string.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final String aValue) {
        return add(aName, Json.value(aValue));
    }

    /**
     * Appends a new property to the end of this object, with the specified name and supplied JSON object.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aObject The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final JsonObject aObject) {
        return add(aName, (JsonValue) aObject);
    }

    /**
     * Appends a new property to the end of this object, with the specified name and supplied JSON array.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aArray The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject add(final String aName, final JsonArray aArray) {
        return add(aName, (JsonValue) aArray);
    }

    /**
     * Appends a new property to the end of this object, with the specified name and the specified JSON value.
     * <p>
     * This method <strong>does not prevent duplicate names</strong>. Calling this method with a name that already
     * exists in the object will append another property with the same name. In order to replace existing properties,
     * use the method <code>set(name, value)</code> instead. However, <strong> <em>add</em> is much faster than
     * <em>set</em></strong> (because it does not need to search for existing properties). Therefore <em>add</em> should
     * be preferred when constructing new objects.
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add, must not be <code>null</code>
     * @return The object itself, to enable method chaining
     */
    protected JsonObject add(final String aName, final JsonValue aValue) {
        Objects.requireNonNull(aName, "name is null");
        Objects.requireNonNull(aValue, "value is null");

        myTable.add(aName, myNames.size());
        myNames.add(aName);
        myValues.add(aValue);

        return this;
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified
     * <code>int</code> value. If this object does not contain a property with this name, a new property is added at the
     * end of the object. If this object contains multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to replace
     * @param aValue The value to set to the property
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final int aValue) {
        return set(aName, Json.value(aValue));
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified
     * <code>long</code> value. If this object does not contain a property with this name, a new property is added at
     * the end of the object. If this object contains multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to replace
     * @param aValue The value to set to the property
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final long aValue) {
        return set(aName, Json.value(aValue));
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified
     * <code>float</code> value. If this object does not contain a property with this name, a new property is added at
     * the end of the object. If this object contains multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final float aValue) {
        return set(aName, Json.value(aValue));
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified
     * <code>double</code> value. If this object does not contain a property with this name, a new property is added at
     * the end of the object. If this object contains multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final double aValue) {
        set(aName, Json.value(aValue));
        return this;
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified
     * <code>boolean</code> value. If this object does not contain a property with this name, a new property is added at
     * the end of the object. If this object contains multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final boolean aValue) {
        return set(aName, Json.value(aValue));
    }

    /**
     * Sets the value of the property with the specified name to the JSON representation of the specified string. If
     * this object does not contain a property with this name, a new property is added at the end of the object. If this
     * object contains multiple properties with this myName, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final String aValue) {
        return set(aName, Json.value(aValue));
    }

    /**
     * Sets the value of the property with the specified name to the specified JSON value. If this object does not
     * contain a property with this name, a new property is added at the end of the object. If this object contains
     * multiple properties with this name, only the last one is changed.
     * <p>
     * This method should <strong>only be used to modify existing objects</strong>. To fill a new object with
     * properties, the method <code>add(name, value)</code> should be preferred which is much faster (as it does not
     * need to search for existing properties).
     * </p>
     *
     * @param aName The name of the property to add
     * @param aValue The value of the property to add, must not be <code>null</code>
     * @return The object itself, to enable method chaining
     */
    public JsonObject set(final String aName, final JsonValue aValue) {
        Objects.requireNonNull(aName, "name is null");
        Objects.requireNonNull(aValue, "value is null");

        final int index = indexOf(aName);

        if (index != -1) {
            myValues.set(index, aValue);
        } else {
            myTable.add(aName, myNames.size());
            myNames.add(aName);
            myValues.add(aValue);
        }

        return this;
    }

    /**
     * Removes a property with the specified name from this object. If this object contains multiple properties with the
     * given name, only the last one is removed. If this object does not contain a property with the specified name, the
     * object is not modified.
     *
     * @param aName The name of the property to remove
     * @return The object itself, to enable method chaining
     */
    public JsonObject remove(final String aName) {
        Objects.requireNonNull(aName, "name is null");

        final int index = indexOf(aName);

        if (index != -1) {
            myTable.remove(index);
            myNames.remove(index);
            myValues.remove(index);
        }

        return this;
    }

    /**
     * Checks if a specified property is present as a child of this object. This will not test if this object contains
     * the literal <code>null</code>, {@link JsonValue#isNull()} should be used for this purpose.
     *
     * @param aName The name of the property to check for
     * @return Whether or not the property is present
     */
    public boolean contains(final String aName) {
        return myNames.contains(aName);
    }

    /**
     * Copies all properties of the specified object into this object. When the specified object contains properties
     * with names that also exist in this object, the existing values in this object will be replaced by the
     * corresponding values in the specified object.
     *
     * @param aObject The object to merge
     * @return The object itself, to enable method chaining
     */
    public JsonObject merge(final JsonObject aObject) {
        Objects.requireNonNull(aObject, "object is null");

        for (final Property property : aObject) {
            set(property.myName, property.myValue);
        }

        return this;
    }

    /**
     * Returns the value of the property with the specified name in this object. If this object contains multiple
     * properties with the given name, this method will return the last one.
     *
     * @param aName The name of the property whose value is to be returned
     * @return An Optional with the value of the last property with the specified name, or an empty Optional if this
     *         object does not contain a property with that name
     */
    public Optional<JsonValue> get(final String aName) {
        return Optional.ofNullable(getJsonValue(aName));
    }

    /**
     * Returns an optional JsonObject property value that's associated with the supplied property name. If this object
     * contains multiple properties with the given name, this method will return the last one. If it doesn't have any
     * properties with the given name, an empty Optional is returned.
     *
     * @param aName The name of the JsonObject property to be returned
     * @return An Optional with the value of the last property with the specified name, or an empty Optional if this
     *         object does not contain a property with that name
     */
    public Optional<JsonObject> getJsonObject(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? Optional.of(value.asObject()) : Optional.empty();
    }

    /**
     * Returns the JsonObject property value that's associated with the supplied property name. If this object doesn't
     * contain a property with the supplied name, the passed in default value is returned instead.
     *
     * @param aName The name of the JsonObject property to be returned
     * @param aDefaultValue The default value returned if the JsonObject doesn't have a property with the supplied name
     * @return The requested JsonObject or the supplied default value
     */
    public JsonObject getJsonObject(final String aName, final JsonObject aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asObject() : aDefaultValue;
    }

    /**
     * Returns an optional JsonArray property value that's associated with the supplied property name. If this object
     * contains multiple properties with the given name, this method will return the last one. If it doesn't have any
     * properties with the given name, an empty Optional is returned.
     *
     * @param aName The name of the JsonArray property to be returned
     * @return An Optional with the value of the last property with the specified name, or an empty Optional if this
     *         object does not contain a property with that name
     */
    public Optional<JsonArray> getJsonArray(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? Optional.of(value.asArray()) : Optional.empty();
    }

    /**
     * Returns the JsonArray property value that's associated with the supplied property name. If this object doesn't
     * contain a property with the supplied name, the pass in default value is returned instead.
     *
     * @param aName The name of the JsonArray property to be returned
     * @param aDefaultValue The default value returned if the JsonObject doesn't have a property with the supplied name
     * @return The requested JsonArray or the supplied default value
     */
    public JsonArray getJsonArray(final String aName, final JsonArray aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asArray() : aDefaultValue;
    }

    /**
     * Returns the <code>int</code> value of the property with the specified name in this object. If this object does
     * not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one will be picked. If this property's value does not represent a JSON
     * number or if it cannot be interpreted as Java <code>int</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue The value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public int getInt(final String aName, final int aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asInt() : aDefaultValue;
    }

    /**
     * Returns the optional <code>int</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON number or if it cannot be interpreted as Java <code>int</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public OptionalInt getInt(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? OptionalInt.of(value.asInt()) : OptionalInt.empty();
    }

    /**
     * Returns the <code>long</code> value of the property with the specified name in this object. If this object does
     * not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one will be picked. If this property's value does not represent a JSON
     * number or if it cannot be interpreted as Java <code>long</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue The value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public long getLong(final String aName, final long aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asLong() : aDefaultValue;
    }

    /**
     * Returns the optional <code>long</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON number or if it cannot be interpreted as Java <code>long</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public OptionalLong getLong(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? OptionalLong.of(value.asLong()) : OptionalLong.empty();
    }

    /**
     * Returns the <code>float</code> value of the property with the specified name in this object. If this object does
     * not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one will be picked. If this property's value does not represent a JSON
     * number or if it cannot be interpreted as Java <code>float</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue The value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public float getFloat(final String aName, final float aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asFloat() : aDefaultValue;
    }

    /**
     * Returns the optional <code>float</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON number or if it cannot be interpreted as Java <code>float</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public Optional<Float> getFloat(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? Optional.of(value.asFloat()) : Optional.empty();
    }

    /**
     * Returns the <code>double</code> value of the property with the specified name in this object. If this object does
     * not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one will be picked. If this property's value does not represent a JSON
     * number or if it cannot be interpreted as Java <code>double</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue the value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public double getDouble(final String aName, final double aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asDouble() : aDefaultValue;
    }

    /**
     * Returns the optional <code>double</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON number or if it cannot be interpreted as Java <code>double</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public OptionalDouble getDouble(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? OptionalDouble.of(value.asDouble()) : OptionalDouble.empty();
    }

    /**
     * Returns the <code>boolean</code> value of the property with the specified name in this object. If this object
     * does not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one will be picked. If this property's value does not represent a JSON
     * <code>true</code> or <code>false</code> value, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue The value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public boolean getBoolean(final String aName, final boolean aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asBoolean() : aDefaultValue;
    }

    /**
     * Returns the optional <code>boolean</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON number or if it cannot be interpreted as Java <code>boolean</code>, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public Optional<Boolean> getBoolean(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? Optional.of(value.asBoolean()) : Optional.empty();
    }

    /**
     * Returns the <code>String</code> value of the property with the specified name in this object. If this object does
     * not contain a property with this name, the given default value is returned. If this object contains multiple
     * properties with the given name, the last one is picked. If this property's value does not represent a JSON
     * string, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @param aDefaultValue The value to be returned if the requested property is missing
     * @return The value of the last property with the specified name, or the given default value if this object does
     *         not contain a property with that name
     */
    public String getString(final String aName, final String aDefaultValue) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? value.asString() : aDefaultValue;
    }

    /**
     * Returns the optional <code>String</code> value of the property with the specified name in this object. If this
     * object does not contain a property with this name, an empty optional is returned. If this object contains
     * multiple properties with the given name, the last one will be picked. If this property's value does not represent
     * a JSON string, an exception is thrown.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or an empty optional if this object does not
     *         contain a property with that name
     */
    public Optional<String> getString(final String aName) {
        final JsonValue value = getJsonValue(aName);
        return value != null ? Optional.of(value.asString()) : Optional.empty();
    }

    /**
     * Returns the number of properties (name/value pairs) in this object.
     *
     * @return The number of properties in this object
     */
    public int size() {
        return myNames.size();
    }

    /**
     * Returns <code>true</code> if this object contains no properties.
     *
     * @return True if this object contains no properties; else, false
     */
    public boolean isEmpty() {
        return myNames.isEmpty();
    }

    /**
     * Returns a list of the names in this object in document order. The returned list is backed by this object and will
     * reflect subsequent changes. It cannot be used to modify this object. Attempts to modify the returned list will
     * result in an exception.
     *
     * @return A list of the names in this object
     */
    public List<String> names() {
        return Collections.unmodifiableList(myNames);
    }

    /**
     * Returns an iterator over the properties of this object in document order. The returned iterator cannot be used to
     * modify this object.
     *
     * @return An iterator over the properties of this object
     */
    @Override
    public ListIterator<Property> iterator() {
        final ListIterator<String> namesIterator = myNames.listIterator();
        final ListIterator<JsonValue> valuesIterator = myValues.listIterator();

        return new ListIterator<>() {

            @Override
            public boolean hasNext() {
                return namesIterator.hasNext();
            }

            @Override
            public Property next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("JsonObject doesn't have another property");
                }

                return new Property(namesIterator.next(), valuesIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(final Property aProperty) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean hasPrevious() {
                return namesIterator.hasPrevious();
            }

            @Override
            public int nextIndex() {
                return namesIterator.nextIndex();
            }

            @Override
            public Property previous() {
                return new Property(namesIterator.previous(), valuesIterator.next());
            }

            @Override
            public int previousIndex() {
                return namesIterator.previousIndex();
            }

            @Override
            public void set(final Property arg0) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    void write(final JsonWriter aWriter) throws IOException {
        aWriter.writeObjectOpen();

        if (aWriter instanceof SortingWriter) {
            final Iterator<Property> iterator = sortedIterator();

            if (iterator.hasNext()) {
                Property property = iterator.next();

                aWriter.writeMemberName(property.getName());
                aWriter.writeMemberSeparator();

                property.getValue().write(aWriter);

                while (iterator.hasNext()) {
                    property = iterator.next();

                    aWriter.writeObjectSeparator();
                    aWriter.writeMemberName(property.getName());
                    aWriter.writeMemberSeparator();

                    property.getValue().write(aWriter);
                }
            }
        } else {
            final Iterator<String> namesIterator = myNames.iterator();
            final Iterator<JsonValue> valuesIterator = myValues.iterator();

            if (namesIterator.hasNext()) {
                aWriter.writeMemberName(namesIterator.next());
                aWriter.writeMemberSeparator();
                valuesIterator.next().write(aWriter);

                while (namesIterator.hasNext()) {
                    aWriter.writeObjectSeparator();
                    aWriter.writeMemberName(namesIterator.next());
                    aWriter.writeMemberSeparator();
                    valuesIterator.next().write(aWriter);
                }
            }
        }

        aWriter.writeObjectClose();
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public JsonObject asObject() {
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + myNames.hashCode();
        return 31 * result + myValues.hashCode();
    }

    @Override
    public boolean equals(final Object aObject) {
        final JsonObject jsonObject;

        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        jsonObject = (JsonObject) aObject;

        return myNames.equals(jsonObject.myNames) && myValues.equals(jsonObject.myValues);
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return ValueUtils.equals(EMPTY, this, aValue, aConfig);
    }

    /**
     * Gets the index of the supplied property name.
     *
     * @param aName A property name
     * @return The index position of the last entry with the supplied name
     */
    int indexOf(final String aName) {
        final int index = myTable.get(aName);

        if (index != -1 && aName.equals(myNames.get(index))) {
            return index;
        }

        return myNames.lastIndexOf(aName);
    }

    /**
     * Iterates through the JsonObject's contents in a sorted fashion.
     *
     * @return An iterator that iterates over the JsonObject's sorted contents
     */
    Iterator<Property> sortedIterator() {
        final List<String> names = new ArrayList<>(myNames);

        // Sort the JsonObject's property names
        Collections.sort(names);

        /**
         * The sorted iterator.
         */
        return new Iterator<>() {

            /**
             * The sorted iterator's index position.
             */
            private int myCurrentIndex;

            @Override
            public boolean hasNext() {
                return myCurrentIndex < names.size();
            }

            @Override
            public Property next() {
                final String name = names.get(myCurrentIndex++);
                return new Property(name, myValues.get(myNames.indexOf(name)));
            }
        };
    }

    /**
     * Reads the object input stream.
     *
     * @param aInputStream An object stream
     * @throws IOException If there is trouble reading the object stream
     * @throws ClassNotFoundException If the class of the object stream cannot be found
     */
    private synchronized void readObject(final ObjectInputStream aInputStream)
            throws IOException, ClassNotFoundException {
        aInputStream.defaultReadObject();
        myTable = new HashCodeTable();

        updateHashIndex();
    }

    /**
     * Updates the object's hash index.
     */
    private void updateHashIndex() {
        final int size = myNames.size();

        for (int index = 0; index < size; index++) {
            myTable.add(myNames.get(index), index);
        }
    }

    /**
     * Returns the value of the property with the specified name in this object. If this object contains multiple
     * properties with the given name, this method will return the last one.
     *
     * @param aName The name of the property whose value is to be returned
     * @return The value of the last property with the specified name, or <code>null</code> if this object does not
     *         contain a property with that name
     */
    private JsonValue getJsonValue(final String aName) {
        final int index = indexOf(Objects.requireNonNull(aName, "name is null"));
        return index != -1 ? myValues.get(index) : null;
    }

    /**
     * Represents a JSON object property, i.e. a name and value.
     */
    public static class Property {

        /**
         * The name of the JSON object's property.
         */
        private final String myName;

        /**
         * The value of the JSON object's property.
         */
        private final JsonValue myValue;

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property string value
         */
        Property(final String aName, final String aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property integer value
         */
        Property(final String aName, final int aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property long value
         */
        Property(final String aName, final long aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property float value
         */
        Property(final String aName, final float aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property double value
         */
        Property(final String aName, final double aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property boolean value
         */
        Property(final String aName, final boolean aValue) {
            this(aName, Json.value(aValue));
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property JSON array value
         */
        Property(final String aName, final JsonArray aValue) {
            this(aName, (JsonValue) aValue);
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property JSON object value
         */
        Property(final String aName, final JsonObject aValue) {
            this(aName, (JsonValue) aValue);
        }

        /**
         * Creates a new JSON object property.
         *
         * @param aName A property name
         * @param aValue A property value
         */
        private Property(final String aName, final JsonValue aValue) {
            myName = aName;
            myValue = aValue;
        }

        /**
         * Returns the name of this property.
         *
         * @return the name of this property, never <code>null</code>
         */
        public String getName() {
            return myName;
        }

        /**
         * Returns the value of this property if it's a string. If the value isn't a string, an
         * UnsupportedOperationException is thrown.
         *
         * @return The string value of this property
         * @throws UnsupportedOperationException If the property's value isn't a string
         */
        public String getString() {
            return myValue.asString();
        }

        /**
         * Returns the value of this property if it's an integer. If the value isn't an integer, an
         * UnsupportedOperationException is thrown.
         *
         * @return The integer value of this property
         * @throws UnsupportedOperationException If the property's value isn't an integer
         */
        public int getInt() {
            return myValue.asInt();
        }

        /**
         * Returns the value of this property if it's a long. If the value isn't a long, an
         * UnsupportedOperationException is thrown.
         *
         * @return The long value of this property
         * @throws UnsupportedOperationException If the property's value isn't a long
         */
        public long getLong() {
            return myValue.asLong();
        }

        /**
         * Returns the value of this property if it's a float. If the value isn't a float, an
         * UnsupportedOperationException is thrown.
         *
         * @return The float value of this property
         * @throws UnsupportedOperationException If the property's value isn't a float
         */
        public float getFloat() {
            return myValue.asFloat();
        }

        /**
         * Returns the value of this property if it's a double. If the value isn't a double, an
         * UnsupportedOperationException is thrown.
         *
         * @return The double value of this property
         * @throws UnsupportedOperationException If the property's value isn't a double
         */
        public double getDouble() {
            return myValue.asDouble();
        }

        /**
         * Returns the value of this property if it's a boolean. If the value isn't a boolean, an
         * UnsupportedOperationException is thrown.
         *
         * @return The boolean value of this property
         * @throws UnsupportedOperationException If the property's value isn't a boolean
         */
        public boolean getBoolean() {
            return myValue.asBoolean();
        }

        /**
         * Returns the value of this property if it's a JsonObject. If the value isn't a JsonObject, an
         * UnsupportedOperationException is thrown.
         *
         * @return The JsonObject value of this property
         * @throws UnsupportedOperationException If the property's value isn't a JsonObject
         */
        public JsonObject getObject() {
            return myValue.asObject();
        }

        /**
         * Returns the value of this property if it's a JsonArray. If the value isn't a JsonArray, an
         * UnsupportedOperationException is thrown.
         *
         * @return The JsonArray value of this property
         * @throws UnsupportedOperationException If the property's value isn't a JsonArray
         */
        public JsonArray getArray() {
            return myValue.asArray();
        }

        /**
         * Returns the value of this property.
         *
         * @return the value of this property, never <code>null</code>
         */
        public JsonValue getValue() {
            return myValue;
        }

        @Override
        public int hashCode() {
            int result = 1;

            result = 31 * result + myName.hashCode();
            return 31 * result + myValue.hashCode();
        }

        /**
         * Indicates whether a given object is "equal to" this JsonObject. An object is considered equal if it is also a
         * <code>JsonObject</code> and both objects contain the same properties <em>in the same order</em>.
         * <p>
         * If two JsonObjects are equal, they will also produce the same JSON output.
         * </p>
         *
         * @param aObject the object to be compared with this JsonObject
         * @return True if the specified object is equal to this JsonObject; else, false
         */
        @Override
        public boolean equals(final Object aObject) {
            final Property jsonObject;

            if (this == aObject) {
                return true;
            }

            if (aObject == null || getClass() != aObject.getClass()) {
                return false;
            }

            jsonObject = (Property) aObject;

            return myName.equals(jsonObject.myName) && myValue.equals(jsonObject.myValue);
        }

    }

    /**
     * A hash code table.
     */
    static class HashCodeTable {

        /**
         * The internal index table.
         */
        private final byte[] myIndexTable = new byte[32]; // Must be a power of two

        /**
         * Creates a new hash code table.
         */
        HashCodeTable() {
        }

        /**
         * Creates a new hash code table from the supplied one.
         *
         * @param aHashCodeTable A hash code table
         */
        HashCodeTable(final HashCodeTable aHashCodeTable) {
            System.arraycopy(aHashCodeTable.myIndexTable, 0, myIndexTable, 0, myIndexTable.length);
        }

        /**
         * Add a new name to the hash code table.
         *
         * @param aName A name
         * @param aIndex An index position
         */
        void add(final String aName, final int aIndex) {
            final int slot = hashSlotFor(aName);

            if (aIndex < 0xff) {
                myIndexTable[slot] = (byte) (aIndex + 1); // Increment by 1
            } else {
                myIndexTable[slot] = 0; // 0 stands for empty
            }
        }

        /**
         * Removes an entry from the hash code table.
         *
         * @param aIndex The index position of the entry to remove
         */
        void remove(final int aIndex) {
            for (int index = 0; index < myIndexTable.length; index++) {
                if ((myIndexTable[index] & 0xff) == aIndex + 1) {
                    myIndexTable[index] = 0;
                } else if ((myIndexTable[index] & 0xff) > aIndex + 1) {
                    myIndexTable[index]--;
                }
            }
        }

        /**
         * Gets the index position of the supplied entry name.
         *
         * @param aName The entry name for which to get the index position
         * @return The index position of the supplied entry name
         */
        int get(final Object aName) {
            return (myIndexTable[hashSlotFor(aName)] & 0xff) - 1; // Decrement by 1; 0 stands for empty
        }

        /**
         * Gets the hash code table slot for the supplied entry.
         *
         * @param aEntry An index table entry.
         * @return A hash slot
         */
        private int hashSlotFor(final Object aEntry) {
            return aEntry.hashCode() & myIndexTable.length - 1;
        }

    }

}
