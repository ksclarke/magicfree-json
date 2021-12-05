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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import info.freelibrary.util.StringUtils;

public class ParseExceptionTest extends AbstractTestBase {

    private Location myLocation;

    @BeforeEach
    public void setUp() {
        myLocation = new Location(4711, 23, 42, 1);
    }

    @Test
    public void location() {
        assertSame(myLocation, new ParseException(myLocation, getNames().next()).getLocation());
    }

    @Test
    public void position() {
        final ParseException exception = new ParseException(myLocation, getNames().next());

        assertEquals(myLocation.getLine(), exception.getLocation().getLine());
        assertEquals(myLocation.getColumn(), exception.getLocation().getColumn());
        assertEquals(myLocation.getNestingLevel(), exception.getLocation().getNestingLevel());
    }

    @Test
    public void message() {
        final String name = getNames().next();
        final ParseException exception = new ParseException(myLocation, name);

        assertEquals(StringUtils.format("{} at line 23, column 42", name), exception.getMessage());
    }

}
