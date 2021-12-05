
package info.freelibrary.json;

import static info.freelibrary.util.Constants.DASH;
import static info.freelibrary.util.Constants.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import info.freelibrary.util.I18nObject;

/**
 * An abstract base class for tests.
 */
class AbstractTestBase extends I18nObject {

    /**
     * A JSON key that's available for use in the tests.
     */
    protected NameIterator myKeys;

    /**
     * Creates a new test class.
     */
    public AbstractTestBase() {
        super(MessageCodes.BUNDLE);
    }

    /**
     * Gets a names/key iterator.
     *
     * @return An iterator of names to be used in testing
     */
    protected NameIterator getNames() {
        if (myKeys == null) {
            myKeys = new NameIterator();
        }

        return myKeys;
    }

    /**
     * Gets the value for the supplied name.
     *
     * @param aName A name for which we want a value
     * @return The value of the supplied name
     */
    protected String getValue(final String aName) {
        return aName.replaceAll(DASH, EMPTY);
    }

    /**
     * Returns a random name iterator.
     */
    protected class NameIterator implements ListIterator<String> {

        /**
         * The internal list of the random name iterator.
         */
        private final List<String> myList = new ArrayList<>();

        /**
         * The current index position of the random name iterator.
         */
        private int myIndex;

        /**
         * Returns whether there is a next name in the iterator. For this particular random name iterator, this method
         * should always return true.
         *
         * @return True if the iterator has another name; else, false
         */
        @Override
        public boolean hasNext() {
            return true;
        }

        /**
         * Gets the next name from the iterator.
         *
         * @return The next name
         * @throws UnsupportedOperationException If the next name cannot be generated
         */
        @Override
        public String next() {
            if (!myList.add(UUID.randomUUID().toString())) {
                throw new UnsupportedOperationException();
            }

            myIndex += 1; // Keep track of where we are

            return myList.get(myList.size() - 1);
        }

        /**
         * Would add the supplied name to the iterator, but this iterator is a read-only iterator so adding to it is not
         * allowed.
         *
         * @param aName A name to add to the name iterator
         * @throws UnsupportedOperationException If a name is attempted to be added to the iterator
         */
        @Override
        public void add(final String aName) {
            throw new UnsupportedOperationException();
        }

        /**
         * Determines if there is a previous name.
         *
         * @return True if there is a previous name; else, false
         */
        @Override
        public boolean hasPrevious() {
            return myIndex > 0;
        }

        /**
         * Gets the next index position.
         *
         * @return The next index
         */
        @Override
        public int nextIndex() {
            return myIndex;
        }

        /**
         * Gets the previous name.
         *
         * @return The previous name
         */
        @Override
        public String previous() {
            return myList.get(previousIndex());
        }

        /**
         * The index position of the previous name.
         *
         * @return The index position of the previous name
         */
        @Override
        public int previousIndex() {
            return myIndex - 1;
        }

        /**
         * Would remove the name from the iterator, but this is not supported by this iterator.
         *
         * @throws UnsupportedOperationException If this method is called
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Would set the previous or next name in the iterator, but this is not supported by this iterator.
         *
         * @param aName to set in the iterator
         * @throws UnsupportedOperationException If this method is called
         */
        @Override
        public void set(final String aName) {
            throw new UnsupportedOperationException();
        }

    }

}
