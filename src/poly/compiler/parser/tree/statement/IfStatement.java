package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The IfStatement class. This class represents an if-statement, containing the
 * main condition, the statement block inside it, and an optional else condition and statements block.
 * @author Vincent Philippe (@vincent64)
 */
public class IfStatement extends Statement {
    private Expression condition;
    private Statement statementBlock;
    private Statement elseStatementBlock;

    public IfStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setStatementBlock(Statement node) {
        statementBlock = node;
    }

    public void setElseStatementBlock(Statement node) {
        elseStatementBlock = node;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getStatementBlock() {
        return statementBlock;
    }

    public Statement getElseStatementBlock() {
        return elseStatementBlock;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitIfStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitIfStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("IfStatement");
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Body:");
        string.addNode(statementBlock);
        string.addString("Else body:");
        string.addNode(elseStatementBlock);

        return string.toString();
    }
}
