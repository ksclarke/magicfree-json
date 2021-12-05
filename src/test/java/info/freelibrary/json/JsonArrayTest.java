/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonArrayTest extends AbstractTestBase {

    /**
     * The JsonArray being tested by this class' tests.
     */
    private JsonArray array;

    /**
     * Optional logging output from the test.
     */
    private String myLog;

    @BeforeEach
    public void setUp() {
        array = new JsonArray();
    }

    @Test
    public void equalsIgnoreOrder_simpleTest() {
        final String name = getNames().next();
        final JsonArray json1 = new JsonArray().add(42).add(false).add(name);
        final JsonArray json2 = new JsonArray().add(false).add(42).add(name);

        assertTrue(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
    }

    @Test
    public void equalsIgnoreOrder_differentSizes() {
        final JsonArray json1 = new JsonArray().add(42).add(false).add(getNames().next());
        final JsonArray json2 = new JsonArray().add(42).add(false);

        assertFalse(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
    }

    @Test
    public void equalsIgnoreOrder_differentValues() {
        final String name = getNames().next();
        final JsonArray json1 = new JsonArray().add(42).add(false).add(name);
        final JsonArray json2 = new JsonArray().add(44).add(name).add(false);

        assertFalse(json1.equals(json2, new JsonOptions().ignoreOrder(true)));
    }

    @Test
    public void copyConstructor_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> new JsonArray(null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_017)));
    }

    @Test
    public void copyConstructor_hasSameValues() {
        array.add(23);
        final JsonArray copy = new JsonArray(array);

        assertEquals(array.values(), copy.values());
    }

    @Test
    public void copyConstructor_worksOnSafeCopy() {
        final JsonArray copy = new JsonArray(array);
        array.add(23);

        assertTrue(copy.isEmpty());
    }

    @Test
    public void unmodifiableArray_hasSameValues() {
        array.add(23);
        final JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);

        assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test
    public void unmodifiableArray_reflectsChanges() {
        final JsonArray unmodifiableArray = JsonArray.unmodifiableArray(array);
        array.add(23);

        assertEquals(array.values(), unmodifiableArray.values());
    }

    @Test
    public void unmodifiableArray_preventsModification() {
        assertThrows(UnsupportedOperationException.class, () -> JsonArray.unmodifiableArray(array).add(23));
    }

    @Test
    public void isEmpty_isTrueAfterCreation() {
        assertTrue(array.isEmpty());
    }

    @Test
    public void isEmpty_isFalseAfterAdd() {
        assertFalse(array.add(true).isEmpty());
    }

    @Test
    public void size_isZeroAfterCreation() {
        assertEquals(0, array.size());
    }

    @Test
    public void size_isOneAfterAdd() {
        array.add(true);

        assertEquals(1, array.size());
    }

    @Test
    public void iterator_isEmptyAfterCreation() {
        assertFalse(array.iterator().hasNext());
    }

    @Test
    public void iterator_hasNextAfterAdd() {
        final Iterator<JsonValue> iterator;

        array.add(true);

        iterator = array.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(Json.TRUE, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator_doesNotAllowModification() {
        array.add(23);

        assertThrows(UnsupportedOperationException.class, () -> {
            final Iterator<JsonValue> iterator = array.iterator();

            iterator.next();
            iterator.remove();
        });
    }

    @Test
    public void iterator_detectsConcurrentModification() {
        assertThrows(ConcurrentModificationException.class, () -> {
            final Iterator<JsonValue> iterator = array.iterator();

            array.add(23);
            iterator.next();
        });
    }

    @Test
    public void values_isEmptyAfterCreation() {
        assertTrue(array.values().isEmpty());
    }

    @Test
    public void values_containsValueAfterAdd() {
        array.add(true);

        assertEquals(1, array.values().size());
        assertEquals(Json.TRUE, array.values().get(0));
    }

    @Test
    public void values_reflectsChanges() {
        final List<JsonValue> values = array.values();

        array.add(true);
        assertEquals(array.values(), values);
    }

    @Test
    public void values_preventsModification() {
        assertThrows(UnsupportedOperationException.class, () -> array.values().add(Json.TRUE));
    }

    @Test
    public void get_returnsValue() {
        array.add(23);
        assertEquals(Json.value(23), array.get(0));
    }

    @Test
    public void get_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
    }

    @Test
    public void add_int() {
        assertEquals("[23]", array.add(23).toString());
    }

    @Test
    public void add_long() {
        assertEquals("[23]", array.add(23l).toString());
    }

    @Test
    public void add_float() {
        assertEquals("[3.14]", array.add(3.14f).toString());
    }

    @Test
    public void add_double() {
        assertEquals("[3.14]", array.add(3.14d).toString());
    }

    @Test
    public void add_double_enablesChaining() {
        assertSame(array, array.add(3.14d));
    }

    @Test
    public void add_boolean() {
        assertEquals("[true]", array.add(true).toString());
    }

    @Test
    public void add_string() {
        assertEquals("[\"foo\"]", array.add("foo").toString());
    }

    @Test
    public void add_string_enablesChaining() {
        assertSame(array, array.add("foo"));
    }

    @Test
    public void add_string_toleratesNull() {
        assertEquals("[null]", array.add((String) null).toString());
    }

    @Test
    public void add_jsonNull() {
        assertEquals("[null]", array.add(Json.NULL).toString());
    }

    @Test
    public void add_jsonArray() {
        assertEquals("[[]]", array.add(new JsonArray()).toString());
    }

    @Test
    public void add_jsonObject() {
        assertEquals("[{}]", array.add(new JsonObject()).toString());
    }

    @Test
    public void add_json_enablesChaining() {
        assertSame(array, array.add(Json.NULL));
    }

    @Test
    public void add_json_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> array.add((JsonValue) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_007)));
    }

    @Test
    public void add_json_nestedArray() {
        assertEquals("[[23]]", array.add(new JsonArray().add(23)).toString());
    }

    @Test
    public void add_json_nestedArray_modifiedAfterAdd() {
        final JsonArray innerArray = new JsonArray();

        array.add(innerArray);
        innerArray.add(23);

        assertEquals("[[23]]", array.toString());
    }

    @Test
    public void add_json_nestedObject() {
        assertEquals("[{\"a\":23}]", array.add(new JsonObject().add("a", 23)).toString());
    }

    @Test
    public void add_json_nestedObject_modifiedAfterAdd() {
        final JsonObject innerObject = new JsonObject();

        array.add(innerObject);
        innerObject.add("a", 23);
        assertEquals("[{\"a\":23}]", array.toString());
    }

    @Test
    public void set_int() {
        assertEquals("[23]", array.add(false).set(0, 23).toString());
    }

    @Test
    public void set_long() {
        assertEquals("[23]", array.add(false).set(0, 23l).toString());
    }

    @Test
    public void set_float() {
        assertEquals("[3.14]", array.add(false).set(0, 3.14f).toString());
    }

    @Test
    public void set_double() {
        assertEquals("[3.14]", array.add(false).set(0, 3.14d).toString());
    }

    @Test
    public void set_boolean() {
        assertEquals("[true]", array.add(false).set(0, true).toString());
    }

    @Test
    public void set_string() {
        assertEquals("[\"foo\"]", array.add(false).set(0, "foo").toString());
    }

    @Test
    public void set_jsonNull() {
        assertEquals("[null]", array.add(false).set(0, Json.NULL).toString());
    }

    @Test
    public void set_jsonArray() {
        assertEquals("[[]]", array.add(false).set(0, new JsonArray()).toString());
    }

    @Test
    public void set_jsonObject() {
        assertEquals("[{}]", array.add(false).set(0, new JsonObject()).toString());
    }

    @Test
    public void set_json_failsWithNull() {
        myLog = assertThrows(NullPointerException.class, () -> array.add(false).set(0, (JsonValue) null)).getMessage();
        assertTrue(myLog.contains(getI18n(MessageCodes.JSON_007)));
    }

    @Test
    public void set_json_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, Json.NULL));
    }

    @Test
    public void set_json_replacesDifferntArrayElements() {
        assertEquals("[3,4,5]", array.add(3).add(6).add(9).set(1, 4).set(2, 5).toString());
    }

    @Test
    public void remove_failsWithInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
    }

    @Test
    public void remove_removesElement() {
        assertEquals("[]", array.add(23).remove(0).toString());
    }

    @Test
    public void remove_keepsOtherElements() {
        assertEquals("[\"a\",\"c\"]", array.add("a").add("b").add("c").remove(1).toString());
    }

    @Test
    public void isArray() {
        assertTrue(array.isArray());
    }

    @Test
    public void asArray() {
        assertSame(array, array.asArray());
    }

    @Test
    public void equals_trueForSameInstance() {
        assertTrue(array.equals(array));
    }

    @Test
    public void equals_trueForEqualArrays() {
        assertTrue(array().equals(array()));
        assertTrue(array("foo", "bar").equals(array("foo", "bar")));
    }

    @Test
    public void equals_falseForDifferentArrays() {
        assertFalse(array("foo", "bar").equals(array("foo", "bar", "baz")));
        assertFalse(array("foo", "bar").equals(array("bar", "foo")));
    }

    @Test
    public void equals_falseForNull() {
        assertFalse(array.equals(null));
    }

    @Test
    public void hashCode_equalsForEqualArrays() {
        final String name = getNames().next();

        assertTrue(array().hashCode() == array().hashCode());
        assertTrue(array(name).hashCode() == array(name).hashCode());
    }

    @Test
    public void hashCode_differsForDifferentArrays() {
        final String name1 = getNames().next();
        final String name2 = getNames().next();

        assertFalse(array().hashCode() == array(name2).hashCode());
        assertFalse(array(name1).hashCode() == array(name2).hashCode());
    }

    /**
     * Converts an array of String values into a JsonArray.
     *
     * @param aValuesArray An array of string values
     * @return A newly constructed JsonArray
     */
    private static JsonArray array(final String... aValuesArray) {
        final JsonArray array = new JsonArray();

        for (final String value : aValuesArray) {
            array.add(value);
        }

        return array;
    }

}
