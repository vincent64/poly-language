package poly.compiler.tokenizer.content;

import poly.compiler.tokenizer.Token;

import static poly.compiler.util.Character.isSameString;

/**
 * The Operator class. This class contains every operator used in the language.
 * The operators are grouped by their usage (mathematical, bitwise, etc.).
 * @author Vincent Philippe (@vincent64)
 */
public class Operator {
    //Equality operators
    public static final char[] EQUAL = {'=', '='};
    public static final char[] NOT_EQUAL = {'!', '='};

    //Comparison operators
    public static final char[] GREATER = {'>'};
    public static final char[] LESS = {'<'};
    public static final char[] GREATER_EQUAL = {'>', '='};
    public static final char[] LESS_EQUAL = {'<', '='};
    public static final char[] TYPE_EQUAL = {'=', '=', ':'};
    public static final char[] TYPE_NOT_EQUAL = {'!', '=', ':'};
    public static final char[] ELVIS = {'?', ':'};
    public static final char[] NULL_COALESCING = {'?', '?'};
    public static final char[] SPACESHIP = {'<', '=', '>'};
    public static final char[] REFERENCE_EQUAL = {'=', '=', '='};
    public static final char[] REFERENCE_NOT_EQUAL = {'!', '=', '='};

    //Mathematical operators
    public static final char[] ADD = {'+'};
    public static final char[] SUB = {'-'};
    public static final char[] MUL = {'*'};
    public static final char[] DIV = {'/'};
    public static final char[] MOD = {'%'};

    //Logical operators
    public static final char[] LOGICAL_AND = {'&', '&'};
    public static final char[] LOGICAL_OR = {'|', '|'};
    public static final char[] LOGICAL_NOT = {'!'};

    //Bitwise operators
    public static final char[] BITWISE_AND = {'&'};
    public static final char[] BITWISE_XOR = {'^'};
    public static final char[] BITWISE_OR = {'|'};
    public static final char[] BITWISE_NOT = {'~'};
    public static final char[] SHIFT_LEFT = {'<', '<'};
    public static final char[] SHIFT_RIGHT = {'>', '>'};
    public static final char[] SHIFT_RIGHT_ARITHMETIC = {'>', '>', '>'};

    //Assignment operators
    public static final char[] ASSIGN_ADD = {'+', '='};
    public static final char[] ASSIGN_SUB = {'-', '='};
    public static final char[] ASSIGN_MUL = {'*', '='};
    public static final char[] ASSIGN_DIV = {'/', '='};
    public static final char[] ASSIGN_MOD = {'%', '='};
    public static final char[] ASSIGN_BITWISE_AND = {'&', '='};
    public static final char[] ASSIGN_BITWISE_XOR = {'^', '='};
    public static final char[] ASSIGN_BITWISE_OR = {'|', '='};
    public static final char[] ASSIGN_SHIFT_LEFT = {'<', '<', '='};
    public static final char[] ASSIGN_SHIFT_RIGHT = {'>', '>', '='};
    public static final char[] ASSIGN_SHIFT_RIGHT_ARITHMETIC = {'>', '>', '>', '='};

    //Increment/decrement operators
    public static final char[] INCREMENT = {'+', '+'};
    public static final char[] DECREMENT = {'-', '-'};

    //Special operators
    public static final char[] METHOD_INVOCATION = {'c', 'a', 'l', 'l'};
    public static final char[] ARRAY_ACCESS = {'a', 'c', 'c', 'e', 's', 's'};

    private static final char[][] ALL = {
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
    };

    private static final char[][] ASSIGNMENT = {
            Symbol.EQUAL, ASSIGN_ADD, ASSIGN_SUB, ASSIGN_MUL, ASSIGN_DIV, ASSIGN_MOD,
            ASSIGN_BITWISE_AND, ASSIGN_BITWISE_XOR, ASSIGN_BITWISE_OR,
            ASSIGN_SHIFT_LEFT, ASSIGN_SHIFT_RIGHT, ASSIGN_SHIFT_RIGHT_ARITHMETIC
    };

    private static final char[][] METHOD = {
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
    };

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
    public static boolean isOperator(char[] content) {
        for(char[] operator : ALL) {
            if(isSameString(content, operator)) return true;
        }

        return false;
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
    public static boolean isAssignOperator(char[] content) {
        for(char[] operator : ASSIGNMENT) {
            if(isSameString(content, operator)) return true;
        }

        return false;
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
    public static boolean isMethodOperator(char[] content) {
        for(char[] operator : METHOD) {
            if(isSameString(content, operator)) return true;
        }

        return false;
    }
}
