
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.freelibrary.util.StringUtils;

import info.freelibrary.json.JsonObject.HashCodeTable;
import info.freelibrary.json.JsonObject.Property;

/**
 * Tests of the JsonObject class.
 */
public class JsonObjectTest extends AbstractTestBase {

    /**
     * The JsonObject being tested.
     */
    private JsonObject myJsonObject;

    /**
     * Optional logging output from the test.
     */
    private String myLog;

    /**
     * Sets up the testing environment.
     */
    @BeforeEach
    public void setUp() {
        myJsonObject = new JsonObject();
    }

    @Test
    public void equalsIgnoreOrder_simpleTest() {
        final Iterator<String> names = getNames();
        final String stringName = names.next();
        final String boolName = names.next();
        final String intName = names.next();
        final String value = names.next();
        final JsonObject json1 = new JsonObject().add(intName, 42).add(boolName, false).add(stringName, value);
        final JsonObject json2 = new JsonObject().add(boolName, false).add(intName, 42).add(stringName, value);

        assertTrue(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
    }

    @Test
    public void equalsIgnoreOrder_differentSizes() {
        final Iterator<String> names = getNames();
        final String stringName = names.next();
        final String boolName = names.next();
        final String intName = names.next();
        final String value = names.next();
        final JsonObject json1 = new JsonObject().add(intName, 42).add(boolName, false);
        final JsonObject json2 = new JsonObject().add(boolName, false).add(intName, 42).add(stringName, value);

        assertFalse(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
    }

    @Test
    public void equalsIgnoreOrder_withObject() {
        final DefaultHandler handler = new DefaultHandler();
        final JsonParser parser = new JsonParser(handler);
        final JsonObject json1;
        final JsonObject json2;

        try {
            parser.parse(new JsonReader(new File("src/test/resources/sample-1.json")));
            json1 = (JsonObject) handler.getResult();
            parser.parse(new JsonReader(new File("src/test/resources/sample-2.json")));
            json2 = (JsonObject) handler.getResult();

            assertTrue(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
        } catch (final IOException details) {
            fail(details);
        }
    }

    /**
     * Tests that the copy constructor fails when null is passed to it.
     */
    @Test
    public void copyConstructor_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> new JsonObject(null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_005)));
    }

    /**
     * Tests that the copy constructor produces an object with the same values.
     */
    @Test
    public void copyConstructor_hasSameValues() {
        final String name = getNames().next();
        final JsonObject copy;

        myJsonObject.add(name, 23);
        copy = new JsonObject(myJsonObject);

        assertEquals(myJsonObject.names(), copy.names());
        assertSame(myJsonObject.get(name).get(), copy.get(name).get());
    }

    /**
     * Tests that the copied object isn't modified by changes in the original object after the copy.
     */
    @Test
    public void copyConstructor_worksOnSafeCopy() {
        final JsonObject copy = new JsonObject(myJsonObject);

        myJsonObject.add(getNames().next(), 23);
        assertTrue(copy.isEmpty());
    }

    /**
     * Tests that an unmodifiable object has the same values as the original.
     */
    @Test
    public void unmodifiableObject_hasSameValues() {
        final String name = getNames().next();
        final JsonObject unmodifiableObject;

        myJsonObject.add(name, 23);
        unmodifiableObject = JsonObject.unmodifiableObject(myJsonObject);

        assertEquals(myJsonObject.names(), unmodifiableObject.names());
        assertSame(myJsonObject.get(name).get(), unmodifiableObject.get(name).get());
    }

    /**
     * Tests that the unmodifiable object reflects changes made to the source object.
     */
    @Test
    public void unmodifiableObject_reflectsChanges() {
        final JsonObject unmodifiableObject = JsonObject.unmodifiableObject(myJsonObject);
        final String name = getNames().next();

        myJsonObject.add(name, 23);

        assertEquals(myJsonObject.names(), unmodifiableObject.names());
        assertSame(myJsonObject.get(name).get(), unmodifiableObject.get(name).get());
    }

    /**
     * Tests that an unmodifiable object cannot be directly modified.
     */
    @Test
    public void unmodifiableObject_preventsModification() {
        assertThrows(UnsupportedOperationException.class,
                () -> JsonObject.unmodifiableObject(myJsonObject).add(getNames().next(), 23));
    }

    /**
     * Tests that a newly created object is empty after creation.
     */
    @Test
    public void isEmpty_trueAfterCreation() {
        assertTrue(myJsonObject.isEmpty());
    }

    /**
     * Tests that a newly created object isn't empty after it's been modified.
     */
    @Test
    public void isEmpty_falseAfterAdd() {
        assertFalse(myJsonObject.add(getNames().next(), true).isEmpty());
    }

    /**
     * Tests that object size is zero after it's been newly created.
     */
    @Test
    public void size_zeroAfterCreation() {
        assertEquals(0, myJsonObject.size());
    }

    /**
     * Tests that object size is one after one property has been added to a newly created object.
     */
    @Test
    public void size_oneAfterAdd() {
        assertEquals(1, myJsonObject.add(getNames().next(), true).size());
    }

    /**
     * Tests that multiple entries with the same name can be added. This is allowed by the JSON specification, though
     * they recommend to only allow one property with a given name.
     */
    @Test
    public void keyRepetition_allowsMultipleEntries() {
        final String name = getNames().next();
        assertEquals(2, myJsonObject.add(name, true).add(name, 42).size());
    }

    /**
     * Tests a get on a object with multiply occurring keys retrieves the last one.
     */
    @Test
    public void keyRepetition_getsLastEntry() {
        final String name = getNames().next();
        final String value = getValue(name);

        myJsonObject.add(name, true);
        myJsonObject.add(name, value);

        assertEquals(value, myJsonObject.getString(name, "missing"));
    }

    /**
     * Tests that object equality takes into consideration multiply occurring keys.
     */
    @Test
    public void keyRepetition_equalityConsidersRepetitions() {
        final JsonObject onlyFirstProperty = new JsonObject();
        final JsonObject bothProperties = new JsonObject();
        final String name = getNames().next();
        final String value = getValue(name);

        myJsonObject.add(name, true);
        myJsonObject.add(name, value);

        onlyFirstProperty.add(name, true);
        assertNotEquals(onlyFirstProperty, myJsonObject);

        bothProperties.add(name, true);
        bothProperties.add(name, value);
        assertEquals(bothProperties, myJsonObject);
    }

    /**
     * Tests that the list of property names is empty after object creation.
     */
    @Test
    public void names_emptyAfterCreation() {
        assertTrue(myJsonObject.names().isEmpty());
    }

    /**
     * Tests that the list of property names contains the names of newly added properties.
     */
    @Test
    public void names_containsNameAfterAdd() {
        final String name = getNames().next();
        final List<String> names;

        myJsonObject.add(name, true);

        names = myJsonObject.names();
        assertEquals(1, names.size());
        assertEquals(name, names.get(0));
    }

    /**
     * Tests that the list of property names is updated with property updates to the object.
     */
    @Test
    public void names_reflectsChanges() {
        final List<String> names = myJsonObject.names();
        final String name = getNames().next();

        myJsonObject.add(name, true);

        assertEquals(1, names.size());
        assertEquals(name, names.get(0));
    }

    /**
     * Tests that the list of property names cannot be directly edited.
     */
    @Test
    public void names_preventsModification() {
        assertThrows(UnsupportedOperationException.class, () -> myJsonObject.names().add(getNames().next()));
    }

    /**
     * Tests that a property iterator doesn't have any properties when created on an empty object.
     */
    @Test
    public void iterator_isEmptyAfterCreation() {
        assertFalse(myJsonObject.iterator().hasNext());
    }

    /**
     * Tests that a property iterator has a next property if the object it was created from has properties.
     */
    @Test
    public void iterator_hasNextAfterAdd() {
        myJsonObject.add(getNames().next(), true);
        assertTrue(myJsonObject.iterator().hasNext());
    }

    /**
     * Tests that a property iterator does return the expected next property.
     */
    @Test
    public void iterator_nextReturnsActualValue() {
        final String name = getNames().next();
        assertEquals(new Property(name, Boolean.TRUE), myJsonObject.add(name, true).iterator().next());
    }

    /**
     * Tests that a property iterator does progress to the next value during iteration.
     */
    @Test
    public void iterator_nextProgressesToNextValue() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();
        final Iterator<Property> iterator;

        myJsonObject.add(name1, true);
        myJsonObject.add(name2, false);
        iterator = myJsonObject.iterator();

        iterator.next();
        assertTrue(iterator.hasNext());
        assertEquals(new Property(name2, Boolean.FALSE), iterator.next());
    }

    /**
     * Tests that a property iterator throws a NoSuchElementException if <code>next()</code> is called after it's
     * reached the end of its properties.
     */
    @Test
    public void iterator_nextFailsAtEnd() {
        assertThrows(NoSuchElementException.class, () -> myJsonObject.iterator().next());
    }

    /**
     * Tests that a property iterator cannot be modified once it's been created.
     */
    @Test
    public void iterator_doesNotAllowModification() {
        myJsonObject.add(getNames().next(), 23);

        assertThrows(UnsupportedOperationException.class, () -> {
            final Iterator<Property> iterator = myJsonObject.iterator();

            iterator.next();
            iterator.remove();
        });
    }

    /**
     * Tests that the iterator will throw a ConcurrentModificationException if the underlying object is modified while
     * the iterator is still in use.
     */
    @Test
    public void iterator_detectsConcurrentModification() {
        final Iterator<Property> iterator = myJsonObject.iterator();

        assertThrows(ConcurrentModificationException.class, () -> {
            myJsonObject.add(getNames().next(), 23);
            iterator.next();
        });
    }

    /**
     * Tests that <code>get()</code> on an object fails when passed a null.
     */
    @Test
    public void get_failsWithNullName() {
        myLog = assertThrows(NullPointerException.class, () -> myJsonObject.get((String) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_006)));
    }

    /**
     * Tests that an object returns an empty optional for non-existent properties.
     */
    @Test
    public void get_returnsEmptyOptionalForNonExistingMember() {
        assertTrue(myJsonObject.get(getNames().next()).isEmpty());
    }

    /**
     * Tests that an object returns the expected value for a given name.
     */
    @Test
    public void get_returnsValueForName() {
        final String name = getNames().next();
        assertEquals(Boolean.TRUE, myJsonObject.add(name, true).getBoolean(name).orElseThrow());
    }

    /**
     * Tests that an object returns the last value for a multiply occurring property name.
     */
    @Test
    public void get_returnsLastValueForName() {
        final String name = getNames().next();
        assertEquals(Boolean.TRUE, myJsonObject.add(name, false).add(name, true).getBoolean(name).orElseThrow());
    }

    /**
     * Tests that <code>getInt()</code> returns the expected value.
     */
    @Test
    public void get_int_returnsValueFromProperty() {
        assertEquals(23, myJsonObject.add(getNames().next(), 23).getInt(getNames().previous(), 42));
    }

    /**
     * Tests that <code>getInt()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_int_returnsDefaultForMissingProperty() {
        assertEquals(23, myJsonObject.getInt(getNames().next(), 23));
    }

    /**
     * Tests that <code>getLong()</code> returns the expected value.
     */
    @Test
    public void get_long_returnsValueFromProperty() {
        assertEquals(23l, myJsonObject.add(getNames().next(), 23l).getLong(getNames().previous(), 42l));
    }

    /**
     * Tests that <code>getLong()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_long_returnsDefaultForMissingProperty() {
        assertEquals(23l, myJsonObject.getLong(getNames().next(), 23l));
    }

    /**
     * Tests that <code>getFloat()</code> returns the expected value.
     */
    @Test
    public void get_float_returnsValueFromProperty() {
        assertEquals(3.14f, myJsonObject.add(getNames().next(), 3.14f).getFloat(getNames().previous(), 1.41f), 0.0001f);
    }

    /**
     * Tests that <code>getFloat()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_float_returnsDefaultForMissingProperty() {
        assertEquals(3.14f, myJsonObject.getFloat(getNames().next(), 3.14f), 0.0001f);
    }

    /**
     * Tests that <code>getDouble()</code> returns the expected value.
     */
    @Test
    public void get_double_returnsValueFromProperty() {
        assertEquals(3.14, myJsonObject.add(getNames().next(), 3.14).getDouble(getNames().previous(), 1.41), 0.0001d);
    }

    /**
     * Tests that <code>getDouble()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_double_returnsDefaultForMissingProperty() {
        assertEquals(3.14, myJsonObject.getDouble(getNames().next(), 3.14), 0.0001d);
    }

    /**
     * Tests that <code>getBoolean()</code> returns the expected value.
     */
    @Test
    public void get_boolean_returnsValueFromProperty() {
        assertTrue(myJsonObject.add(getNames().next(), true).getBoolean(getNames().previous(), false));
    }

    /**
     * Tests that <code>getBoolean()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_boolean_returnsDefaultForMissingProperty() {
        assertFalse(myJsonObject.getBoolean(getNames().next(), false));
    }

    /**
     * Tests that <code>getString()</code> returns the expected value.
     */
    @Test
    public void get_string_returnsValueFromProperty() {
        final String name = getNames().next();
        assertEquals(getValue(name), myJsonObject.add(name, getValue(name)).getString(name, getNames().next()));
    }

    /**
     * Tests that <code>getString()</code> returns a supplied default value for a missing property.
     */
    @Test
    public void get_string_returnsDefaultForMissingProperty() {
        final String name = getNames().next();
        final String value = getValue(UUID.randomUUID().toString());

        assertEquals(value, myJsonObject.add(name, value).getString(name, getValue(name)));
    }

    /**
     * Tests that <code>add()</code> doesn't accept a null property name.
     */
    @Test
    public void add_failsWithNullName() {
        myLog = assertThrows(NullPointerException.class, () -> myJsonObject.add(null, 23)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_006)));
    }

    /**
     * Tests that <code>addInt()</code> adds an integer property to the object.
     */
    @Test
    public void add_int() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":23}", name), myJsonObject.add(name, 23).toString());
    }

    /**
     * Tests that <code>addLong()</code> adds a long property to the object.
     */
    @Test
    public void add_long() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":24}", name), myJsonObject.add(name, 24l).toString());
    }

    /**
     * Tests that <code>addFloat()</code> adds a float property to the object.
     */
    @Test
    public void add_float() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":3.14}", name), myJsonObject.add(name, 3.14f).toString());
    }

    /**
     * Tests that <code>addDouble()</code> adds a double property to the object.
     */
    @Test
    public void add_double() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":3.15}", name), myJsonObject.add(name, 3.15d).toString());
    }

    /**
     * Tests that <code>addBoolean()</code> adds a boolean property to the object.
     */
    @Test
    public void add_boolean() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":true}", name), myJsonObject.add(name, true).toString());
    }

    /**
     * Tests that <code>addString()</code> adds a String property to the object.
     */
    @Test
    public void add_string() {
        final String name = getNames().next();
        final String value = getValue(name);

        assertEquals(StringUtils.format("{\"{}\":\"{}\"}", name, value), myJsonObject.add(name, value).toString());
    }

    /**
     * Tests that a String property can contain a null value.
     */
    @Test
    public void add_string_toleratesNull() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":null}", name), myJsonObject.add(name, (String) null).toString());
    }

    /**
     * Tests that a JSON null can be added as a property.
     */
    @Test
    public void add_jsonNull() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":null}", name), myJsonObject.add(name, Json.NULL).toString());
    }

    /**
     * Tests adding a JsonArray to the object.
     */
    @Test
    public void add_jsonArray() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":[]}", name), myJsonObject.add(name, new JsonArray()).toString());
    }

    /**
     * Tests adding a JsonObject to the object.
     */
    @Test
    public void add_jsonObject() {
        final String name = getNames().next();
        assertEquals("{\"" + name + "\":{}}", myJsonObject.add(name, new JsonObject()).toString());
    }

    @Test
    public void add_json_failsWithNull() {
        final String name = getNames().next();

        myLog = assertThrows(NullPointerException.class, () -> myJsonObject.add(name, (JsonValue) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_007)));
    }

    @Test
    public void add_json_nestedArray() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":[23]}", name),
                myJsonObject.add(name, new JsonArray().add(23)).toString());
    }

    @Test
    public void add_json_nestedArray_modifiedAfterAdd() {
        final JsonArray innerArray = new JsonArray();
        final String name = getNames().next();

        myJsonObject.add(name, innerArray);
        innerArray.add(24);

        assertEquals(StringUtils.format("{\"{}\":[24]}", name), myJsonObject.toString());
    }

    @Test
    public void add_json_nestedObject() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":{\"{}\":23}}", name, name),
                myJsonObject.add(name, new JsonObject().add(name, 23)).toString());
    }

    @Test
    public void add_json_nestedObject_modifiedAfterAdd() {
        final String name = getNames().next();

        myJsonObject.add(name, new JsonObject().add(name, 24));
        assertEquals(StringUtils.format("{\"{}\":{\"{}\":24}}", name, name), myJsonObject.toString());
    }

    @Test
    public void set_int() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":23}", name), myJsonObject.set(name, 23).toString());
    }

    @Test
    public void set_int_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), 23));
    }

    @Test
    public void set_long() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":24}", name), myJsonObject.set(name, 24l).toString());
    }

    @Test
    public void set_long_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), 23l));
    }

    @Test
    public void set_float() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":3.14}", name), myJsonObject.set(name, 3.14f).toString());
    }

    @Test
    public void set_float_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), 3.14f));
    }

    @Test
    public void set_double() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":3.15}", name), myJsonObject.set(name, 3.15d).toString());
    }

    @Test
    public void set_double_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), 3.14d));
    }

    @Test
    public void set_boolean() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":true}", name), myJsonObject.set(name, true).toString());
    }

    @Test
    public void set_boolean_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), true));
    }

    @Test
    public void set_string() {
        final String name = getNames().next();
        final String value = getValue(name);

        assertEquals(StringUtils.format("{\"{}\":\"{}\"}", name, value), myJsonObject.set(name, value).toString());
    }

    @Test
    public void set_string_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), getValue(getNames().previous())));
    }

    @Test
    public void set_jsonNull() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":null}", name), myJsonObject.set(name, Json.NULL).toString());
    }

    @Test
    public void set_jsonArray() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":[]}", name), myJsonObject.set(name, new JsonArray()).toString());
    }

    @Test
    public void set_jsonObject() {
        final String name = getNames().next();
        assertEquals("{\"" + name + "\":{}}", myJsonObject.set(name, new JsonObject()).toString());
    }

    @Test
    public void set_json_enablesChaining() {
        assertSame(myJsonObject, myJsonObject.set(getNames().next(), Json.NULL));
    }

    @Test
    public void set_addsElementIfMissing() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":true}", name), myJsonObject.set(name, Json.TRUE).toString());
    }

    @Test
    public void set_modifiesElementIfExisting() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":false}", name),
                myJsonObject.add(name, Json.TRUE).set(name, Json.FALSE).toString());
    }

    @Test
    public void set_modifiesLastElementIfMultipleExisting() {
        final String name = getNames().next();
        assertEquals(StringUtils.format("{\"{}\":1,\"{}\":true}", name, name),
                myJsonObject.add(name, 1).add(name, 2).set(name, Json.TRUE).toString());
    }

    @Test
    public void remove_failsWithNullName() {
        myLog = assertThrows(NullPointerException.class, () -> myJsonObject.remove(null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_006)));
    }

    @Test
    public void remove_removesMatchingMember() {
        final String name = getNames().next();
        assertEquals("{}", myJsonObject.add(name, 23).remove(name).toString());
    }

    @Test
    public void remove_removesOnlyMatchingMember() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();
        final String name3 = getNames().next();

        myJsonObject.add(name1, 23).add(name2, 42).add(name3, true).remove(name2);
        assertEquals(StringUtils.format("{\"{}\":23,\"{}\":true}", name1, name3), myJsonObject.toString());
    }

    @Test
    public void remove_removesOnlyLastMatchingMember() {
        final String name = getNames().next();

        myJsonObject.add(name, 23).add(name, 42).remove(name);
        assertEquals(StringUtils.format("{\"{}\":23}", name), myJsonObject.toString());
    }

    @Test
    public void remove_removesOnlyLastMatchingMember_afterRemove() {
        final String name = getNames().next();

        myJsonObject.add(name, 23).remove(name).add(name, 42).add(name, 47).remove(name);
        assertEquals(StringUtils.format("{\"{}\":42}", name), myJsonObject.toString());
    }

    @Test
    public void remove_doesNotModifyObjectWithoutMatchingMember() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertEquals(StringUtils.format("{\"{}\":25}", name1), myJsonObject.add(name1, 25).remove(name2).toString());
    }

    @Test
    public void contains_findsAddedMembers() {
        assertTrue(myJsonObject.add(getNames().next(), 15f).contains(getNames().previous()));
    }

    @Test
    public void contains_doesNotFindAbsentMembers() {
        assertFalse(myJsonObject.contains(getNames().next()));
    }

    @Test
    public void contains_doesNotFindDeletedMembers() {
        final String name = getNames().next();
        assertFalse(myJsonObject.add(name, getValue(name)).remove(name).contains(name));
    }

    @Test
    public void merge_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> myJsonObject.merge(null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_005)));
    }

    @Test
    public void merge_appendsMembers() {
        final NameIterator names = getNames();
        final String name1 = names.next();
        final String name2 = names.next();
        final String name3 = names.next();
        final String name4 = names.next();

        myJsonObject.add(name1, 1).add(name2, 1).merge(Json.object().add(name3, 2).add(name4, 2));
        assertEquals(Json.object().add(name1, 1).add(name2, 1).add(name3, 2).add(name4, 2), myJsonObject);
    }

    @Test
    public void merge_replacesMembers() {
        final NameIterator names = getNames();
        final String name1 = names.next();
        final String name2 = names.next();
        final String name3 = names.next();
        final String name4 = names.next();

        myJsonObject.add(name1, 1).add(name2, 1).add(name3, 1);
        myJsonObject.merge(Json.object().add(name2, 2).add(name4, 2));

        assertEquals(Json.object().add(name1, 1).add(name2, 2).add(name3, 1).add(name4, 2), myJsonObject);
    }

    @Test
    public void isObject() {
        assertTrue(myJsonObject.isObject());
    }

    @Test
    public void asObject() {
        assertSame(myJsonObject, myJsonObject.asObject());
    }

    @Test
    public void equals_trueForSameInstance() {
        assertTrue(myJsonObject.equals(myJsonObject));
    }

    @Test
    public void equals_trueForEqualObjects() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertTrue(object().equals(object()));
        assertTrue(object(name1, "1", name2, "2").equals(object(name1, "1", name2, "2")));
    }

    @Test
    public void equals_falseForDifferentObjects() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertFalse(object(name1, "1").equals(object(name1, "2")));
        assertFalse(object(name1, "1").equals(object(name2, "1")));
        assertFalse(object(name1, "1", name2, "2").equals(object(name2, "2", name1, "1")));
    }

    @Test
    public void equals_falseForNull() {
        assertFalse(new JsonObject().equals(null));
    }

    @Test
    public void hashCode_equalsForEqualObjects() {
        final String name = getNames().next();
        final String value = getValue(name);

        assertTrue(object().hashCode() == object().hashCode());
        assertTrue(object(name, value).hashCode() == object(name, value).hashCode());
    }

    @Test
    public void hashCode_differsForDifferentObjects() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertFalse(object().hashCode() == object(name1, "1").hashCode());
        assertFalse(object(name1, "1").hashCode() == object(name1, "2").hashCode());
        assertFalse(object(name1, "1").hashCode() == object(name2, "1").hashCode());
    }

    @Test
    public void indexOf_returnsNoIndexIfEmpty() {
        assertEquals(-1, myJsonObject.indexOf(getNames().next()));
    }

    @Test
    public void indexOf_returnsIndexOfMember() {
        assertEquals(0, myJsonObject.add(getNames().next(), true).indexOf(getNames().previous()));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember() {
        final String name = getNames().next();

        myJsonObject.add(name, true);
        myJsonObject.add(name, true);

        assertEquals(1, myJsonObject.indexOf(name));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember_afterRemove() {
        final String name = getNames().next();
        assertEquals(0, myJsonObject.add(name, true).add(name, true).remove(name).indexOf(name));
    }

    @Test
    public void indexOf_returnsUpdatedIndexAfterRemove() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        // See issue #16
        myJsonObject.add(name1, true);
        myJsonObject.add(name2, true);
        myJsonObject.remove(name1);

        assertEquals(0, myJsonObject.indexOf(name2));
    }

    @Test
    public void indexOf_returnsIndexOfLastMember_forBigObject() {
        final String name = getNames().next();

        myJsonObject.add(name, true);

        // For indexes above 255, the hash code table does not return a value
        for (int index = 0; index < 256; index++) {
            myJsonObject.add("x-" + index, 0);
        }

        myJsonObject.add(name, true);
        assertEquals(257, myJsonObject.indexOf(name));
    }

    @Test
    public void hashIndexTable_copyConstructor() {
        final HashCodeTable table = new HashCodeTable();
        final String name = getNames().next();
        final HashCodeTable tableCopy;

        table.add(name, 23);
        tableCopy = new HashCodeTable(table);

        assertEquals(23, tableCopy.get(name));
    }

    @Test
    public void hashIndexTable_add() {
        final HashCodeTable indexTable = new HashCodeTable();

        indexTable.add("name-0", 0);
        indexTable.add("name-1", 1);
        indexTable.add("name-fe", 0xfe);
        indexTable.add("name-ff", 0xff);

        assertEquals(0, indexTable.get("name-0"));
        assertEquals(1, indexTable.get("name-1"));
        assertEquals(0xfe, indexTable.get("name-fe"));
        assertEquals(-1, indexTable.get("name-ff"));
    }

    @Test
    public void hashIndexTable_add_overwritesPreviousValue() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name = getNames().next();

        indexTable.add(name, 23);
        indexTable.add(name, 42);

        assertEquals(42, indexTable.get(name));
    }

    @Test
    public void hashIndexTable_add_clearsPreviousValueIfIndexExceeds0xff() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name = getNames().next();

        indexTable.add(name, 23);
        indexTable.add(name, 300);

        assertEquals(-1, indexTable.get(name));
    }

    @Test
    public void hashIndexTable_remove_deletesMapping() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name = getNames().next();

        indexTable.add(name, 23);
        indexTable.remove(23);

        assertEquals(-1, indexTable.get(name));
    }

    @Test
    public void hashIndexTable_remove_deletesMapping_withIndexAbove127() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name = getNames().next();

        indexTable.add(name, 142);
        indexTable.remove(142);

        assertEquals(-1, indexTable.get(name));
    }

    @Test
    public void hashIndexTable_remove_updatesSubsequentElements() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        indexTable.add(name1, 23);
        indexTable.add(name2, 42);
        indexTable.remove(23);

        assertEquals(41, indexTable.get(name2));
    }

    @Test
    public void hashIndexTable_remove_updatesSubsequentElements_withIndexAbove127() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        indexTable.add(name1, 128);
        indexTable.add(name2, 142);
        indexTable.remove(128);

        assertEquals(141, indexTable.get(name2));
    }

    @Test
    public void hashIndexTable_remove_doesNotChangePrecedingElements() {
        final HashCodeTable indexTable = new HashCodeTable();
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        indexTable.add(name1, 23);
        indexTable.add(name2, 128);
        indexTable.remove(128);

        // FIXME: Sometimes this fails (?!)
        assertEquals(23, indexTable.get(name1));
    }

    @Test
    public void member_returnsNameAndValue() {
        final String name = getNames().next();
        final Property property = new Property(name, Boolean.TRUE);

        assertEquals(name, property.getName());
        assertEquals(Boolean.TRUE, property.getBoolean());
    }

    @Test
    public void member_equals_trueForSameInstance() {
        final Property property = new Property(getNames().next(), Boolean.TRUE);
        assertTrue(property.equals(property));
    }

    @Test
    public void member_equals_trueForEqualObjects() {
        final String name = getNames().next();
        final Property property = new Property(name, Boolean.TRUE);

        assertTrue(property.equals(new Property(name, Boolean.TRUE)));
    }

    @Test
    public void member_equals_falseForDifferingObjects() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();
        final Property property = new Property(name1, Boolean.TRUE);

        assertFalse(property.equals(new Property(name2, Boolean.TRUE)));
        assertFalse(property.equals(new Property(name1, Boolean.FALSE)));
    }

    @Test
    public void member_equals_falseForNull() {
        final Property property = new Property(getNames().next(), Boolean.TRUE);
        assertFalse(property.equals(null));
    }

    @Test
    public void member_equals_falseForSubclass() {
        final Property property = new Property(getNames().next(), Boolean.TRUE);
        assertFalse(property.equals(new Property(getNames().previous(), Boolean.TRUE) {}));
    }

    @Test
    public void member_hashCode_equalsForEqualObjects() {
        final Property property = new Property(getNames().next(), Boolean.TRUE);
        assertTrue(property.hashCode() == new Property(getNames().previous(), Boolean.TRUE).hashCode());
    }

    @Test
    public void member_hashCode_differsForDifferingobjects() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();
        final Property property = new Property(name1, Boolean.TRUE);

        assertFalse(property.hashCode() == new Property(name2, Boolean.TRUE).hashCode());
        assertFalse(property.hashCode() == new Property(name1, Boolean.FALSE).hashCode());
    }

    /**
     * Creates a JSON object from the array of name/value pairs.
     *
     * @param aNvArray A name/value array
     * @return A newly created JsonObject
     */
    private static JsonObject object(final String... aNvArray) {
        final JsonObject object = new JsonObject();

        // Our array contains interspersed names and values
        for (int index = 0; index < aNvArray.length; index += 2) {
            object.add(aNvArray[index], aNvArray[index + 1]);
        }

        return object;
    }

}
