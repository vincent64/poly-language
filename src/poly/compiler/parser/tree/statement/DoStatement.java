package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The DoStatement class. This class represents a do-while-statement, and contains
 * the condition to be verified after every iteration, and the block of statements
 * to be executed for each iteration.
 * @author Vincent Philippe (@vincent64)
 */
public class DoStatement extends Statement {
    private Expression condition;
    private Statement statementBlock;

    public DoStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setStatementBlock(Statement node) {
        statementBlock = node;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getStatementBlock() {
        return statementBlock;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitDoStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitDoStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("DoStatement");
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Body:");
        string.addNode(statementBlock);

        return string.toString();
    }
}
