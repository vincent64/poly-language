package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The IfExpression class. This class represents an if-expression, and contains
 * the condition expression, the first expression and the else-condition expression.
 * Unlike the if-statement, the if- and else-body are expressions, and not statements.
 * @author Vincent Philippe (@vincent64)
 */
public class IfExpression extends Expression {
    private Expression condition;
    private Expression expression;
    private Expression elseExpression;

    public IfExpression(Meta meta) {
        super(meta);
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public void setElseExpression(Expression node) {
        elseExpression = node;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getExpression() {
        return expression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitIfExpression(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitIfExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("IfExpression");
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Else expression:");
        string.addNode(elseExpression);

        return string.toString();
    }
}
