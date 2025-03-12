package poly.compiler.parser.literal;

import poly.compiler.error.ParsingError;
import poly.compiler.tokenizer.Token;

/**
 * The FloatParser class. This class is used to parse a literal decimal token
 * as an array of characters to a float (or double) value.
 * @author Vincent Philippe (@vincent64)
 */
public class FloatParser {
    private static final char UNDERSCORE = '_';
    private static final int MAX_FLOAT_CHARACTER_LENGTH = 32;
    private static final int MAX_DOUBLE_CHARACTER_LENGTH = 64;
    private static final int DECIMAL_RADIX = 10;
    private final Token token;
    private final char[] content;

    FloatParser(Token token) {
        this.token = token;
        content = token.getContent();
    }

    float parseFloat() {
        float value = 0.0f;

        //Get decimal point index in content
        int decimalPointIndex = getDecimalPointIndex();

        //Parse the integer part
        int integerWeight = 1;
        for(int i = decimalPointIndex - 1, j = 0; i >= 0; i--) {
            //Make sure the integer is not too long
            if(j++ >= MAX_FLOAT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE) {
                value += getDecimalDigit(content[i]) * integerWeight;
                integerWeight *= DECIMAL_RADIX;
            }
        }

        //Parse the fractional part
        float fractionalWeight = 1;
        for(int i = decimalPointIndex + 1, j = 0; i < content.length - (hasFloatSuffix() ? 1 : 0); i++) {
            //Make sure the integer is not too long
            if(j++ >= MAX_DOUBLE_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value += getDecimalDigit(content[i]) / (fractionalWeight *= DECIMAL_RADIX);
        }

        return value;
    }

    double parseDouble() {
        double value = 0.0d;

        //Get decimal point index in content
        int decimalPointIndex = getDecimalPointIndex();


        //Parse the integer part
        int integerWeight = 1;
        for(int i = decimalPointIndex - 1, j = 0; i >= 0; i--) {
            //Make sure the integer is not too long
            if(j++ >= MAX_FLOAT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE) {
                value += getDecimalDigit(content[i]) * integerWeight;
                integerWeight *= DECIMAL_RADIX;
            }
        }

        //Parse the fractional part
        double fractionalWeight = 1;
        for(int i = decimalPointIndex + 1, j = 0; i < content.length - (hasDoubleSuffix() ? 1 : 0); i++) {
            //Make sure the integer is not too long
            if(j++ >= MAX_FLOAT_CHARACTER_LENGTH)
                new ParsingError.InvalidNumericalValue(token);

            //Treat every non-underscore character as digit
            if(content[i] != UNDERSCORE)
                value += getDecimalDigit(content[i]) / (fractionalWeight *= DECIMAL_RADIX);
        }

        return value;
    }

    private int getDecimalPointIndex() {
        for(int i = 0; i < content.length; i++) {
            if(content[i] == '.')
                return i;
        }

        return content.length - 1;
    }

    private int getDecimalDigit(char c) {
        //Make sure the digit is valid
        if(c < 48 || c > 57)
            new ParsingError.InvalidNumericalValue(token);

        return c - '0';
    }

    private boolean hasFloatSuffix() {
        char lastCharacter = content[content.length - 1];

        return lastCharacter == 'f' || lastCharacter == 'F';
    }

    private boolean hasDoubleSuffix() {
        char lastCharacter = content[content.length - 1];

        return lastCharacter == 'd' || lastCharacter == 'D';
    }
}
