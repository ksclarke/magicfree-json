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

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * A JSON literal.
 */
@SuppressWarnings("serial") // use default serial UID
class JsonLiteral extends JsonValue {

    /**
     * The value of this JSON literal.
     */
    private final String myValue;

    /**
     * Whether this literal is a NULL.
     */
    private final boolean isNull;

    /**
     * Whether this literal is TRUE.
     */
    private final boolean isTrue;

    /**
     * Whether this literal is FALSE.
     */
    private final boolean isFalse;

    /**
     * Creates a new JSON literal from the supplied string value.
     *
     * @param aValue A literal value
     */
    JsonLiteral(final String aValue) {
        Objects.requireNonNull(aValue, "null value not accepted");

        myValue = aValue.toLowerCase(Locale.US);

        isNull = "null".equals(myValue);
        isTrue = "true".equals(myValue);
        isFalse = "false".equals(myValue);
    }

    @Override
    void write(final JsonWriter writer) throws IOException {
        writer.writeLiteral(myValue);
    }

    @Override
    public String toString() {
        return myValue;
    }

    @Override
    public int hashCode() {
        return myValue.hashCode();
    }

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public boolean isTrue() {
        return isTrue;
    }

    @Override
    public boolean isFalse() {
        return isFalse;
    }

    @Override
    public boolean isBoolean() {
        return isTrue || isFalse;
    }

    @Override
    public boolean asBoolean() {
        return isNull ? super.asBoolean() : isTrue;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return myValue.equals(((JsonLiteral) object).myValue);
    }

    @Override
    public boolean equals(final JsonValue aValue, final JsonOptions aConfig) {
        return equals(aValue);
    }
}
