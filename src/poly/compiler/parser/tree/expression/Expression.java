package poly.compiler.parser.tree.expression;

import poly.compiler.analyzer.type.Type;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;

/**
 * The Expression abstract class. This class represents an expression node, and extends from Node.
 * This class attaches a type to expression nodes extending from it.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Expression extends Node {
    /** The expression type. */
    private Type type;

    /**
     * Constructs an expression node with the given metadata information.
     * @param meta the metadata information
     */
    public Expression(Meta meta) {
        super(meta);
    }

    /**
     * Sets the given type to the expression.
     * @param type the type
     */
    public void setExpressionType(Type type) {
        this.type = type;
    }

    /**
     * Returns the expression type.
     * @return the expression type
     */
    public Type getExpressionType() {
        return type;
    }

    /**
     * Accepts the visit of the given node modifier and returns the expression.
     * @param modifier the node modifier
     * @return the expression
     */
    public abstract Expression accept(NodeModifier modifier);
}
