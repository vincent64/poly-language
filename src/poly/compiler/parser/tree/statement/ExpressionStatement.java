package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The ExpressionStatement class. This class represents an expression that acts
 * as a standalone statement. It only contains the expression of the statement.
 * @author Vincent Philippe (@vincent64)
 */
public class ExpressionStatement extends Statement {
    private Expression expression;

    public ExpressionStatement(Meta meta) {
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
        visitor.visitExpressionStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitExpressionStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ExpressionStatement");
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }
}
