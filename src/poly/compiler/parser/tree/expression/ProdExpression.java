package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ProdExpression class. This class represents a product-expression, and contains
 * the variable initialization, the condition expression, the increment expression
 * and the body expression of the product.
 * @author Vincent Philippe (@vincent64)
 */
public class ProdExpression extends Expression {
    private Node variableInitialization;
    private Node condition;
    private Node incrementExpression;
    private Node expression;

    public ProdExpression(Meta meta) {
        super(meta);
    }

    public void setVariableInitialization(Node node) {
        variableInitialization = node;
    }

    public void setCondition(Node node) {
        condition = node;
    }

    public void setIncrementExpression(Node node) {
        incrementExpression = node;
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public Node getVariableInitialization() {
        return variableInitialization;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getIncrementExpression() {
        return incrementExpression;
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitProdExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitProdExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ProdExpression");
        string.addString("Variable initialization:");
        string.addNode(variableInitialization);
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Increment expression:");
        string.addNode(incrementExpression);
        string.addString("Expression:");
        string.addNode(expression);

        return string.toString();
    }
}
