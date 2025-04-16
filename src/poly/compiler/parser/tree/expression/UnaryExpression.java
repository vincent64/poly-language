package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The UnaryExpression class. This class represents an expression that has a single child node.
 * There are several type of unary expression, such as logical not, negation, etc.
 * Some of these operations are post-expression, including post increment and decrement.
 * The types of unary expression are defined by the Type enum.
 * @author Vincent Philippe (@vincent64)
 */
public class UnaryExpression extends Expression {
    private Expression expression;
    private Kind kind;

    public UnaryExpression(Meta meta) {
        super(meta);
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public void setType(Kind kind) {
        this.kind = kind;
    }

    public Expression getExpression() {
        return expression;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitUnaryExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("UnaryExpression",
                "kind=" + kind);
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }

    /**
     * The UnaryExpression.Type enum. This enum contains every type
     * of unary expression there exists.
     */
    public enum Kind {
        OPERATION_NEGATE("-"),
        LOGICAL_NOT("!"),
        BITWISE_NOT("~"),
        PRE_INCREMENT("++"),
        PRE_DECREMENT("--"),
        POST_INCREMENT("++"),
        POST_DECREMENT("--");

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        public boolean isIncrement() {
            return this == PRE_INCREMENT || this == POST_INCREMENT;
        }

        public boolean isDecrement() {
            return this == PRE_DECREMENT || this == POST_DECREMENT;
        }

        public boolean isPreIncrementDecrement() {
            return this == PRE_INCREMENT || this == PRE_DECREMENT;
        }

        public boolean isPostIncrementDecrement() {
            return this == POST_INCREMENT || this == POST_DECREMENT;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
