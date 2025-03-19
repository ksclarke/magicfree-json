// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.util.Objects;

import info.freelibrary.util.I18nRuntimeException;
import info.freelibrary.util.StringUtils;

/**
 * An unchecked exception to indicate that an input does not qualify as valid JSON.
 */
public class ParseException extends I18nRuntimeException {

    /** The location details string template. */
    private static final String LOCATION = " at line {}, column {}";

    /** The {@code serialVersionUID} for the {@code ParseException} class. */
    private static final long serialVersionUID = 3289678212057322726L;

    /** The myLocation of the exception. */
    private final Location myLocation;

    /**
     * Creates a new parse exception.
     *
     * @param aLocation An exception location
     * @param aMessageCode An exception message code
     */
    public ParseException(final Location aLocation, final String aMessageCode) {
        super(MessageCodes.BUNDLE, aMessageCode);
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

    @Override
    public boolean equals(final Object aObject) {
        final ParseException exception;

        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        exception = (ParseException) aObject;
        return Objects.equals(myLocation, exception.myLocation) &&
                Objects.equals(getMessage(), exception.getMessage()) &&
                Objects.equals(getCause(), exception.getCause());
    }

    /**
     * Returns the myLocation at which the error occurred.
     *
     * @return the error myLocation
     */
    public Location getLocation() {
        return myLocation;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + StringUtils.format(LOCATION, myLocation.getLine(), myLocation.getColumn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(myLocation, getMessage(), getCause());
    }
}
