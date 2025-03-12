package poly.compiler.resolver;

import poly.compiler.analyzer.content.SpecialMethod;
import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.analyzer.type.Void;
import poly.compiler.error.ResolvingError;
import poly.compiler.output.content.AccessModifier;
import poly.compiler.parser.tree.ClassDeclaration;
import poly.compiler.parser.tree.FieldDeclaration;
import poly.compiler.parser.tree.MethodDeclaration;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.ArrayType;
import poly.compiler.parser.tree.expression.QualifiedName;
import poly.compiler.parser.tree.statement.StatementBlock;
import poly.compiler.parser.tree.statement.SuperStatement;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.resolver.symbol.*;
import poly.compiler.tokenizer.content.Keyword;
import poly.compiler.util.ClassName;

import java.util.List;

/**
 * The SymbolResolver class. This class is used to resolve the symbols of a class symbol,
 * checking the importations table and making sure the class symbol is valid.
 * This class also adds and generates implicit declarations.
 * This class represents the second step of the resolving process.
 * @author Vincent Philippe (@vincent64)
 */
public class SymbolResolver {
    private final ClassDefinition classDefinition;
    private final ImportTable importTable;

    private SymbolResolver(ClassDefinition classDefinition, ImportTable importTable) {
        this.classDefinition = classDefinition;
        this.importTable = importTable;
    }

    public static SymbolResolver getInstance(ClassDefinition classDefinition, ImportTable importTable) {
        return new SymbolResolver(classDefinition, importTable);
    }

    /**
     * Resolves the class definition symbols and import table.
     */
    public void resolve() {
        //Make sure importations are valid
        importTable.check();

        //Resolve the class symbols
        resolveSymbols();

        //Resolve the class symbol
        resolveClass();
    }

    /**
     * Resolves the class symbol, its superclass symbol and interface symbols.
     */
    private void resolveClass() {
        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Resolve superclass
        TypeSymbol superclassTypeSymbol = (TypeSymbol) classSymbol.getSuperclassSymbol();
        ClassSymbol superclassSymbol = resolveTypeSymbol(superclassTypeSymbol);

        if(superclassSymbol != null) {
            //Make sure the superclass is not an interface if the class is not an interface
            if(!classSymbol.isInterface() && superclassSymbol.isInterface())
                new ResolvingError.ExpectedClass(classDefinition.getClassDeclaration());

            //Make sure the superclass is an interface if the class is an interface
            if(classSymbol.isInterface() && !superclassSymbol.isInterface() && !superclassSymbol.isRoot())
                new ResolvingError.ExpectedInterface(classDefinition.getClassDeclaration());

            //Make sure the superclass is not constant
            if(superclassSymbol.isConstant())
                new ResolvingError.InvalidConstantSuperclass(classDefinition.getClassDeclaration());
        }

        //Replace type symbol with class symbol
        classSymbol.setSuperclassSymbol(superclassSymbol);

        List<Symbol> interfaceSymbols = classSymbol.getInterfaceSymbols();

        //Make sure there is no interfaces if the class is an interface
        if(classSymbol.isInterface() && !interfaceSymbols.isEmpty())
            new ResolvingError.InvalidInterfaceInheritance(classDefinition.getClassDeclaration());

        //Resolve interfaces
        for(int i = 0; i < interfaceSymbols.size(); i++) {
            TypeSymbol interfaceTypeSymbol = (TypeSymbol) interfaceSymbols.get(i);
            ClassSymbol interfaceSymbol = resolveTypeSymbol(interfaceTypeSymbol);

            //Make sure the class is an interface
            if(interfaceSymbol != null && !interfaceSymbol.isInterface())
                new ResolvingError.ExpectedInterface(classDefinition.getClassDeclaration());

            //Replace type symbol with class symbol
            interfaceSymbols.set(i, interfaceSymbol);
        }
    }

    /**
     * Resolves the field and method symbols and adds them to the class symbol.
     */
    private void resolveSymbols() {
        ClassDeclaration classDeclaration = classDefinition.getClassDeclaration();
        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Resolve inner class references
        if(classSymbol.isInner())
            resolveInnerReference(classSymbol);

        boolean hasStaticField = false;

        //Resolve field symbols
        for(Node node : classDeclaration.getFields()) {
            FieldSymbol fieldSymbol = FieldSymbol.fromFieldDeclaration((FieldDeclaration) node, classSymbol, importTable);

            //Set field as static if class is static
            if(classSymbol.isStatic())
                fieldSymbol = fieldSymbol.asStatic();

            //Set field as static and constant if class is an interface
            if(classSymbol.isInterface())
                fieldSymbol = fieldSymbol.asStaticConstant();

            //Add the field to the class symbol
            if(!classSymbol.addSymbol(fieldSymbol))
                new ResolvingError.DuplicateField(node, fieldSymbol.getName());

            if(fieldSymbol.isStatic())
                hasStaticField = true;
        }

        boolean hasConstructor = false;

        //Resolve method symbols
        for(Node node : classDeclaration.getMethods()) {
            MethodSymbol methodSymbol = MethodSymbol.fromMethodDeclaration((MethodDeclaration) node, classSymbol, importTable);

            //Make sure the method is not empty if the class is not an interface
            if(methodSymbol.isEmpty() && !classSymbol.isInterface())
                new ResolvingError.InvalidEmptyMethod(node, methodSymbol);

            //Make sure the method is not constant if the class is an interface
            if(classSymbol.isInterface() && methodSymbol.isConstant())
                new ResolvingError.InvalidConstantMethod(node);

            //Make sure the method is not a constructor in interface
            if(methodSymbol.isConstructor() && classSymbol.isInterface())
                new ResolvingError.InvalidInterfaceConstructor(node);

            //Make sure the method is not a constructor in static class
            if(methodSymbol.isConstructor() && classSymbol.isStatic())
                new ResolvingError.InvalidStaticConstructor(node);

            //Make sure interface methods are implicitly public
            if(classSymbol.isInterface()) {
                if(methodSymbol.getAccessModifier() != AccessModifier.PUBLIC
                        && methodSymbol.getAccessModifier() != AccessModifier.DEFAULT)
                    new ResolvingError.InvalidInterfaceMethod(node, methodSymbol);
                else {
                    //Set method as implicitly public
                    methodSymbol = methodSymbol.asPublic();
                }
            }

            //Set method as static if class is static
            if(classSymbol.isStatic())
                methodSymbol = methodSymbol.asStatic();

            //Set method as constant if class is constant
            if(classSymbol.isConstant())
                methodSymbol = methodSymbol.asConstant();

            //Generate implicit main method
            if(methodSymbol.getName().equals(SpecialMethod.Name.MAIN)
                    && methodSymbol.getAccessModifier() == AccessModifier.PUBLIC
                    && methodSymbol.isStatic()
                    && methodSymbol.getReturnType() instanceof Void
                    && methodSymbol.getParameterCount() == 0) {
                methodSymbol = MethodSymbol.generateMainMethod(classSymbol);
                updateMainMethodDeclaration((MethodDeclaration) node);
            }

            //Add the method to the class symbol
            if(!classSymbol.addSymbol(methodSymbol))
                new ResolvingError.DuplicateMethod(node, methodSymbol);

            if(methodSymbol.isConstructor())
                hasConstructor = true;

            classDefinition.addMethodDefinition(new MethodDefinition((MethodDeclaration) node, methodSymbol));
        }

        //Make sure there is at least one constructor
        if(!hasConstructor && !classSymbol.isInterface() && !classSymbol.isStatic())
            new ResolvingError.MissingConstructor(classDeclaration);

        //Add private constructor for static class
        if(classSymbol.isStatic())
            addConstructor(classSymbol);

        //Add static constructor for static fields
        if(hasStaticField)
            addStaticConstructor(classSymbol);
    }

    /**
     * Resolves the given type symbol and returns the associated class symbol.
     * @param typeSymbol the type symbol
     * @return the class symbol (null if not resolvable)
     */
    private ClassSymbol resolveTypeSymbol(TypeSymbol typeSymbol) {
        ClassName className = typeSymbol.getClassName();

        //Return empty type symbol
        if(className == null)
            return null;

        ClassSymbol classSymbol;

        //Find class in importations table
        if((classSymbol = importTable.findImportation(className.getFirst())) != null)
            return classSymbol.findClass(className.withoutFirst());

        //Get current package
        PackageSymbol packageSymbol = classDefinition.getClassSymbol().getPackageSymbol();

        //Find class in current package
        if((classSymbol = packageSymbol.findClass(className)) != null)
            return classSymbol;

        //Find class as fully qualified name
        if((classSymbol = Classes.findClass(className)) == null)
            new ResolvingError.UnresolvableClass(classDefinition.getClassDeclaration(), className.toQualifiedName());

        return classSymbol;
    }

    /**
     * Resolves the inner class field and constructor outer class references.
     * @param classSymbol the class symbol
     */
    private void resolveInnerReference(ClassSymbol classSymbol) {
        //Add outer field symbol
        classSymbol.addSymbol(FieldSymbol.generateOuterField(classSymbol));

        ClassSymbol outerClassSymbol = (ClassSymbol) classSymbol.getOwnerSymbol();
        for(Node node : classDefinition.getClassDeclaration().getMethods()) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) node;

            //Add outer class reference in constructor parameters
            if(methodDeclaration.isConstructor()) {
                ParameterList parameterList = (ParameterList) methodDeclaration.getParameterList();
                Parameter parameter = new Parameter(null);
                parameter.setType(QualifiedName.fromClassName(outerClassSymbol.getClassName()));
                parameter.setName(String.valueOf(Keyword.EXPRESSION_OUTER));
                parameterList.addFirstParameter(parameter);
            }
        }
    }

    /**
     * Adds a constructor to the given class symbol.
     * @param classSymbol the class symbol
     */
    private void addConstructor(ClassSymbol classSymbol) {
        MethodSymbol constructorSymbol = MethodSymbol.generateConstructor(classSymbol, AccessModifier.PRIVATE);

        //Generate method content
        SuperStatement superStatement = new SuperStatement(null);
        superStatement.setArgumentList(new ArgumentList(null));
        StatementBlock statementBlock = new StatementBlock(null);
        statementBlock.addStatement(superStatement);

        //Generate method declaration
        MethodDeclaration methodDeclaration = new MethodDeclaration(null);
        methodDeclaration.setStatementBlock(statementBlock);
        methodDeclaration.setParameterList(new ParameterList(null));
        methodDeclaration.setConstructor();

        //Add the constructor to the class symbol
        classSymbol.addSymbol(constructorSymbol);

        classDefinition.addMethodDefinition(new MethodDefinition(methodDeclaration, constructorSymbol));
    }

    /**
     * Adds a static constructor to the given class symbol.
     * @param classSymbol the class symbol
     */
    private void addStaticConstructor(ClassSymbol classSymbol) {
        MethodSymbol staticConstructorSymbol = MethodSymbol.generateStaticConstructor(classSymbol);

        //Generate static method declaration
        MethodDeclaration methodDeclaration = new MethodDeclaration(null);
        methodDeclaration.setStatementBlock(new StatementBlock(null));
        methodDeclaration.setParameterList(new ParameterList(null));
        methodDeclaration.setStaticConstructor();
        methodDeclaration.setStatic();

        //Add the constructor to the class symbol
        classSymbol.addSymbol(staticConstructorSymbol);

        classDefinition.addMethodDefinition(new MethodDefinition(methodDeclaration, staticConstructorSymbol));
    }

    /**
     * Updates the implicit main method declaration.
     * @param methodDeclaration the method declaration
     */
    private void updateMainMethodDeclaration(MethodDeclaration methodDeclaration) {
        //Generate arguments type
        ArrayType arrayType = new ArrayType(methodDeclaration.getMeta());
        arrayType.setType(QualifiedName.fromClassName(ClassName.STRING));

        //Generate implicit parameter
        Parameter parameter = new Parameter(methodDeclaration.getMeta());
        parameter.setName("");
        parameter.setType(arrayType);
        ParameterList parameterList = (ParameterList) methodDeclaration.getParameterList();
        parameterList.addParameter(parameter);
    }
}
