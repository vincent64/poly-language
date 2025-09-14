package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import static poly.compiler.util.Character.isSameString;

/**
 * The Keyword class. This class contains every reserved keywords of the Poly language.
 * It also contains utility methods to check if a given string is a keyword.
 * This class is mainly used during the tokenization and parsing phases.
 * @author Vincent Philippe (@vincent64)
 */
public class Keyword {
    //Primitive keywords
    public static final char[] PRIMITIVE_BOOLEAN = {'b', 'o', 'o', 'l'};
    public static final char[] PRIMITIVE_BYTE = {'b', 'y', 't', 'e'};
    public static final char[] PRIMITIVE_CHAR = {'c', 'h', 'a', 'r'};
    public static final char[] PRIMITIVE_SHORT = {'s', 'h', 'o', 'r', 't'};
    public static final char[] PRIMITIVE_INTEGER = {'i', 'n', 't'};
    public static final char[] PRIMITIVE_LONG = {'l', 'o', 'n', 'g'};
    public static final char[] PRIMITIVE_FLOAT = {'f', 'l', 'o', 'a', 't'};
    public static final char[] PRIMITIVE_DOUBLE = {'d', 'o', 'u', 'b', 'l', 'e'};

    //Expression keywords
    public static final char[] EXPRESSION_TRUE = {'t', 'r', 'u', 'e'};
    public static final char[] EXPRESSION_FALSE = {'f', 'a', 'l', 's', 'e'};
    public static final char[] EXPRESSION_NULL = {'n', 'u', 'l', 'l'};
    public static final char[] EXPRESSION_THIS = {'t', 'h', 'i', 's'};
    public static final char[] EXPRESSION_SUPER = {'s', 'u', 'p', 'e', 'r'};
    public static final char[] EXPRESSION_OUTER = {'o', 'u', 't', 'e', 'r'};
    public static final char[] EXPRESSION_SUM = {'s', 'u', 'm'};
    public static final char[] EXPRESSION_PROD = {'p', 'r', 'o', 'd'};

    //Statement keywords
    public static final char[] STATEMENT_IF = {'i', 'f'};
    public static final char[] STATEMENT_ELSE = {'e', 'l', 's', 'e'};
    public static final char[] STATEMENT_FOR = {'f', 'o', 'r'};
    public static final char[] STATEMENT_WHILE = {'w', 'h', 'i', 'l', 'e'};
    public static final char[] STATEMENT_DO = {'d', 'o'};
    public static final char[] STATEMENT_RETURN = {'r', 'e', 't', 'u', 'r', 'n'};
    public static final char[] STATEMENT_SWITCH = {'s', 'w', 'i', 't', 'c', 'h'};
    public static final char[] STATEMENT_MATCH = {'m', 'a', 't', 'c', 'h'};
    public static final char[] STATEMENT_CASE = {'c', 'a', 's', 'e'};
    public static final char[] STATEMENT_ASSERT = {'a', 's', 's', 'e', 'r', 't'};
    public static final char[] STATEMENT_CONTINUE = {'c', 'o', 'n', 't', 'i', 'n', 'u', 'e'};
    public static final char[] STATEMENT_BREAK = {'b', 'r', 'e', 'a', 'k'};
    public static final char[] STATEMENT_FOREACH = {'f', 'o', 'r', 'e', 'a', 'c', 'h'};
    public static final char[] STATEMENT_TRY = {'t', 'r', 'y'};
    public static final char[] STATEMENT_CATCH = {'c', 'a', 't', 'c', 'h'};
    public static final char[] STATEMENT_THROW = {'t', 'h', 'r', 'o', 'w'};

    //Class keywords
    public static final char[] CLASS = {'c', 'l', 'a', 's', 's'};
    public static final char[] CLASS_NEW = {'n', 'e', 'w'};
    public static final char[] CLASS_INTERFACE = {'i', 'n', 't', 'e', 'r', 'f', 'a', 'c', 'e'};
    public static final char[] CLASS_ENUM = {'e', 'n', 'u', 'm'};
    public static final char[] CLASS_INNER = {'i', 'n', 'n', 'e', 'r'};
    public static final char[] CLASS_EXCEPTION = {'e', 'x', 'c', 'e', 'p', 't', 'i', 'o', 'n'};

    //Method keywords
    public static final char[] METHOD = {'f', 'n'};
    public static final char[] METHOD_VOID = {'v', 'o', 'i', 'd'};
    public static final char[] METHOD_CONSTRUCTOR = {'c', 'o', 'n', 's', 't', 'r', 'u', 'c', 't', 'o', 'r'};
    public static final char[] METHOD_OPERATOR = {'o', 'p'};
    public static final char[] METHOD_NULLIFIER = {'n', 'u', 'l', 'l', 'i', 'f', 'i', 'e', 'r'};

    //Variable keywords
    public static final char[] VAR_CONST = {'c', 'o', 'n', 's', 't'};
    public static final char[] VAR_ATTRIBUTE = {'a', 't', 't', 'r'};

    //Header keywords
    public static final char[] PACKAGE = {'p', 'a', 'c', 'k', 'a', 'g', 'e'};
    public static final char[] IMPORT = {'i', 'm', 'p', 'o', 'r', 't'};
    public static final char[] DEFINE = {'d', 'e', 'f', 'i', 'n', 'e'};

    //Concurrency keywords (reserved for later use)
    public static final char[] CONCURRENCY_ASYNC = {'a', 's', 'y', 'n', 'c'};
    public static final char[] CONCURRENCY_AWAIT = {'a', 'w', 'a', 'i', 't'};
    public static final char[] CONCURRENCY_THREAD = {'t', 'h', 'r', 'e', 'a', 'd'};

    //All keywords
    private static final char[][] ALL = {
            PRIMITIVE_BOOLEAN, PRIMITIVE_BYTE, PRIMITIVE_CHAR, PRIMITIVE_SHORT,
            PRIMITIVE_INTEGER, PRIMITIVE_LONG, PRIMITIVE_FLOAT, PRIMITIVE_DOUBLE,
            EXPRESSION_TRUE, EXPRESSION_FALSE, EXPRESSION_NULL, EXPRESSION_SUPER, EXPRESSION_THIS,
            EXPRESSION_SUM, EXPRESSION_PROD,
            STATEMENT_IF, STATEMENT_ELSE, STATEMENT_FOR, STATEMENT_WHILE,
            STATEMENT_DO, STATEMENT_RETURN, STATEMENT_SWITCH, STATEMENT_MATCH, STATEMENT_CASE,
            STATEMENT_ASSERT, STATEMENT_CONTINUE, STATEMENT_BREAK, STATEMENT_FOREACH,
            STATEMENT_TRY, STATEMENT_CATCH, STATEMENT_THROW,
            CLASS, CLASS_NEW, CLASS_INTERFACE, CLASS_ENUM, CLASS_INNER, CLASS_EXCEPTION,
            METHOD, METHOD_VOID, METHOD_CONSTRUCTOR, METHOD_OPERATOR, METHOD_NULLIFIER,
            VAR_CONST, VAR_ATTRIBUTE,
            PACKAGE, IMPORT, DEFINE,
            CONCURRENCY_ASYNC, CONCURRENCY_AWAIT, CONCURRENCY_THREAD
    };

    private static final char[][] PRIMITIVE = {
            PRIMITIVE_BOOLEAN, PRIMITIVE_BYTE, PRIMITIVE_CHAR, PRIMITIVE_SHORT,
            PRIMITIVE_INTEGER, PRIMITIVE_LONG, PRIMITIVE_FLOAT, PRIMITIVE_DOUBLE
    };

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
        for(char[] keyword : ALL) {
            if(isSameString(content, keyword)) return true;
        }

        return false;
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
    public static boolean isPrimitiveKeyword(char[] content) {
        for(char[] keyword : PRIMITIVE) {
            if(isSameString(content, keyword)) return true;
        }

        return false;
    }
}
