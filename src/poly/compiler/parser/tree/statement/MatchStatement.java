package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The MatchStatement class. This class represents a match-statement, i.e. a statement
 * where each cases represent an if-else branch. Unlike the switch-statement, this
 * statement doesn't have a general expression, only cases.
 * @author Vincent Philippe (@vincent64)
 */
public class MatchStatement extends Statement {
    private Statement[] cases;

    public MatchStatement(Meta meta) {
        super(meta);

        //Initialize cases array
        cases = new Statement[0];
    }

    public void addCase(Statement node) {
        cases = (Statement[]) add(cases, node);
    }

    public Statement[] getCases() {
        return cases;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMatchStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitMatchStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("MatchStatement");
        string.addString("Cases:");
        string.addNodes(cases);

        return string.toString();
    }
}
