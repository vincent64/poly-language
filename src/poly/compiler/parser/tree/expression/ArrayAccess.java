package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ArrayAccess class. This class represents an array access expression, and contains
 * the array being accessed and the access expression.
 * @author Vincent Philippe (@vincent64)
 */
public class ArrayAccess extends Expression {
    private Expression array;
    private Expression accessExpression;

    public ArrayAccess(Meta meta) {
        super(meta);
    }

    public void setArray(Expression node) {
        array = node;
    }

    public void setAccessExpression(Expression node) {
        accessExpression = node;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getAccessExpression() {
        return accessExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitArrayAccess(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
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
