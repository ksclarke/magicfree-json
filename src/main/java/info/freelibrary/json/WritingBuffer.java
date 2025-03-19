// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;

/**
 * A lightweight writing buffer to reduce the amount of write operations to be performed on the underlying writer. This
 * implementation is not thread-safe. It deliberately deviates from the contract of Writer. In particular, it does not
 * flush or close the wrapped writer nor does it ensure that the wrapped writer is open.
 */
class WritingBuffer extends Writer {

    /** An internal buffer for the writer. */
    private final char[] myBuffer;

    /** A fill size. */
    private int myFill;

    /** An encapsulated <code>java.io</code> writer. */
    private final Writer myWriter;

    /**
     * Creates a new writing buffer.
     *
     * @param aWriter A writer
     */
    WritingBuffer(final Writer aWriter) {
        this(aWriter, 16);
    }

    /**
     * Creates a new writing buffer.
     *
     * @param aWriter A writer
     * @param aBufferSize A writing buffer size
     */
    WritingBuffer(final Writer aWriter, final int aBufferSize) {
        myBuffer = new char[aBufferSize];
        myWriter = aWriter;
    }

    /**
     * Does not close or flush the wrapped writer.
     */
    @Override
    public void close() throws IOException {
        // This is intentionally left empty
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }
        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }
        final WritingBuffer buffer = (WritingBuffer) aObject;
        return myFill == buffer.myFill && Objects.equals(myWriter, buffer.myWriter) &&
                Arrays.equals(myBuffer, buffer.myBuffer);
    }

    /**
     * Flushes the internal buffer but does not flush the wrapped writer.
     */
    @Override
    public void flush() throws IOException {
        myWriter.write(myBuffer, 0, myFill);
        myFill = 0;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(myWriter, myFill) + Arrays.hashCode(myBuffer);
    }

    @Override
    public String toString() {
        return myWriter.toString();
    }

    @Override
    public void write(final char[] aCharBuffer, final int aOffset, final int aLength) throws IOException {
        if (myFill > myBuffer.length - aLength) {
            flush();

            if (aLength > myBuffer.length) {
                myWriter.write(aCharBuffer, aOffset, aLength);
                return;
            }
        }

        System.arraycopy(aCharBuffer, aOffset, myBuffer, myFill, aLength);
        myFill += aLength;
    }

    @Override
    public void write(final int aChar) throws IOException {
        if (myFill > myBuffer.length - 1) {
            flush();
        }

        myBuffer[myFill++] = (char) aChar;
    }

    @Override
    public void write(final String aString, final int aOffset, final int aLength) throws IOException {
        if (myFill > myBuffer.length - aLength) {
            flush();

            if (aLength > myBuffer.length) {
                myWriter.write(aString, aOffset, aLength);
                return;
            }
        }

        aString.getChars(aOffset, aOffset + aLength, myBuffer, myFill);
        myFill += aLength;
    }
}
