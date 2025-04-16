package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ClassCreation class. This class represents a class-creation expression, and contains
 * the class type and the constructor arguments list.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassCreation extends Expression {
    private Node type;
    private Node argumentList;

    public ClassCreation(Meta meta) {
        super(meta);
    }

    public void setType(Node node) {
        type = node;
    }

    public void setArgumentList(Node node) {
        argumentList = node;
    }

    public Node getType() {
        return type;
    }

    public Node getArgumentList() {
        return argumentList;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitClassCreation(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitClassCreation(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ClassCreation");
        string.addString("Type:");
        string.addNode(type);
        string.addString("Arguments list:");
        string.addNode(argumentList);

        return string.toString();
    }
}
