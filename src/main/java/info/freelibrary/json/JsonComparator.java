
package info.freelibrary.json;

import java.util.Comparator;

/**
 * A comparator that can compare JSON values.
 */
public class JsonComparator implements Comparator<JsonValue> {

    /** A configuration for handling JSON comparisons. */
    private final JsonOptions myConfig;

    /**
     * Creates a new JSON comparator with the default configuration.
     */
    public JsonComparator() {
        myConfig = new JsonOptions();
    }

    /**
     * Creates a new JSON comparator from the supplied configuration.
     *
     * @param aConfig A JSON comparator configuration
     */
    public JsonComparator(final JsonOptions aConfig) {
        myConfig = aConfig;
    }

    @Override
    public int compare(final JsonValue a1stValue, final JsonValue a2ndValue) {
        if (a1stValue == a2ndValue) {
            return 0;
        }

        if (a1stValue instanceof JsonLiteral && a2ndValue instanceof JsonLiteral) {
            return compare((JsonLiteral) a1stValue, (JsonLiteral) a2ndValue);
        }

        if (a1stValue instanceof JsonNumber && a2ndValue instanceof JsonNumber) {
            return compare((JsonNumber) a1stValue, (JsonNumber) a2ndValue);
        }

        if (a1stValue instanceof JsonString && a2ndValue instanceof JsonString) {
            return a1stValue.toString().compareTo(a2ndValue.toString());
        }

        // sort json object by keys

        // sort json array by values
        if (a1stValue instanceof JsonArray && a2ndValue instanceof JsonArray) {

        }

        return 0;
    }

    /**
     * Compare two JSON numbers.
     *
     * @param a1stNumber A first JSON number
     * @param a2ndNumber A second JSON number
     * @return An integer result of the comparison
     */
    private int compare(final JsonNumber a1stNumber, final JsonNumber a2ndNumber) {
        return a1stNumber == a2ndNumber ? 0
                : Float.valueOf(a1stNumber.toString()).compareTo(Float.valueOf(a2ndNumber.toString()));
    }

    private int compare(final JsonLiteral a1stLiteral, final JsonLiteral a2ndLiteral) {
        if (a1stLiteral.isNull() && a2ndLiteral.isNull() || a1stLiteral.isTrue() && a2ndLiteral.isTrue() ||
                a1stLiteral.isFalse() && a2ndLiteral.isFalse()) {
            return 0;
        }

        return a1stLiteral.toString().compareTo(a2ndLiteral.toString());
    }
}
