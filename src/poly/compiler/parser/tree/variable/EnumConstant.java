package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The Constant class. This class represents a single constant in an enum class.
 * It contains the constant name and an optional arguments list.
 * @author Vincent Philippe (@vincent64)
 */
public class EnumConstant extends Node {
    private String name;
    private Node argumentList;

    public EnumConstant(Meta meta) {
        super(meta);
    }

    public void setName(Token token) {
        name = String.valueOf(token.getContent());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArgumentList(Node node) {
        argumentList = node;
    }

    public String getName() {
        return name;
    }

    public Node getArgumentList() {
        return argumentList;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitEnumConstant(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitEnumConstant(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("EnumConstant",
                "name=" + name);
        string.addString("Arguments list:");
        string.addNode(argumentList);

        return string.toString();
    }
}
