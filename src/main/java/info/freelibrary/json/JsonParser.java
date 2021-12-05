
package info.freelibrary.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.warnings.JDK;

/**
 * A streaming parser for JSON text. The parser reports all events to a given handler.
 */
public final class JsonParser {

    /** The logger used by the JsonParser. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParser.class, MessageCodes.BUNDLE);

    /** Maximum number of nesting levels we allow. */
    private static final int MAX_NESTING_LEVEL = 1000;

    /** A minimum character buffer size. */
    private static final int MIN_BUFFER_SIZE = 10;

    /** A default character buffer size. */
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /** A map of JSON handlers with the handlers' class names used as keys. */
    private final Deque<JsonHandler<Object, Object>> myHandlers;

    /** The character buffer. */
    private StringBuilder myCaptureBuffer;

    /** A snapshot of the parser's state. */
    private Snapshot mySnapshot;

    /** The reader that the parser is using to read the incoming JSON. */
    private Reader myReader;

    /** The character buffer's offset. */
    private int myBufferOffset;

    /** The JSON document's line offset. */
    private int myLineOffset;

    /** The current size of the character buffer. */
    private int myBufferSize;

    /** The character buffer. */
    private char[] myBuffer;

    /** The parser buffer's index position. */
    private int myIndex;

    /** The parsing buffer's fill index position. */
    private int myFill;

    /** The parser's current line number. */
    private int myLine;

    /** The JSON document's current index position. */
    private int myCurrent;

    /** The character buffer's capture start position. */
    private int myCaptureStart;

    /** The current nesting level of the parser. */
    private int myNestingLevel;

    /** Whether the parsing is finished. */
    private boolean isFinished;

    /** Whether the parser is to be reset at the next opportunity. */
    private boolean isToBeReset;

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
     * Parses the given input string. The input must contain a valid JSON value, optionally padded with whitespace.
     *
     * @param aString the input string, must be valid JSON
     * @throws ParseException if the input is not valid JSON
     */
    public void parse(final String aString) {
        Objects.requireNonNull(aString, "string is null");

        try {
            parse(new StringReader(aString), getBufferSize(aString.length()));
        } catch (final IOException details) {
            throw new RuntimeException(details); // StringReader does not throw IOException
        }
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
     * Changes the handler currently being used by the parser and returns a consumer from that handler.
     *
     * @param aHandler A JSON handler
     */
    @SuppressWarnings(JDK.UNCHECKED)
    public void addHandler(final JsonHandler<?, ?> aHandler) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Updating parser's handler to: {}", getSimpleName(aHandler));
        }

        myHandlers.push((JsonHandler<Object, Object>) aHandler);
        aHandler.setJsonParser(this);
    }

    /**
     * Removes the current handler and returns it.
     *
     * @return The current handler
     */
    public JsonHandler<Object, Object> removeHandler() {
        final JsonHandler<Object, Object> handler = myHandlers.pop();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Updating parser's handler to: {}", getSimpleName(myHandlers.peek()));
        }

        return handler;
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
            LOGGER.trace("Updating parser's handler to: {}", getSimpleName(myHandlers.peek()));
        }

        return myHandlers.peek();
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
            throw new IllegalArgumentException("buffersize is zero or negative");
        }

        myBufferSize = aBufferSize;
        myReader = aReader;

        startParsing();
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
            throw error("Unexpected character");
        }

        if (isToBeReset) {
            resetState();
        }
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
                throw expected("value");
        }
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
            throw error("Nesting too deep");
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
            throw expected("',' or ']'");
        }

        myNestingLevel--;
        currentHandler.endArray(array);
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
            throw error("Nesting too deep");
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
                throw expected("':'");
            }

            skipWhiteSpace();
            myHandlers.peek().startPropertyValue(object, name);
            readValue();
            myHandlers.peek().endPropertyValue(object, name);
            skipWhiteSpace();
        } while (readChar(',') && !isFinished);

        if (!readChar('}') && !isFinished) {
            throw expected("',' or '}'");
        }

        myNestingLevel--;
        myHandlers.peek().endJsonObject(object);
    }

    /**
     * Reads the property name.
     *
     * @return The property name
     * @throws IOException If there is trouble reading the property name
     */
    private String readName() throws IOException {
        if (myCurrent != '"') {
            throw expected("name");
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
     * Reads the next required character.
     *
     * @param aChar A character
     * @throws IOException If there is trouble reading the required character
     */
    private void readRequiredChar(final char aChar) throws IOException {
        if (!readChar(aChar)) {
            throw expected("'" + aChar + "'");
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
                throw expected("valid string character");
            } else {
                read();
            }
        }

        string = endCapture();
        read();

        return string;
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
                        throw expected("hexadecimal digit");
                    }

                    hexChars[index] = (char) myCurrent;
                }

                myCaptureBuffer.append((char) Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected("valid escape sequence");
        }

        read();
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
            throw expected("digit");
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
            throw expected("digit");
        }

        while (readDigit()) {

        }
        return true;
    }

    private boolean readExponent() throws IOException {
        if (!readChar('e') && !readChar('E')) {
            return false;
        }

        if (!readChar('+')) {
            readChar('-');
        }

        if (!readDigit()) {
            throw expected("digit");
        }

        while (readDigit()) {

        }
        return true;
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

    private void skipWhiteSpace() throws IOException {
        while (isWhiteSpace()) {
            read();
        }
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

    private void startCapture() {
        if (myCaptureBuffer == null) {
            myCaptureBuffer = new StringBuilder();
        }

        myCaptureStart = myIndex - 1;
    }

    private void pauseCapture() {
        final int end = myCurrent == -1 ? myIndex : myIndex - 1;

        myCaptureBuffer.append(myBuffer, myCaptureStart, end - myCaptureStart);
        myCaptureStart = -1;
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

    private ParseException expected(final String aExpected) {
        if (isEndOfText()) {
            return error("Unexpected end of input");
        }

        return error("Expected " + aExpected);
    }

    private ParseException error(final String aMessage) {
        return new ParseException(getLocation(), aMessage);
    }

    private boolean isWhiteSpace() {
        return myCurrent == ' ' || myCurrent == '\t' || myCurrent == '\n' || myCurrent == '\r';
    }

    private boolean isDigit() {
        return myCurrent >= '0' && myCurrent <= '9';
    }

    private boolean isHexDigit() {
        return myCurrent >= '0' && myCurrent <= '9' || myCurrent >= 'a' && myCurrent <= 'f' ||
                myCurrent >= 'A' && myCurrent <= 'F';
    }

    private boolean isEndOfText() {
        return myCurrent == -1;
    }

    /**
     * A snapshot of the JSON parser.
     */
    private class Snapshot {

        final char[] mySnapshotBuffer;

        final int mySnapshotBufferOffset;

        final int mySnapshotIndex;

        final int mySnapshotFill;

        final int mySnapshotLine;

        final int mySnapshotLineOffset;

        final int mySnapshotCurrent;

        final int mySnapshotCaptureStart;

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
