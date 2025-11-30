package poly.compiler.analyzer.content;

import poly.compiler.parser.tree.expression.AssignmentExpression;
import poly.compiler.parser.tree.expression.BinaryExpression;
import poly.compiler.parser.tree.expression.UnaryExpression;
import poly.compiler.tokenizer.content.Operator;
import poly.compiler.util.Character;

/**
 * The OperatorMethod class. This class is used to obtain the internal name
 * of operator overloading methods.
 * @author Vincent Philippe (@vincent64)
 */
public class OperatorMethod {
    private OperatorMethod() { }

    /**
     * Returns the method name for the given operator.
     * @param operator the operator
     * @return the operator overload method name
     */
    public static String getNameFromOperator(String operator) {
        for(Kind kind : Kind.values()) {
            if(kind.operator.equals(operator))
                return kind.getMethodName();
        }

        return null;
    }

    /**
     * Returns the operator for the given method name.
     * @param name the method name
     * @return the operator overload operator
     */
    public static String getOperatorFromName(String name) {
        for(Kind kind : Kind.values()) {
            if(name.equals(kind.methodName))
                return String.valueOf(kind.getOperator());
        }

        return null;
    }

    /**
     * Returns the method name for the given binary expression kind.
     * @param expressionKind the binary expression kind
     * @return the operator overload method name
     */
    public static String getNameFromBinaryExpression(BinaryExpression.Kind expressionKind) {
        return switch(expressionKind) {
            case OPERATION_ADDITION -> Kind.ADD.getMethodName();
            case OPERATION_SUBTRACTION -> Kind.SUB.getMethodName();
            case OPERATION_MULTIPLICATION -> Kind.MUL.getMethodName();
            case OPERATION_DIVISION -> Kind.DIV.getMethodName();
            case OPERATION_MODULO -> Kind.MOD.getMethodName();
            case EQUALITY_EQUAL -> Kind.EQUAL.getMethodName();
            case EQUALITY_NOT_EQUAL -> Kind.NOT_EQUAL.getMethodName();
            case COMPARISON_GREATER -> Kind.GREATER.getMethodName();
            case COMPARISON_LESS -> Kind.LESS.getMethodName();
            case COMPARISON_GREATER_EQUAL -> Kind.GREATER_EQUAL.getMethodName();
            case COMPARISON_LESS_EQUAL -> Kind.LESS_EQUAL.getMethodName();
            case COMPARISON_SPACESHIP -> Kind.SPACESHIP.getMethodName();
            case LOGICAL_AND -> Kind.LOGICAL_AND.getMethodName();
            case LOGICAL_OR -> Kind.LOGICAL_OR.getMethodName();
            case BITWISE_AND -> Kind.BITWISE_AND.getMethodName();
            case BITWISE_XOR -> Kind.BITWISE_XOR.getMethodName();
            case BITWISE_OR -> Kind.BITWISE_OR.getMethodName();
            case BITWISE_SHIFT_LEFT -> Kind.SHIFT_LEFT.getMethodName();
            case BITWISE_SHIFT_RIGHT -> Kind.SHIFT_RIGHT.getMethodName();
            case BITWISE_SHIFT_RIGHT_ARITHMETIC -> Kind.SHIFT_RIGHT_ARITHMETIC.getMethodName();
            default -> null;
        };
    }

    /**
     * Returns the method name for the given unary expression kind.
     * @param expressionKind the unary expression kind
     * @return the operator overload method name
     */
    public static String getNameFromUnaryExpression(UnaryExpression.Kind expressionKind) {
        return switch(expressionKind) {
            case OPERATION_NEGATE -> Kind.NEGATE.getMethodName();
            case LOGICAL_NOT -> Kind.LOGICAL_NOT.getMethodName();
            case BITWISE_NOT -> Kind.BITWISE_NOT.getMethodName();
            case POST_INCREMENT, PRE_INCREMENT -> Kind.INCREMENT.getMethodName();
            case POST_DECREMENT, PRE_DECREMENT -> Kind.DECREMENT.getMethodName();
        };
    }

    /**
     * Returns the method name for the given assignment kind.
     * @param assignmentKind the assignment kind
     * @return the operator overload method name
     */
    public static String getNameFromAssignmentExpression(AssignmentExpression.Kind assignmentKind) {
        return switch(assignmentKind) {
            case ASSIGNMENT_ADDITION -> Kind.ASSIGN_ADD.getMethodName();
            case ASSIGNMENT_SUBTRACTION -> Kind.ASSIGN_SUB.getMethodName();
            case ASSIGNMENT_MULTIPLICATION -> Kind.ASSIGN_MUL.getMethodName();
            case ASSIGNMENT_DIVISION -> Kind.ASSIGN_DIV.getMethodName();
            case ASSIGNMENT_MODULO -> Kind.ASSIGN_MOD.getMethodName();
            case ASSIGNMENT_BITWISE_AND -> Kind.ASSIGN_BITWISE_AND.getMethodName();
            case ASSIGNMENT_BITWISE_XOR -> Kind.ASSIGN_BITWISE_XOR.getMethodName();
            case ASSIGNMENT_BITWISE_OR -> Kind.ASSIGN_BITWISE_OR.getMethodName();
            case ASSIGNMENT_SHIFT_LEFT -> Kind.ASSIGN_SHIFT_LEFT.getMethodName();
            case ASSIGNMENT_SHIFT_RIGHT -> Kind.ASSIGN_SHIFT_RIGHT.getMethodName();
            case ASSIGNMENT_SHIFT_RIGHT_ARITHMETIC -> Kind.ASSIGN_SHIFT_RIGHT_ARITHMETIC.getMethodName();
            default -> null;
        };
    }

    /**
     * The OperatorMethod.Kind enum. This enum contains every kind of operator
     * that can be overloaded by a method.
     */
    public enum Kind {
        //Mathematical operations
        ADD(Operator.ADD, Name.ADD),
        SUB(Operator.SUB, Name.SUB),
        MUL(Operator.MUL, Name.MUL),
        DIV(Operator.DIV, Name.DIV),
        MOD(Operator.MOD, Name.MOD),
        NEGATE(Operator.SUB, Name.NEGATE),

        //Equality operations
        EQUAL(Operator.EQUAL, Name.EQUAL),
        NOT_EQUAL(Operator.NOT_EQUAL, Name.NOT_EQUAL),

        //Comparison operations
        GREATER(Operator.GREATER, Name.GREATER),
        LESS(Operator.LESS, Name.LESS),
        GREATER_EQUAL(Operator.GREATER_EQUAL, Name.GREATER_EQUAL),
        LESS_EQUAL(Operator.LESS_EQUAL, Name.LESS_EQUAL),
        SPACESHIP(Operator.SPACESHIP, Name.SPACESHIP),

        //Logical operations
        LOGICAL_AND(Operator.LOGICAL_AND, Name.LOGICAL_AND),
        LOGICAL_OR(Operator.LOGICAL_OR, Name.LOGICAL_OR),
        LOGICAL_NOT(Operator.LOGICAL_NOT, Name.LOGICAL_NOT),

        //Bitwise operations
        BITWISE_AND(Operator.BITWISE_AND, Name.BITWISE_AND),
        BITWISE_XOR(Operator.BITWISE_XOR, Name.BITWISE_XOR),
        BITWISE_OR(Operator.BITWISE_OR, Name.BITWISE_OR),
        BITWISE_NOT(Operator.BITWISE_NOT, Name.BITWISE_NOT),
        SHIFT_LEFT(Operator.SHIFT_LEFT, Name.SHIFT_LEFT),
        SHIFT_RIGHT(Operator.SHIFT_RIGHT, Name.SHIFT_RIGHT),
        SHIFT_RIGHT_ARITHMETIC(Operator.SHIFT_RIGHT_ARITHMETIC, Name.SHIFT_RIGHT_ARITHMETIC),

        //Assignment operations
        ASSIGN_ADD(Operator.ASSIGN_ADD, Name.ASSIGN_ADD),
        ASSIGN_SUB(Operator.ASSIGN_SUB, Name.ASSIGN_SUB),
        ASSIGN_MUL(Operator.ASSIGN_MUL, Name.ASSIGN_MUL),
        ASSIGN_DIV(Operator.ASSIGN_DIV, Name.ASSIGN_DIV),
        ASSIGN_MOD(Operator.ASSIGN_MOD, Name.ASSIGN_MOD),
        ASSIGN_BITWISE_AND(Operator.ASSIGN_BITWISE_AND, Name.ASSIGN_BITWISE_AND),
        ASSIGN_BITWISE_XOR(Operator.ASSIGN_BITWISE_XOR, Name.ASSIGN_BITWISE_XOR),
        ASSIGN_BITWISE_OR(Operator.ASSIGN_BITWISE_OR, Name.ASSIGN_BITWISE_OR),
        ASSIGN_SHIFT_LEFT(Operator.ASSIGN_SHIFT_LEFT, Name.ASSIGN_SHIFT_LEFT),
        ASSIGN_SHIFT_RIGHT(Operator.ASSIGN_SHIFT_RIGHT, Name.ASSIGN_SHIFT_RIGHT),
        ASSIGN_SHIFT_RIGHT_ARITHMETIC(Operator.ASSIGN_SHIFT_RIGHT_ARITHMETIC, Name.ASSIGN_SHIFT_RIGHT_ARITHMETIC),

        //Increment/decrement operations
        INCREMENT(Operator.INCREMENT, Name.INCREMENT),
        DECREMENT(Operator.DECREMENT, Name.DECREMENT),

        //Method invocation operation
        METHOD_INVOCATION(Operator.METHOD_INVOCATION, Name.METHOD_INVOCATION),

        //Array access operation
        ARRAY_ACCESS(Operator.ARRAY_ACCESS, Name.ARRAY_ACCESS);

        private final String operator;
        private final String methodName;

        Kind(String operator, String methodName) {
            this.operator = operator;
            this.methodName = methodName;
        }

        public String getOperator() {
            return operator;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    /**
     * The OperatorMethod.Name class. This class contains the internal name
     * of every operator overload methods.
     */
    public static class Name {
        private Name() { }

        //Mathematical operators
        public static final String ADD = "$add";
        public static final String SUB = "$sub";
        public static final String MUL = "$mul";
        public static final String DIV = "$div";
        public static final String MOD = "$mod";
        public static final String NEGATE = "$neg";

        //Equality operators
        public static final String EQUAL = "$equal";
        public static final String NOT_EQUAL = "$notEqual";

        //Comparison operators
        public static final String GREATER = "$greater";
        public static final String LESS = "$less";
        public static final String GREATER_EQUAL = "$greaterEqual";
        public static final String LESS_EQUAL = "$lessEqual";
        public static final String SPACESHIP = "$spaceship";

        //Logical operators
        public static final String LOGICAL_AND = "$logicalAnd";
        public static final String LOGICAL_OR = "$logicalOr";
        public static final String LOGICAL_NOT = "$logicalNot";

        //Bitwise operators
        public static final String BITWISE_AND = "$bitwiseAnd";
        public static final String BITWISE_XOR = "$bitwiseXor";
        public static final String BITWISE_OR = "$bitwiseOr";
        public static final String BITWISE_NOT = "$bitwiseNot";
        public static final String SHIFT_LEFT = "$shiftLeft";
        public static final String SHIFT_RIGHT = "$shiftRight";
        public static final String SHIFT_RIGHT_ARITHMETIC = "$shiftRightArithmetic";

        //Assignment operators
        public static final String ASSIGN_ADD = "$assignAdd";
        public static final String ASSIGN_SUB = "$assignSub";
        public static final String ASSIGN_MUL = "$assignMul";
        public static final String ASSIGN_DIV = "$assignDiv";
        public static final String ASSIGN_MOD = "$assignMod";
        public static final String ASSIGN_BITWISE_AND = "$assignBitwiseAnd";
        public static final String ASSIGN_BITWISE_XOR = "$assignBitwiseXor";
        public static final String ASSIGN_BITWISE_OR = "$assignBitwiseOr";
        public static final String ASSIGN_SHIFT_LEFT = "$assignShiftLeft";
        public static final String ASSIGN_SHIFT_RIGHT = "$assignShiftRight";
        public static final String ASSIGN_SHIFT_RIGHT_ARITHMETIC = "$assignShiftRightArithmetic";

        //Increment/decrement operators
        public static final String INCREMENT = "$increment";
        public static final String DECREMENT = "$decrement";

        //Method invocation operator
        public static final String METHOD_INVOCATION = "$invoke";

        //Array access operator
        public static final String ARRAY_ACCESS = "$access";
    }
}
