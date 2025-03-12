package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The ArrayCreation class. This class represents an array creation expression, and contains
 * the array type, the array initialization expressions and the array dimension.
 * @author Vincent Philippe (@vincent64)
 */
public class ArrayCreation extends Expression {
    private Node type;
    private Node initializationExpression;

    public ArrayCreation(Meta meta) {
        super(meta);
    }

    public void setType(Node node) {
        type = node;
    }

    public void setInitializationExpression(Node node) {
        initializationExpression = node;
    }

    public Node getType() {
        return type;
    }

    public Node getInitializationExpression() {
        return initializationExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitArrayCreation(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitArrayCreation(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ArrayCreation");
        string.addString("Type:");
        string.addNode(type);
        string.addString("Initialization expression:");
        string.addNode(initializationExpression);

        return string.toString();
    }
}
