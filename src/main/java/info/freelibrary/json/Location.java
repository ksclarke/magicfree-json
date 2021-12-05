/*******************************************************************************
 * Copyright (c) 2016 EclipseSource.
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

import info.freelibrary.util.StringUtils;

/**
 * An immutable object that represents a location in the parsed text.
 */
public class Location {

    /**
     * The absolute character index, starting at 0.
     */
    private final int myOffset;

    /**
     * The line number, starting at 1.
     */
    private final int myLine;

    /**
     * The column number, starting at 1.
     */
    private final int myColumn;

    /**
     * The nesting level of the parsed text.
     */
    private final int myNestingLevel;

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

    /**
     * Gets the offset of this location.
     *
     * @return A parsing offset
     */
    public int getOffset() {
        return myOffset;
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
     * Gets the column of the parsing location.
     *
     * @return A parsing column
     */
    public int getColumn() {
        return myColumn;
    }

    /**
     * Gets the nesting level of the parsing location.
     *
     * @return A parsing level
     */
    public int getNestingLevel() {
        return myNestingLevel;
    }

    @Override
    public String toString() {
        return StringUtils.format("line {}, column {}", myLine, myColumn);
    }

    @Override
    public int hashCode() {
        return myOffset;
    }

    @Override
    public boolean equals(final Object aObject) {
        if (this == aObject) {
            return true;
        }

        if ((aObject == null) || (getClass() != aObject.getClass())) {
            return false;
        }

        final Location location = (Location) aObject;
        return myOffset == location.myOffset && myColumn == location.myColumn && myLine == location.myLine;
    }

}