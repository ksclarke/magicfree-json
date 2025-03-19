// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

/**
 * An immutable object that represents a location in the parsed text.
 */
public class Location {

    /** The logger for the {@code Location} class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Location.class, MessageCodes.BUNDLE);

    /** The column number, starting at 1. */
    private final int myColumn;

    /** The line number, starting at 1. */
    private final int myLine;

    /** The nesting level of the parsed text. */
    private final int myNestingLevel;

    /** The absolute character index, starting at 0. */
    private final int myOffset;

    /**
     * Creates a new parsing location.
     *
     * @param aOffset A parsing offset
     * @param aLine A parsing line
     * @param aColumn A parsing column
     * @param aNestingLevel A parsing level
     */
    Location(final int aOffset, final int aLine, final int aColumn, final int aNestingLevel) {
        myNestingLevel = aNestingLevel;
        myOffset = aOffset;
        myColumn = aColumn;
        myLine = aLine;
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        final Location location = (Location) aObject;
        return myOffset == location.myOffset && myColumn == location.myColumn && myLine == location.myLine;
    }

    /**
     * Gets the column of the parsing location.
     *
     * @return A parsing column
     */
    public int getColumn() {
        return myColumn;
    }

    /**
     * Gets the line of this parsing location.
     *
     * @return A parsing line
     */
    public int getLine() {
        return myLine;
    }

    /**
     * Gets the nesting level of the parsing location.
     *
     * @return A parsing level
     */
    public int getNestingLevel() {
        return myNestingLevel;
    }

    /**
     * Gets the offset of this location.
     *
     * @return A parsing offset
     */
    public int getOffset() {
        return myOffset;
    }

    @Override
    public int hashCode() {
        return myOffset;
    }

    @Override
    public String toString() {
        return LOGGER.getMessage(MessageCodes.JSON_050, myLine, myColumn);
    }

}