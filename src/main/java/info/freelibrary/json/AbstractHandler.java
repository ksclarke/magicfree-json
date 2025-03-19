// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;
import info.freelibrary.util.warnings.JDK;

/**
 * An abstract handler that provides additional methods that are not implemented in {@code JsonHandler}.
 */
public abstract class AbstractHandler<O extends Object, L extends List<?>> implements JsonHandler<O, L> {

    /** The abstract handler's logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHandler.class, MessageCodes.BUNDLE);

    /** The nesting level at which the handler was initialized. */
    protected int myNestingLevel;

    /** The parser used by the handler. */
    protected JsonParser myParser;

    /** A JSON array result. */
    private L myList;

    /** A JSON object result. */
    private O myObject;

    @Override
    public boolean equals(final Object aObject) {
        final AbstractHandler<?, ?> handler;

        if (this == aObject) {
            return true;
        }

        if (aObject == null || getClass() != aObject.getClass()) {
            return false;
        }

        handler = (AbstractHandler<?, ?>) aObject;
        return myNestingLevel == handler.myNestingLevel && Objects.equals(myParser, handler.myParser) &&
                Objects.equals(myObject, handler.myObject) && Objects.equals(myList, handler.myList);
    }

    @Override
    public L getIterable() {
        return myList;
    }

    @Override
    public O getObject() {
        return myObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myParser, myNestingLevel, myObject, myList);
    }

    @Override
    public void setIterable(final L aList) {
        myList = aList;
    }

    @Override
    public void setJsonParser(final JsonParser aParser) {
        if (myParser != null) {
            throw new IllegalStateException(LOGGER.getMessage(MessageCodes.JSON_021));
        }

        myParser = aParser;
        myNestingLevel = myParser.getLocation().getNestingLevel();
    }

    @Override
    public void setObject(final O aObject) {
        myObject = aObject;
    }

    /**
     * Casts the handler's list to a list of the supplied class type.
     *
     * @param <T> A type of list to output
     * @param aClass A class to which to cast the list
     * @return A list of the supplied type
     * @throws ClassCastException If not all the list's contents are of the supplied type
     */
    @SuppressWarnings(JDK.UNCHECKED)
    protected <T> List<T> castList(final Class<T> aClass) {
        for (final Object object : myList) {
            if (!aClass.isInstance(object)) {
                throw new ClassCastException();
            }
        }

        return (List<T>) myList;
    }

    /**
     * Casts the handler's object to the supplied class's type.
     *
     * @param <T> The type to which to cast the object to
     * @param aClass A class with the type to cast the object to
     * @return A object cast to the supplied type
     */
    protected <T> T castObject(final Class<T> aClass) {
        return aClass.cast(myObject);
    }

    /**
     * Creates a new strongly-typed list from the hander's list.
     *
     * @param <T> A type of list to output
     * @param aClass A class to which to cast the list
     * @return A list of the supplied type
     */
    protected <T> List<T> copyList(final Class<T> aClass) {
        return myList.stream().map(obj -> aClass.cast(obj)).collect(Collectors.toList());
    }

    /**
     * Gets the simple name of the supplied object.
     *
     * @return The simple name of the supplied object
     */
    protected final String getName(final Object aObject) {
        return aObject.getClass().getSimpleName();
    }
}
