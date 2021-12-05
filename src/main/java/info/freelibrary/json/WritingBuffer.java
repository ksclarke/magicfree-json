/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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
import java.io.Writer;

/**
 * A lightweight writing buffer to reduce the amount of write operations to be performed on the underlying writer. This
 * implementation is not thread-safe. It deliberately deviates from the contract of Writer. In particular, it does not
 * flush or close the wrapped writer nor does it ensure that the wrapped writer is open.
 */
class WritingBuffer extends Writer {

    /** An encapsulated <code>java.io</code> writer. */
    private final Writer myWriter;

    /** An internal buffer for the writer. */
    private final char[] myBuffer;

    /** A fill size. */
    private int myFill;

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

    @Override
    public void write(final int aChar) throws IOException {
        if (myFill > myBuffer.length - 1) {
            flush();
        }

        myBuffer[myFill++] = (char) aChar;
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

    /**
     * Flushes the internal buffer but does not flush the wrapped writer.
     */
    @Override
    public void flush() throws IOException {
        myWriter.write(myBuffer, 0, myFill);
        myFill = 0;
    }

    /**
     * Does not close or flush the wrapped writer.
     */
    @Override
    public void close() throws IOException {
        // This is intentionally left empty
    }

    @Override
    public String toString() {
        return myWriter.toString();
    }
}
