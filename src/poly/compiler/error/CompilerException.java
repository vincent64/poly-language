package poly.compiler.error;

/**
 * The CompilerException exception. This exception is thrown when an issue that
 * should never happen has been detected.
 * @author Vincent Philippe (@vincent64)
 */
public class CompilerException extends RuntimeException {
    private static final String MESSAGE = "An unexpected exception was thrown while compiling the code." +
            "Please report this issue on GitHub immediatly.";

    public CompilerException() {
        super(MESSAGE);
    }
}
