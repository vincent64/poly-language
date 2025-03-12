package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The MatchStatement class. This class represents a match-statement, i.e. a statement
 * where each cases represent an if-else branch. Unlike the switch-statement, this
 * statement doesn't have a general expression, only cases.
 * @author Vincent Philippe (@vincent64)
 */
public class MatchStatement extends Node {
    private Node[] cases;

    public MatchStatement(Meta meta) {
        super(meta);

        //Initialize cases array
        cases = new Node[0];
    }

    public void addCase(Node node) {
        cases = add(cases, node);
    }

    public Node[] getCases() {
        return cases;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMatchStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
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
