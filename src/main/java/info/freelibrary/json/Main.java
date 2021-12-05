
package info.freelibrary.json;

import java.io.File;

/**
 * A test class.
 */
public class Main {

    /**
     * Runs the main program.
     *
     * @param aArgsArray An array of arguments
     * @throws Exception If there is trouble while running the program
     */
    public static void main(final String[] aArgsArray) throws Exception {
        System.out.println(Json.parse(new JsonReader(new File(aArgsArray[0]))).toString());
    }

}
