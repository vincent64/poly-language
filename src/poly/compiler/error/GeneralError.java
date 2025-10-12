package poly.compiler.error;

/**
 * The GeneralError class. Error classes extending from this class represent
 * errors linked to the compilation process in general. This includes errors
 * when reading the code files, errors when parsing compiler parameters, etc.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class GeneralError extends Error {
    private final static int CODE = 1;

    public GeneralError(String message) {
        super(message, CODE);
    }

    public static class UnknownArgument extends GeneralError {
        private static final String MESSAGE = "Unknown argument with name '%s'";

        public UnknownArgument(String argumentName) {
            super(MESSAGE.formatted(argumentName));
        }
    }

    public static class InvalidParameterPath extends GeneralError {
        private static final String MESSAGE = "Invalid parameter path '%s'";

        public InvalidParameterPath(String path) {
            super(MESSAGE.formatted(path));
        }
    }

    public static class UnresolvableType extends GeneralError {
        private static final String MESSAGE = "Cannot resolve type '%s'";

        public UnresolvableType(String type) {
            super(MESSAGE.formatted(type));
        }
    }

    public static class InvalidUTF8Encoding extends GeneralError {
        private static final String MESSAGE = "Invalid UTF-8 encoding detected when decoding class file";

        public InvalidUTF8Encoding() {
            super(MESSAGE);
        }
    }
}
