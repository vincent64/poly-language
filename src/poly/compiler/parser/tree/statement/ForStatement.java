package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The ForStatement class. This class represents a for-statement, containing the
 * variable initialization, condition, incrementation and statement block inside it.
 * This class does not represent a foreach-statement.
 * @author Vincent Philippe (@vincent64)
 */
public class ForStatement extends Statement {
    private Statement statement;
    private Expression condition;
    private Statement expression;
    private Statement body;

    public ForStatement(Meta meta) {
        super(meta);
    }

    public void setStatement(Statement node) {
        statement = node;
    }

    public void setCondition(Expression node) {
        condition = node;
    }

    public void setExpression(Statement node) {
        expression = node;
    }

    public void setBody(Statement node) {
        body = node;
    }

    public Statement getStatement() {
        return statement;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getExpression() {
        return expression;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitForStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitForStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ForStatement");
        string.addString("Statement:");
        string.addNode(statement);
        string.addString("Condition:");
        string.addNode(condition);
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Body:");
        string.addNode(body);

        return string.toString();
    }
}
