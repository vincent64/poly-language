package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The SuperExpression class. This class represents a super-expression.
 * @author Vincent Philippe (@vincent64)
 */
public class SuperExpression extends Expression {
    public SuperExpression(Meta meta) {
        super(meta);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSuperExpression(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitSuperExpression(this);
    }

    @Override
    public String toString() {
        return "SuperExpression: super";
    }
}
