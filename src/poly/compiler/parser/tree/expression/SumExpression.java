package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.statement.Statement;
import poly.compiler.util.NodeStringifier;

/**
 * The SumExpression class. This class represents a sum-expression, and contains
 * the variable initialization, the condition expression, the increment expression
 * and the body expression of the summation.
 * @author Vincent Philippe (@vincent64)
 */
public class SumExpression extends Expression {
    private Statement variableInitialization;
    private Expression condition;
    private Statement incrementExpression;
    private Expression expression;

    public SumExpression(Meta meta) {
        super(meta);
    }

    public void setVariableInitialization(Statement node) {
        variableInitialization = node;
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setIncrementExpression(Statement node) {
        incrementExpression = node;
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public Statement getVariableInitialization() {
        return variableInitialization;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getIncrementExpression() {
        return incrementExpression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSumExpression(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitSumExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("SumExpression");
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
