package poly.compiler.parser.tree;

import poly.compiler.output.content.AccessModifier;
import poly.compiler.parser.tree.statement.Statement;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The FieldDeclaration class. This class represents a field declaration, i.e. a node
 * with a variable declaration, and sometimes an initialization expression with it.
 * @author Vincent Philippe (@vincent64)
 */
public class FieldDeclaration extends Node {
    private AccessModifier accessModifier = AccessModifier.DEFAULT;
    private Statement variable;
    private boolean isStatic;

    /**
     * Constructs a field declaration with the given metadata information.
     * @param meta the metadata information
     */
    public FieldDeclaration(Meta meta) {
        super(meta);
    }

    /**
     * Sets the field access modifier from the given token.
     * @param token the token
     */
    public void setAccessModifier(Token token) {
        accessModifier = AccessModifier.findAccessModifier(token.getContent());
    }

    /**
     * Sets the field variable declaration node.
     * @param node the variable declaration node
     */
    public void setVariable(Statement node) {
        variable = node;
    }

    /**
     * Sets the current field declaration as static.
     */
    public void setStatic() {
        isStatic = true;
    }

    /**
     * Returns the field access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    /**
     * Returns the variable declaration node.
     * @return the variable declaration
     */
    public Statement getVariable() {
        return variable;
    }

    /**
     * Returns whether the field declaration is static.
     * @return true if the field is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitFieldDeclaration(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitFieldDeclaration(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("FieldDeclaration",
                "accessModifier=" + accessModifier,
                "isStatic=" + isStatic);
        string.addString("Variable:");
        string.addNode(variable);

        return string.toString();
    }
}
