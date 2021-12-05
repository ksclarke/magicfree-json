
package info.freelibrary.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;

/**
 * A JSON writer that pretty prints its output.
 */
public class PrettyWriter extends JsonWriter {

    /** A default indentation. */
    private static final char[] DEFAULT_INDENT_CHARS = { ' ', ' ' };

    /** The indentation characters the writer should use. */
    private final char[] myIndentChars;

    /** The current indentation of the writer. */
    private int myIndentCount;

    /**
     * Creates a new pretty printer that indents JSON output with a single character.
     *
     * @param aWriter
     */
    public PrettyWriter(final Writer aWriter) {
        super(aWriter);
        myIndentChars = DEFAULT_INDENT_CHARS;
    }

    /**
     * Creates a new pretty printer with indentation from the supplied character array. If an empty array is supplied,
     * the pretty printer will still add space formatting to the output, but will not indent. The supplied indentation
     * array cannot be null. To use the default settings, use <code>PrettyPrinter</code>'s single argument constructor.
     *
     * @param aWriter An underlying writer
     * @param anIndentArray An indentation array
     */
    public PrettyWriter(final Writer aWriter, final char... anIndentArray) {
        super(aWriter);
        myIndentChars = Objects.requireNonNullElse(anIndentArray, DEFAULT_INDENT_CHARS);
    }

    /**
     * Creates a new pretty printer from the supplied indent character and indentation size.
     *
     * @param aWriter An underlying writer
     * @param aIndentChar An indentation character
     * @param aIndentSize An indentation size
     */
    public PrettyWriter(final Writer aWriter, final char aIndentChar, final int aIndentSize) {
        super(aWriter);
        myIndentChars = new char[aIndentSize];
        Arrays.fill(myIndentChars, aIndentChar);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void writeArrayOpen() throws IOException {
        myIndentCount += 1;
        myWriter.write('[');
        writeNewLine();
    }

    @Override
    protected void writeArrayClose() throws IOException {
        myIndentCount -= 1;
        writeNewLine();
        myWriter.write(']');
    }

    @Override
    protected void writeArraySeparator() throws IOException {
        myWriter.write(',');

        if (!writeNewLine()) {
            myWriter.write(' ');
        }
    }

    @Override
    protected void writeObjectOpen() throws IOException {
        myIndentCount += 1;
        myWriter.write('{');
        writeNewLine();
    }

    @Override
    protected void writeObjectClose() throws IOException {
        myIndentCount -= 1;
        writeNewLine();
        myWriter.write('}');
    }

    @Override
    protected void writeMemberSeparator() throws IOException {
        myWriter.write(':');
        myWriter.write(' ');
    }

    @Override
    protected void writeObjectSeparator() throws IOException {
        myWriter.write(',');

        if (!writeNewLine()) {
            myWriter.write(' ');
        }
    }

    /**
     * Write a new line.
     *
     * @return True if the new line was successfully written
     * @throws IOException If there is trouble writing a new line
     */
    private boolean writeNewLine() throws IOException {
        if (myIndentChars == null || myIndentChars.length == 0) {
            return false;
        }

        myWriter.write('\n');

        for (int index = 0; index < myIndentCount; index++) {
            myWriter.write(myIndentChars);
        }

        return true;
    }
}
