package poly.compiler.resolver;

import poly.compiler.parser.tree.MethodDeclaration;
import poly.compiler.resolver.symbol.MethodSymbol;

/**
 * The MethodDefinition class. This class represents the definition of a method,
 * and contains the method declaration and method symbol of a method.
 * @author Vincent Philippe (@vincent64)
 */
public class MethodDefinition {
    private final MethodDeclaration methodDeclaration;
    private final MethodSymbol methodSymbol;

    /**
     * Constructs a method definition with the given method declaration and method symbol.
     * @param methodDeclaration the method declaration
     * @param methodSymbol the method symbol
     */
    public MethodDefinition(MethodDeclaration methodDeclaration, MethodSymbol methodSymbol) {
        this.methodDeclaration = methodDeclaration;
        this.methodSymbol = methodSymbol;
    }

    /**
     * Returns the method declaration node.
     * @return the method declaration
     */
    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    /**
     * Returns the method symbol.
     * @return the method symbol
     */
    public MethodSymbol getMethodSymbol() {
        return methodSymbol;
    }
}
