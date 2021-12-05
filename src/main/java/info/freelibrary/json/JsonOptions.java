
package info.freelibrary.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the options used when JsonObject(s) or JsonArray(s) are compared.
 */
public class JsonOptions {

    // TODO:
    // * Option to compare values with regex similarity, by property name

    /**
     * Whether the comparison ignores JSON value order.
     */
    private boolean myComparisonIgnoresOrder;

    /**
     * A list of keys that can have singular or array values.
     */
    private List<String> myCollapsibleArrays;

    /**
     * Whether the JSON should be formatted on output.
     */
    private boolean myJsonIsFormatted;

    /**
     * Creates a new JsonOptions.
     */
    public JsonOptions() {
        // This is intentionally left empty
    }

    /**
     * Creates a new JsonOptions from the supplied one.
     *
     * @param aOptions A JsonOptions to clone
     */
    public JsonOptions(final JsonOptions aOptions) {
        myComparisonIgnoresOrder = aOptions.ignoreOrder();
        myCollapsibleArrays = new ArrayList<>(aOptions.getCollapsibleArrays());
        myJsonIsFormatted = aOptions.isFormatted();
    }

    /**
     * Sets whether or not to pretty print the JSON on serialization.
     *
     * @param isFormatted True if the JSON should be formatted; else, false
     * @return The options
     */
    public JsonOptions format(final boolean isFormatted) {
        myJsonIsFormatted = isFormatted;
        return this;
    }

    /**
     * Gets whether the JSON should be formatted on serialization.
     *
     * @return Whether the JSON should be formatted on serialization
     */
    public boolean isFormatted() {
        return myJsonIsFormatted;
    }

    /**
     * Gets whether there are any options set.
     *
     * @return True if not ignoring order and there are no collapsible arrays
     */
    public boolean isEmpty() {
        return !ignoreOrder() && getCollapsibleArrays().isEmpty();
    }

    /**
     * Sets the comparison to ignore or care about JSON value order.
     *
     * @param aOrderAgnosticComparison Whether comparisons should be made independent of order
     * @return The options
     */
    public JsonOptions ignoreOrder(final boolean aOrderAgnosticComparison) {
        myComparisonIgnoresOrder = aOrderAgnosticComparison;
        return this;
    }

    /**
     * Gets whether order should be ignored while comparing JSON values.
     *
     * @return True if order is ignored; else, false
     */
    public boolean ignoreOrder() {
        return myComparisonIgnoresOrder;
    }

    /**
     * Sets a list of arrays that can be collapsed when doing an equality test. The arrays are identified by their keys.
     *
     * @param aListOfKeys A list of keys of arrays that can be collapsed
     * @return The options
     */
    public JsonOptions setCollapsibleArrays(final List<String> aListOfKeys) {
        myCollapsibleArrays = aListOfKeys;
        return this;
    }

    /**
     * Gets the list of array keys representing arrays that can be collapsed during an equality test.
     *
     * @return A list of keys of arrays that can be collapsed
     */
    public List<String> getCollapsibleArrays() {
        if (myCollapsibleArrays == null) {
            myCollapsibleArrays = new ArrayList<>();
        }

        return myCollapsibleArrays;
    }

    /**
     * Gets whether the configuration includes collapsible arrays.
     *
     * @return True if the options contain collapsible arrays; else, false
     */
    public boolean hasCollapsibleArrays() {
        return !getCollapsibleArrays().isEmpty();
    }

    /**
     * Returns whether the supplied property name corresponds to a collapsible array.
     *
     * @param aPropertyName A name to check
     * @return True if the supplied property name represents an array that can be collapsed
     */
    public boolean isCollapsible(final String aPropertyName) {
        return getCollapsibleArrays().contains(aPropertyName);
    }

    @Override
    public String toString() {
        final JsonObject json = new JsonObject().add("ignoreOrder", myComparisonIgnoresOrder);
        final List<String> collapsibleArrays = getCollapsibleArrays();

        if (!collapsibleArrays.isEmpty()) {
            json.add("collapsibleArrays", collapsibleArrays.toString());
        }

        return json.toString();
    }
}
