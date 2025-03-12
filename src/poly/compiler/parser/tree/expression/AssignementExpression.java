package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The AssignementExpression class. This class represents an assignement-expression,
 * and contains the assignement kind, the variable being assigned and the
 * assignement expression.
 * @author Vincent Philippe (@vincent64)
 */
public class AssignementExpression extends Expression {
    private Kind kind;
    private Node variable;
    private Node expression;

    public AssignementExpression(Meta meta) {
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
        visitor.visitVariableAssignement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitVariableAssignement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("AssignementExpression",
                "kind=" + kind);
        string.addString("Variable:");
        string.addNode(variable);
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }

    /**
     * The AssignementExpression.Kind enum. This enum contains every kind of
     * assignement expression there exists.
     */
    public enum Kind {
        ASSIGNEMENT,
        ASSIGNEMENT_ADDITION,
        ASSIGNEMENT_SUBTRACTION,
        ASSIGNEMENT_MULTIPLICATION,
        ASSIGNEMENT_DIVISION,
        ASSIGNEMENT_MODULO,
        ASSIGNEMENT_BITWISE_AND,
        ASSIGNEMENT_BITWISE_XOR,
        ASSIGNEMENT_BITWISE_OR,
        ASSIGNEMENT_SHIFT_LEFT,
        ASSIGNEMENT_SHIFT_RIGHT,
        ASSIGNEMENT_SHIFT_RIGHT_ARITHMETIC
    }
}
