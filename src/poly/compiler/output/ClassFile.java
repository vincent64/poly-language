package poly.compiler.output;

import poly.compiler.file.CodeReader;
import poly.compiler.output.attribute.Attribute;
import poly.compiler.output.attribute.CodeAttribute;
import poly.compiler.output.attribute.InnerClassesAttribute;
import poly.compiler.output.attribute.SourceFileAttribute;
import poly.compiler.output.content.*;
import poly.compiler.output.jvm.Instructions;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.FieldSymbol;
import poly.compiler.resolver.symbol.MethodSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ByteArray;
import poly.compiler.util.ClassName;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The ClassFile class. This class represents a class file and its content,
 * as described by the JVM specification. The class file structure is as follows :
 * <pre>
 *      u4              magic;
 *      u2              minor_version;
 *      u2              major_version;
 *      u2              constant_pool_count;
 *      cp_info         constant_pool[constant_pool_count-1];
 *      u2              access_flags;
 *      u2              this_class;
 *      u2              super_class;
 *      u2              interfaces_count;
 *      u2              interfaces[interfaces_count];
 *      u2              fields_count;
 *      field_info      fields[fields_count];
 *      u2              methods_count;
 *      method_info     methods[methods_count];
 *      u2              attributes_count;
 *      attribute_info  attributes[attributes_count];
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public class ClassFile implements Byteable {
    private final String classQualifiedName;
    private final ConstantPool constantPool;
    private final Interfaces interfaces;
    private final Fields fields;
    private final Methods methods;
    private final Attributes attributes;
    private final short classIndex;
    private final short superclassIndex;
    private final short accessFlag;

    /**
     * Constructs a class file from the given class symbol.
     * @param classSymbol the class symbol
     */
    public ClassFile(ClassSymbol classSymbol) {
        //Initialize class file content
        constantPool = new ConstantPool();
        interfaces = new Interfaces();
        fields = new Fields();
        methods = new Methods();
        attributes = new Attributes();

        classQualifiedName = classSymbol.getClassInternalQualifiedName();

        //Set class access modifier
        short accessFlag = classSymbol.getAccessModifier().getClassAccessFlag();

        //Set class interface flag
        if(classSymbol.isInterface())
            accessFlag |= Instructions.ClassAccessFlag.INTERFACE
                    | Instructions.ClassAccessFlag.ABSTRACT;

        //Set class enum flag
        if(classSymbol.isEnum())
            accessFlag |= Instructions.ClassAccessFlag.ENUM;

        //Set class final flag
        if(classSymbol.isConstant())
            accessFlag |= Instructions.ClassAccessFlag.FINAL;

        //Set class static flag if nested but not inner
        if(classSymbol.isNested() && !classSymbol.isInner())
            accessFlag |= Instructions.ClassAccessFlag.STATIC;

        this.accessFlag = accessFlag;

        //Set class in constant pool
        classIndex = (short) constantPool.addClassConstant(classQualifiedName);

        //Set superinterface in constant pool
        if(classSymbol.isInterface()) {
            superclassIndex = (short) constantPool.addClassConstant(ClassName.OBJECT.toInternalQualifiedName());

            //Add superinterface in constant pool
            ClassSymbol superclassSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();
            if(!superclassSymbol.isRoot()) {
                String interfaceName = superclassSymbol.getClassInternalQualifiedName();
                interfaces.addInterface((short) constantPool.addClassConstant(interfaceName));
            }
        }

        //Set superclass in constant pool
        else if(classSymbol.getSuperclassSymbol() != null) {
            String superclassName = ((ClassSymbol) classSymbol.getSuperclassSymbol()).getClassInternalQualifiedName();
            superclassIndex = (short) constantPool.addClassConstant(superclassName);
        } else {
            superclassIndex = 0;
        }

        //Add interfaces
        for(Symbol symbol : classSymbol.getInterfaceSymbols()) {
            ClassSymbol interfaceSymbol = (ClassSymbol) symbol;

            //Add interface in constant pool
            String interfaceName = interfaceSymbol.getClassInternalQualifiedName();
            interfaces.addInterface((short) constantPool.addClassConstant(interfaceName));
        }
    }

    /**
     * Constructs a class file from the given access flag, class index, superclass index,
     * constant pool, interfaces, fields, methods and attributes.
     * @param accessFlag the access flag
     * @param classIndex the class index
     * @param superclassIndex the superclass index
     * @param constantPool the constant pool
     * @param interfaces the interfaces
     * @param fields the fields
     * @param methods the methods
     * @param attributes the attributes
     */
    public ClassFile(short accessFlag, short classIndex, short superclassIndex, ConstantPool constantPool,
                     Interfaces interfaces, Fields fields, Methods methods, Attributes attributes) {
        this.accessFlag = accessFlag;
        this.classIndex = classIndex;
        this.superclassIndex = superclassIndex;
        this.constantPool = constantPool;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.attributes = attributes;

        //Get class constant
        Constant classConstant = constantPool.getConstant(classIndex);
        short nameIndex = ByteArray.getShortFromByteArray(classConstant.getContent());
        //Get class name constant
        Constant nameConstant = constantPool.getConstant(nameIndex);
        classQualifiedName = new String(nameConstant.getContent(), StandardCharsets.UTF_8);
    }

    /**
     * Adds a field in the class file from the given field symbol.
     * @param fieldSymbol the field symbol
     */
    public void addField(FieldSymbol fieldSymbol) {
        //Set field access modifier
        short accessFlag = fieldSymbol.getAccessModifier().getFieldAccessFlag();

        //Set static field flag
        if(fieldSymbol.isStatic())
            accessFlag |= Instructions.FieldAccessFlag.STATIC;

        //Set final field flag
        if(fieldSymbol.isConstant())
            accessFlag |= Instructions.FieldAccessFlag.FINAL;

        //Set enum field flag
        if(fieldSymbol.isEnum())
            accessFlag |= Instructions.FieldAccessFlag.ENUM;

        //Add field name to constant pool
        short nameIndex = (short) constantPool.addUTF8Constant(fieldSymbol.getName());

        //Add descriptor to constant pool
        char[] descriptor = Descriptor.generateFieldDescriptor(fieldSymbol);
        short descriptorIndex = (short) constantPool.addUTF8Constant(String.valueOf(descriptor));

        //Add field to fields content
        fields.addField(accessFlag, nameIndex, descriptorIndex);
    }

    /**
     * Adds a field in the class file from the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public void addField(short accessFlag, short nameIndex, short descriptorIndex) {
        fields.addField(accessFlag, nameIndex, descriptorIndex);
    }

    /**
     * Adds a method in the class file from the given method symbol and code attribute.
     * @param methodSymbol the method symbol
     * @param codeAttribute the code attribute
     */
    public void addMethod(MethodSymbol methodSymbol, CodeAttribute codeAttribute) {
        //Set method access modifier
        short accessFlag = methodSymbol.getAccessModifier().getMethodAccessFlag();

        //Set static method flag
        if(methodSymbol.isStatic())
            accessFlag |= Instructions.MethodAccessFlag.STATIC;

        //Set final method flag
        if(methodSymbol.isConstant())
            accessFlag |= Instructions.MethodAccessFlag.FINAL;

        //Set empty method abstract flag
        if(methodSymbol.isEmpty())
            accessFlag |= Instructions.MethodAccessFlag.ABSTRACT;

        //Add method name to constant pool
        short nameIndex = (short) constantPool.addUTF8Constant(methodSymbol.getName());

        //Add descriptor to constant pool
        char[] descriptor = Descriptor.generateMethodDescriptor(methodSymbol);
        short descriptorIndex = (short) constantPool.addUTF8Constant(String.valueOf(descriptor));

        //Add method to methods content
        methods.addMethod(accessFlag, nameIndex, descriptorIndex, codeAttribute);
    }

    /**
     * Adds a method in the class file from the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public void addMethod(short accessFlag, short nameIndex, short descriptorIndex) {
        methods.addMethod(accessFlag, nameIndex, descriptorIndex);
    }

    /**
     * Adds the given attribute to the class attributes.
     * @param attribute the attribute
     */
    public void addAttribute(Attribute attribute) {
        attributes.addAttribute(attribute);
    }

    /**
     * Adds a source file attribute with the given file name.
     * @param fileName the file name
     */
    public void addSourceFileAttribute(String fileName) {
        //Generate source file attribute
        SourceFileAttribute sourceFileAttribute = new SourceFileAttribute(
                constantPool,
                fileName + CodeReader.EXTENSION);
        attributes.addAttribute(sourceFileAttribute);
    }

    /**
     * Returns the bytes array making up the class file.
     * @return the class file byte array
     */
    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add Java magic code and version
        byteArray.add(Instructions.MAGIC);
        byteArray.add(Instructions.Version.JAVA_19);

        //Add constant pool count and content
        byteArray.add(constantPool.getBytes());

        //Add access flag
        byteArray.add(accessFlag);

        //Add this class and super class index
        byteArray.add(classIndex);
        byteArray.add(superclassIndex);

        //Add interfaces content
        byteArray.add(interfaces.getBytes());

        //Add fields content
        byteArray.add(fields.getBytes());

        //Add methods content
        byteArray.add(methods.getBytes());

        //Add attributes content
        byteArray.add(attributes.getBytes());

        return byteArray.getBytes();
    }

    /**
     * Returns the class access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return AccessModifier.fromClassAccessFlag(accessFlag);
    }

    /**
     * Returns whether the class is an interface.
     * @return true if the class is an interface
     */
    public boolean isInterface() {
        return (accessFlag & Instructions.ClassAccessFlag.INTERFACE) != 0;
    }

    /**
     * Returns whether the class is an enum.
     * @return true if the class is an enum
     */
    public boolean isEnum() {
        return (accessFlag & Instructions.ClassAccessFlag.ENUM) != 0;
    }

    /**
     * Returns whether the class is static.
     * @return true if the class is static
     */
    public boolean isStatic() {
        return (accessFlag & Instructions.ClassAccessFlag.STATIC) != 0;
    }

    /**
     * Returns whether the class is constant.
     * @return true if the class is constant
     */
    public boolean isConstant() {
        return (accessFlag & Instructions.ClassAccessFlag.FINAL) != 0;
    }

    /**
     * Returns the class interface indices list.
     * @return the interfaces
     */
    public List<Short> getInterfaces() {
        return interfaces.getInterfaces();
    }

    /**
     * Returns the class fields list.
     * @return the fields
     */
    public List<Field> getFields() {
        return fields.getFields();
    }

    /**
     * Returns the class methods list.
     * @return the methods
     */
    public List<Method> getMethods() {
        return methods.getMethods();
    }

    /**
     * Returns the class qualified name.
     * @return the qualified name
     */
    public String getClassQualifiedName() {
        return classQualifiedName;
    }

    /**
     * Returns the superclass qualified name.
     * @return the superclass qualified name (null if root)
     */
    public String getSuperclassQualifiedName() {
        //Return root class case
        if(superclassIndex == 0)
            return null;

        //Get superclass constant
        Constant classConstant = constantPool.getConstant(superclassIndex);
        short nameIndex = ByteArray.getShortFromByteArray(classConstant.getContent());
        //Get superclass name constant
        Constant nameConstant = constantPool.getConstant(nameIndex);
        return new String(nameConstant.getContent(), StandardCharsets.UTF_8);
    }

    /**
     * Returns the class constant pool.
     * @return the constant pool
     */
    public ConstantPool getConstantPool() {
        return constantPool;
    }
}
