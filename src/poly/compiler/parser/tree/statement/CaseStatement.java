package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The Case class. This class represents a case-statement in either a switch-statement,
 * or a match-statement. It contains the case expression and its statements block.
 * @author Vincent Philippe (@vincent64)
 */
public class CaseStatement extends Node {
    private Node expression;
    private Node statementBlock;

    public CaseStatement(Meta meta) {
        super(meta);
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public void setStatementBlock(Node node) {
        statementBlock = node;
    }

    public Node getExpression() {
        return expression;
    }

    public Node getStatementBlock() {
        return statementBlock;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitCaseStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitCaseStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("CaseStatement");
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Body:");
        string.addNode(statementBlock);

        return string.toString();
    }
}
