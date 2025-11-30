package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import java.util.HashSet;
import java.util.Set;

/**
 * The Operator class. This class contains every operator used in the language.
 * The operators are grouped by their usage (mathematical, bitwise, etc.).
 * @author Vincent Philippe (@vincent64)
 */
public class Operator {
    //Equality operators
    public static final String EQUAL = "==";
    public static final String NOT_EQUAL = "!=";

    //Comparison operators
    public static final String GREATER = ">";
    public static final String LESS = "<";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS_EQUAL = "<=";
    public static final String TYPE_EQUAL = "==:";
    public static final String TYPE_NOT_EQUAL = "!=:";
    public static final String ELVIS = "?:";
    public static final String NULL_COALESCING = "??";
    public static final String SPACESHIP = "<=>";
    public static final String REFERENCE_EQUAL = "===";
    public static final String REFERENCE_NOT_EQUAL = "!==";

    //Mathematical operators
    public static final String ADD = "+";
    public static final String SUB = "-";
    public static final String MUL = "*";
    public static final String DIV = "/";
    public static final String MOD = "%";

    //Logical operators
    public static final String LOGICAL_AND = "&&";
    public static final String LOGICAL_OR = "||";
    public static final String LOGICAL_NOT = "!";

    //Bitwise operators
    public static final String BITWISE_AND = "&";
    public static final String BITWISE_XOR = "^";
    public static final String BITWISE_OR = "|";
    public static final String BITWISE_NOT = "~";
    public static final String SHIFT_LEFT = "<<";
    public static final String SHIFT_RIGHT = ">>";
    public static final String SHIFT_RIGHT_ARITHMETIC = ">>>";

    //Assignment operators
    public static final String ASSIGN_ADD = "+=";
    public static final String ASSIGN_SUB = "-=";
    public static final String ASSIGN_MUL = "*=";
    public static final String ASSIGN_DIV = "/=";
    public static final String ASSIGN_MOD = "%=";
    public static final String ASSIGN_BITWISE_AND = "&=";
    public static final String ASSIGN_BITWISE_XOR = "^=";
    public static final String ASSIGN_BITWISE_OR = "|=";
    public static final String ASSIGN_SHIFT_LEFT = "<<=";
    public static final String ASSIGN_SHIFT_RIGHT = ">>=";
    public static final String ASSIGN_SHIFT_RIGHT_ARITHMETIC = ">>>=";

    //Increment/decrement operators
    public static final String INCREMENT = "++";
    public static final String DECREMENT = "--";

    //Special operators
    public static final String METHOD_INVOCATION = "call";
    public static final String ARRAY_ACCESS = "access";

    private static final Set<String> ALL = new HashSet<>(Set.of(
            EQUAL, NOT_EQUAL,
            GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, TYPE_EQUAL, TYPE_NOT_EQUAL, ELVIS, NULL_COALESCING,
            SPACESHIP, REFERENCE_EQUAL, REFERENCE_NOT_EQUAL,
            ADD, SUB, MUL, DIV, MOD,
            LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,
            BITWISE_AND, BITWISE_XOR, BITWISE_OR, BITWISE_NOT,
            SHIFT_LEFT, SHIFT_RIGHT, SHIFT_RIGHT_ARITHMETIC,
            ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD,
            ASSIGN_BITWISE_AND, ASSIGN_BITWISE_XOR, ASSIGN_BITWISE_OR,
            ASSIGN_SHIFT_LEFT, ASSIGN_SHIFT_RIGHT, ASSIGN_SHIFT_RIGHT_ARITHMETIC,
            INCREMENT, DECREMENT
    ));

    private static final Set<String> ASSIGNMENT = new HashSet<>(Set.of(
            Symbol.EQUAL, ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD,
            ASSIGN_BITWISE_AND, ASSIGN_BITWISE_XOR, ASSIGN_BITWISE_OR,
            ASSIGN_SHIFT_LEFT, ASSIGN_SHIFT_RIGHT, ASSIGN_SHIFT_RIGHT_ARITHMETIC
    ));

    private static final Set<String> METHOD = new HashSet<>(Set.of(
            EQUAL, NOT_EQUAL,
            GREATER, LESS, GREATER_EQUAL, LESS_EQUAL, SPACESHIP,
            ADD, SUB, MUL, DIV, MOD,
            LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,
            BITWISE_AND, BITWISE_XOR, BITWISE_OR, BITWISE_NOT,
            SHIFT_LEFT, SHIFT_RIGHT, SHIFT_RIGHT_ARITHMETIC,
            ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD,
            ASSIGN_BITWISE_AND, ASSIGN_BITWISE_XOR, ASSIGN_BITWISE_OR,
            ASSIGN_SHIFT_LEFT, ASSIGN_SHIFT_RIGHT, ASSIGN_SHIFT_RIGHT_ARITHMETIC,
            INCREMENT, DECREMENT,
            METHOD_INVOCATION, ARRAY_ACCESS
    ));

    /**
     * Returns whether the given token is an operator token.
     * @param token the token
     * @return true if the token is an operator
     */
    public static boolean isOperator(Token token) {
        return isOperator(token.getContent());
    }

    /**
     * Returns whether the given content is an operator.
     * @param content the content
     * @return true if the content is an operator
     */
    public static boolean isOperator(String content) {
        return ALL.contains(content);
    }

    /**
     * Returns whether the given content is an operator.
     * @param content the content
     * @return true if the content is an operator
     */
    public static boolean isOperator(char[] content) {
        return isOperator(String.valueOf(content));
    }

    /**
     * Returns whether the given token is an assignment operator token.
     * @param token the token
     * @return true if the token is an assignment operator
     */
    public static boolean isAssignOperator(Token token) {
        return isAssignOperator(token.getContent());
    }

    /**
     * Returns whether the given content is an assignment operator.
     * @param content the content
     * @return true if the content is an assignment operator
     */
    public static boolean isAssignOperator(String content) {
        return ASSIGNMENT.contains(content);
    }

    /**
     * Returns whether the given token is a method operator token.
     * @param token the token
     * @return true if the token is a method operator
     */
    public static boolean isMethodOperator(Token token) {
        return isMethodOperator(token.getContent());
    }

    /**
     * Returns whether the given content is a method operator.
     * @param content the content
     * @return true if the content is a method operator.
     */
    public static boolean isMethodOperator(String content) {
        return METHOD.contains(content);
    }
}
