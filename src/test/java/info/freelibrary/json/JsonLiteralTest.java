
package info.freelibrary.json;

import static info.freelibrary.json.Json.FALSE;
import static info.freelibrary.json.Json.NULL;
import static info.freelibrary.json.Json.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JsonLiteralTest {

    @Test
    public void isNull() {
        assertTrue(NULL.isNull());

        assertFalse(TRUE.isNull());
        assertFalse(FALSE.isNull());
    }

    @Test
    public void isTrue() {
        assertTrue(TRUE.isTrue());

        assertFalse(NULL.isTrue());
        assertFalse(FALSE.isTrue());
    }

    @Test
    public void isFalse() {
        assertTrue(FALSE.isFalse());

        assertFalse(NULL.isFalse());
        assertFalse(TRUE.isFalse());
    }

    @Test
    public void isBoolean() {
        assertTrue(TRUE.isBoolean());
        assertTrue(FALSE.isBoolean());

        assertFalse(NULL.isBoolean());
    }

    @Test
    public void NULL_toString() {
        assertEquals("null", NULL.toString());
    }

    @Test
    public void TRUE_toString() {
        assertEquals("true", TRUE.toString());
    }

    @Test
    public void FALSE_toString() {
        assertEquals("false", FALSE.toString());
    }

    @Test
    public void NULL_equals() {
        assertTrue(NULL.equals(NULL));

        assertFalse(NULL.equals(null));
        assertFalse(NULL.equals(TRUE));
        assertFalse(NULL.equals(FALSE));
        assertFalse(NULL.equals(Json.value("null")));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void TRUE_equals() {
        assertTrue(TRUE.equals(TRUE));

        assertFalse(TRUE.equals(null));
        assertFalse(TRUE.equals(FALSE));
        assertFalse(TRUE.equals(Boolean.TRUE));
        assertFalse(NULL.equals(Json.value("true")));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void FALSE_equals() {
        assertTrue(FALSE.equals(FALSE));

        assertFalse(FALSE.equals(null));
        assertFalse(FALSE.equals(TRUE));
        assertFalse(FALSE.equals(Boolean.FALSE));
        assertFalse(NULL.equals(Json.value("false")));
    }

}
