package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import static poly.compiler.util.Character.isSameString;

/**
 * The Attribute class. This class contains the 4 different primitive attributes.
 * It also contains utility methods to check if a given string is an attribute.
 * @author Vincent Philippe (@vincent64)
 */
public class Attribute {
    public static final char[] BITS = {'b', 'i', 't', 's'};
    public static final char[] BYTES = {'b', 'y', 't', 'e', 's'};
    public static final char[] MINIMUM = {'m', 'i', 'n'};
    public static final char[] MAXIMUM = {'m', 'a', 'x'};

    private static final char[][] ALL = {
            BITS, BYTES, MINIMUM, MAXIMUM
    };

    /**
     * Returns whether the given token is an attribute token.
     * @param token the token
     * @return true if the token is an attribute
     */
    public static boolean isAttribute(Token token) {
        return isAttribute(token.getContent());
    }

    /**
     * Returns whether the given content is an attribute.
     * @param content the content
     * @return true if the content is an attribute
     */
    public static boolean isAttribute(char[] content) {
        for(char[] attribute : ALL) {
            if(isSameString(content, attribute)) return true;
        }

        return false;
    }
}
