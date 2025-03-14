package poly.compiler;

import poly.compiler.error.GeneralError;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * The Parameters class. This class contains the various parameters for the compiler.
 * Every parameter has a default value, and can be changed by the user through the
 * command interface's arguments when starting the compilation process.
 * @author Vincent Philippe (@vincent64)
 */
public class Parameters {
    private static boolean OPTIMIZATIONS = false;
    private static boolean VERBOSITY = false;
    private static boolean WARNINGS = true;
    private static boolean JAR_OUTPUT = false;
    private static String PROJECT_PATH = null;
    private static String SOURCE_PATH = "src";
    private static String OUTPUT_PATH = "out";
    private static String LIBRARY_PATH = null;
    private static String POLYLIB_PATH = null;

    /**
     * Initialize the parameters from the given program arguments.
     * @param arguments the program arguments
     */
    public static void initialize(String[] arguments) {
        //Set first argument as project path
        PROJECT_PATH = arguments[0];

        //Make sure project folder exists
        if(!new File(PROJECT_PATH).exists())
            new GeneralError.InvalidParameterPath(PROJECT_PATH);

        int i = 1;
        while(i < arguments.length) {
            //Get current argument
            String argument = arguments[i++];

            switch(argument) {
                //Initialize options
                case "-verbose" -> VERBOSITY = true;
                case "-warnings" -> WARNINGS = true;
                case "-optimize" -> OPTIMIZATIONS = true;
                case "-jar" -> JAR_OUTPUT = true;

                //Initialize arguments
                case "--src" -> SOURCE_PATH = arguments[i++];
                case "--out" -> OUTPUT_PATH = arguments[i++];
                case "--libs" -> LIBRARY_PATH = arguments[i++];

                //Make sure the argument is valid
                default -> new GeneralError.UnknownArgument(argument);
            }
        }

        //Resolve paths according to project path
        SOURCE_PATH = Path.of(PROJECT_PATH).resolve(SOURCE_PATH).toString();
        OUTPUT_PATH = Path.of(PROJECT_PATH).resolve(OUTPUT_PATH).toString();
        if(LIBRARY_PATH != null) LIBRARY_PATH = Path.of(PROJECT_PATH).resolve(LIBRARY_PATH).toString();

        try {
            //Find working folder
            URL url = Compiler.class.getProtectionDomain().getCodeSource().getLocation();
            File folder = new File(url.toURI()).getParentFile();

            //Find Poly standard library file
            Path path = folder.toPath().resolve("polylib.jar");
            File file = new File(path.toString());

            //Define Poly standard library path
            if(file.exists())
                POLYLIB_PATH = path.toString();
        } catch(URISyntaxException e) {
            throw new RuntimeException("An error occured while attempting to read PDK folder.");
        }
    }

    /**
     * Returns whether the output bytecode should be optimized.
     * @return true if the output bytecode should be optimized
     */
    public static boolean optimizations() {
        return OPTIMIZATIONS;
    }

    /**
     * Returns whether compilation information and timings should be printed
     * in the console during the compilation process.
     * @return true if compilation information should be printed
     */
    public static boolean verbosity() {
        return VERBOSITY;
    }

    /**
     * Returns whether code warnings should be printed during the compilation process.
     * @return true if code warnings should be printed
     */
    public static boolean warnings() {
        return WARNINGS;
    }

    /**
     * Returns whether the compiler should output an executable JAR file.
     * @return true if the compiler should output a JAR file
     */
    public static boolean jarOutput() {
        return JAR_OUTPUT;
    }

    /**
     * Returns the path of the project folder.
     * @return the project folder path
     */
    public static String getProjectPath() {
        return PROJECT_PATH;
    }

    /**
     * Returns the path of the source folder.
     * @return the source folder path
     */
    public static String getSourcePath() {
        return SOURCE_PATH;
    }

    /**
     * Returns the path of the output folder.
     * @return the output folder path
     */
    public static String getOutputPath() {
        return OUTPUT_PATH;
    }

    /**
     * Returns the path of the library folder.
     * @return the libraries folder path
     */
    public static String getLibraryPath() {
        return LIBRARY_PATH;
    }

    /**
     * Returns the path of the Poly standard library file.
     * @return the Poly standard library file path
     */
    public static String getPolylibPath() {
        return POLYLIB_PATH;
    }
}
