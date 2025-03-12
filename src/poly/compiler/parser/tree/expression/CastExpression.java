package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The CastExpression class. This class represents a type-casting expression, and contains
 * the expression that is being cast and the cast type.
 * @author Vincent Philippe (@vincent64)
 */
public class CastExpression extends Expression {
    private Node expression;
    private Node castType;

    public CastExpression(Meta meta) {
        super(meta);
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public void setCastType(Node node) {
        castType = node;
    }

    public Node getExpression() {
        return expression;
    }

    public Node getCastType() {
        return castType;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitCastExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitCastExpression(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("CastExpression");
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Cast type:");
        string.addNode(castType);

        return string.toString();
    }
}
