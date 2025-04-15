package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The WhileStatement class. This class represents a while-statement, containing
 * the condition to be verified before every iteration, and the block of statements
 * to be executed for each iteration.
 * @author Vincent Philippe (@vincent64)
 */
public class WhileStatement extends Statement {
    private Node condition;
    private Node statementBlock;

    public WhileStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Node node) {
        condition = node;
    }

    public void setStatementBlock(Node node) {
        statementBlock = node;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getStatementBlock() {
        return statementBlock;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitWhileStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitWhileStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("WhileStatement");
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Body:");
        string.addNode(statementBlock);

        return string.toString();
    }
}
