package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;

/**
 * The Statement abstract class. This class represents a statement node, and extends from Node.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Statement extends Node {
    /**
     * Constructs a statement node with the given metadata information.
     * @param meta the metadata information
     */
    public Statement(Meta meta) {
        super(meta);
    }

    /**
     * Accepts the visit of the given node modifier and returns the statement.
     * @param modifier the node modifier
     * @return the statement
     */
    public abstract Statement accept(NodeModifier modifier);
}
