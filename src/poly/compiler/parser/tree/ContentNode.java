package poly.compiler.parser.tree;

import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The ContentNode class. This class reprensents the root node of a source file content.
 * It has no parent, and contains only import statement and class declaration nodes.
 * Furthermore, a content node has no metadata.
 * @author Vincent Philippe (@vincent64)
 */
public class ContentNode extends Node {
    private List<Node> importations;
    private List<Node> classes;

    /**
     * Constructs a content node.
     */
    public ContentNode() {
        super(null);

        //Initialize importations and classes list
        importations = new ArrayList<>();
        classes = new ArrayList<>();
    }

    /**
     * Adds the given import statement node.
     * @param node the import node
     */
    public void addImport(Node node) {
        importations.add(node);
    }

    /**
     * Adds the given class declaration node.
     * @param node the class node
     */
    public void addClass(Node node) {
        classes.add(node);
    }

    /**
     * Returns the import declaration nodes.
     * @return the imports
     */
    public List<Node> getImports() {
        return importations;
    }

    /**
     * Returns the class declaration nodes.
     * @return the classes
     */
    public List<Node> getClasses() {
        return classes;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitContentNode(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitContentNode(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("Content");
        string.addString("Importations:");
        string.addNodes(importations);
        string.addString("Classes:");
        string.addNodes(classes);

        return string.toString();
    }
}
