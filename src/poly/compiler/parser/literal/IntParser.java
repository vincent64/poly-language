package poly.compiler.parser.literal;

import poly.compiler.error.ParsingError;
import poly.compiler.tokenizer.Token;

/**
 * The IntParser class. This class is used to parse a literal integer token
 * as an array of characters to an integer (or long) value.
 * This class handles every type of integer format, including binary (0b) and hexadecimal (0x).
 * @author Vincent Philippe (@vincent64)
 */
public class IntParser {
    private static final char UNDERSCORE = '_';
    private static final int MAX_INT_CHARACTER_LENGTH = 64;
    private static final int MAX_LONG_CHARACTER_LENGTH = 128;
    private static final int DECIMAL_RADIX = 10;
    private final Token token;
    private final char[] content;

    IntParser(Token token) {
        this.token = token;
        content = token.getContent().toCharArray();
    }

    int parseInt() {
        //Check if the value is written in binary or hexadecimal
        if(content.length > 2 && content[0] == '0') {
            if(content[1] == 'b' || content[1] == 'B')
                return parseBinaryInt();
            if(content[1] == 'x' || content[1] == 'X')
                return parseHexInt();
        }

        return parseDecimalInt();
    }

    long parseLong() {
        //Check if the value is written in binary or hexadecimal
        if(content.length > 2 && content[0] == '0') {
            if(content[1] == 'b' || content[1] == 'B')
                return parseBinaryLong();
            if(content[1] == 'x' || content[1] == 'X')
                return parseHexLong();
        }

        return parseDecimalLong();
    }

    private int parseDecimalInt() {
        int value = 0;
        int weight = 1;

        for(int i = content.length - 1, j = 0; i >= 0; i--) {
            //Make sure the integer is not too long
            if(j++ >= MAX_INT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE) {
                value += getDecimalDigit(content[i]) * weight;
                weight *= DECIMAL_RADIX;
            }
        }

        return value;
    }

    private int parseBinaryInt() {
        int value = 0;

        for(int i = content.length - 1, j = 0; i >= 2; i--) {
            //Make sure the integer is not too long
            if(j >= MAX_INT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value |= (getBinDigit(content[i]) << j++);
        }

        return value;
    }

    private int parseHexInt() {
        int value = 0;

        for(int i = content.length - 1, j = 0; i >= 2; i--) {
            //Make sure the integer is not too long
            if(j >= MAX_INT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value |= (getHexDigit(content[i]) << j++ * 4);
        }

        return value;
    }

    private long parseDecimalLong() {
        long value = 0;
        int weight = 1;

        for(int i = content.length - (hasLongSuffix() ? 2 : 1), j = 0; i >= 0; i--) {
            //Make sure the long is not too long
            if(j++ >= MAX_LONG_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE) {
                value += (long) getDecimalDigit(content[i]) * weight;
                weight *= DECIMAL_RADIX;
            }
        }

        return value;
    }

    private long parseBinaryLong() {
        long value = 0;

        for(int i = content.length - (hasLongSuffix() ? 2 : 1), j = 0; i >= 2; i--) {
            //Make sure the long is not too long
            if(j >= MAX_LONG_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value |= ((long) getBinDigit(content[i]) << j++);
        }

        return value;
    }

    private long parseHexLong() {
        long value = 0;

        for(int i = content.length - (hasLongSuffix() ? 2 : 1), j = 0; i >= 2; i--) {
            //Make sure the long is not too long
            if(j >= MAX_LONG_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value |= ((long) getHexDigit(content[i]) << j++ * 4);
        }

        return value;
    }

    private int getDecimalDigit(char c) {
        //Make sure the digit is valid
        if(c < 48 || c > 57)
            new ParsingError.InvalidNumericalValue(token);

        return c - '0';
    }

    private int getBinDigit(char c) {
        //Make sure the digit is valid
        if(c != '0' && c != '1')
            new ParsingError.InvalidNumericalValue(token);

        return c - '0';
    }

    private int getHexDigit(char c) {
        //Make sure the digit is valid
        if((c < 48 || c > 57) && (c < 65 || c > 70) && (c < 97 || c > 102))
            new ParsingError.InvalidNumericalValue(token);

        if(c >= 65 && c <= 70)
            return c - 55;
        if(c >= 97 && c <= 102)
            return c - 87;

        return c - '0';
    }

    private boolean hasLongSuffix() {
        char lastCharacter = content[content.length - 1];

        return lastCharacter == 'l' || lastCharacter == 'L';
    }
}
