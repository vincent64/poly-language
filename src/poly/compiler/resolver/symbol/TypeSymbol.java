package poly.compiler.resolver.symbol;

import poly.compiler.parser.tree.Node;
import poly.compiler.util.ClassName;

/**
 * The TypeSymbol class. This class is used to represents a class or type
 * where the actual class symbol has yet to be loaded or resolved.
 * @author Vincent Philippe (@vincent64)
 */
public class TypeSymbol extends Symbol {
    private final ClassName className;

    /**
     * Constructs a type symbol with the given class name.
     * @param className the class name
     */
    protected TypeSymbol(ClassName className) {
        super(null, null, null, false, false);
        this.className = className;
    }

    /**
     * Creates and returns a type symbol from the given node.
     * @param node the node
     * @return the type symbol
     */
    public static TypeSymbol fromNode(Node node) {
        return new TypeSymbol(node != null ? ClassName.fromNodeQualifiedName(node) : null);
    }

    /**
     * Returns the class name.
     * @return the class name
     */
    public ClassName getClassName() {
        return className;
    }

    /**
     * Returns whether the given type symbol is equal to the current type symbol.
     * @param object the type symbol
     * @return false
     */
    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    public String toString() {
        return "TypeSymbol: " + className.toString();
    }
}
