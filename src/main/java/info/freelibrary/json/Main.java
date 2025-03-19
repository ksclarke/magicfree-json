// License info: https://github.com/ksclarke/magicfree-json#licenses

package info.freelibrary.json;

import java.io.File;
import java.io.IOException;

/**
 * A test class.
 */
public class Main {

    /**
     * Runs the main program.
     *
     * @param aArgsArray An array of arguments
     * @throws IOException If the program has trouble reading or writing JSON
     */
    public static void main(final String[] aArgsArray) throws IOException {
        System.out.println(Json.parse(new JsonReader(new File(aArgsArray[0]))).toString());
    }

}
