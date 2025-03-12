package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The AssertStatement class. This class represents an assert-statement, containg a
 * boolean expression that must evaluate to true in order to avoid throwing an exception.
 * @author Vincent Philippe (@vincent64)
 */
public class AssertStatement extends Node {
    private Node condition;

    public AssertStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Node node) {
        condition = node;
    }

    public Node getCondition() {
        return condition;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitAssertStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitAssertStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("AssertStatement");
        string.addString("Condition:");
        string.addNode(condition);

        return string.toString();
    }
}
