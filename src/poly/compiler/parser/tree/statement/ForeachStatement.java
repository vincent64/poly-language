package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

/**
 * The ForeachStatement class. This class represents a foreach-statement, containing the
 * variable declaration and iterable expression.
 * @author Vincent Philippe(@vincent64)
 */
public class ForeachStatement extends Statement {
    private Statement variableDeclaration;
    private Expression expression;
    private Statement body;

    public ForeachStatement(Meta meta) {
        super(meta);
    }

    public void setVariableDeclaration(Statement node) {
        variableDeclaration = node;
    }

    public void setExpression(Expression node) {
        expression = node;
    }

    public void setBody(Statement node) {
        body = node;
    }

    public Statement getVariableDeclaration() {
        return variableDeclaration;
    }

    public Expression getExpression() {
        return expression;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitForeachStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitForeachStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ForeachStatement");
        string.addString("Variable declaration:");
        string.addNode(variableDeclaration);
        string.addString("Expression:");
        string.addNode(expression);
        string.addString("Body:");
        string.addNode(body);

        return string.toString();
    }
}
