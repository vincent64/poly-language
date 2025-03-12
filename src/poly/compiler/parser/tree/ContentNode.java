package poly.compiler.parser.tree;

import poly.compiler.util.NodeStringifier;

/**
 * The ContentNode class. This class reprensents the root node of a source file content.
 * It has no parent, and contains only import statement and class declaration nodes.
 * Furthermore, a content node has no metadata.
 * @author Vincent Philippe (@vincent64)
 */
public class ContentNode extends Node {
    private Node[] importNodes;
    private Node[] classNodes;

    /**
     * Constructs a content node.
     */
    public ContentNode() {
        super(null);

        //Initialize import and class arrays
        importNodes = new Node[0];
        classNodes = new Node[0];
    }

    /**
     * Adds the given import statement node.
     * @param node the import node
     */
    public void addImport(Node node) {
        importNodes = add(importNodes, node);
    }

    /**
     * Adds the given class declaration node.
     * @param node the class node
     */
    public void addClass(Node node) {
        classNodes = add(classNodes, node);
    }

    /**
     * Returns the import declaration nodes.
     * @return the imports
     */
    public Node[] getImports() {
        return importNodes;
    }

    /**
     * Returns the class declaration nodes.
     * @return the classes
     */
    public Node[] getClasses() {
        return classNodes;
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
        string.addNodes(importNodes);
        string.addString("Classes:");
        string.addNodes(classNodes);

        return string.toString();
    }
}
