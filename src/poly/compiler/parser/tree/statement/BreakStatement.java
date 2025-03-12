package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The BreakStatement class. This class represents a break-statement.
 * @author Vincent Philippe (@vincent64)
 */
public class BreakStatement extends Node {
    public BreakStatement(Meta meta) {
        super(meta);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitBreakStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitBreakStatement(this);
    }

    @Override
    public String toString() {
        return "BreakStatement";
    }
}
