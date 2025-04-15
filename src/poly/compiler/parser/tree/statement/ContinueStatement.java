package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The ContinueStatement class. This class represents a continue-statement.
 * @author Vincent Philippe (@vincent64)
 */
public class ContinueStatement extends Statement {
    public ContinueStatement(Meta meta) {
        super(meta);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitContinueStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitContinueStatement(this);
    }

    @Override
    public String toString() {
        return "ContinueStatement";
    }
}
