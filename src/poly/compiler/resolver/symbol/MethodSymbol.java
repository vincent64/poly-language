package poly.compiler.resolver.symbol;

import poly.compiler.analyzer.content.SpecialMethod;
import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.analyzer.type.Array;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Type;
import poly.compiler.analyzer.type.Void;
import poly.compiler.error.ResolvingError;
import poly.compiler.output.content.*;
import poly.compiler.parser.tree.MethodDeclaration;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.resolver.LibraryClasses;
import poly.compiler.util.ClassName;
import poly.compiler.util.MethodStringifier;

import java.nio.charset.StandardCharsets;

/**
 * The MethodSymbol class. This class is the symbol representation of a method.
 * It can be created either from a method declaration in the source code,
 * or from a library class file.
 * @author Vincent Philippe (@vincent64)
 */
public final class MethodSymbol extends Symbol implements Comparable<MethodSymbol> {
    private final ClassSymbol classSymbol;
    private final Type returnType;
    private final Type[] parameterTypes;
    private final boolean isEmpty;

    private MethodSymbol(ClassSymbol classSymbol, AccessModifier accessModifier, Type returnType, Type[] parameterTypes,
                         String name, boolean isStatic, boolean isConstant, boolean isEmpty) {
        super(Kind.METHOD, accessModifier, name, isStatic, isConstant);
        this.classSymbol = classSymbol;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.isEmpty = isEmpty;
    }

    /**
     * Creates and returns the method symbol from the given method declaration.
     * @param methodDeclaration the method declaration
     * @param classSymbol the class symbol
     * @param importTable the importations table
     * @return the method symbol
     */
    public static MethodSymbol fromMethodDeclaration(MethodDeclaration methodDeclaration, ClassSymbol classSymbol, ImportTable importTable) {
        //Get parameters list from method declaration
        ParameterList parameterList = (ParameterList) methodDeclaration.getParameterList();
        Node[] parameters = parameterList.getParameters();

        Type[] parameterTypes = new Type[parameters.length];

        //Compute parameter types
        for(int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = Type.fromTypeNode(((Parameter) parameters[i]).getType(), classSymbol, importTable);

            //Make sure the parameter type is valid
            if(parameterTypes[i] == null)
                new ResolvingError.UnresolvableType(((Parameter) parameters[i]).getType());
        }

        Type returnType = Type.fromTypeNode(methodDeclaration.getReturnType(), classSymbol, importTable);

        //Make sure the return type is valid
        if(methodDeclaration.getReturnType() != null && returnType == null)
            new ResolvingError.UnresolvableType(methodDeclaration.getReturnType());

        //Transform return type to void if null
        if(returnType == null)
            returnType = new Void();

        return new MethodSymbol(classSymbol,
                methodDeclaration.getAccessModifier(),
                returnType,
                parameterTypes,
                methodDeclaration.getName(),
                methodDeclaration.isStatic(),
                methodDeclaration.isConstant(),
                methodDeclaration.isEmpty());
    }

    /**
     * Creates and returns the method symbol from the given class file method.
     * @param method the class file method
     * @param classSymbol the class symbol
     * @param constantPool the constant pool
     * @return the method symbol
     */
    public static MethodSymbol fromClassFile(Method method, ClassSymbol classSymbol, ConstantPool constantPool) {
        //Get constants from method in constant pool
        Constant nameConstant = constantPool.getConstant(method.getNameIndex());
        Constant descriptorConstant = constantPool.getConstant(method.getDescriptorIndex());

        //Get name from constant
        String name = new String(nameConstant.getContent(), StandardCharsets.UTF_8);

        //Get descriptor from constant
        String descriptor = new String(descriptorConstant.getContent(), StandardCharsets.UTF_8);

        //Parse descriptor to get method types
        Type[] types = Descriptor.getTypesFromMethodDescriptor(descriptor.toCharArray());

        Type returnType = types[types.length - 1];
        Type[] parameterTypes = new Type[types.length - 1];
        System.arraycopy(types, 0, parameterTypes, 0, parameterTypes.length);

        return new MethodSymbol(classSymbol,
                method.getAccessModifier(),
                returnType,
                parameterTypes,
                name,
                method.isStatic(),
                method.isConstant(),
                method.isEmpty());
    }

    /**
     * Generates and returns the method symbol of a constructor for the given class symbol.
     * @param classSymbol the class symbol
     * @param accessModifier the access modifier
     * @return the constructor method symbol
     */
    public static MethodSymbol generateConstructor(ClassSymbol classSymbol, AccessModifier accessModifier) {
        return new MethodSymbol(classSymbol,
                accessModifier,
                new Void(),
                new Type[0],
                SpecialMethod.Name.CONSTRUCTOR,
                false, false, false);
    }

    /**
     * Generates and returns the method symbol of a static constructor for the given class symbol.
     * @param classSymbol the class symbol
     * @return the static constructor method symbol
     */
    public static MethodSymbol generateStaticConstructor(ClassSymbol classSymbol) {
        return new MethodSymbol(classSymbol,
                AccessModifier.DEFAULT,
                new Void(),
                new Type[0],
                SpecialMethod.Name.STATIC_CONSTRUCTOR,
                true, false, false);
    }

    /**
     * Generates and returns the method symbol of a main method for the given class symbol.
     * @param classSymbol the class symbol
     * @return the main method symbol
     */
    public static MethodSymbol generateMainMethod(ClassSymbol classSymbol) {
        return new MethodSymbol(classSymbol,
                AccessModifier.PUBLIC,
                new Void(),
                new Type[]{ new Array(new Object(LibraryClasses.findClass(ClassName.STRING))) },
                SpecialMethod.Name.MAIN,
                true, false, false);
    }

    /**
     * Returns whether the current method can be accessed from the given class symbol origin.
     * @param classSymbol the class symbol origin
     * @return true if the method can be accessed from the class
     */
    public boolean isAccessibleFrom(ClassSymbol classSymbol) {
        return switch(accessModifier) {
            case PUBLIC -> true;
            case PRIVATE -> classSymbol.equals(this.classSymbol) || this.classSymbol.isInnerClassOf(classSymbol);
            case DEFAULT -> classSymbol.getPackageSymbol().equals(this.classSymbol.getPackageSymbol());
            case PROTECTED ->
                    classSymbol.getPackageSymbol().equals(this.classSymbol.getPackageSymbol())
                    || classSymbol.isSubtypeOf(this.classSymbol);
        };
    }

    /**
     * Returns whether the method is a constructor.
     * @return true if the method is a constructor
     */
    public boolean isConstructor() {
        return name.equals(SpecialMethod.Name.CONSTRUCTOR);
    }

    /**
     * Returns whether the method is a static constructor.
     * @return true if the method is a static constructor
     */
    public boolean isStaticConstructor() {
        return name.equals(SpecialMethod.Name.STATIC_CONSTRUCTOR);
    }

    /**
     * Returns the same method symbol with the static behavior modifier set.
     * @return the same method symbol but static
     */
    public MethodSymbol asStatic() {
        return new MethodSymbol(classSymbol, accessModifier, returnType, parameterTypes, name, true, isConstant, isEmpty);
    }

    /**
     * Returns the same method symbol with the constant behavior modifier set.
     * @return the same method symbol but constant
     */
    public MethodSymbol asConstant() {
        return new MethodSymbol(classSymbol, accessModifier, returnType, parameterTypes, name, isStatic, true, isEmpty);
    }

    /**
     * Returns the same method symbol with the public access modifier set.
     * @return the same method symbol but public
     */
    public MethodSymbol asPublic() {
        return new MethodSymbol(classSymbol, AccessModifier.PUBLIC, returnType, parameterTypes, name, isStatic, isConstant, isEmpty);
    }

    /**
     * Returns the method parameters count.
     * @return the parameters count
     */
    public int getParameterCount() {
        return parameterTypes.length;
    }

    /**
     * Returns the method class symbol owner.
     * @return the class symbol
     */
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    /**
     * Returns the method return type.
     * @return the return type
     */
    public Type getReturnType() {
        return returnType;
    }

    /**
     * Returns the method parameter types.
     * @return the parameter types
     */
    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Returns whether the method has a body (i.e. if it is abstract or not).
     * @return true if the method is empty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Returns whether the given types array contains the same types in the same order.
     * @param types1 the first types array
     * @param types2 the second types array
     * @return true if both types array are equal
     */
    public static boolean isSameTypes(Type[] types1, Type[] types2) {
        //Make sure the arrays are of same length
        if(types1.length != types2.length)
            return false;

        //Check every type one-by-one
        for(int i = 0; i < types1.length; i++) {
            if(!types1[i].equals(types2[i]))
                return false;
        }

        return true;
    }

    /**
     * Returns whether the given types array are matching, i.e. they have
     * the same types in the same order, where subtypes of objects are allowed.
     * @param types1 the first types array
     * @param types2 the second types array
     * @return true if both types array are matching
     */
    public static boolean isMatchingTypes(Type[] types1, Type[] types2) {
        //Make sure the arrays are of same length
        if(types1.length != types2.length)
            return false;

        //Check every type one-by-one
        for(int i = 0; i < types1.length; i++) {
            Type type1 = types1[i];
            Type type2 = types2[i];

            //Continue if type are perfect matches
            if(type1.equals(type2))
                continue;

            //Continue if it is a subtype
            if(type1 instanceof Object object1 && type2 instanceof Object object2) {
                if(object1.getClassSymbol().isSubtypeOf(object2.getClassSymbol()))
                    continue;
            }

            return false;
        }

        return true;
    }

    /**
     * Returns whether the given method symbol has the same signature as the current one.
     * @param object the method symbol
     * @return true if the symbols have the same signature
     */
    @Override
    public boolean equals(java.lang.Object object) {
        if(!(object instanceof MethodSymbol methodSymbol))
            return false;

        //Make sure the method names are equal
        if(!name.equals(methodSymbol.name))
            return false;

        //Make sure the method parameters lengths are equal
        if(parameterTypes.length != methodSymbol.parameterTypes.length)
            return false;

        //Make sure the method parameters types are equal
        for(int i = 0; i < parameterTypes.length; i++) {
            if(!parameterTypes[i].equals(methodSymbol.parameterTypes[i]))
                return false;
        }

        return true;
    }

    /**
     * Compares the given method symbol with the current method symbol.
     * This method compares the two method symbol according to their specificity,
     * i.e. which method has the most specific parameters for a method call.
     * @param methodSymbol the method symbol
     * @return 1 if the current method is more specific than the other method,
     *         0 if the methods are ambiguous,
     *         -1 if the other method is more specific than the current method
     */
    @Override
    public int compareTo(MethodSymbol methodSymbol) {
        for(int i = 0; i < parameterTypes.length; i++) {
            Type type1 = parameterTypes[i];
            Type type2 = methodSymbol.parameterTypes[i];

            if(type1.equals(type2))
                continue;

            if(type1 instanceof Object object1 && type2 instanceof Object object2) {
                if(object1.getClassSymbol().isSubtypeOf(object2.getClassSymbol()))
                    return 1;

                if(object2.getClassSymbol().isSubtypeOf(object1.getClassSymbol()))
                    return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return "MethodSymbol("
                + accessModifier + ", "
                + returnType + ", "
                + name + ", "
                + MethodStringifier.stringify(parameterTypes) + ", "
                + "isStatic=" + isStatic + ", "
                + "isConstant=" + isConstant + ", "
                + "isEmpty=" + isEmpty + ")";
    }
}
