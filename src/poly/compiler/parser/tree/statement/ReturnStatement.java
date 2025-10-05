package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The ReturnStatement class. This class represents a return statement, and contains
 * the return expression.
 * @author Vincent Philippe (@vincent64)
 */
public class ReturnStatement extends Statement {
    private Expression expression;

    public ReturnStatement(Meta meta) {
        super(meta);
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitReturnStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitReturnStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ReturnStatement");
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }
}
