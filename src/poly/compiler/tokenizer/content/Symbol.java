package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import static poly.compiler.util.Character.isSameString;

/**
 * The Symbol class. This class contains every symbol used in the language.
 * Symbols include separator symbols, such as parenthesis, and access modifier symbols.
 * @author Vincent Philippe (@vincent64)
 */
public class Symbol {
    //Separator symbols
    public static final char[] OPENING_CURLY_BRACKET = {'{'};
    public static final char[] CLOSING_CURLY_BRACKET = {'}'};
    public static final char[] OPENING_PARENTHESIS = {'('};
    public static final char[] CLOSING_PARENTHESIS = {')'};
    public static final char[] OPENING_SQUARE_BRACKET = {'['};
    public static final char[] CLOSING_SQUARE_BRACKET = {']'};

    //Access modifier symbols
    public static final char[] PLUS_SIGN = {'+'};
    public static final char[] MINUS_SIGN = {'-'};
    public static final char[] TILDA_SIGN = {'~'};

    //Other symbols
    public static final char[] SEMICOLON = {';'};
    public static final char[] COLON = {':'};
    public static final char[] AROBASE = {'@'};
    public static final char[] SHARP = {'#'};
    public static final char[] EQUAL = {'='};
    public static final char[] COMMA = {','};
    public static final char[] DOT = {'.'};

    private static final char[][] ACCESS_MODIFIER = {
            PLUS_SIGN, MINUS_SIGN, TILDA_SIGN
    };

    /**
     * Returns whether the given token is an access modifier symbol token.
     * @param token the token
     * @return true if the token is an access modifier symbol
     */
    public static boolean isAccessModifierSymbol(Token token) {
        return isAccessModifierSymbol(token.getContent());
    }

    /**
     * Returns whether the given content is an access modifier symbol.
     * @param content the content
     * @return true if the content is an access modifier symbol
     */
    public static boolean isAccessModifierSymbol(char[] content) {
        for(char[] symbol : ACCESS_MODIFIER) {
            if(isSameString(content, symbol)) return true;
        }

        return false;
    }
}
