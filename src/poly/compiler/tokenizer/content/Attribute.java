package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import java.util.HashSet;
import java.util.Set;

/**
 * The Attribute class. This class contains the 4 different primitive attributes.
 * It also contains utility methods to check if a given string is an attribute.
 * @author Vincent Philippe (@vincent64)
 */
public class Attribute {
    public static final String BITS = "bits";
    public static final String BYTES = "bytes";
    public static final String MINIMUM = "min";
    public static final String MAXIMUM = "max";

    private static final Set<String> ALL = new HashSet<>(Set.of(
            BITS, BYTES, MINIMUM, MAXIMUM
    ));

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
    public static boolean isAttribute(String content) {
        return ALL.contains(content);
    }
}
