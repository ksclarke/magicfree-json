// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * A convenience wrapper that makes creating various types of readers easier.
 */
public class JsonReader extends Reader {

    /** A logger for the JSON reader. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonReader.class, MessageCodes.BUNDLE);

    /** A source file's size. */
    private int myFileSize;

    /** An internal reader. */
    private Reader myReader;

    /**
     * Creates a JSON reader for the supplied file.
     *
     * @param aFile A file to be read
     * @throws FileNotFoundException If there is trouble reading from the file
     */
    public JsonReader(final File aFile) throws FileNotFoundException {
        Objects.requireNonNull(aFile, LOGGER.getMessage(MessageCodes.JSON_004));
        myReader = new InputStreamReader(new FileInputStream(aFile), StandardCharsets.UTF_8);
        myFileSize = (int) aFile.length();
    }

    /**
     * Creates a JSON reader for the supplied file path.
     *
     * @param aPath The path to the file to be read
     * @throws FileNotFoundException If there is trouble reading from the file
     */
    public JsonReader(final Path aPath) throws FileNotFoundException {
        this(aPath.toFile());
    }

    /**
     * Creates a JSON reader for the supplied string.
     *
     * @param aString A string with JSON content
     */
    public JsonReader(final String aString) {
        myReader = new StringReader(Objects.requireNonNull(aString, LOGGER.getMessage(MessageCodes.JSON_003)));
    }

    /**
     * Creates a JSON reader for the supplied URL.
     *
     * @param aURL A URL to be read
     * @throws IOException If there is trouble reading from the file
     */
    public JsonReader(final URL aURL) throws IOException {
        try (InputStream inStream = Objects.requireNonNull(aURL, LOGGER.getMessage(MessageCodes.JSON_041)).openStream();
                BufferedReader jsonStream = new BufferedReader(new InputStreamReader(inStream, UTF_8))) {
            final StringBuilder builder = new StringBuilder();

            jsonStream.lines().forEach(line -> {
                builder.append(line);
            });

            myReader = new StringReader(builder.toString());
        }
    }

    @Override
    public void close() throws IOException {
        myReader.close();
    }

    @Override
    public boolean equals(final Object aObject) {
        final JsonReader reader;

        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        reader = (JsonReader) aObject;
        return myFileSize == reader.myFileSize && Objects.equals(myReader, reader.myReader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myFileSize, myReader);
    }

    @Override
    public void mark(final int aReaderIndex) throws IOException {
        if (!myReader.markSupported()) {
            if (myFileSize <= 0) {
                throw new UnsupportedOperationException();
            }

            myReader = new BufferedReader(myReader, myFileSize);
        }

        myReader.mark(aReaderIndex);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        return myReader.read();
    }

    @Override
    public int read(final char[] aBuffer) throws IOException {
        return myReader.read(aBuffer);
    }

    @Override
    public int read(final char[] aBuffer, final int aStart, final int aLength) throws IOException {
        return myReader.read(aBuffer, aStart, aLength);
    }

    @Override
    public int read(final CharBuffer aCharBuffer) throws IOException {
        return myReader.read(aCharBuffer);
    }

    @Override
    public boolean ready() throws IOException {
        return myReader.ready();
    }

    @Override
    public void reset() throws IOException {
        if (!myReader.markSupported()) {
            throw new UnsupportedOperationException(LOGGER.getMessage(MessageCodes.JSON_042));
        }

        myReader.reset();
    }

    @Override
    public long skip(final long aCharCount) throws IOException {
        return myReader.skip(aCharCount);
    }

    @Override
    public long transferTo(final Writer aWriter) throws IOException {
        return myReader.transferTo(aWriter);
    }
}
