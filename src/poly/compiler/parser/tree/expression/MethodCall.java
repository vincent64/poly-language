package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The MethodCall class. This class represents a method invokation (or call), and contains
 * the name of the method being invoked, and the arguments list.
 * @author Vincent Philippe (@vincent64)
 */
public class MethodCall extends Expression {
    private String methodName;
    private Expression method;
    private Node argumentList;

    public MethodCall(Meta meta) {
        super(meta);
    }

    public void setMethodName(Token token) {
        methodName = String.valueOf(token.getContent());
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setMethod(Expression node) {
        method = node;
    }

    public void setArgumentList(Node node) {
        argumentList = node;
    }

    public String getMethodName() {
        return methodName;
    }

    public Expression getMethod() {
        return method;
    }

    public Node getArgumentList() {
        return argumentList;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMethodCall(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitMethodCall(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("MethodCall",
                "name=" + methodName);
        string.addString("Method:");
        string.addNode(method);
        string.addString("Arguments list:");
        string.addNode(argumentList);

        return string.toString();
    }
}
