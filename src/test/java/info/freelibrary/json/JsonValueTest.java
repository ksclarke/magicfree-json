
package info.freelibrary.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonValueTest extends AbstractTestBase {

    /** Output from a test. */
    private String myOutput;

    @Test
    @SuppressWarnings("serial")
    public void equalsNotImplementedTest() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            new JsonValue() {

                @Override
                void write(final JsonWriter aWriter) throws IOException {
                    // This is intentionally left empty
                }
            }.equals(null);
        });
    }

    @Test
    public void writeTo() throws IOException {
        final JsonWriter writer = new JsonWriter(new StringWriter());
        new JsonObject().writeTo(writer);
        assertEquals("{}", writer.toString());
    }

    @Test
    public void asObject_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asObject()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_011)));
    }

    @Test
    public void asArray_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asArray()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_012)));
    }

    @Test
    public void asString_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asString()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_013)));
    }

    @Test
    public void asInt_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asInt()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_014)));
    }

    @Test
    public void asLong_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asLong()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_014)));
    }

    @Test
    public void asFloat_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asFloat()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_014)));
    }

    @Test
    public void asDouble_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asDouble()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_014)));
    }

    @Test
    public void asBoolean_failsOnIncompatibleType() {
        myOutput = assertThrows(UnsupportedOperationException.class, () -> Json.NULL.asBoolean()).getMessage();
        assertTrue(myOutput.contains(getI18n(MessageCodes.JSON_015)));
    }

    @Test
    public void isXxx_returnsFalseForIncompatibleType() {
        final JsonValue jsonValue = new JsonValue() {

            /**
             * The <code>serialVersionUID</code> for this JsonValue subclass.
             */
            private static final long serialVersionUID = 1L;

            @Override
            void write(final JsonWriter writer) throws IOException {
            }
        };

        assertFalse(jsonValue.isArray());
        assertFalse(jsonValue.isObject());
        assertFalse(jsonValue.isString());
        assertFalse(jsonValue.isNumber());
        assertFalse(jsonValue.isBoolean());
        assertFalse(jsonValue.isNull());
        assertFalse(jsonValue.isTrue());
        assertFalse(jsonValue.isFalse());
    }

}
