// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import info.freelibrary.util.I18nRuntimeException;
import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.warnings.JDK;

/**
 * A streaming parser for JSON text. The parser reports all events to a given handler.
 */
public final class JsonParser {

    /** A default character buffer size. */
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /** The logger used by the JsonParser. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParser.class, MessageCodes.BUNDLE);

    /** Maximum number of nesting levels we allow. */
    private static final int MAX_NESTING_LEVEL = 1000;

    /** A minimum character buffer size. */
    private static final int MIN_BUFFER_SIZE = 10;

    /** Whether the parsing is finished. */
    private boolean isFinished;

    /** Whether the parser is to be reset at the next opportunity. */
    private boolean isToBeReset;

    /** The character buffer. */
    private char[] myBuffer;

    /** The character buffer's offset. */
    private int myBufferOffset;

    /** The current size of the character buffer. */
    private int myBufferSize;

    /** The character buffer. */
    private StringBuilder myCaptureBuffer;

    /** The character buffer's capture start position. */
    private int myCaptureStart;

    /** The JSON document's current index position. */
    private int myCurrent;

    /** The parsing buffer's fill index position. */
    private int myFill;

    /** A map of JSON handlers with the handlers' class names used as keys. */
    private final Deque<JsonHandler<Object, Object>> myHandlers;

    /** The parser buffer's index position. */
    private int myIndex;

    /** The parser's current line number. */
    private int myLine;

    /** The JSON document's line offset. */
    private int myLineOffset;

    /** The current nesting level of the parser. */
    private int myNestingLevel;

    /** The reader that the parser is using to read the incoming JSON. */
    private Reader myReader;

    /** A snapshot of the parser's state. */
    private Snapshot mySnapshot;

    /* ASCII illustration of parsing offset, index, etc.
     *
     * @formatter:off
     *
     * |                      bufferOffset
     *                        v
     * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t]        < input
     *                       [l|m|n|o|p|q|r|s|t|?|?]    < buffer
     *                          ^               ^
     *                       |  index           fill
     *
     * @formatter:on
     */

    /**
     * Creates a new JsonParser with the given handler. The parser will report all parser events to this handler.
     *
     * @param aHandler The handler to process parser events
     */
    @SuppressWarnings(JDK.UNCHECKED)
    public JsonParser(final JsonHandler<?, ?> aHandler) {
        Objects.requireNonNull(aHandler, LOGGER.getMessage(MessageCodes.JSON_019));

        myHandlers = new LinkedList<>();
        aHandler.setJsonParser(this);
        myHandlers.push((JsonHandler<Object, Object>) aHandler);
    }

    /**
     * Changes the handler currently being used by the parser and returns a consumer from that handler.
     *
     * @param aHandler A JSON handler
     */
    @SuppressWarnings(JDK.UNCHECKED)
    public void addHandler(final JsonHandler<?, ?> aHandler) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(MessageCodes.JSON_025, getSimpleName(aHandler));
        }

        myHandlers.push((JsonHandler<Object, Object>) aHandler);
        aHandler.setJsonParser(this);
    }

    /**
     * Gets the parser's current location.
     *
     * @return The parser's location
     */
    public Location getLocation() {
        final int offset = myBufferOffset + myIndex - 1;
        final int column = offset - myLineOffset + 1;
        return new Location(offset, myLine, column, myNestingLevel);
    }

    /**
     * Mark the parsing process so that a reset will return to this location instead of to the beginning of the JSON
     * document.
     *
     * @return This parser
     * @throws IOException If the current parsing position couldn't be marked for future use
     */
    public JsonParser mark() throws IOException {
        mySnapshot = new Snapshot();
        return this;
    }

    /**
     * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
     * optionally padded with whitespace.
     * <p>
     * Characters are read in chunks into a default-sized input buffer. Hence, wrapping a reader in an additional
     * <code>BufferedReader</code> likely won't improve reading performance.
     * </p>
     *
     * @param aReader A reader from which to read the input
     * @throws IOException If an I/O error occurs in the reader
     * @throws ParseException If the input is not valid JSON
     */
    public void parse(final Reader aReader) throws IOException {
        parse(aReader, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Parses the given input string. The input must contain a valid JSON value, optionally padded with whitespace.
     *
     * @param aString the input string, must be valid JSON
     * @throws ParseException if the input is not valid JSON
     */
    public void parse(final String aString) {
        Objects.requireNonNull(aString, LOGGER.getMessage(MessageCodes.JSON_003));

        try {
            parse(new StringReader(aString), getBufferSize(aString.length()));
        } catch (final IOException details) {
            throw new I18nRuntimeException(details); // StringReader does not throw IOException
        }
    }

    /**
     * Removes the current handler and returns it.
     *
     * @return The current handler
     */
    public JsonHandler<Object, Object> removeHandler() {
        final JsonHandler<Object, Object> handler = myHandlers.pop();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(MessageCodes.JSON_025, getSimpleName(myHandlers.peek()));
        }

        return handler;
    }

    /**
     * Resets the parser so that it will start parsing from the beginning of the document again.
     *
     * @return This parser
     */
    public JsonParser reset() {
        isToBeReset = true;
        stop();
        return this;
    }

    /**
     * Removes all the handlers but the root one.
     *
     * @return The root handler
     */
    public JsonHandler<Object, Object> resetHandler() {
        while (myHandlers.size() != 1) {
            myHandlers.pop();
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(MessageCodes.JSON_025, getSimpleName(myHandlers.peek()));
        }

        return myHandlers.peek();
    }

    /**
     * Stops parsing the document, ignoring any yet to be parsed content. What's parsed at the point of calling this is
     * what you have.
     *
     * @return This parser
     */
    public JsonParser stop() {
        isFinished = true;
        return this;
    }

    /**
     * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
     * optionally padded with whitespace.
     * <p>
     * Characters are read in chunks into an input buffer of the given size. Hence, wrapping a reader in an additional
     * <code>BufferedReader</code> likely won't improve reading performance.
     * </p>
     *
     * @param aReader The reader to read the input from
     * @param aBufferSize The size of the input buffer in chars
     * @throws IOException If an I/O error occurs in the reader
     * @throws ParseException If the input is not valid JSON
     */
    void parse(final Reader aReader, final int aBufferSize) throws IOException {
        if (aBufferSize <= 0) {
            throw new IllegalArgumentException(LOGGER.getMessage(MessageCodes.JSON_026));
        }

        myBufferSize = aBufferSize;
        myReader = aReader;

        startParsing();
    }

    private String endCapture() {
        final int start = myCaptureStart;
        final int end = myIndex - 1;

        myCaptureStart = -1;

        if (myCaptureBuffer.length() > 0) {
            final String captured;

            myCaptureBuffer.append(myBuffer, start, end - start);
            captured = myCaptureBuffer.toString();
            myCaptureBuffer.setLength(0);

            return captured;
        }

        return new String(myBuffer, start, end - start);
    }

    private ParseException error(final String aMessageCode) {
        return new ParseException(getLocation(), LOGGER.getMessage(aMessageCode));
    }

    private ParseException expected(final String aMessageCode) {
        if (isEndOfText()) {
            return error(MessageCodes.JSON_027);
        }

        return error(LOGGER.getMessage(aMessageCode));
    }

    /**
     * Gets a parsing buffer size based on the size of the string input.
     *
     * @param aActualSize The actual size of the string to be parsed
     * @return A buffer size
     */
    private int getBufferSize(final int aActualSize) {
        return Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, aActualSize));
    }

    /**
     * Gets the simple name of the supplied handler.
     *
     * @param aHandler A JSON handler
     * @return The simple name of the supplied handler
     */
    private String getSimpleName(final JsonHandler<?, ?> aHandler) {
        return aHandler.getClass().getSimpleName();
    }

    private boolean isDigit() {
        return myCurrent >= '0' && myCurrent <= '9';
    }

    private boolean isEndOfText() {
        return myCurrent == -1;
    }

    private boolean isHexDigit() {
        return myCurrent >= '0' && myCurrent <= '9' || myCurrent >= 'a' && myCurrent <= 'f' ||
                myCurrent >= 'A' && myCurrent <= 'F';
    }

    private boolean isWhiteSpace() {
        return myCurrent == ' ' || myCurrent == '\t' || myCurrent == '\n' || myCurrent == '\r';
    }

    private void pauseCapture() {
        final int end = myCurrent == -1 ? myIndex : myIndex - 1;

        myCaptureBuffer.append(myBuffer, myCaptureStart, end - myCaptureStart);
        myCaptureStart = -1;
    }

    private void read() throws IOException {
        if (myIndex == myFill) {
            if (myCaptureStart != -1) {
                myCaptureBuffer.append(myBuffer, myCaptureStart, myFill - myCaptureStart);
                myCaptureStart = 0;
            }

            myBufferOffset += myFill;
            myFill = myReader.read(myBuffer, 0, myBuffer.length);
            myIndex = 0;

            if (myFill == -1) {
                myCurrent = -1;
                myIndex++;

                return;
            }
        }

        if (myCurrent == '\n') {
            myLine++;
            myLineOffset = myBufferOffset + myIndex;
        }

        myCurrent = myBuffer[myIndex++];
    }

    /**
     * Reads an array from the incoming JSON stream.
     *
     * @throws IOException If there is trouble reading from the JSON stream
     */
    private void readArray() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();
        final Object array = currentHandler.startArray();

        read();

        if (++myNestingLevel > MAX_NESTING_LEVEL) {
            throw error(LOGGER.getMessage(MessageCodes.JSON_030));
        }

        skipWhiteSpace();

        if (readChar(']')) {
            myNestingLevel--;
            currentHandler.endArray(array);

            return;
        }

        do {
            skipWhiteSpace();
            currentHandler.startArrayValue(array);
            readValue();
            currentHandler.endArrayValue(array);
            skipWhiteSpace();
        } while (readChar(',') && !isFinished);

        if (!readChar(']') && !isFinished) {
            throw expected(MessageCodes.JSON_029);
        }

        myNestingLevel--;
        currentHandler.endArray(array);
    }

    private boolean readChar(final char aChar) throws IOException {
        if (myCurrent != aChar) {
            return false;
        }

        read();
        return true;
    }

    private boolean readDigit() throws IOException {
        if (!isDigit()) {
            return false;
        }

        read();
        return true;
    }

    /**
     * Reads an escape character.
     *
     * @throws IOException If there is trouble reading an escape character
     */
    private void readEscape() throws IOException {
        read();

        switch (myCurrent) {
            case '"':
            case '/':
            case '\\':
                myCaptureBuffer.append((char) myCurrent);
                break;
            case 'b':
                myCaptureBuffer.append('\b');
                break;
            case 'f':
                myCaptureBuffer.append('\f');
                break;
            case 'n':
                myCaptureBuffer.append('\n');
                break;
            case 'r':
                myCaptureBuffer.append('\r');
                break;
            case 't':
                myCaptureBuffer.append('\t');
                break;
            case 'u':
                final char[] hexChars = new char[4];

                for (int index = 0; index < 4; index++) {
                    read();

                    if (!isHexDigit()) {
                        throw expected(MessageCodes.JSON_032);
                    }

                    hexChars[index] = (char) myCurrent;
                }

                myCaptureBuffer.append((char) Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected(MessageCodes.JSON_031);
        }

        read();
    }

    /**
     * Reads an exponent.
     *
     * @return True if exponent was read; else, false
     * @throws IOException If there is trouble reading from the stream
     */
    private boolean readExponent() throws IOException {
        if (!readChar('e') && !readChar('E')) {
            return false;
        }

        if (!readChar('+')) {
            readChar('-');
        }

        if (!readDigit()) {
            throw expected(MessageCodes.JSON_039);
        }

        while (readDigit()) {
            // This is intentionally left empty
        }

        return true;
    }

    /**
     * Reads a boolean FALSE.
     *
     * @throws IOException If there is trouble reading the boolean FALSE
     */
    private void readFalse() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();

        currentHandler.startBoolean();
        read();
        readRequiredChar('a');
        readRequiredChar('l');
        readRequiredChar('s');
        readRequiredChar('e');
        currentHandler.endBoolean(false);
    }

    /**
     * Reads a numeric fraction.
     *
     * @return True if a fraction was read; else, false
     * @throws IOException If there is trouble reading a fraction
     */
    private boolean readFraction() throws IOException {
        if (!readChar('.')) {
            return false;
        }

        if (!readDigit()) {
            throw expected(MessageCodes.JSON_039);
        }

        while (readDigit()) {
            // This is intentionally left empty
        }

        return true;
    }

    /**
     * Reads the property name.
     *
     * @return The property name
     * @throws IOException If there is trouble reading the property name
     */
    private String readName() throws IOException {
        if (myCurrent != '"') {
            throw expected(MessageCodes.JSON_040);
        }

        return readStringInternal();
    }

    /**
     * Reads a JSON Null.
     *
     * @throws IOException If there is trouble reading the JSON Null
     */
    private void readNull() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();

        currentHandler.startNull();
        read();
        readRequiredChar('u');
        readRequiredChar('l');
        readRequiredChar('l');
        currentHandler.endNull();
    }

    /**
     * Reads a number.
     *
     * @throws IOException If there is trouble reading a number
     */
    private void readNumber() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();
        final int firstDigit;

        currentHandler.startNumber();
        startCapture();
        readChar('-');
        firstDigit = myCurrent;

        if (!readDigit()) {
            throw expected(MessageCodes.JSON_039);
        }

        if (firstDigit != '0') {
            while (readDigit()) {
            }
        }

        readFraction();
        readExponent();
        currentHandler.endNumber(endCapture());
    }

    /**
     * Reads a JSON object from the stream.
     *
     * @throws IOException If there is trouble reading from the stream
     */
    private void readObject() throws IOException {
        final Object object = myHandlers.peek().startJsonObject();

        read();

        if (++myNestingLevel > MAX_NESTING_LEVEL) {
            throw error(LOGGER.getMessage(MessageCodes.JSON_030));
        }

        skipWhiteSpace();

        if (readChar('}')) {
            myNestingLevel--;
            myHandlers.peek().endJsonObject(object);

            return;
        }

        do {
            final String name;

            skipWhiteSpace();
            myHandlers.peek().startPropertyName(object);
            name = readName();
            myHandlers.peek().endPropertyName(object, name);
            skipWhiteSpace();

            if (!readChar(':')) {
                throw expected(MessageCodes.JSON_038);
            }

            skipWhiteSpace();
            myHandlers.peek().startPropertyValue(object, name);
            readValue();
            myHandlers.peek().endPropertyValue(object, name);
            skipWhiteSpace();
        } while (readChar(',') && !isFinished);

        if (!readChar('}') && !isFinished) {
            throw expected(MessageCodes.JSON_037);
        }

        myNestingLevel--;
        myHandlers.peek().endJsonObject(object);
    }

    /**
     * Reads the next required character.
     *
     * @param aChar A character
     * @throws IOException If there is trouble reading the required character
     */
    private void readRequiredChar(final char aChar) throws IOException {
        if (!readChar(aChar)) {
            // Strings that aren't message codes, just get written as is
            throw expected(LOGGER.getMessage(MessageCodes.JSON_036, aChar));
        }
    }

    /**
     * Reads a string.
     *
     * @throws IOException If there is trouble reading a string
     */
    private void readString() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();

        currentHandler.startString();
        currentHandler.endString(readStringInternal());
    }

    private String readStringInternal() throws IOException {
        final String string;

        read();
        startCapture();

        while (myCurrent != '"') {
            if (myCurrent == '\\') {
                pauseCapture();
                readEscape();
                startCapture();
            } else if (myCurrent < 0x20) {
                throw expected(MessageCodes.JSON_033);
            } else {
                read();
            }
        }

        string = endCapture();
        read();

        return string;
    }

    /**
     * Reads a boolean TRUE.
     *
     * @throws IOException If there is trouble reading the boolean TRUE
     */
    private void readTrue() throws IOException {
        final JsonHandler<Object, Object> currentHandler = myHandlers.peek();

        currentHandler.startBoolean();
        read();
        readRequiredChar('r');
        readRequiredChar('u');
        readRequiredChar('e');
        currentHandler.endBoolean(true);
    }

    /**
     * Reads a JSON value from the incoming JSON stream.
     *
     * @throws IOException If there is trouble reading from the JSON stream
     */
    private void readValue() throws IOException {
        switch (myCurrent) {
            case 'n':
                readNull();
                break;
            case 't':
                readTrue();
                break;
            case 'f':
                readFalse();
                break;
            case '"':
                readString();
                break;
            case '[':
                readArray();
                break;
            case '{':
                readObject();
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                readNumber();
                break;
            default:
                throw expected(MessageCodes.JSON_035);
        }
    }

    /**
     * Resets the parser so that it can start parsing the same JSON document with a different handler.
     */
    private void resetState() {
        isToBeReset = false;
        isFinished = false;

        try {
            myReader.reset();
            startParsing();
        } catch (final IOException details) {
            throw new ParseException(getLocation(), details.getMessage());
        }
    }

    private void skipWhiteSpace() throws IOException {
        while (isWhiteSpace()) {
            read();
        }
    }

    private void startCapture() {
        if (myCaptureBuffer == null) {
            myCaptureBuffer = new StringBuilder();
        }

        myCaptureStart = myIndex - 1;
    }

    /**
     * Starts parsing the JSON document.
     *
     * @throws IOException If there is trouble reading the JSON document
     */
    private void startParsing() throws IOException {
        if (mySnapshot != null) {
            mySnapshot.restore();
        } else {
            myBuffer = new char[myBufferSize];
            myBufferOffset = 0;
            myIndex = 0;
            myFill = 0;
            myLine = 1;
            myLineOffset = 0;
            myCurrent = 0;
            myCaptureStart = -1;
        }

        read();
        skipWhiteSpace();
        readValue();
        skipWhiteSpace();

        if (!isFinished && !isEndOfText()) {
            throw error(MessageCodes.JSON_034);
        }

        if (isToBeReset) {
            resetState();
        }
    }

    /**
     * A snapshot of the JSON parser.
     */
    private class Snapshot {

        /** A snapshot of the parser's buffer. */
        final char[] mySnapshotBuffer;

        /** A snapshot of the parser's buffer offset. */
        final int mySnapshotBufferOffset;

        /** A snapshot of the parser's start of capture. */
        final int mySnapshotCaptureStart;

        /** A snapshot of the parser's current character. */
        final int mySnapshotCurrent;

        /** A snapshot of the parser's fill. */
        final int mySnapshotFill;

        /** A snapshot of the parser's index position. */
        final int mySnapshotIndex;

        /** A snapshot of the parser's current line. */
        final int mySnapshotLine;

        /** A snapshot of the parser's current line offset. */
        final int mySnapshotLineOffset;

        /**
         * Creates a new snapshot of the JSON parser.
         */
        private Snapshot() throws IOException {
            myReader.mark(getLocation().getOffset());

            mySnapshotBuffer = myBuffer;
            mySnapshotBufferOffset = myBufferOffset;
            mySnapshotIndex = myIndex;
            mySnapshotFill = myFill;
            mySnapshotLine = myLine;
            mySnapshotLineOffset = myLineOffset;
            mySnapshotCurrent = myCurrent;
            mySnapshotCaptureStart = myCaptureStart;
        }

        /**
         * Restores the JSON parser to the state at which the snapshot was taken.
         */
        private void restore() {
            myBuffer = mySnapshotBuffer;
            myBufferOffset = mySnapshotBufferOffset;
            myIndex = mySnapshotIndex;
            myFill = mySnapshotFill;
            myLine = mySnapshotLine;
            myLineOffset = mySnapshotLineOffset;
            myCurrent = mySnapshotCurrent;
            myCaptureStart = mySnapshotCaptureStart;
        }
    }
}
