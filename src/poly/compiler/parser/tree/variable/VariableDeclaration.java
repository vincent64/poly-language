package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.parser.tree.statement.Statement;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The VariableDeclaration class. This class represents a variable declaration, and contains
 * the type of the variable, its name, whether it is constant or not, and an optional
 * initialization expression.
 * @author Vincent Philippe (@vincent64)
 */
public class VariableDeclaration extends Statement {
    private Node type;
    private String name;
    private boolean isConstant;
    private Expression initializationExpression;

    public VariableDeclaration(Meta meta) {
        super(meta);
    }

    public void setType(Node node) {
        type = node;
    }

    public void setName(Token token) {
        name = String.valueOf(token.getContent());
    }

    public void setConstant() {
        isConstant = true;
    }

    public void setInitializationExpression(Expression node) {
        initializationExpression = node;
    }

    public Node getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public Expression getInitializationExpression() {
        return initializationExpression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitVariableDeclaration(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
        return modifier.visitVariableDeclaration(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("VariableDeclaration",
                "name=" + name,
                "isConstant=" + isConstant);
        string.addString("Type:");
        string.addNode(type);
        string.addString("Initialization expression:");
        string.addNode(initializationExpression);

        return string.toString();
    }
}
