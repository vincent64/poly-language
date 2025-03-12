package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The ArrayType class. This class represents an array type node.
 * @author Vincent Philippe (@vincent64)
 */
public class ArrayType extends Expression {
    private Node type;

    public ArrayType(Meta meta) {
        super(meta);
    }

    public void setType(Node node) {
        type = node;
    }

    public Node getType() {
        return type;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitArrayType(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitArrayType(this);
    }

    @Override
    public String toString() {
        return type.toString() + "[]";
    }
}
