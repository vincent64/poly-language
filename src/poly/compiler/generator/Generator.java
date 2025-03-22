package poly.compiler.generator;

import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.output.ClassFile;
import poly.compiler.output.attribute.CodeAttribute;
import poly.compiler.output.attribute.InnerClassesAttribute;
import poly.compiler.output.attribute.NestHostAttribute;
import poly.compiler.output.attribute.NestMembersAttribute;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.resolver.ClassDefinition;
import poly.compiler.resolver.MethodDefinition;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.FieldSymbol;

import java.util.List;

/**
 * The Generator class. This class is used to transform the class symbol and the
 * analyzed AST into a Java bytecode class file. This class will transform
 * field and method symbols into bytecode descriptor, and will generate the
 * bytecode content from the method declarations.
 * @author Vincent Philippe (@vincent64)
 */
public final class Generator {
    private final ClassDefinition classDefinition;
    private final ImportTable importTable;
    private final ClassFile classFile;

    private Generator(ClassDefinition classDefinition, ImportTable importTable, String fileName) {
        this.classDefinition = classDefinition;
        this.importTable = importTable;

        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Create class file
        classFile = new ClassFile(classSymbol);

        //Add source file attribute to class file
        classFile.addSourceFileAttribute(fileName);
    }

    public static Generator getInstance(ClassDefinition classDefinition, ImportTable importTable, String fileName) {
        return new Generator(classDefinition, importTable, fileName);
    }

    /**
     * Generates and returns the class file.
     * @return the class file
     */
    public ClassFile generate() {
        ClassSymbol classSymbol = classDefinition.getClassSymbol();

        //Generate every field
        for(FieldSymbol fieldSymbol : classSymbol.getFields())
            generateField(fieldSymbol);

        //Generate every method
        for(MethodDefinition methodDefinition : classDefinition.getMethodDefinitions())
            generateMethod(methodDefinition);

        //Generate nested classes attributes
        generateNestedClasses(classSymbol);

        //Generate outer class attribute
        if(classSymbol.getOwnerSymbol() instanceof ClassSymbol outerClassSymbol)
            classFile.addAttribute(new NestHostAttribute(classFile.getConstantPool(), outerClassSymbol));

        return classFile;
    }

    /**
     * Generates the field from the given field symbol.
     * @param fieldSymbol the field symbol
     */
    private void generateField(FieldSymbol fieldSymbol) {
        //Add field to class file
        classFile.addField(fieldSymbol);
    }

    /**
     * Generates the method and its code content from the given method definition.
     * @param methodDefinition the method definition
     */
    private void generateMethod(MethodDefinition methodDefinition) {
        CodeAttribute codeAttribute = null;

        //Generate method code content
        if(!methodDefinition.getMethodSymbol().isEmpty()) {
            codeAttribute = CodeGenerator.getInstance(classDefinition, classFile.getConstantPool(), importTable)
                    .generate(methodDefinition.getMethodDeclaration());
        }

        //Add method to class file
        classFile.addMethod(methodDefinition.getMethodSymbol(), codeAttribute);
    }

    /**
     * Generates the attributes for the given class symbol nested classes.
     * @param classSymbol the class symbol
     */
    private void generateNestedClasses(ClassSymbol classSymbol) {
        List<ClassSymbol> nestedClassSymbols = classSymbol.getClasses();
        ConstantPool constantPool = classFile.getConstantPool();

        //Return no nested classes
        if(nestedClassSymbols.isEmpty())
            return;

        InnerClassesAttribute innerClassesAttribute = new InnerClassesAttribute(constantPool);
        NestMembersAttribute nestMembersAttribute = new NestMembersAttribute(constantPool);

        for(ClassSymbol nestedClassSymbol : nestedClassSymbols) {
            //Add inner class
            innerClassesAttribute.addInnerClass(
                    nestedClassSymbol.getAccessModifier().getClassAccessFlag(),
                    (short) constantPool.addUTF8Constant(nestedClassSymbol.getName()),
                    (short) constantPool.addClassConstant(nestedClassSymbol.getClassInternalQualifiedName()),
                    (short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName()));
            //Add nested class
            nestMembersAttribute.addNestedClass((short) constantPool.addClassConstant(nestedClassSymbol.getClassInternalQualifiedName()));
        }

        //Add the attributes to the class file
        classFile.addAttribute(innerClassesAttribute);
        classFile.addAttribute(nestMembersAttribute);
    }
}
