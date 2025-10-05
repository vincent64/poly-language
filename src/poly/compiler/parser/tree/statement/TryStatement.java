package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The TryStatement class. This class represents a try-catch-statement, containing
 * the statement block, the exception parameter and the caught statement block.
 * @author Vincent Philippe (@vincent64)
 */
public class TryStatement extends Statement {
    private Statement body;
    private Statement catchBody;
    private Node exceptionParameter;

    public TryStatement(Meta meta) {
        super(meta);
    }

    public void setBody(Statement node) {
        body = node;
    }

    public void setCatchBody(Statement node) {
        catchBody = node;
    }

    public void setExceptionParameter(Node node) {
        exceptionParameter = node;
    }

    public Statement getBody() {
        return body;
    }

    public Statement getCatchBody() {
        return catchBody;
    }

    public Node getExceptionParameter() {
        return exceptionParameter;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitTryStatement(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitTryStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("TryStatement");
        string.addString("Body:");
        string.addNode(body);
        string.addString("Catch body:");
        string.addNode(catchBody);
        string.addString("Exception:");
        string.addNode(exceptionParameter);

        return string.toString();
    }
}
