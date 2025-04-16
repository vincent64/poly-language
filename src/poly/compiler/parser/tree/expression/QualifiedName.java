package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.ClassName;

/**
 * The QualifiedName class. This class represents a qualified name.
 * A qualified name contains the current name and a node, which can either be another
 * qualified name, or a simple name. A qualified name can be the fully qualified name of a class.
 * It can also represent a static member access, or a variable access.
 * @author Vincent Philippe (@vincent64)
 */
public class QualifiedName extends Expression {
    private Expression qualifiedName;
    private String name;

    public QualifiedName(Meta meta) {
        super(meta);
    }

    public void setQualifiedName(Expression node) {
        qualifiedName = node;
    }

    public void setName(char[] name) {
        this.name = String.valueOf(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Expression getQualifiedName() {
        return qualifiedName;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the qualified name node from the given class name.
     * @param className the class name
     * @return the qualified name node
     */
    public static Expression fromClassName(ClassName className) {
        Expression expression;

        //Returns empty qualified name
        if(className.isEmpty())
            return null;

        //Add first simple name
        SimpleName simpleName = new SimpleName(null);
        simpleName.setName(className.getFirst());
        expression = simpleName;

        //Add every qualified name
        while(!(className = className.withoutFirst()).isEmpty()) {
            QualifiedName qualifiedName = new QualifiedName(null);
            qualifiedName.setQualifiedName(expression);
            qualifiedName.setName(className.getFirst());
            expression = qualifiedName;
        }

        return expression;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitQualifiedName(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitQualifiedName(this);
    }

    @Override
    public String toString() {
        return qualifiedName.toString() + "." + name;
    }
}
