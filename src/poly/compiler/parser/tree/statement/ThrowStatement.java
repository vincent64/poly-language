package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The ThrowStatement class. This class represents a throw-statement, containing
 * the expression to throw.
 * @author Vincent Philippe (@vincent64)
 */
public class ThrowStatement extends Statement {
    private Expression expression;

    public ThrowStatement(Meta meta) {
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
        visitor.visitThrowStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitThrowStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ThrowStatement");
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }
}
