package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The OuterExpression class. This class represents an outer-expression.
 * @author Vincent Philippe (@vincent64)
 */
public class OuterExpression extends Expression {
    public OuterExpression(Meta meta) {
        super(meta);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitOuterExpression(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitOuterExpression(this);
    }

    @Override
    public String toString() {
        return "OuterExpression: outer";
    }
}
