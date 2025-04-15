package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ThisStatement class. This class represents a this-constructor call statement.
 * @author Vincent Philippe (@vincent64)
 */
public class ThisStatement extends Statement {
    private Node argumentList;

    public ThisStatement(Meta meta) {
        super(meta);
    }

    public void setArgumentList(Node node) {
        argumentList = node;
    }

    public Node getArgumentList() {
        return argumentList;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitThisStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitThisStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ThisStatement");
        string.addString("Arguments list:");
        string.addNode(argumentList);

        return string.toString();
    }
}
