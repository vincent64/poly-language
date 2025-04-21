package poly.compiler.util;

import poly.compiler.parser.tree.Node;

import java.util.List;

/**
 * The NodeStringifier class. This class is used to create a string representation of a node
 * in an Abstract Syntax Tree (AST). This tool may call the toString method of nodes recursively.
 * @author Vincent Philippe (@vincent64)
 */
public class NodeStringifier {
    private static final int INDENT = 3;
    private final StringBuilder stringBuilder;

    /**
     * Constructs a node stringifier with the given node name and node attributes.
     * @param name the node name
     * @param attributes the node attributes
     */
    public NodeStringifier(String name, String... attributes) {
        stringBuilder = new StringBuilder(name);

        //Add attributes inside parenthesis
        if(attributes.length > 0) {
            stringBuilder.append("(");

            for(int i = 0; i < attributes.length; i++) {
                stringBuilder.append(attributes[i]);

                //Add comma between attributes
                if(i < attributes.length - 1)
                    stringBuilder.append(", ");
            }

            stringBuilder.append(")");
        }

        //Add line break at the end
        stringBuilder.append(":\n");
    }

    /**
     * Adds the given string on a new line at the same indentation level.
     * @param string the string
     */
    public void addString(String string) {
        stringBuilder.append(string).append("\n");
    }

    /**
     * Adds the string representation of the given node on a new indentation level.
     * @param node the node (nullable)
     */
    public void addNode(Node node) {
        if(node != null)
            stringBuilder.append(node.toString().indent(INDENT));
    }

    /**
     * Adds the string representations of the given nodes on a new indentation level.
     * @param nodes the nodes (nullable)
     */
    public void addNodes(List<? extends Node> nodes) {
        for(Node node : nodes) {
            if(node != null)
                stringBuilder.append(node.toString().indent(INDENT));
        }
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
