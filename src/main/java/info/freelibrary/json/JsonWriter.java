// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import info.freelibrary.util.I18nRuntimeException;

/**
 * A bare-bones JSON writer which can be extended to include additional formatting niceties.
 */
public class JsonWriter {

    /** A constant for a backspace character. */
    private static final char[] BS_CHARS = { '\\', '\\' };

    /** A constant for the end of control characters. */
    private static final int CONTROL_CHARACTERS_END = 0x001f;

    /** A constant for a carriage return character. */
    private static final char[] CR_CHARS = { '\\', 'r' };

    /** An array of hex digits. */
    private static final char[] HEX_DIGITS =
            { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /** A constant for a line feed character. */
    private static final char[] LF_CHARS = { '\\', 'n' };

    /** A constant for a quote character. */
    private static final char[] QUOT_CHARS = { '\\', '"' };

    /** A constant for a tab character. */
    private static final char[] TAB_CHARS = { '\\', 't' };

    /**
     * In JavaScript, U+2028 characters count as line endings and must be encoded.
     * http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
     */
    private static final char[] UNICODE_2028_CHARS = { '\\', 'u', '2', '0', '2', '8' };

    /**
     * In JavaScript, U+2029 characters count as line endings and must be encoded.
     * http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
     */
    private static final char[] UNICODE_2029_CHARS = { '\\', 'u', '2', '0', '2', '9' };

    /** Encapsulated writer. */
    protected final Writer myWriter;

    /**
     * Creates a new bare-bones JSON writer.
     *
     * @param aWriter A wrapped writer
     */
    public JsonWriter(final Writer aWriter) {
        myWriter = new WritingBuffer(aWriter, 128);
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        return Objects.equals(myWriter, ((JsonWriter) aObject).myWriter);
    }

    /**
     * Flushes the writer's buffer.
     *
     * @throws IOException If there was an error while flushing the buffer
     */
    public void flush() throws IOException {
        myWriter.flush();
    }

    @Override
    public int hashCode() {
        return Objects.hash(myWriter);
    }

    @Override
    public String toString() {
        try {
            myWriter.flush();
        } catch (final IOException details) {
            throw new I18nRuntimeException(details);
        }

        return myWriter.toString();
    }

    protected void writeArrayClose(final int aSize) throws IOException {
        myWriter.write(']');
    }

    protected void writeArrayOpen(final int aSize) throws IOException {
        myWriter.write('[');
    }

    protected void writeArraySeparator() throws IOException {
        myWriter.write(',');
    }

    protected void writeJsonString(final String aJsonString) throws IOException {
        final int length = aJsonString.length();

        int start = 0;

        for (int index = 0; index < length; index++) {
            final char[] replacement = getReplacementChars(aJsonString.charAt(index));

            if (replacement != null) {
                myWriter.write(aJsonString, start, index - start);
                myWriter.write(replacement);
                start = index + 1;
            }
        }

        myWriter.write(aJsonString, start, length - start);
    }

    protected void writeLiteral(final String aLiteralValue) throws IOException {
        myWriter.write(aLiteralValue);
    }

    protected void writeMemberName(final String aName) throws IOException {
        myWriter.write('"');
        writeJsonString(aName);
        myWriter.write('"');
    }

    protected void writeMemberSeparator() throws IOException {
        myWriter.write(':');
    }

    protected void writeNumber(final String aNumString) throws IOException {
        myWriter.write(aNumString);
    }

    protected void writeObjectClose() throws IOException {
        myWriter.write('}');
    }

    protected void writeObjectOpen() throws IOException {
        myWriter.write('{');
    }

    protected void writeObjectSeparator() throws IOException {
        myWriter.write(',');
    }

    protected void writeString(final String aString) throws IOException {
        myWriter.write('"');
        writeJsonString(aString);
        myWriter.write('"');
    }

    /**
     * Get an encoded form of the supplied character, when necessary.
     *
     * @param aChar A character to replace
     * @return An encoded form of the character
     */
    private static char[] getReplacementChars(final char aChar) {
        if (aChar > '\\') {
            if (aChar < '\u2028' || aChar > '\u2029') {
                // The lower range contains 'a' .. 'z'. Only 2 checks required.
                return null;
            }

            return aChar == '\u2028' ? UNICODE_2028_CHARS : UNICODE_2029_CHARS;
        }

        if (aChar == '\\') {
            return BS_CHARS;
        }

        if (aChar > '"') {
            // This range contains '0' .. '9' and 'A' .. 'Z'. Need 3 checks to get here.
            return null;
        }

        if (aChar == '"') {
            return QUOT_CHARS;
        }

        if (aChar > CONTROL_CHARACTERS_END) {
            return null;
        }

        switch (aChar) {
            case '\n':
                return LF_CHARS;
            case '\r':
                return CR_CHARS;
            case '\t':
                return TAB_CHARS;
            default:
                break;
        }

        return new char[] { '\\', 'u', '0', '0', HEX_DIGITS[aChar >> 4 & 0x000f], HEX_DIGITS[aChar & 0x000f] };
    }
}
