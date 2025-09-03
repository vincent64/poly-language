package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The ConstantList class. This class represents the list of constants of an enum class.
 * @author Vincent Philippe (@vincent64)
 */
public class EnumConstantList extends Node {
    private List<Node> constants;

    public EnumConstantList(Meta meta) {
        super(meta);

        //Initialize constants list
        constants = new ArrayList<>();
    }

    public void addConstant(Node node) {
        constants.add(node);
    }

    public List<Node> getConstants() {
        return constants;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitEnumConstantList(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitEnumConstantList(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("EnumConstantList");
        string.addString("Constants:");
        string.addNodes(constants);

        return string.toString();
    }
}
