package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The BinaryExpression class. This class represents an expression that has two child nodes.
 * There are several kind of binary expression, including mathematical operations,
 * bitwise operations, logical comparison, etc. These kinds are defined by the Kind enum.
 * @author Vincent Philippe (@vincent64)
 */
public class BinaryExpression extends Expression {
    private Kind kind;
    private Node first, second;

    public BinaryExpression(Meta meta) {
        super(meta);
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setFirst(Node node) {
        first = node;
    }

    public void setSecond(Node node) {
        second = node;
    }

    public Kind getKind() {
        return kind;
    }

    public Node getFirst() {
        return first;
    }

    public Node getSecond() {
        return second;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitBinaryExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("BinaryExpression",
                "kind=" + kind);
        string.addString("First:");
        string.addNode(first);
        string.addString("Second:");
        string.addNode(second);

        return string.toString();
    }

    /**
     * The BinaryExpression.Kind enum. This enum contains every kind
     * of binary expression there exists.
     */
    public enum Kind {
        OPERATION_ADDITION("+"),
        OPERATION_SUBTRACTION("-"),
        OPERATION_MULTIPLICATION("*"),
        OPERATION_DIVISION("/"),
        OPERATION_MODULO("%"),
        EQUALITY_EQUAL("=="),
        EQUALITY_NOT_EQUAL("!="),
        COMPARISON_GREATER(">"),
        COMPARISON_GREATER_EQUAL(">="),
        COMPARISON_LESS("<"),
        COMPARISON_LESS_EQUAL("<="),
        COMPARISON_NULL("??"),
        COMPARISON_SPACESHIP("<=>"),
        REFERENCE_EQUAL("==="),
        REFERENCE_NOT_EQUAL("!=="),
        TYPE_EQUAL("==:"),
        TYPE_NOT_EQUAL("!=:"),
        LOGICAL_AND("&&"),
        LOGICAL_OR("||"),
        BITWISE_AND("&"),
        BITWISE_XOR("^"),
        BITWISE_OR("|"),
        BITWISE_SHIFT_LEFT("<<"),
        BITWISE_SHIFT_RIGHT(">>"),
        BITWISE_SHIFT_RIGHT_ARITHMETIC(">>>");

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        public boolean isShift() {
            return this == BITWISE_SHIFT_LEFT || this == BITWISE_SHIFT_RIGHT || this == BITWISE_SHIFT_RIGHT_ARITHMETIC;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
