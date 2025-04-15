package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;

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
}
