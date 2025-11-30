package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import java.util.HashSet;
import java.util.Set;

/**
 * The Symbol class. This class contains every symbol used in the language.
 * Symbols include separator symbols, such as parenthesis, and access modifier symbols.
 * @author Vincent Philippe (@vincent64)
 */
public class Symbol {
    //Separator symbols
    public static final String OPENING_CURLY_BRACKET = "{";
    public static final String CLOSING_CURLY_BRACKET = "}";
    public static final String OPENING_PARENTHESIS = "(";
    public static final String CLOSING_PARENTHESIS = ")";
    public static final String OPENING_SQUARE_BRACKET = "[";
    public static final String CLOSING_SQUARE_BRACKET = "]";

    //Access modifier symbols
    public static final String PLUS_SIGN = "+";
    public static final String MINUS_SIGN = "-";
    public static final String TILDA_SIGN = "~";

    //Other symbols
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String AROBASE = "@";
    public static final String SHARP = "#";
    public static final String EQUAL = "=";
    public static final String COMMA = ",";
    public static final String DOT = ".";

    private static final Set<String> ACCESS_MODIFIER = new HashSet<>(Set.of(
            PLUS_SIGN, MINUS_SIGN, TILDA_SIGN
    ));

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
    public static boolean isAccessModifierSymbol(String content) {
        return ACCESS_MODIFIER.contains(content);
    }
}
