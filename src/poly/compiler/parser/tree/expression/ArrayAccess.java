package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ArrayAccess class. This class represents an array access expression, and contains
 * the array being accessed and the access expression.
 * @author Vincent Philippe (@vincent64)
 */
public class ArrayAccess extends Expression {
    private Node array;
    private Node accessExpression;

    public ArrayAccess(Meta meta) {
        super(meta);
    }

    public void setArray(Node node) {
        array = node;
    }

    public void setAccessExpression(Node node) {
        accessExpression = node;
    }

    public Node getArray() {
        return array;
    }

    public Node getAccessExpression() {
        return accessExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitArrayAccess(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitArrayAccess(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ArrayAccess");
        string.addString("Array:");
        string.addNode(array);
        string.addString("Access expression:");
        string.addNode(accessExpression);

        return string.toString();
    }
}
