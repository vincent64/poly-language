package poly.compiler.resolver;

import poly.compiler.error.LimitError;
import poly.compiler.error.ResolvingError;
import poly.compiler.output.jvm.Limitations;
import poly.compiler.parser.tree.ClassDeclaration;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.FieldSymbol;
import poly.compiler.resolver.symbol.MethodSymbol;
import poly.compiler.resolver.symbol.Symbol;

import java.util.*;

/**
 * The DependencyResolver class. This class is used to resolve the inheritance and interfaces
 * of a class symbol. Most importantly, it makes sure there is no cyclic inheritance.
 * This class represents the third and last step of the resolving process.
 * @author Vincent Philippe (@vincent64)
 */
public final class DependencyResolver {
    private static final HashMap<ClassSymbol, List<MethodSymbol>> inheritedMethods = new HashMap<>();
    private static final HashMap<ClassSymbol, List<MethodSymbol>> implementationMethods = new HashMap<>();
    private final ClassDefinition classDefinition;

    private DependencyResolver(ClassDefinition classDefinition) {
        this.classDefinition = classDefinition;
    }

    public static DependencyResolver getInstance(ClassDefinition classDefinition) {
        return new DependencyResolver(classDefinition);
    }

    /**
     * Resolves the class definition inheritance and implementations.
     */
    public void resolve() {
        //Make sure inheritance is valid
        checkInheritance();

        //Make sure implementations are valid
        checkImplementations();

        //Make sure class does not exceed JVM limits
        checkLimits();
    }

    /**
     * Checks the class symbol and makes sure there is no cyclic inheritance or
     * cyclic interface implementations.
     */
    private void checkInheritance() {
        Set<ClassSymbol> superclasses = new HashSet<>();
        Set<ClassSymbol> interfaces = new HashSet<>();

        ClassSymbol classSymbol = classDefinition.getClassSymbol();
        superclasses.add(classSymbol);

        //Make sure a superclass does not extend itself
        while(classSymbol != null) {
            classSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

            //Make sure the superclass is not extended several times
            if(!superclasses.add(classSymbol))
                new ResolvingError.CyclicInheritance(classDefinition.getClassDeclaration(),
                        classSymbol.getClassQualifiedName());
        }

        classSymbol = classDefinition.getClassSymbol();
        interfaces.add(classSymbol);

        //Make sure an interface does not extend itself
        for(Symbol interfaceSymbol : classSymbol.getInterfaceSymbols()) {
            classSymbol = (ClassSymbol) interfaceSymbol;

            //Make sure the same interface is not implemented several times
            do if(!interfaces.add(classSymbol)) {
                new ResolvingError.CyclicImplementation(classDefinition.getClassDeclaration(),
                        classSymbol.getClassQualifiedName());
            } while(!(classSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol()).isRoot());
        }
    }

    /**
     * Checks the class symbol's methods and makes sure they are correctly overriden
     * and methods that must be implemented are correctly implemented.
     */
    private void checkImplementations() {
        ClassDeclaration classDeclaration = classDefinition.getClassDeclaration();
        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Check overridable methods
        checkOverride(classSymbol, new ArrayList<>());

        List<MethodSymbol> implementableMethods = new ArrayList<>();

        //Check implementable methods
        for(Symbol interfaceSymbol : classSymbol.getInterfaceSymbols()) {
            List<MethodSymbol> interfaceMethods = new ArrayList<>();

            checkImplement((ClassSymbol) interfaceSymbol, interfaceMethods);

            for(MethodSymbol methodSymbol : interfaceMethods) {
                if(implementableMethods.contains(methodSymbol)) {
                    MethodSymbol implementableMethod = implementableMethods.get(implementableMethods.indexOf(methodSymbol));

                    //Make sure the methods are valid
                    checkSignature(methodSymbol, implementableMethod);
                } else {
                    implementableMethods.add(methodSymbol);
                }
            }
        }

        List<MethodSymbol> methodSymbols = classSymbol.getMethods();

        //Make sure every method to be implemented are implemented
        for(MethodSymbol methodSymbol : implementableMethods) {
            if(methodSymbols.contains(methodSymbol)) {
                MethodSymbol implementedMethod = methodSymbols.get(methodSymbols.indexOf(methodSymbol));

                //Make sure the implementation is not static
                if(implementedMethod.isStatic())
                    new ResolvingError.MissingImplementation(classDeclaration, classDefinition.getClassSymbol(), methodSymbol);

                //Make sure the methods are valid
                checkSignature(implementedMethod, methodSymbol);
            } else if(methodSymbol.isEmpty() && !classSymbol.isInterface())
                new ResolvingError.MissingImplementation(classDeclaration, classDefinition.getClassSymbol(), methodSymbol);
        }
    }

    /**
     * Checks every method of the given class with the methods inherited from its superclass and interfaces.
     * This includes making sure methods with the same signature as a superclass or interface method
     * have the same return type and no weaker access modifier.
     * @param classSymbol the class symbol
     */
    private void checkOverride(ClassSymbol classSymbol) {
        List<MethodSymbol> overridableMethods = new ArrayList<>();
        ClassSymbol superclassSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

        //Check superclass overriden methods
        if(!classSymbol.isRoot()) {
            if(inheritedMethods.containsKey(superclassSymbol)) {
                overridableMethods.addAll(inheritedMethods.get(superclassSymbol));
            } else if(!classSymbol.isInterface() || superclassSymbol.isInterface()) {
                checkOverride(superclassSymbol);
            }
        }

        //Check interfaces overriden methods
        for(Symbol symbol : classSymbol.getInterfaceSymbols()) {
            ClassSymbol interfaceSymbol = (ClassSymbol) symbol;

            if(inheritedMethods.containsKey(interfaceSymbol)) {
                overridableMethods.addAll(inheritedMethods.get(interfaceSymbol));
            } else {
                checkOverride(interfaceSymbol);
            }
        }

        //Check class overriden methods
        for(MethodSymbol methodSymbol : classSymbol.getMethods()) {
            //Skip static and empty methods
            if(methodSymbol.isStatic() || methodSymbol.isEmpty())
                continue;

            //Add non-overriding method
            if(!overridableMethods.contains(methodSymbol)) {
                overridableMethods.add(methodSymbol);
            }

            //Add overriding method
            else {
                MethodSymbol overridenMethod = overridableMethods.get(overridableMethods.indexOf(methodSymbol));

                //Make sure the overriding method signature is valid
                checkSignature(methodSymbol, overridenMethod);

                //Make sure the method is not constant
                if(overridenMethod.isConstant()) {
                    new ResolvingError.InvalidConstantOverride(classDefinition.getClassDeclaration(),
                            methodSymbol, classDefinition.getClassSymbol().getName());
                }

                //Replace the method
                overridableMethods.remove(overridenMethod);
                overridableMethods.add(methodSymbol);
            }
        }

        //Add the class symbol and its inherited methods
        inheritedMethods.put(classSymbol, overridableMethods);
    }

    /**
     * Checks the class symbol's methods and makes sure the ones that must be implemented
     * are correctly implemented. This includes making sure methods with the same signature
     * have the same return type and no weaker access modifier.
     * @param classSymbol the class symbol
     * @param implementableMethods the list of methods to be implemented
     */
    private void checkImplement(ClassSymbol classSymbol, List<MethodSymbol> implementableMethods) {
        ClassSymbol superinterfaceSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

        //Check superclass implements
        if(!classSymbol.isRoot())
            checkImplement(superinterfaceSymbol, implementableMethods);

        for(MethodSymbol methodSymbol : classSymbol.getMethods()) {
            //Skip static methods
            if(methodSymbol.isStatic())
                continue;

            if(!implementableMethods.contains(methodSymbol)) {
                implementableMethods.add(methodSymbol);
            } else {
                //Get the implementable method
                MethodSymbol implementableMethod = implementableMethods.get(implementableMethods.indexOf(methodSymbol));

                //Make sure the methods are valid
                checkSignature(methodSymbol, implementableMethod);

                //Replace the method
                if(!implementableMethod.isEmpty() && methodSymbol.isEmpty()) {
                    implementableMethods.remove(implementableMethod);
                    implementableMethods.add(methodSymbol);
                }
            }
        }
    }

    /**
     * Checks the given method symbols' signature. This includes making sure the return types
     * are the same and the access modifier is not weaker.
     * @param methodSymbol the method symbol
     * @param implementableMethodSymbol the implementable method symbol
     */
    private void checkSignature(MethodSymbol methodSymbol, MethodSymbol implementableMethodSymbol) {
        //Make sure the methods have the same return type
        if(!(methodSymbol.getReturnType() == null && implementableMethodSymbol.getReturnType() == null)) {
            if(methodSymbol.getReturnType() == null || implementableMethodSymbol.getReturnType() == null
                    || !methodSymbol.getReturnType().equals(implementableMethodSymbol.getReturnType()))
                new ResolvingError.InvalidOverrideReturnType(classDefinition.getClassDeclaration(),
                        methodSymbol, implementableMethodSymbol.getClassSymbol().getClassInternalQualifiedName());
        }

        //Make sure the implementable method does not have a weaker access modifier
        if(implementableMethodSymbol.getAccessModifier().isWeakerThan(methodSymbol.getAccessModifier())
                && !methodSymbol.isConstructor())
            new ResolvingError.InvalidOverrideAccessModifier(classDefinition.getClassDeclaration(),
                    methodSymbol, implementableMethodSymbol.getClassSymbol().getClassInternalQualifiedName());
    }

    /**
     * Checks the class symbol and makes sure it does not exceed the JVM limitations.
     */
    private void checkLimits() {
        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Make sure there is not too many fields
        if(classSymbol.getFieldsCount() >= Limitations.MAX_FIELDS_COUNT)
            new LimitError.FieldCount(classDefinition);

        //Make sure there is not too many methods
        if(classSymbol.getMethodCount() >= Limitations.MAX_METHODS_COUNT)
            new LimitError.MethodCount(classDefinition);

        //Make sure there is not too many interfaces
        if(classSymbol.getInterfaceSymbols().size() >= Limitations.MAX_INTERFACES_COUNT)
            new LimitError.InterfaceCount(classDefinition);

        //Make sure field names are not too long
        for(FieldSymbol fieldSymbol : classSymbol.getFields()) {
            if(fieldSymbol.getName().length() >= Limitations.MAX_IDENTIFIER_LENGTH)
                new LimitError.IdentifierLength(classDefinition);
        }

        //Make sure method names are not too long and signature not too large
        for(MethodSymbol methodSymbol : classSymbol.getMethods()) {
            if(methodSymbol.getName().length() >= Limitations.MAX_IDENTIFIER_LENGTH)
                new LimitError.IdentifierLength(classDefinition);

            if(methodSymbol.getParameterTypes().length >= Limitations.MAX_PARAMETERS_COUNT)
                new LimitError.MethodParameterCount(classDefinition);
        }
    }
}
