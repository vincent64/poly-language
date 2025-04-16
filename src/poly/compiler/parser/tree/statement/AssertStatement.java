package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The AssertStatement class. This class represents an assert-statement, containg a
 * boolean expression that must evaluate to true in order to avoid throwing an exception.
 * @author Vincent Philippe (@vincent64)
 */
public class AssertStatement extends Statement {
    private Expression condition;

    public AssertStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitAssertStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitAssertStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("AssertStatement");
        string.addString("Condition:");
        string.addNode(condition);

        return string.toString();
    }
}
