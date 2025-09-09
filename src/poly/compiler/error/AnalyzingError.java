package poly.compiler.error;

import poly.compiler.analyzer.type.Type;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.VariableDeclaration;
import poly.compiler.util.MethodStringifier;

/**
 * The AnalyzingError class. Error classes extending from this class represent
 * code errors that have been detected during the analyzing phase.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class AnalyzingError extends Error {
    private static final int CODE = 4;
    private static final String BASE_MESSAGE = "File %s (on line %s, char. %s) :\n    ";

    public AnalyzingError(Node node, String message) {
        super(BASE_MESSAGE.formatted(
                node.getMeta().getFileName(),
                node.getMeta().getLine(),
                node.getMeta().getCharacter()) + message,
                CODE);
    }

    public static class NotAStatement extends AnalyzingError {
        private static final String MESSAGE = "Not a statement";

        public NotAStatement(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UnreachableStatement extends AnalyzingError {
        private static final String MESSAGE = "Unreachable statement";

        public UnreachableStatement(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class MissingReturnStatement extends AnalyzingError {
        private static final String MESSAGE = "Missing return statement";

        public MissingReturnStatement(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class MissingReturnValue extends AnalyzingError {
        private static final String MESSAGE = "Missing return value";

        public MissingReturnValue(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UnexpectedReturnValue extends AnalyzingError {
        private static final String MESSAGE = "Unexpected return value";

        public UnexpectedReturnValue(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class BreakOutsideLoop extends AnalyzingError {
        private static final String MESSAGE = "Break statement outside of a loop";

        public BreakOutsideLoop(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ContinueOutsideLoop extends AnalyzingError {
        private static final String MESSAGE = "Continue statement outside of a loop";

        public ContinueOutsideLoop(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UnresolvableClass extends AnalyzingError {
        private static final String MESSAGE = "Cannot resolve class with name '%s'";

        public UnresolvableClass(Node node, String name) {
            super(node, MESSAGE.formatted(name));
        }
    }

    public static class UnresolvableMethod extends AnalyzingError {
        private static final String MESSAGE = "Cannot resolve method '%s'";

        public UnresolvableMethod(MethodCall node, String name, Type[] argumentTypes) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(name, argumentTypes)));
        }
    }

    public static class UnresolvableSymbol extends AnalyzingError {
        private static final String MESSAGE = "Cannot resolve symbol with name '%s'";

        public UnresolvableSymbol(Node node, String name) {
            super(node, MESSAGE.formatted(name));
        }
    }

    public static class UnresolvableConstructor extends AnalyzingError {
        private static final String MESSAGE = "Cannot resolve constructor with types %s";

        public UnresolvableConstructor(Node node, Type[] argumentTypes) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(argumentTypes)));
        }
    }

    public static class UnresolvableType extends AnalyzingError {
        private static final String MESSAGE = "Cannot resolve type '%s'";

        public UnresolvableType(Node node) {
            super(node, MESSAGE.formatted(node));
        }
    }

    public static class DuplicateVariable extends AnalyzingError {
        private static final String MESSAGE = "Variable with name '%s' already exists in current scope";

        public DuplicateVariable(VariableDeclaration node) {
            super(node, MESSAGE.formatted(node.getName()));
        }

        public DuplicateVariable(Parameter node) {
            super(node, MESSAGE.formatted(node.getName()));
        }
    }

    public static class TypeConversion extends AnalyzingError {
        private static final String MESSAGE = "Cannot convert type '%s' to type '%s'";

        public TypeConversion(Node node, Type actual, Type expected) {
            super(node, MESSAGE.formatted(actual, expected));
        }
    }

    public static class TypeCast extends AnalyzingError {
        private static final String MESSAGE = "Cannot cast type '%s' to type '%s'";

        public TypeCast(CastExpression node) {
            super(node, MESSAGE.formatted(
                    ((Expression) node.getExpression()).getExpressionType(),
                    node.getCastType()
            ));
        }
    }

    public static class ExpectedBooleanExpression extends AnalyzingError {
        private static final String MESSAGE = "Boolean expression expected";

        public ExpectedBooleanExpression(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedVariableExpression extends AnalyzingError {
        private static final String MESSAGE = "Variable expected";

        public ExpectedVariableExpression(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedLiteralExpression extends AnalyzingError {
        private static final String MESSAGE = "Literal expression expected";

        public ExpectedLiteralExpression(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidBinaryOperation extends AnalyzingError {
        private static final String MESSAGE = "Cannot apply operation '%s' between '%s' and '%s'";

        public InvalidBinaryOperation(BinaryExpression node) {
            super(node, MESSAGE.formatted(
                    node.getKind(),
                    ((Expression) node.getFirst()).getExpressionType(),
                    ((Expression) node.getSecond()).getExpressionType()
            ));
        }
    }

    public static class InvalidUnaryOperation extends AnalyzingError {
        private static final String MESSAGE = "Cannot apply operation '%s' on '%s'";

        public InvalidUnaryOperation(UnaryExpression node) {
            super(node, MESSAGE.formatted(
                    node.getKind(),
                    ((Expression) node.getExpression()).getExpressionType()
            ));
        }
    }

    public static class InvalidPrimitiveAttribute extends AnalyzingError {
        private static final String MESSAGE = "Invalid primitive attribute";

        public InvalidPrimitiveAttribute(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class StaticThisReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference current class 'this' inside static method";

        public StaticThisReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class StaticSuperReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference superclass 'super' inside static method";

        public StaticSuperReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class StaticOuterReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference outer class 'outer' inside static method";

        public StaticOuterReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UninitializedThisReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference current class from uninitialized class";

        public UninitializedThisReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UninitializedSuperReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference superclass from uninitialized class";

        public UninitializedSuperReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UninitializedOuterReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference outer class from uninitialized class";

        public UninitializedOuterReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidOuterReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot reference outer class 'outer' inside non-inner class";

        public InvalidOuterReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class UnknownSuperReference extends AnalyzingError {
        private static final String MESSAGE = "Cannot find superclass reference";

        public UnknownSuperReference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidConstructorCall extends AnalyzingError {
        private static final String MESSAGE = "Cannot use constructor call outside of constructor";

        public InvalidConstructorCall(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class MissingConstructorCall extends AnalyzingError {
        private static final String MESSAGE = "Missing super constructor call";

        public MissingConstructorCall(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InterfaceCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate interface";

        public InterfaceCreation(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class EnumCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate enum";

        public EnumCreation(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class StaticCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate static class";

        public StaticCreation(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InnerCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate inner class";

        public InnerCreation(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidInnerCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate non-inner nested class";

        public InvalidInnerCreation(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidTypeCreation extends AnalyzingError {
        private static final String MESSAGE = "Cannot instantiate type '%s'";

        public InvalidTypeCreation(Node node, Type type) {
            super(node, MESSAGE.formatted(type));
        }
    }

    public static class MissingMethodBody extends AnalyzingError {
        private static final String MESSAGE = "Missing method body";

        public MissingMethodBody(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class TypeInference extends AnalyzingError {
        private static final String MESSAGE = "Cannot infer type";

        public TypeInference(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidObjectCall extends AnalyzingError {
        private static final String MESSAGE = "Cannot call object with parameter types %s";

        public InvalidObjectCall(Node node, Type[] parameterTypes) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(parameterTypes)));
        }
    }

    public static class InvalidSwitchExpression extends AnalyzingError {
        private static final String MESSAGE = "Switch statement expression must a primitive integer value";

        public InvalidSwitchExpression(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedNumericalExpression extends AnalyzingError {
        private static final String MESSAGE = "Numerical expression expected";

        public ExpectedNumericalExpression(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class AmbiguousMethodCall extends AnalyzingError {
        private static final String MESSAGE = "Ambiguous method call";

        public AmbiguousMethodCall(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidMemberAccess extends AnalyzingError {
        private static final String MESSAGE = "Cannot access member with type '%s'";

        public InvalidMemberAccess(Node node, Type type) {
            super(node, MESSAGE.formatted(type));
        }
    }

    public static class InvalidArrayAccess extends AnalyzingError {
        private static final String MESSAGE = "Cannot access member with array type '%s'";

        public InvalidArrayAccess(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedArrayType extends AnalyzingError {
        private static final String MESSAGE = "Array type expected";

        public ExpectedArrayType(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidAssignment extends AnalyzingError {
        private static final String MESSAGE = "Cannot assign variable with type '%s'";

        public InvalidAssignment(Node node, Type type) {
            super(node, MESSAGE.formatted(type));
        }
    }

    public static class InvalidConstantAssignment extends AnalyzingError {
        private static final String MESSAGE = "Constant variable or field '%s' is already assigned";

        public InvalidConstantAssignment(Node node, String name) {
            super(node, MESSAGE.formatted(name));
        }
    }
}
