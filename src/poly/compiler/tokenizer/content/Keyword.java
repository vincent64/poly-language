package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import java.util.HashSet;
import java.util.Set;

/**
 * The Keyword class. This class contains every reserved keywords of the Poly language.
 * It also contains utility methods to check if a given string is a keyword.
 * This class is mainly used during the tokenization and parsing phases.
 * @author Vincent Philippe (@vincent64)
 */
public class Keyword {
    //Primitive keywords
    public static final String PRIMITIVE_BOOLEAN = "bool";
    public static final String PRIMITIVE_BYTE = "byte";
    public static final String PRIMITIVE_CHAR = "char";
    public static final String PRIMITIVE_SHORT = "short";
    public static final String PRIMITIVE_INTEGER = "int";
    public static final String PRIMITIVE_LONG = "long";
    public static final String PRIMITIVE_FLOAT = "float";
    public static final String PRIMITIVE_DOUBLE = "double";

    //Expression keywords
    public static final String EXPRESSION_TRUE = "true";
    public static final String EXPRESSION_FALSE = "false";
    public static final String EXPRESSION_NULL = "null";
    public static final String EXPRESSION_THIS = "this";
    public static final String EXPRESSION_SUPER = "super";
    public static final String EXPRESSION_OUTER = "outer";
    public static final String EXPRESSION_SUM = "sum";
    public static final String EXPRESSION_PROD = "prod";

    //Statement keywords
    public static final String STATEMENT_IF = "if";
    public static final String STATEMENT_ELSE = "else";
    public static final String STATEMENT_FOR = "for";
    public static final String STATEMENT_WHILE = "while";
    public static final String STATEMENT_DO = "do";
    public static final String STATEMENT_RETURN = "return";
    public static final String STATEMENT_SWITCH = "switch";
    public static final String STATEMENT_MATCH = "match";
    public static final String STATEMENT_CASE = "case";
    public static final String STATEMENT_ASSERT = "assert";
    public static final String STATEMENT_CONTINUE = "continue";
    public static final String STATEMENT_BREAK = "break";
    public static final String STATEMENT_FOREACH = "foreach";
    public static final String STATEMENT_TRY = "try";
    public static final String STATEMENT_CATCH = "catch";
    public static final String STATEMENT_THROW = "throw";

    //Class keywords
    public static final String CLASS = "class";
    public static final String CLASS_NEW = "new";
    public static final String CLASS_INTERFACE = "interface";
    public static final String CLASS_ENUM = "enum";
    public static final String CLASS_INNER = "inner";
    public static final String CLASS_EXCEPTION = "exception";
    public static final String CLASS_DATUM = "datum";

    //Method keywords
    public static final String METHOD = "fn";
    public static final String METHOD_VOID = "void";
    public static final String METHOD_CONSTRUCTOR = "constructor";
    public static final String METHOD_OPERATOR = "op";
    public static final String METHOD_EXTERNAL = "ext";
    public static final String METHOD_NULLIFIER = "nullifier";

    //Variable keywords
    public static final String VAR_CONST = "const";
    public static final String VAR_ATTRIBUTE = "attr";

    //Header keywords
    public static final String PACKAGE = "package";
    public static final String IMPORT = "import";
    public static final String DEFINE = "define";

    //Concurrency keywords (reserved for later use)
    public static final String CONCURRENCY_ASYNC = "async";
    public static final String CONCURRENCY_AWAIT = "await";
    public static final String CONCURRENCY_THREAD = "thread";

    private static final Set<String> ALL = new HashSet<>(Set.of(
            PRIMITIVE_BOOLEAN, PRIMITIVE_BYTE, PRIMITIVE_CHAR, PRIMITIVE_SHORT,
            PRIMITIVE_INTEGER, PRIMITIVE_LONG, PRIMITIVE_FLOAT, PRIMITIVE_DOUBLE,
            EXPRESSION_TRUE, EXPRESSION_FALSE, EXPRESSION_NULL, EXPRESSION_SUPER, EXPRESSION_THIS,
            EXPRESSION_SUM, EXPRESSION_PROD,
            STATEMENT_IF, STATEMENT_ELSE, STATEMENT_FOR, STATEMENT_WHILE,
            STATEMENT_DO, STATEMENT_RETURN, STATEMENT_SWITCH, STATEMENT_MATCH, STATEMENT_CASE,
            STATEMENT_ASSERT, STATEMENT_CONTINUE, STATEMENT_BREAK, STATEMENT_FOREACH,
            STATEMENT_TRY, STATEMENT_CATCH, STATEMENT_THROW,
            CLASS, CLASS_NEW, CLASS_INTERFACE, CLASS_ENUM, CLASS_INNER, CLASS_EXCEPTION, CLASS_DATUM,
            METHOD, METHOD_VOID, METHOD_CONSTRUCTOR, METHOD_OPERATOR, METHOD_EXTERNAL, METHOD_NULLIFIER,
            VAR_CONST, VAR_ATTRIBUTE,
            PACKAGE, IMPORT, DEFINE,
            CONCURRENCY_ASYNC, CONCURRENCY_AWAIT, CONCURRENCY_THREAD
    ));

    private static final Set<String> PRIMITIVE = new HashSet<>(Set.of(
            PRIMITIVE_BOOLEAN, PRIMITIVE_BYTE, PRIMITIVE_CHAR, PRIMITIVE_SHORT,
            PRIMITIVE_INTEGER, PRIMITIVE_LONG, PRIMITIVE_FLOAT, PRIMITIVE_DOUBLE
    ));

    /**
     * Returns whether the given token is a keyword token.
     * @param token the token
     * @return true if the token is a keyword
     */
    public static boolean isKeyword(Token token) {
        return isKeyword(token.getContent());
    }

    /**
     * Returns whether the given content is a keyword.
     * @param content the content
     * @return true if the content is a keyword
     */
    public static boolean isKeyword(char[] content) {
        return isKeyword(String.valueOf(content));
    }

    /**
     * Returns whether the given content is a keyword.
     * @param content the content
     * @return true if the content is a keyword
     */
    public static boolean isKeyword(String content) {
        return ALL.contains(content);
    }

    /**
     * Returns whether the given token is a primitive keyword token.
     * @param token the token
     * @return true if the token is a primitive keyword
     */
    public static boolean isPrimitiveKeyword(Token token) {
        return isPrimitiveKeyword(token.getContent());
    }

    /**
     * Returns whether the given content is a keyword.
     * @param content the content
     * @return true if the content is a keyword
     */
    public static boolean isPrimitiveKeyword(String content) {
        return PRIMITIVE.contains(content);
    }
}
