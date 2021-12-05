
package info.freelibrary.json;

import java.util.Iterator;
import java.util.ListIterator;

import info.freelibrary.util.Logger;
import info.freelibrary.util.LoggerFactory;

import info.freelibrary.json.JsonObject.Property;

/**
 * A utility for making assertions about JsonValue(s).
 */
class ValueUtils {

    /** A utilities logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValueUtils.class, MessageCodes.BUNDLE);

    /**
     * Creates a new assertions utility.
     */
    private ValueUtils() {
        // This is intentionally left empty
    }

    /**
     * Asserts equality between the two supplied ValueUtils, using the supplied JsonOptions as a configuration for the
     * comparison.
     *
     * @param aPropertyName A property name that may be an empty string if it's not known or relevant
     * @param a2stValue A first JSON value
     * @param a2ndValue A second JSON value
     * @param aConfig A configuration to use when comparing the two JSON values
     * @return True if the supplied two JSON values are equal; else, false
     */
    static final boolean equals(final String aPropertyName, final JsonValue a1stValue, final JsonValue a2ndValue,
            final JsonOptions aConfig) {
        if (a1stValue == a2ndValue) {
            return true;
        }

        if (a1stValue == null || a2ndValue == null || a1stValue.getClass() != a2ndValue.getClass()) {
            return false;
        }

        if (a1stValue.isArray() && a2ndValue.isArray()) {
            LOGGER.debug("Comparing two JSON arrays for equality");
            return equals(a1stValue.asArray(), a2ndValue.asArray(), aConfig);
        }

        if (a1stValue.isObject() && a2ndValue.isObject()) {
            LOGGER.debug("Comparing two JSON objects for equality");
            return equals(a1stValue.asObject(), a2ndValue.asObject(), aConfig);
        }

        // If non-object, non-array JSON values, we can do a standard comparison
        LOGGER.debug("Comparing two non-array/non-object simple JSON values");
        return a1stValue.equals(a2ndValue);
    }

    /**
     * Compares two JSON arrays for equality.
     *
     * @param a1stArray A first JSON array
     * @param a2ndArray A second JSON array
     * @param aConfig A equality check configuration
     * @return True if the two arrays are equal; else, false
     */
    private static final boolean equals(final JsonArray a1stArray, final JsonArray a2ndArray,
            final JsonOptions aConfig) {
        final ListIterator<JsonValue> firstIterator;
        final ListIterator<JsonValue> secondIterator;
        final boolean orderIgnored;

        if (a1stArray.size() != a2ndArray.size()) {
            return false;
        }

        orderIgnored = aConfig == null ? false : aConfig.ignoreOrder();
        firstIterator = a1stArray.iterator();
        secondIterator = a2ndArray.iterator();

        if (orderIgnored) {
            LOGGER.debug("Doing order insensitive comparison");

            while (firstIterator.hasNext()) {
                final JsonValue firstValue = firstIterator.next();

                boolean match = false;

                while (secondIterator.hasNext()) {
                    final JsonValue secondValue = secondIterator.next();

                    if (firstValue.isObject() && secondValue.isObject()) {
                        LOGGER.debug("  Checking equality on JsonObject property");

                        if (equals(firstValue.asObject(), secondValue.asObject(), aConfig)) {
                            match = true;
                        }
                    } else if (firstValue.isArray() && secondValue.isArray()) {
                        LOGGER.debug("  Checking equality on JsonArray property");

                        if (equals(firstValue.asArray(), secondValue.asArray(), aConfig)) {
                            match = true;
                        }
                    } else {
                        LOGGER.debug("  Checking equality on JsonValue");

                        if (firstValue.equals(secondValue)) {
                            match = true;
                        }
                    }
                }

                if (!match) {
                    return false;
                }

                // Rewind the iterator
                while (secondIterator.hasPrevious()) {
                    secondIterator.previous();
                }
            }
        } else {
            LOGGER.debug("Doing order sensitive comparison");

            while (firstIterator.hasNext()) {
                final JsonValue firstValue = firstIterator.next();
                final JsonValue secondValue = secondIterator.next();

                if (firstValue.isObject()) {
                    if (!firstValue.asObject().equals(secondValue, aConfig)) {
                        return false;
                    }
                } else if (firstValue.isArray()) {
                    if (!firstValue.asArray().equals(secondValue, aConfig)) {
                        return false;
                    }
                } else if (!firstValue.equals(secondValue)) {
                    return false;
                }
            }
        }

        LOGGER.debug("Returning JsonArray equality check: returning true");
        return true;
    }

    private static final boolean equals(final JsonObject a1stObj, final JsonObject a2ndObj, final JsonOptions aConfig) {
        final Iterator<Property> secondIterator;
        final Iterator<Property> firstIterator;
        final boolean orderIgnored;

        if (a1stObj.size() != a2ndObj.size()) {
            return false;
        }

        orderIgnored = aConfig == null ? false : aConfig.ignoreOrder();

        if (orderIgnored) {
            LOGGER.debug("Doing order insensitive JsonObject comparison");
            secondIterator = a2ndObj.sortedIterator();
            firstIterator = a1stObj.sortedIterator();
        } else {
            LOGGER.debug("Doing order sensitive JsonObject comparison");
            secondIterator = a2ndObj.iterator();
            firstIterator = a1stObj.iterator();
        }

        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            final Property secondProperty = secondIterator.next();
            final Property firstProperty = firstIterator.next();
            final String secondName = secondProperty.getName();
            final String firstName = firstProperty.getName();

            JsonValue secondValue = secondProperty.getValue();
            JsonValue firstValue = firstProperty.getValue();

            LOGGER.debug("Checking next JsonObject property: {}", firstName);

            if (!firstName.equals(secondName)) {
                LOGGER.debug("  Next JsonObject property name didn't match: returning false");
                return false;
            }

            if (firstValue.isObject() && secondValue.isObject()) {
                LOGGER.debug("  Checking equality on JsonObject property");

                if (!firstValue.asObject().equals(secondValue.asObject(), aConfig)) {
                    return false;
                }
            } else if (firstValue.isArray() && secondValue.isArray()) {
                LOGGER.debug("  Checking equality on JsonArray property");

                if (!firstValue.asArray().equals(secondValue.asArray(), aConfig)) {
                    return false;
                }
            } else {
                if (aConfig.isCollapsible(firstName) && (firstValue.isArray() || secondValue.isArray())) {
                    final JsonArray array = firstValue.isArray() ? firstValue.asArray() : secondValue.asArray();

                    LOGGER.debug("  Checking equality on a collapsible JsonArray property");

                    if (array == firstValue) {
                        firstValue = array.get(0);
                    } else {
                        secondValue = array.get(0);
                    }
                }

                LOGGER.debug("  Checking equality on JsonValue");

                if (!firstValue.equals(secondValue, aConfig)) {
                    LOGGER.debug(" JsonValue not equal");
                    return false;
                }

                LOGGER.debug("  JsonValue equal");
            }
        }

        return true;
    }
}
