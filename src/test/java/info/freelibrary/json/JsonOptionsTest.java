
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests of JsonOptions.
 */
class JsonOptionsTest {

    /**
     * Tests the option's isEmpty() is not empty when ignore order has been set.
     */
    @Test
    final void testIsEmptyIgnoreOrder() {
        assertFalse(new JsonOptions().ignoreOrder(true).isEmpty());
    }

    /**
     * Tests the option's isEmpty().
     */
    @Test
    final void testIsEmpty() {
        assertTrue(new JsonOptions().isEmpty());
    }

    /**
     * Tests the option's isEmpty() when ignoreOrder is set.
     */
    @Test
    final void testIsNotEmptyWithOrder() {
        assertFalse(new JsonOptions().ignoreOrder(true).isEmpty());
    }

    /**
     * Tests the option's isEmpty() when collapsible arrays are set.
     */
    @Test
    final void testIsNotEmptyWithCollapsible() {
        final List<String> list = new ArrayList<>();

        list.add(UUID.randomUUID().toString());
        assertFalse(new JsonOptions().setCollapsibleArrays(list).isEmpty());
    }

    /**
     * Tests <code>toString()</code> with <code>ignoreOrder</code> set.
     */
    @Test
    final void testToStringIgnoreOrderTrue() {
        assertEquals("{\"ignoreOrder\":true}", new JsonOptions().ignoreOrder(true).toString());
    }

    /**
     * Tests <code>toString()</code> with <code>ignoreOrder</code> not set.
     */
    final void testToStringIgnoreOrderFalse() {
        assertEquals("{\"ignoreOrder\":false}", new JsonOptions().toString());
    }

    /**
     * Tests <code>toString()</code> with <code>ignoreOrder</code> and collapsible arrays set.
     */
    @Test
    final void testToStringCollapsibleArray() {
        final JsonOptions opts = new JsonOptions().ignoreOrder(true);
        final List<String> list = new ArrayList<>();

        list.add("asdf");
        opts.setCollapsibleArrays(list);

        assertEquals("{\"ignoreOrder\":true,\"collapsibleArrays\":\"[asdf]\"}", opts.toString());
    }

    /**
     * Tests <code>toString()</code> with <code>ignoreOrder</code> and collapsible arrays set.
     */
    @Test
    final void testToStringCollapsibleArrayWithoutIgnoreOrder() {
        final JsonOptions opts = new JsonOptions().ignoreOrder(false);
        final List<String> list = new ArrayList<>();

        list.add("aaaa");
        opts.setCollapsibleArrays(list);

        assertEquals("{\"ignoreOrder\":false,\"collapsibleArrays\":\"[aaaa]\"}", opts.toString());
    }

    /**
     * Tests setting/getting ignoreOrder to true.
     */
    @Test
    final void testIgnoreOrderTrue() {
        assertTrue(new JsonOptions().ignoreOrder(true).ignoreOrder());
    }

    /**
     * Tests setting/getting ignoreOrder to false.
     */
    @Test
    final void testIgnoreOrderFalse() {
        assertFalse(new JsonOptions().ignoreOrder(false).ignoreOrder());
    }

    /**
     * Tests getting the default ignoreOrder value.
     */
    @Test
    final void testIgnoreOrderDefault() {
        assertFalse(new JsonOptions().ignoreOrder());
    }
}
