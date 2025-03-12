package poly.compiler.log;

import poly.compiler.Parameters;

/**
 * The Verbose class. This uninstantiable class is used to print verbose messages
 * during the compilation process. In order for the methods to actually print
 * a message, the verbosity parameter must be enabled.
 * @author Vincent Philippe (@vincent64)
 */
public class Verbose {
    private Verbose() { }

    /**
     * Prints the given verbose message on a new line.
     * @param message the verbose message
     */
    public static void println(String message) {
        if(Parameters.verbosity())
            System.out.println(message);
    }

    /**
     * Prints the given verbose message on the current line.
     * @param message the verbose message
     */
    public static void print(String message) {
        if(Parameters.verbosity())
            System.out.print(message);
    }
}
