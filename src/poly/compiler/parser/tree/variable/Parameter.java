package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The Parameter class. This class represents a single parameter in a method declaration.
 * It contains the parameter type, name and whether it is constant or not.
 * @author Vincent Philippe (@vincent64)
 */
public class Parameter extends Node {
    private Node type;
    private String name;
    private boolean isConstant;

    public Parameter(Meta meta) {
        super(meta);
    }

    public void setType(Node node) {
        type = node;
    }

    public void setName(Token token) {
        name = String.valueOf(token.getContent());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConstant() {
        isConstant = true;
    }

    public Node getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitParameter(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitParameter(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("Parameter",
                "name=" + name,
                "isConstant=" + isConstant);
        string.addString("Type:");
        string.addNode(type);

        return string.toString();
    }
}
