package poly.compiler.resolver.symbol;

import poly.compiler.analyzer.content.SpecialMethod;
import poly.compiler.analyzer.type.Type;
import poly.compiler.error.AnalyzingError;
import poly.compiler.output.ClassFile;
import poly.compiler.output.content.AccessModifier;
import poly.compiler.parser.tree.ClassDeclaration;
import poly.compiler.parser.tree.Node;
import poly.compiler.util.ClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * The ClassSymbol class. This class is the symbol representation of a class.
 * It can be created either from a class declaration in the source code,
 * or from a library class file.
 * @author Vincent Philippe (@vincent64)
 */
public final class ClassSymbol extends Symbol {
    private final Kind kind;
    private final ClassName className;
    private final List<Symbol> symbols;
    private final Symbol ownerSymbol;
    private final PackageSymbol packageSymbol;
    private Symbol superclassSymbol;
    private List<Symbol> interfaceSymbols;

    private ClassSymbol(AccessModifier accessModifier, Kind kind, String name, boolean isStatic, boolean isConstant,
                        ClassName className, ClassName superclassName, List<Node> interfaceNodes, Symbol ownerSymbol, PackageSymbol packageSymbol) {
        super(Symbol.Kind.CLASS, accessModifier, name, isStatic, isConstant);
        this.kind = kind;
        this.className = className;
        this.ownerSymbol = ownerSymbol;
        this.packageSymbol = packageSymbol;

        //Set unresolved superclass symbol
        superclassSymbol = new TypeSymbol(superclassName);

        //Initialize symbols list
        symbols = new ArrayList<>();
        //Initialize interface symbols list
        interfaceSymbols = new ArrayList<>();

        for(Node node : interfaceNodes)
            interfaceSymbols.add(TypeSymbol.fromNode(node));
    }

    /**
     * Creates and returns the class symbol from the given class declaration.
     * If the class declaration does not have a superclass, it will receive object as superclass.
     * @param classDeclaration the class declaration
     * @param className the class name
     * @param ownerSymbol the owner symbol
     * @param packageSymbol the package symbol
     * @return the class symbol
     */
    public static ClassSymbol fromClassDeclaration(ClassDeclaration classDeclaration, ClassName className,
                                                   Symbol ownerSymbol, PackageSymbol packageSymbol) {
        Node superclassNode = classDeclaration.getSuperclass();

        return new ClassSymbol(classDeclaration.getAccessModifier(),
                classDeclaration.getKind(),
                classDeclaration.getName(),
                classDeclaration.isStatic(),
                classDeclaration.isConstant() || classDeclaration.isEnum(),
                className,
                superclassNode == null
                    ? classDeclaration.isException()
                        ? ClassName.RUNTIME_EXCEPTION
                        : classDeclaration.isEnum()
                            ? ClassName.ENUM
                            : ClassName.OBJECT
                    : ClassName.fromNodeQualifiedName(superclassNode),
                classDeclaration.getInterfaces(),
                ownerSymbol,
                packageSymbol);
    }

    /**
     * Creates and returns the class symbol from the given class file.
     * @param classFile the class file
     * @param ownerSymbol the owner symbol
     * @param packageSymbol the package symbol
     * @return the class symbol
     */
    public static ClassSymbol fromClassFile(ClassFile classFile, Symbol ownerSymbol, PackageSymbol packageSymbol) {
        ClassName className = ClassName.fromStringQualifiedName(classFile.getClassQualifiedName());
        String superclassName = classFile.getSuperclassQualifiedName();

        return new ClassSymbol(classFile.getAccessModifier(),
                classFile.isInterface() ? Kind.INTERFACE
                        : classFile.isEnum() ? Kind.ENUM
                        : ownerSymbol instanceof ClassSymbol && !classFile.isStatic() ? Kind.INNER
                        : Kind.CLASS,
                className.getLast(),
                false,
                classFile.isConstant(),
                className,
                superclassName == null
                        ? null
                        : ClassName.fromStringQualifiedName(superclassName),
                new ArrayList<>(),
                ownerSymbol,
                packageSymbol);
    }

    /**
     * Adds the given symbol to the class symbol if it was not already present,
     * and returns whether the given symbol was already present.
     * The symbol can either be a field, a method or a nested class.
     * @param symbol the symbol
     * @return true if the class symbol already contained the symbol
     */
    public boolean addSymbol(Symbol symbol) {
        if(symbols.contains(symbol))
            return false;

        return symbols.add(symbol);
    }

    /**
     * Finds the field symbol corresponding to the given field name.
     * The field can be one from the current class, superclass or interfaces.
     * @param name the field name
     * @return the corresponding field symbol (null if none was found)
     */
    public FieldSymbol findField(String name, ClassSymbol sourceClass) {
        //Find field in current class
        for(Symbol symbol : symbols) {
            if(symbol instanceof FieldSymbol fieldSymbol
                    && name.equals(fieldSymbol.name)
                    && fieldSymbol.isAccessibleFrom(sourceClass)) {
                return fieldSymbol;
            }
        }

        FieldSymbol fieldSymbol;

        //Find field in superclass
        if(!isRoot()) {
            if((fieldSymbol = ((ClassSymbol) superclassSymbol).findField(name, sourceClass)) != null)
                return fieldSymbol;
        }

        //Find field in interfaces
        if(!interfaceSymbols.isEmpty()) {
            for(Symbol interfaceSymbol : interfaceSymbols) {
                if((fieldSymbol = ((ClassSymbol) interfaceSymbol).findField(name, sourceClass)) != null)
                    return fieldSymbol;
            }
        }

        return null;
    }

    /**
     * Finds the method symbol corresponding to the given method name and parameter types.
     * @param name the method name
     * @param types the method parameter types
     * @param sourceClass the source class symbol
     * @param node the node
     * @return the corresponding method symbol (null if none was found)
     */
    public MethodSymbol findMethod(String name, Type[] types, ClassSymbol sourceClass, Node node) {
        return findMethod(name, types, sourceClass, node, true);
    }

    /**
     * Finds the method symbol corresponding to the given method name and parameter types.
     * This method will find the best (most specific) method within the current class,
     * superclass and interfaces. Because this method is meant to be called by the analyzer,
     * it will throw a compilation error if there is an ambuigity.
     * @param name the method name
     * @param types the method parameter types
     * @param sourceClass the source class symbol
     * @param node the node
     * @param includeSuperclass whether superclass methods should be included
     * @return the corresponding method symbol (null if none was found)
     */
    private MethodSymbol findMethod(String name, Type[] types, ClassSymbol sourceClass, Node node, boolean includeSuperclass) {
        List<MethodSymbol> methodCandidates = new ArrayList<>();

        //Find candidates in current class
        findMethodCandidates(name, types, methodCandidates, sourceClass, includeSuperclass);

        //Return no method
        if(methodCandidates.isEmpty())
            return null;

        //Return the only candidate
        if(methodCandidates.size() == 1)
            return methodCandidates.getFirst();

        //Sort candidates by best match
        methodCandidates.sort(MethodSymbol::compareTo);

        //Make sure there is no ambiguity
        if(methodCandidates.getFirst().compareTo(methodCandidates.get(1)) == 0)
            new AnalyzingError.AmbiguousMethodCall(node);

        return methodCandidates.getFirst();
    }

    /**
     * Finds the method candidates with the given method name and parameter types.
     * @param name the method name
     * @param types the method parameter types
     * @param methodCandidates the method candidates
     * @param sourceClass the source class symbol
     * @param includeSuperclass whether superclass methods should be included
     */
    private void findMethodCandidates(String name, Type[] types, List<MethodSymbol> methodCandidates,
                                      ClassSymbol sourceClass, boolean includeSuperclass) {
        for(Symbol symbol : symbols) {
            if(symbol instanceof MethodSymbol methodSymbol) {
                //Add method as candidate
                if(methodSymbol.name.equals(name)
                        && MethodSymbol.isMatchingTypes(types, methodSymbol.getParameterTypes())
                        && methodSymbol.isAccessibleFrom(sourceClass)) {
                    if(!methodCandidates.contains(methodSymbol))
                        methodCandidates.add(methodSymbol);
                }
            }
        }

        //Find candidates in the superclass
        if(!isRoot() && includeSuperclass)
            ((ClassSymbol) superclassSymbol).findMethodCandidates(name, types, methodCandidates, sourceClass, true);

        //Find candidates in the interfaces
        if(includeSuperclass) {
            for(Symbol interfaceSymbol : interfaceSymbols)
                ((ClassSymbol) interfaceSymbol).findMethodCandidates(name, types, methodCandidates, sourceClass, true);
        }
    }

    /**
     * Returns the constructor method symbol with the given parameter types.
     * @param types the parameter types
     * @param sourceClass the source class symbol
     * @param node the node
     * @return the constructor method symbol (null if not found)
     */
    public MethodSymbol findConstructor(Type[] types, ClassSymbol sourceClass, Node node) {
        return findMethod(SpecialMethod.Name.CONSTRUCTOR, types, sourceClass, node, false);
    }

    /**
     * Returns the enum constructor method symbol with the given parameter types.
     * @param types the parameter types
     * @param sourceClass the source class symbol
     * @param node the node
     * @return the enum constructor method symbol (null if not found)
     */
    public MethodSymbol findEnumConstructor(Type[] types, ClassSymbol sourceClass, Node node) {
        return findConstructor(MethodSymbol.getEnumConstructorParameterTypes(types), sourceClass, node);
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public ClassSymbol findClass(String className) {
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(className)
                    && symbol instanceof ClassSymbol classSymbol) {
                return classSymbol;
            }
        }

        return null;
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public ClassSymbol findClass(ClassName className) {
        if(className.isEmpty())
            return this;

        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(className.getFirst())
                    && symbol instanceof ClassSymbol classSymbol) {
                return classSymbol.findClass(className.withoutFirst());
            }
        }

        return null;
    }

    /**
     * Sets the given class symbol as superclass.
     * @param superclassSymbol the superclass symbol
     */
    public void setSuperclassSymbol(Symbol superclassSymbol) {
        this.superclassSymbol = superclassSymbol;
    }

    /**
     * Returns whether the current class is a subtype of the given class symbol.
     * @param classSymbol the class symbol
     * @return true if the current class is a subtype
     */
    public boolean isSubtypeOf(ClassSymbol classSymbol) {
        if(classSymbol.equals(this))
            return true;

        if(superclassSymbol != null && ((ClassSymbol) superclassSymbol).isSubtypeOf(classSymbol))
            return true;

        for(Symbol interfaceSymbol : interfaceSymbols) {
            if(((ClassSymbol) interfaceSymbol).isSubtypeOf(classSymbol))
                return true;
        }

        return false;
    }

    /**
     * Returns whether the current class is a supertype of the given class symbol.
     * @param classSymbol the class symbol
     * @return true if the current class is a supertype
     */
    public boolean isSupertypeOf(ClassSymbol classSymbol) {
        return classSymbol.isSubtypeOf(this);
    }

    /**
     * Returns whether the current class is an inner class of the given class symbol.
     * @param classSymbol the class symbol
     * @return true if the current class is an inner class
     */
    public boolean isInnerClassOf(ClassSymbol classSymbol) {
        ClassSymbol innerClassSymbol = this;
        while(innerClassSymbol.getOwnerSymbol() instanceof ClassSymbol outerClassSymbol) {
            if(outerClassSymbol.equals(classSymbol))
                return true;

            innerClassSymbol = outerClassSymbol;
        }

        return false;
    }

    /**
     * Returns whether the current class can be accessed from the given class symbol origin.
     * @param classSymbol the class symbol origin
     * @return true if the class can be accessed from the class
     */
    public boolean isAccessibleFrom(ClassSymbol classSymbol) {
        return switch(accessModifier) {
            case PUBLIC -> true;
            case PRIVATE -> classSymbol.equals(this) || this.isInnerClassOf(classSymbol);
            case DEFAULT -> classSymbol.getPackageSymbol().equals(this.getPackageSymbol());
            case PROTECTED ->
                    classSymbol.getPackageSymbol().equals(this.getPackageSymbol())
                            || classSymbol.isSubtypeOf(this);
        };
    }

    /**
     * Returns the list of field symbols of the class symbol.
     * @return the field symbols list
     */
    public List<FieldSymbol> getFields() {
        List<FieldSymbol> fieldSymbols = new ArrayList<>();

        for(Symbol symbol : symbols) {
            if(symbol instanceof FieldSymbol fieldSymbol)
                fieldSymbols.add(fieldSymbol);
        }

        return fieldSymbols;
    }

    /**
     * Returns the list of method symbols of the class symbol.
     * @return the method symbols list
     */
    public List<MethodSymbol> getMethods() {
        List<MethodSymbol> methodSymbols = new ArrayList<>();

        for(Symbol symbol : symbols) {
            if(symbol instanceof MethodSymbol methodSymbol)
                methodSymbols.add(methodSymbol);
        }

        return methodSymbols;
    }

    /**
     * Returns the list of nested class symbols of the class symbol.
     * @return the nested class symbols list
     */
    public List<ClassSymbol> getClasses() {
        List<ClassSymbol> classSymbols = new ArrayList<>();

        for(Symbol symbol : symbols) {
            if(symbol instanceof ClassSymbol methodSymbol)
                classSymbols.add(methodSymbol);
        }

        return classSymbols;
    }

    /**
     * Returns the amount of fields the class symbol has.
     * @return the amount of fields
     */
    public int getFieldsCount() {
        int count = 0;
        for(Symbol symbol : symbols) {
            if(symbol instanceof FieldSymbol)
                count++;
        }

        return count;
    }

    /**
     * Returns the amount of methods the class symbol has.
     * @return the amount of methods
     */
    public int getMethodCount() {
        int count = 0;
        for(Symbol symbol : symbols) {
            if(symbol instanceof MethodSymbol)
                count++;
        }

        return count;
    }

    /**
     * Returns whether the current class is the root in the inheritance hierarchy.
     * @return true if the class is the root class
     */
    public boolean isRoot() {
        return superclassSymbol == null;
    }

    /**
     * Returns whether the current class is a nested class inside another class.
     * @return true if the class is a nested class
     */
    public boolean isNested() {
        return ownerSymbol instanceof ClassSymbol;
    }

    /**
     * Returns the class' class name.
     * @return the class name
     */
    public ClassName getClassName() {
        return className;
    }

    /**
     * Returns the class qualified name.
     * @return the qualified name
     */
    public String getClassQualifiedName() {
        return className.toQualifiedName();
    }

    /**
     * Returns the class internal qualified name.
     * The internal qualified name is the name used by the JVM.
     * @return the internal qualified name
     */
    public String getClassInternalQualifiedName() {
        return className.toInternalQualifiedName();
    }

    /**
     * Returns the owner symbol.
     * @return the owner symbol
     */
    public Symbol getOwnerSymbol() {
        return ownerSymbol;
    }

    /**
     * Returns the package symbol.
     * @return the package symbol
     */
    public PackageSymbol getPackageSymbol() {
        return packageSymbol;
    }

    /**
     * Returns the superclass symbol.
     * @return the superclass symbol
     */
    public Symbol getSuperclassSymbol() {
        return superclassSymbol;
    }

    /**
     * Returns the list of interface symbols.
     * @return the interface symbols
     */
    public List<Symbol> getInterfaceSymbols() {
        return interfaceSymbols;
    }

    /**
     * Returns whether the class is an interface.
     * @return true if the class is an interface
     */
    public boolean isInterface() {
        return kind == Kind.INTERFACE;
    }

    /**
     * Returns whether the class is an enum.
     * @return true if the class is an enum
     */
    public boolean isEnum() {
        return kind == Kind.ENUM;
    }

    /**
     * Returns whether the class is inner.
     * @return true if the class is inner
     */
    public boolean isInner() {
        return kind == Kind.INNER;
    }

    /**
     * Returns whether the given class symbol has the same qualified name as the current one.
     * @param object the class symbol
     * @return true if the symbols have the same qualified name
     */
    @Override
    public boolean equals(java.lang.Object object) {
        if(!(object instanceof ClassSymbol classSymbol))
            return false;

        return className.equals(classSymbol.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("ClassSymbol("
                + accessModifier + ", "
                + kind + ", "
                + name + ", "
                + "isStatic=" + isStatic + ", "
                + "isConstant=" + isConstant + ", "
                + "qualifiedName=" + className + ", "
                + "superclass=" + superclassSymbol + "):\n");

        //Append every interface
        for(Symbol symbol : interfaceSymbols)
            string.append(symbol.toString().indent(4));

        //Append every symbol
        for(Symbol symbol : symbols)
            string.append(symbol.toString().indent(4));

        return string.toString();
    }

    /**
     * The ClassSymbol.Kind enum. This enum contains every kind of
     * class symbol there exists.
     */
    public enum Kind {
        CLASS,
        INTERFACE,
        ENUM,
        INNER,
        EXCEPTION
    }
}
