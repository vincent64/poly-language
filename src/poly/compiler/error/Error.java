package poly.compiler.error;

/**
 * The Error class. This class is used to throw an error message and stop the
 * compiling process when an unexpected issue is detected in the code.
 * For example, if a token that does not match with the code grammar is
 * detected, it will throw an UnexpectedTokenError, which extends from Error.
 * The error will display a message in red in the console, giving information
 * on where the error was found and possible ways to fix it.
 * Furthermore, every kind of error has its own error code.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Error {
    /**
     * Constructs an error with the given message and error code.
     * @param message the error message
     * @param code the error code
     */
    public Error(String message, int code) {
        //Print error message and stop compilation
        System.err.println("[ERROR] " + message);
        System.exit(code);
    }
}
