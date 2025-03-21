package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The AssignmentExpression class. This class represents an assignment-expression,
 * and contains the assignment kind, the variable being assigned and the
 * assignment expression.
 * @author Vincent Philippe (@vincent64)
 */
public class AssignmentExpression extends Expression {
    private Kind kind;
    private Node variable;
    private Node expression;

    public AssignmentExpression(Meta meta) {
        super(meta);
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setVariable(Node node) {
        variable = node;
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public Kind getKind() {
        return kind;
    }

    public Node getVariable() {
        return variable;
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitAssignmentExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitAssignmentExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("AssignmentExpression",
                "kind=" + kind);
        string.addString("Variable:");
        string.addNode(variable);
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }

    /**
     * The AssignmentExpression.Kind enum. This enum contains every kind of
     * assignment expression there exists.
     */
    public enum Kind {
        ASSIGNMENT,
        ASSIGNMENT_ADDITION,
        ASSIGNMENT_SUBTRACTION,
        ASSIGNMENT_MULTIPLICATION,
        ASSIGNMENT_DIVISION,
        ASSIGNMENT_MODULO,
        ASSIGNMENT_BITWISE_AND,
        ASSIGNMENT_BITWISE_XOR,
        ASSIGNMENT_BITWISE_OR,
        ASSIGNMENT_SHIFT_LEFT,
        ASSIGNMENT_SHIFT_RIGHT,
        ASSIGNMENT_SHIFT_RIGHT_ARITHMETIC
    }
}
