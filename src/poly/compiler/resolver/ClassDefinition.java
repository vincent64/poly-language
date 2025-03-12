package poly.compiler.resolver;

import poly.compiler.parser.tree.ClassDeclaration;
import poly.compiler.resolver.symbol.ClassSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * The ClassDefinition class. This class represents the definiton of a class,
 * and contains the class declaration and class symbol of a class.
 * It also contains the list of method definitions of the class.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassDefinition {
    private final ClassDeclaration classDeclaration;
    private final ClassSymbol classSymbol;
    private final List<MethodDefinition> methodDefinitions;

    /**
     * Constructs a class definition with the given class declaration and class symbol.
     * @param classDeclaration the class declaration
     * @param classSymbol the class symbol
     */
    public ClassDefinition(ClassDeclaration classDeclaration, ClassSymbol classSymbol) {
        this.classDeclaration = classDeclaration;
        this.classSymbol = classSymbol;

        //Initialize method definitions list
        methodDefinitions = new ArrayList<>();
    }

    /**
     * Adds a method definition to the current class definition.
     * @param methodDefinition the method definition
     */
    void addMethodDefinition(MethodDefinition methodDefinition) {
        methodDefinitions.add(methodDefinition);
    }

    /**
     * Returns the class declaration node.
     * @return the class declaration
     */
    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    /**
     * Returns the class symbol.
     * @return the class symbol
     */
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    /**
     * Returns the list of method definitions
     * @return the method definitions list
     */
    public List<MethodDefinition> getMethodDefinitions() {
        return List.copyOf(methodDefinitions);
    }
}
