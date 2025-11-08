package poly.compiler.parser.tree;

import poly.compiler.analyzer.content.OperatorMethod;
import poly.compiler.analyzer.content.SpecialMethod;
import poly.compiler.output.content.AccessModifier;
import poly.compiler.parser.tree.statement.Statement;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The MethodDeclaration class. This class represents a method declaration, i.e. a node
 * with method attributes, such as the method name, method access modifier, etc.
 * and its statement block.
 * @author Vincent Philippe (@vincent64)
 */
public class MethodDeclaration extends Node {
    private AccessModifier accessModifier = AccessModifier.DEFAULT;
    private String name;
    private Node returnType;
    private Node parameterList;
    private Statement body;
    private boolean isStatic, isConstant;
    private boolean isConstructor, isStaticConstructor, isOperator;
    private boolean isEmpty;

    /**
     * Constructs a method declaration node with the given metadata information.
     * @param meta the metadata information
     */
    public MethodDeclaration(Meta meta) {
        super(meta);
    }

    /**
     * Sets the method access modifier from the given token.
     * @param token the token
     */
    public void setAccessModifier(Token token) {
        accessModifier = AccessModifier.findAccessModifier(token.getContent());
    }

    /**
     * Sets the method name from the given token.
     * @param token the token
     */
    public void setName(Token token) {
        name = !isOperator
                ? String.valueOf(token.getContent())
                : OperatorMethod.getNameFromOperator(token.getContent());
    }

    /**
     * Sets the given return type node.
     * @param node the return type node
     */
    public void setReturnType(Node node) {
        returnType = node;
    }

    /**
     * Sets the given parameters list node.
     * @param node the parameters list node
     */
    public void setParameterList(Node node) {
        parameterList = node;
    }

    /**
     * Sets the method declaration body node.
     * @param node the body node
     */
    public void setBody(Statement node) {
        body = node;
    }

    /**
     * Sets the current method declaration as static.
     */
    public void setStatic() {
        isStatic = true;
    }

    /**
     * Sets the current method declaration as constant.
     */
    public void setConstant() {
        isConstant = true;
    }

    /**
     * Sets the current method declaration as a constructor.
     * The name of the method will be automatically set.
     */
    public void setConstructor() {
        isConstructor = true;
        name = SpecialMethod.Name.CONSTRUCTOR;
    }

    /**
     * Sets the current method declaration as a static constructor.
     * The name of the method will be automatically set.
     */
    public void setStaticConstructor() {
        isStaticConstructor = true;
        name = SpecialMethod.Name.STATIC_CONSTRUCTOR;
    }

    /**
     * Sets the current method declaration as operator overload.
     */
    public void setOperator() {
        isOperator = true;
    }

    /**
     * Sets the method declaration as empty (i.e. abstract).
     */
    public void setEmpty() {
        isEmpty = true;
    }

    /**
     * Returns the method access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    /**
     * Returns the method name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the method return type node.
     * @return the return type node
     */
    public Node getReturnType() {
        return returnType;
    }

    /**
     * Returns the method parameters list node.
     * @return the parameters list node
     */
    public Node getParameterList() {
        return parameterList;
    }

    /**
     * Returns the method declaration body node.
     * @return the method body node
     */
    public Statement getBody() {
        return body;
    }

    /**
     * Returns whether the method declaration is static.
     * @return true if the method is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Returns whether the method declaration is constant.
     * @return true if the method is constant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Returns whether the method declaration is a constructor.
     * @return true if the method is a constructor
     */
    public boolean isConstructor() {
        return isConstructor;
    }

    /**
     * Returns whether the method declaration is a static constructor.
     * @return true if the method is a static constructor.
     */
    public boolean isStaticConstructor() {
        return isStaticConstructor;
    }

    /**
     * Returns whether the method declaration is an operator overload.
     * @return true if the method is an operator overload
     */
    public boolean isOperator() {
        return isOperator;
    }

    /**
     * Returns whether the method declaration is empty (i.e. abstract).
     * @return true if the method is empty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMethodDeclaration(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitMethodDeclaration(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("MethodDeclaration",
                "accessModifier=" + accessModifier,
                "name=" + name,
                "isStatic=" + isStatic,
                "isConstant=" + isConstant,
                "isConstructor=" + isConstructor,
                "isOperator=" + isOperator,
                "isEmpty=" + isEmpty);
        string.addString("Return type:");
        if(returnType != null) string.addNode(returnType);
        else string.addString("void");
        string.addString("Parameters list:");
        string.addNode(parameterList);
        string.addString("Body:");
        string.addNode(body);

        return string.toString();
    }
}
