package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ForStatement class. This class represents a for-statement, containing the
 * variable initialization, condition, incrementation and statement block inside it.
 * This class does not represent a foreach-statement.
 * @author Vincent Philippe (@vincent64)
 */
public class ForStatement extends Statement {
    private Node variableInitialization;
    private Node condition;
    private Node incrementExpression;
    private Node statementBlock;

    public ForStatement(Meta meta) {
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

    public void setStatementBlock(Node node) {
        statementBlock = node;
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

    public Node getStatementBlock() {
        return statementBlock;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitForStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitForStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ForStatement");
        string.addString("Variable initialization:");
        string.addNode(variableInitialization);
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Increment expression:");
        string.addNode(incrementExpression);
        string.addString("Body:");
        string.addNode(statementBlock);

        return string.toString();
    }
}
