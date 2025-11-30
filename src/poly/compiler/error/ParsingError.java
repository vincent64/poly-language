package poly.compiler.error;

import poly.compiler.tokenizer.Token;

/**
 * The ParsingError class. Error classes extending from this class represent
 * code errors that have been detected during the parsing phase.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class ParsingError extends Error {
    private static final int CODE = 2;
    private static final String BASE_MESSAGE = "File %s (on line %s, char. %s) :\n    ";

    public ParsingError(Token token, String message) {
        super(BASE_MESSAGE.formatted(
                token.getMeta().getFileName(),
                token.getMeta().getLine(),
                token.getMeta().getCharacter()) + message,
                CODE);
    }

    public static class UnexpectedToken extends ParsingError {
        private static final String MESSAGE = "Unexpected token '%s'";

        public UnexpectedToken(Token token) {
            super(token, MESSAGE.formatted(token.getContent()));
        }
    }

    public static class UnexpectedEndOfCode extends ParsingError {
        private static final String MESSAGE = "Unexpected end of code";

        public UnexpectedEndOfCode(Token token) {
            super(token, MESSAGE);
        }
    }

    public static class MissingToken extends ParsingError {
        private static final String MESSAGE = "Missing token '%s'";

        public MissingToken(Token token, String symbol) {
            super(token, MESSAGE.formatted(symbol));
        }
    }

    public static class InvalidNumericalValue extends ParsingError {
        private static final String MESSAGE = "Invalid literal numerical value '%s'";

        public InvalidNumericalValue(Token token) {
            super(token, MESSAGE.formatted(token.getContent()));
        }
    }

    public static class InvalidCharacterValue extends ParsingError {
        private static final String MESSAGE = "Invalid literal character value '%s'";

        public InvalidCharacterValue(Token token) {
            super(token, MESSAGE.formatted(token.getContent()));
        }
    }
}
