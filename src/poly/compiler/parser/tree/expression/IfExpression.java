package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
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
    private Node condition;
    private Node expression;
    private Node elseExpression;

    public IfExpression(Meta meta) {
        super(meta);
    }

    public void setCondition(Node node) {
        condition = node;
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public void setElseExpression(Node node) {
        elseExpression = node;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getExpression() {
        return expression;
    }

    public Node getElseExpression() {
        return elseExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitIfExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
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
