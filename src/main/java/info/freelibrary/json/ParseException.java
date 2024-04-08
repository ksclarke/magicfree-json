/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource.
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

import info.freelibrary.util.I18nRuntimeException;

/**
 * An unchecked exception to indicate that an input does not qualify as valid JSON.
 */
@SuppressWarnings("serial") // use default serial UID
public class ParseException extends I18nRuntimeException {

    /**
     * The myLocation of the exception.
     */
    private final Location myLocation;

    /**
     * Creates a new parse exception.
     *
     * @param aLocation An exception location
     * @param aMessage An exception message
     */
    public ParseException(final Location aLocation, final String aMessage) {
        super(MessageCodes.BUNDLE, aMessage + " at " + aLocation);
        myLocation = aLocation;
    }

    /**
     * Creates a new parse exception.
     *
     * @param aLocation An exception location
     * @param aMessage An exception message
     * @param aDetailsArray Additional details to add to the message
     */
    public ParseException(final Location aLocation, final String aMessage, final Object... aDetailsArray) {
        super(MessageCodes.BUNDLE, aMessage, aDetailsArray);
        myLocation = aLocation;
    }

    /**
     * Returns the myLocation at which the error occurred.
     *
     * @return the error myLocation
     */
    public Location getLocation() {
        return myLocation;
    }

}
