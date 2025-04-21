package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The SwitchStatement class. This class represents a switch-statement, containing the
 * main expression and the cases to execute.
 * @author Vincent Philippe (@vincent64)
 */
public class SwitchStatement extends Statement {
    private Expression expression;
    private List<Statement> cases;

    public SwitchStatement(Meta meta) {
        super(meta);

        //Initialize cases list
        cases = new ArrayList<>();
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public void addCase(Statement node) {
        cases.add(node);
    }

    public Expression getExpression() {
        return expression;
    }

    public List<Statement> getCases() {
        return cases;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
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
