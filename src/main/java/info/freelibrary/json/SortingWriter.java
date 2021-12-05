
package info.freelibrary.json;

import java.io.Writer;

/**
 * A JSON writer that gets its {@link JsonObject} and {@link JsonArray} values sorted. The sorting is actually handled
 * in the <code>JsonObject</code> and <code>JsonArray</code>. The identity of this writer is just used as an indicator
 * that the contents need to be sorted before being written.
 */
public class SortingWriter extends PrettyWriter {

    /**
     * Creates a new sorting writer from the supplied external writer.
     *
     * @param aWriter A writer
     */
    public SortingWriter(final Writer aWriter) {
        super(aWriter);
    }

    /**
     * Creates a new sorting writer from the supplied external writer and indentation array
     *
     * @param aWriter A writer
     * @param anIndentArray An indentation character array
     */
    public SortingWriter(final Writer aWriter, final char... anIndentArray) {
        super(aWriter, anIndentArray);
    }

    /**
     * Creates a new sorting writer from the supplied external writer, indentation character and count.
     *
     * @param aWriter A writer
     * @param anIndentChar An indentation character
     * @param anIndentCount The number of characters to indent
     */
    public SortingWriter(final Writer aWriter, final char anIndentChar, final int anIndentCount) {
        super(aWriter, anIndentChar, anIndentCount);
    }

}
