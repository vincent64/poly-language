package poly.compiler.tokenizer;

/**
 * The Alphabet class. This class contains static lists of the various
 * primary and secondary characters used to make a token.
 * The primary alphabet is used to detect the start of a token and define its type,
 * while the secondary alphabet is used to accept any non-starting characters
 * that does not satisfy the primary alphabet.
 * Some alphabets don't have any primary or secondary lists, such as the operator characters.
 * @author Vincent Philippe (@vincent64)
 */
public class Alphabet {
    /** The primary string characters alphabet. */
    private static final char[] PRIMARY_STRING_ALPHABET = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '_'
    };

    /** The secondary string characters alphabet. */
    private static final char[] SECONDARY_STRING_ALPHABET = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /** The primary numeric characters alphabet. */
    private static final char[] PRIMARY_NUMERIC_ALPHABET = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /** The secondary numeric characters alphabet. */
    private static final char[] SECONDARY_NUMERIC_ALPHABET = new char[] {
            '.', '_', 'x', 'X', 'b', 'B', 'l', 'L', 'f', 'F', 'd', 'D',
            'a', 'A', 'c', 'C', 'e', 'E', 'f', 'F'
    };

    /** The operator characters alphabet. */
    private static final char[] OPERATOR_ALPHABET = new char[] {
            '=', '>', '<', '!', '+', '-', '*', '/', '%',
            '&', '|', '^', '~', ':', '?'
    };

    /** The empty characters alphabet. */
    private static final char[] EMPTY_ALPHABET = new char[] {
            ' ', '\n', '\r', '\t'
    };

    /**
     * Returns the alphabet type from the given main character.
     * @param character the main character
     * @return the alphabet type
     */
    public static Type getTypeFromMainCharacter(char character) {
        if(Type.STRING.isInPrimaryAlphabet(character)) return Type.STRING;
        if(Type.NUMERIC.isInPrimaryAlphabet(character)) return Type.NUMERIC;
        if(Type.OPERATOR.isInPrimaryAlphabet(character)) return Type.OPERATOR;
        if(Type.EMPTY.isInPrimaryAlphabet(character)) return Type.EMPTY;

        return Type.NULL;
    }

    /**
     * The Alphabet.Type enum. This enum contains every type of alphabet there is,
     * primary and secondary grouped together.
     */
    public enum Type {
        STRING(PRIMARY_STRING_ALPHABET, SECONDARY_STRING_ALPHABET),
        NUMERIC(PRIMARY_NUMERIC_ALPHABET, SECONDARY_NUMERIC_ALPHABET),
        OPERATOR(OPERATOR_ALPHABET, OPERATOR_ALPHABET),
        EMPTY(EMPTY_ALPHABET, EMPTY_ALPHABET),
        NULL(new char[0], new char[0]);

        private final char[] mainAlphabet;
        private final char[] secAlphabet;

        Type(char[] mainAlphabet, char[] secAlphabet) {
            this.mainAlphabet = mainAlphabet;
            this.secAlphabet = secAlphabet;
        }

        /**
         * Returns whether the given character is part of the primary alphabet.
         * @param character the character
         * @return true if the character is in the primary alphabet
         */
        public boolean isInPrimaryAlphabet(char character) {
            for(char mainCharacter : mainAlphabet) {
                if(mainCharacter == character)
                    return true;
            }

            return false;
        }

        /**
         * Returns whether the given character is part of the secondary alphabet.
         * @param character the character
         * @return true if the character is in the secondary alphabet
         */
        public boolean isInSecondaryAlphabet(char character) {
            for(char secCharacter : secAlphabet) {
                if(secCharacter == character)
                    return true;
            }

            return false;
        }
    }
}
