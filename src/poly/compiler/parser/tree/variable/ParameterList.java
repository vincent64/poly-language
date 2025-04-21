package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The ParameterList class. This class represents the list of parameters of a method
 * in the method declaration.
 * @author Vincent Philippe (@vincent64)
 */
public class ParameterList extends Node {
    private List<Node> parameters;

    public ParameterList(Meta meta) {
        super(meta);

        //Initialize parameters list
        parameters = new ArrayList<>();
    }

    public void addParameter(Node node) {
        parameters.add(node);
    }

    public void addFirstParameter(Node node) {
        parameters.addFirst(node);
    }

    public List<Node> getParameters() {
        return parameters;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitParameterList(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitParameterList(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ParameterList");
        string.addString("Parameters:");
        string.addNodes(parameters);

        return string.toString();
    }
}
