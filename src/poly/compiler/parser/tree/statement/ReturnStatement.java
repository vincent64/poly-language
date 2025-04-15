package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ReturnStatement class. This class represents a return statement, and contains
 * the return expression.
 * @author Vincent Philippe (@vincent64)
 */
public class ReturnStatement extends Statement {
    private Node expression;

    public ReturnStatement(Meta meta) {
        super(meta);
    }

    public void setExpression(Node expression) {
        this.expression = expression;
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitReturnStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitReturnStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ReturnStatement");
        string.addString("Expression:");
        string.addString(expression.toString());

        return string.toString();
    }
}
