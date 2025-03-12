package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The SwitchStatement class. This class represents a switch-statement, containing the
 * main expression and the cases to execute.
 * @author Vincent Philippe (@vincent64)
 */
public class SwitchStatement extends Node {
    private Node expression;
    private Node[] cases;

    public SwitchStatement(Meta meta) {
        super(meta);

        //Initialize cases array
        cases = new Node[0];
    }

    public void setExpression(Node node) {
        expression = node;
    }

    public void addCase(Node node) {
        cases = add(cases, node);
    }

    public Node getExpression() {
        return expression;
    }

    public Node[] getCases() {
        return cases;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitSwitchStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("SwitchStatement");
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Cases:");
        string.addNodes(cases);

        return string.toString();
    }
}
