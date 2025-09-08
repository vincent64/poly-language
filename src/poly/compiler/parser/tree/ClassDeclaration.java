package poly.compiler.parser.tree;

import poly.compiler.output.content.AccessModifier;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;
import poly.compiler.resolver.symbol.ClassSymbol.Kind;

import java.util.ArrayList;
import java.util.List;

/**
 * The ClassDeclaration class. This class represents a class declaration node,
 * with class attributes, such at the class name, superclass name, access modifier, etc.
 * It also contains method declarations, field declarations, and nested class declarations.
 * This node is also the node used for an interface declaration.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassDeclaration extends Node {
    private AccessModifier accessModifier = AccessModifier.DEFAULT;
    private Kind kind = Kind.CLASS;
    private String name;
    private Node superclass;
    private List<Node> interfaces;
    private List<Node> fieldNodes;
    private List<Node> methodNodes;
    private List<Node> nestedClasses;
    private Node constantList;
    private boolean isStatic, isConstant;

    /**
     * Constructs a class declaration node with the given metadata information.
     * @param meta the metdata information
     */
    public ClassDeclaration(Meta meta) {
        super(meta);

        //Initialize interface names list
        interfaces = new ArrayList<>();

        //Initialize fields, methods and nested classes list
        fieldNodes = new ArrayList<>();
        methodNodes = new ArrayList<>();
        nestedClasses = new ArrayList<>();
    }

    /**
     * Sets the class access modifier from the given token.
     * @param token the token
     */
    public void setAccessModifier(Token token) {
        accessModifier = AccessModifier.findAccessModifier(token.getContent());
    }

    /**
     * Sets the class name from the given token.
     * @param token the token
     */
    public void setName(Token token) {
        name = String.valueOf(token.getContent());
    }

    /**
     * Sets the given superclass node.
     * @param node the superclass node
     */
    public void setSuperclass(Node node) {
        superclass = node;
    }

    /**
     * Adds the given interface node.
     * @param node the interface node
     */
    public void addInterface(Node node) {
        interfaces.add(node);
    }

    /**
     * Adds the given field declaration node.
     * @param node the field node
     */
    public void addField(Node node) {
        fieldNodes.add(node);
    }

    /**
     * Adds the given method declaration node.
     * @param node the methode node
     */
    public void addMethod(Node node) {
        methodNodes.add(node);
    }

    /**
     * Adds the given nested class declaration node.
     * @param node the nested class node
     */
    public void addNestedClass(Node node) {
        nestedClasses.add(node);
    }

    /**
     * Sets the enum constants list node.
     * @param node the constants list node
     */
    public void setConstantList(Node node) {
        constantList = node;
    }

    /**
     * Sets the current class declaration as static.
     */
    public void setStatic() {
        isStatic = true;
    }

    /**
     * Sets the current class declaration as constant.
     */
    public void setConstant() {
        isConstant = true;
    }

    /**
     * Sets the current class declaration as an interface.
     */
    public void setInterface() {
        kind = Kind.INTERFACE;
    }

    /**
     * Sets the current class declaration as inner.
     */
    public void setInner() {
        kind = Kind.INNER;
    }

    /**
     * Sets the current class declaration as an exception.
     */
    public void setException() {
        kind = Kind.EXCEPTION;
    }

    /**
     * Returns the class access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    /**
     * Returns the class kind.
     * @return the kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the class name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the superclass node.
     * @return the superclass
     */
    public Node getSuperclass() {
        return superclass;
    }

    /**
     * Returns the interface nodes.
     * @return the interfaces
     */
    public List<Node> getInterfaces() {
        return interfaces;
    }

    /**
     * Returns the field declaration nodes.
     * @return the fields
     */
    public List<Node> getFields() {
        return fieldNodes;
    }

    /**
     * Returns the method declaration nodes.
     * @return the methods
     */
    public List<Node> getMethods() {
        return methodNodes;
    }

    /**
     * Returns the nested class declaration nodes.
     * @return the nested classes
     */
    public List<Node> getNestedClasses() {
        return nestedClasses;
    }

    /**
     * Returns the enum constants list node.
     * @return the constants list node
     */
    public Node getConstantList() {
        return constantList;
    }

    /**
     * Returns whether the class declaration is static.
     * @return true if the class is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Returns whether the class declaration is constant.
     * @return true if the class is constant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Returns whether the class declaration is an interface.
     * @return true if the class is an interface
     */
    public boolean isInterface() {
        return kind == Kind.INTERFACE;
    }

    /**
     * Returns whether the class declaration is inner.
     * @return true if the class is inner
     */
    public boolean isInner() {
        return kind == Kind.INNER;
    }

    /**
     * Returns whether the class declaration is an exception.
     * @return true if the class is an exception
     */
    public boolean isException() {
        return kind == Kind.EXCEPTION;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitClassDeclaration(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitClassDeclaration(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ClassDeclaration",
                "accessModifier=" + accessModifier,
                "kind=" + kind,
                "name=" + name,
                "isStatic=" + isStatic,
                "isConstant=" + isConstant);
        string.addString("Superclass:");
        string.addNode(superclass);
        string.addString("Interfaces:");
        string.addNodes(interfaces);
        string.addString("Fields:");
        string.addNodes(fieldNodes);
        string.addString("Methods:");
        string.addNodes(methodNodes);
        string.addString("Nested classes:");
        string.addNodes(nestedClasses);
        string.addString("Constants list:");
        string.addNode(constantList);

        return string.toString();
    }
}
