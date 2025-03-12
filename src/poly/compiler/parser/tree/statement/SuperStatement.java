package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The SuperStatement class. This class represents a super-constructor call statement.
 * @author Vincent Philippe (@vincent64)
 */
public class SuperStatement extends Node {
    private Node argumentList;

    public SuperStatement(Meta meta) {
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
        visitor.visitSuperStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitSuperStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("SuperStatement");
        string.addString("Arguments list:");
        string.addNode(argumentList);

        return string.toString();
    }
}
