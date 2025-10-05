package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The WhileStatement class. This class represents a while-statement, containing
 * the condition to be verified before every iteration, and the block of statements
 * to be executed for each iteration.
 * @author Vincent Philippe (@vincent64)
 */
public class WhileStatement extends Statement {
    private Expression condition;
    private Statement body;

    public WhileStatement(Meta meta) {
        super(meta);
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setBody(Statement node) {
        body = node;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitWhileStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitWhileStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("WhileStatement");
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Body:");
        string.addNode(body);

        return string.toString();
    }
}
