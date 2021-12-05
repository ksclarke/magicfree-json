
package info.freelibrary.json;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * A test of the executable compiled by GraalVM's native-image tool.
 */
class MainExecIT {

    /** The JSON test resource. */
    private static final String TEST_FILE = "src/test/resources/sample-1.json";

    /**
     * Tests the GraalVM compiled test program.
     *
     * @throws Exception If there is trouble running the program
     */
    @Test
    final void test() throws Exception {
        final String expected = Json.parse(new JsonReader(Files.readString(Path.of(TEST_FILE)))).toString();

        final String found = tapSystemOutNormalized(() -> {
            Main.main(new String[] { TEST_FILE });
        }).trim();

        assertEquals(expected, found);
    }

}
