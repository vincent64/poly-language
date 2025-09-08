package poly.compiler.resolver.symbol;

import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Type;
import poly.compiler.error.ResolvingError;
import poly.compiler.output.content.*;
import poly.compiler.parser.tree.FieldDeclaration;
import poly.compiler.parser.tree.variable.EnumConstant;
import poly.compiler.parser.tree.variable.VariableDeclaration;
import poly.compiler.tokenizer.content.Keyword;

import java.nio.charset.StandardCharsets;

/**
 * The FieldSymbol class. This class is the symbol representation of a field.
 * It can be created either from a field declaration in the source code,
 * or from a library class file.
 * @author Vincent Philippe (@vincent64)
 */
public final class FieldSymbol extends Symbol {
    private final ClassSymbol classSymbol;
    private final Type type;
    private final boolean isEnum;

    private FieldSymbol(ClassSymbol classSymbol, AccessModifier accessModifier, Type type,
                        String name, boolean isStatic, boolean isConstant, boolean isEnum) {
        super(Kind.FIELD, accessModifier, name, isStatic, isConstant);
        this.classSymbol = classSymbol;
        this.type = type;
        this.isEnum = isEnum;
    }

    /**
     * Creates and returns the field symbol from the given field declaration.
     * @param fieldDeclaration the field declaration
     * @param classSymbol the class symbol
     * @param importTable the importations table
     * @return the field symbol
     */
    public static FieldSymbol fromFieldDeclaration(FieldDeclaration fieldDeclaration, ClassSymbol classSymbol, ImportTable importTable) {
        //Get the variable declaration
        VariableDeclaration variableDeclaration = (VariableDeclaration) fieldDeclaration.getVariable();

        Type type = Type.fromTypeNode(variableDeclaration.getType(), classSymbol, importTable);

        //Make sure the type is valid
        if(type == null)
            new ResolvingError.UnresolvableType(variableDeclaration.getType());

        return new FieldSymbol(classSymbol,
                fieldDeclaration.getAccessModifier(),
                type,
                variableDeclaration.getName(),
                fieldDeclaration.isStatic(),
                variableDeclaration.isConstant(),
                false);
    }

    /**
     * Creates and returns the field symbol from the given enum constant.
     * @param constant the enum constant
     * @param classSymbol the class symbol
     * @return the field symbol
     */
    public static FieldSymbol fromEnumConstant(EnumConstant constant, ClassSymbol classSymbol) {
        return new FieldSymbol(classSymbol,
                AccessModifier.PUBLIC,
                new Object(classSymbol),
                constant.getName(),
                true, true, true);
    }

    /**
     * Creates and returns the field symbol from the given class file field.
     * @param field the class file field
     * @param classSymbol the class symbol
     * @param constantPool the constant pool
     * @return the field symbol
     */
    public static FieldSymbol fromClassFile(Field field, ClassSymbol classSymbol, ConstantPool constantPool) {
        //Get constants from field in constant pool
        Constant nameConstant = constantPool.getConstant(field.getNameIndex());
        Constant descriptorConstant = constantPool.getConstant(field.getDescriptorIndex());

        //Get name from constant
        String name = new String(nameConstant.getContent(), StandardCharsets.UTF_8);

        //Get descriptor from constant
        String descriptor = new String(descriptorConstant.getContent(), StandardCharsets.UTF_8);

        //Parse descriptor to get field type
        Type type = Descriptor.getTypeFromFieldDescriptor(descriptor.toCharArray());

        return new FieldSymbol(classSymbol,
                field.getAccessModifier(),
                type,
                name,
                field.isStatic(),
                field.isConstant(),
                false);
    }

    /**
     * Generates and returns the field symbol of an outer reference for the given class symbol
     * @param classSymbol the class symbol
     * @return the outer reference field symbol
     */
    public static FieldSymbol generateOuterField(ClassSymbol classSymbol) {
        return new FieldSymbol(classSymbol,
                AccessModifier.DEFAULT,
                new Object((ClassSymbol) classSymbol.getOwnerSymbol()),
                String.valueOf(Keyword.EXPRESSION_OUTER),
                false, true, false);
    }

    /**
     * Returns whether the current field can be accessed from the given class symbol origin.
     * @param classSymbol the class symbol origin
     * @return true if the field can be accessed from the class
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
     * Returns the same field symbol with the static behavior modifier set.
     * @return the same field symbol but static
     */
    public FieldSymbol asStatic() {
        return new FieldSymbol(classSymbol, accessModifier, type, name, true, isConstant, isEnum);
    }

    /**
     * Returns the same field symbol with the static and constant behavior modifiers set.
     * @return the same field symbol but static and constant
     */
    public FieldSymbol asStaticConstant() {
        return new FieldSymbol(classSymbol, accessModifier, type, name, true, true, isEnum);
    }

    /**
     * Returns the field class symbol owner.
     * @return the class symbol
     */
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    /**
     * Returns the field type.
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns whether the field is an enum constant.
     * @return true if the field is an enum constant
     */
    public boolean isEnum() {
        return isEnum;
    }

    /**
     * Returns whether the given field symbol has the same name as the current one.
     * @param object the field symbol
     * @return true if the symbols have the same name
     */
    @Override
    public boolean equals(java.lang.Object object) {
        if(!(object instanceof FieldSymbol fieldSymbol))
            return false;

        return name.equals(fieldSymbol.name);
    }

    @Override
    public String toString() {
        return "FieldSymbol("
                + accessModifier + ", "
                + type + ", "
                + name + ", "
                + "isStatic=" + isStatic + ", "
                + "isConstant=" + isConstant + ", "
                + "isEnum=" + isEnum + ")";
    }
}
