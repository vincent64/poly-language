package poly.compiler.resolver;

import poly.compiler.error.ResolvingError;
import poly.compiler.output.content.AccessModifier;
import poly.compiler.parser.tree.ClassDeclaration;
import poly.compiler.parser.tree.ContentNode;
import poly.compiler.parser.tree.Node;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.PackageSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ClassName;
import poly.compiler.util.PackageName;

import java.util.ArrayList;
import java.util.List;

/**
 * The Resolver class. This class is used to resolve a source code file's content into a list
 * of class definitions, and creating the associated class symbol for each class declaration.
 * This class represents the first step of the resolving process.
 * @author Vincent Philippe (@vincent64)
 */
public final class Resolver {
    private final ContentNode contentNode;
    private final PackageName packageName;
    private final List<ClassDefinition> classDefinitions;

    private Resolver(ContentNode contentNode, PackageName packageName) {
        this.contentNode = contentNode;
        this.packageName = packageName;

        //Initialize class definitions list
        classDefinitions = new ArrayList<>();
    }

    public static Resolver getInstance(ContentNode contentNode, PackageName packageName) {
        return new Resolver(contentNode, packageName);
    }

    /**
     * Resolves the class declarations and returns a list of class definitions.
     * @return the class definitions
     */
    public List<ClassDefinition> resolve() {
        //Generate package
        PackageSymbol packageSymbol = ProjectClasses.generatePackage(packageName);

        //Resolve every class declaration in the file
        for(Node node : contentNode.getClasses()) {
            ClassDeclaration classDeclaration = (ClassDeclaration) node;

            //Make sure the class access modifier is default or public
            if(classDeclaration.getAccessModifier() != AccessModifier.DEFAULT
                    && classDeclaration.getAccessModifier() != AccessModifier.PUBLIC)
                new ResolvingError.InvalidClassAccessModifier(classDeclaration);

            //Resolve class symbol and add to package
            ClassSymbol classSymbol = resolveClass(classDeclaration, new ClassName(packageName), packageSymbol, packageSymbol);

            //Add the class symbol to the project classes
            if(!packageSymbol.addSymbol(classSymbol))
                new ResolvingError.DuplicateClass(classDeclaration, classDeclaration.getName());
        }

        return classDefinitions;
    }

    /**
     * Resolves the given class declaration and returns the associated resolved class symbol.
     * @param classDeclaration the class declaration
     * @param packageName the class package name
     * @param ownerSymbol the class owner symbol
     * @param packageSymbol the class package symbol
     * @return the class symbol
     */
    private ClassSymbol resolveClass(ClassDeclaration classDeclaration, ClassName packageName,
                                     Symbol ownerSymbol, PackageSymbol packageSymbol) {
        //Compute the class qualified name
        ClassName className = packageName.addClassName(classDeclaration.getName());

        //Resolve the class symbols
        ClassSymbol classSymbol = ClassSymbol.fromClassDeclaration(classDeclaration, className, ownerSymbol, packageSymbol);

        //Make sure the class is not static if it is an interface
        if(classSymbol.isInterface() && classSymbol.isStatic())
            new ResolvingError.InvalidStaticInterface(classDeclaration);

        //Make sure the class is not constant if it is an interface
        if(classSymbol.isInterface() && classSymbol.isConstant())
            new ResolvingError.InvalidConstantInterface(classDeclaration);

        //Make sure there is no superclass if the class is static
        if(classSymbol.isStatic() && classDeclaration.getSuperclass() != null)
            new ResolvingError.InvalidStaticSuperclass(classDeclaration);

        //Make sure the class is not static or interface if it is inner
        if(ownerSymbol instanceof ClassSymbol outerClassSymbol) {
            if(classSymbol.isInner() && (outerClassSymbol.isStatic() || outerClassSymbol.isInterface()))
                new ResolvingError.InvalidInnerDeclaration(classDeclaration);
        }

        //Resolve nested class symbols
        for(Node node : classDeclaration.getNestedClasses()) {
            ClassSymbol nestedClassSymbol = resolveClass((ClassDeclaration) node, className, classSymbol, packageSymbol);

            //Add the class symbol to the outer class
            if(!classSymbol.addSymbol(nestedClassSymbol))
                new ResolvingError.DuplicateClass(node, nestedClassSymbol.getName());
        }

        //Create the class definition
        classDefinitions.add(new ClassDefinition(classDeclaration, classSymbol));

        return classSymbol;
    }
}
