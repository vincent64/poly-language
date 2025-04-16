package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The ThisExpression class. This class represents a this-expression.
 * @author Vincent Philippe (@vincent64)
 */
public class ThisExpression extends Expression {
    public ThisExpression(Meta meta) {
        super(meta);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitThisExpression(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitThisExpression(this);
    }

    @Override
    public String toString() {
        return "ThisExpression: this";
    }
}
