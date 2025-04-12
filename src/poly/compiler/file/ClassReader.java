package poly.compiler.file;

import poly.compiler.output.ClassFile;
import poly.compiler.output.content.*;
import poly.compiler.output.jvm.Instructions;
import poly.compiler.resolver.LibraryClasses;
import poly.compiler.resolver.symbol.*;
import poly.compiler.util.ByteArray;
import poly.compiler.util.ClassName;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The ClassReader class. This class is used to read the content of a class file
 * and generate its corresponding class symbol and resolve its dependencies.
 * This class will omit every byte that are not necessary for the compiler,
 * mainly the attributes and other constants.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassReader {
    private final byte[] content;
    private int index;

    /**
     * Constructs a class reader with the given byte content.
     * @param content the byte content
     */
    public ClassReader(byte[] content) {
        this.content = content;
    }

    /**
     * Reads the class file content and returns its resolved corresponding class symbol.
     * @return the corresponding class symbol
     */
    public ClassSymbol read() {
        //Make sure the file starts with magic code
        if(readInt() != Instructions.MAGIC) return null;

        //Skip version number
        skipBytes(4);

        //Generate constant pool
        ConstantPool constantPool = generateConstantPool(readShort());

        //Get class access flag
        short accessFlag = readShort();
        //Get class index in constant pool
        short classIndex = readShort();
        //Get superclass index in constant pool
        short superclassIndex = readShort();

        //Read interfaces
        Interfaces interfaces = readInterfaces(readShort());

        //Initialize class file
        ClassFile classFile = new ClassFile(accessFlag, classIndex, superclassIndex, constantPool, interfaces);

        //Read fields
        readFields(classFile, readShort());
        //Read methods
        readMethods(classFile, readShort());

        ClassName className = ClassName.fromStringQualifiedName(classFile.getClassQualifiedName());

        //Generate package
        PackageSymbol packageSymbol = LibraryClasses.getRootSymbol().generatePackage(className.getPackageName());

        ClassName outerClassName = className.getOuterClassName();

        ClassSymbol outerClassSymbol = null;
        if(outerClassName != null)
            outerClassSymbol = LibraryClasses.findClass(outerClassName);

        Symbol ownerSymbol = outerClassName == null ? packageSymbol : outerClassSymbol;

        //Resolve class symbol
        ClassSymbol classSymbol = ClassSymbol.fromClassFile(classFile, ownerSymbol, packageSymbol);

        //Add nested class symbol
        if(outerClassSymbol != null) {
            outerClassSymbol.addSymbol(classSymbol);
        } else {
            packageSymbol.addSymbol(classSymbol);
        }

        //Resolve superclass
        resolveSuperclass(constantPool, classSymbol, superclassIndex);

        //Resolve interfaces
        resolveInterfaces(constantPool, classSymbol, classFile.getInterfaces());

        //Resolve field symbols
        for(Field field : classFile.getFields())
            classSymbol.addSymbol(FieldSymbol.fromClassFile(field, classSymbol, constantPool));

        //Resolve method symbols
        for(Method method : classFile.getMethods())
            classSymbol.addSymbol(MethodSymbol.fromClassFile(method, classSymbol, constantPool));

        return classSymbol;
    }

    /**
     * Resolves the superclass from the given superclass index.
     * @param constantPool the constant pool
     * @param classSymbol the class symbol
     * @param superclassIndex the superclass index
     */
    private void resolveSuperclass(ConstantPool constantPool, ClassSymbol classSymbol, short superclassIndex) {
        if(superclassIndex != 0) {
            ClassName superclassName = resolveClassName(constantPool, superclassIndex);

            //Find or load superclass symbol in library
            ClassSymbol superclassSymbol = LibraryClasses.findClass(superclassName);
            classSymbol.setSuperclassSymbol(superclassSymbol);
        } else {
            classSymbol.setSuperclassSymbol(null);
        }
    }

    /**
     * Resolves the interfaces from the given interface indices.
     * @param constantPool the constant pool
     * @param classSymbol the class symbol
     * @param interfaceIndies the interface indices
     */
    private void resolveInterfaces(ConstantPool constantPool, ClassSymbol classSymbol, List<Short> interfaceIndies) {
        for(short interfaceIndex : interfaceIndies) {
            ClassName interfaceName = resolveClassName(constantPool, interfaceIndex);

            //Find or load interface symbol in library
            ClassSymbol interfaceSymbol = LibraryClasses.findClass(interfaceName);
            classSymbol.getInterfaceSymbols().add(interfaceSymbol);
        }
    }

    /**
     * Resolves and returns the class name from the given class constant index.
     * @param constantPool the constant pool
     * @param index the index
     * @return the class name
     */
    private ClassName resolveClassName(ConstantPool constantPool, short index) {
        //Get name index from constant
        Constant constant = constantPool.getConstant(index);
        short nameIndex = ByteArray.getShortFromByteArray(constant.getContent());

        //Get name constant
        Constant nameConstant = constantPool.getConstant(nameIndex);

        return ClassName.fromStringQualifiedName(new String(nameConstant.getContent(), StandardCharsets.UTF_8));
    }

    /**
     * Reads and returns the given amount of interfaces.
     * @param count the interface count
     * @return the interfaces
     */
    private Interfaces readInterfaces(short count) {
        Interfaces interfaces = new Interfaces();

        for(int i = 0; i < count; i++)
            interfaces.addInterface(readShort());

        return interfaces;
    }

    /**
     * Reads and returns the given amount of fields.
     * @param count the field count
     * @return the fields
     */
    private Fields readFields(short count) {
        Fields fields = new Fields();

        for(int i = 0; i < count; i++) {
            fields.addField(readShort(), readShort(), readShort());
            skipAttributes(readShort());
        }

        return fields;
    }

    /**
     * Reads and adds the given amount of methods to the given class file.
     * @param classFile the class file
     * @param methodCount the method count
     */
    private void readMethods(ClassFile classFile, short methodCount) {
        for(int i = 0; i < methodCount; i++) {
            classFile.addMethod(readShort(), readShort(), readShort());
            skipAttributes(readShort());
        }
    }

    /**
     * Skips the given amount of attributes.
     * @param count the attribute count
     */
    private void skipAttributes(short count) {
        for(int i = 0; i < count; i++) {
            skipBytes(2);
            skipBytes(readInt());
        }
    }

    /**
     * Generates the constant pool from the given pool size.
     * @param size the pool size
     * @return the constant pool
     */
    private ConstantPool generateConstantPool(short size) {
        ConstantPool constantPool = new ConstantPool();

        for(int i = 0; i < size - 1; i++) {
            switch(readByte()) {
                case Instructions.ConstantTag.UTF8 ->
                    constantPool.putConstant(new Constant(Constant.Kind.UTF8, readBytes(readShort())));
                case Instructions.ConstantTag.CLASS ->
                        constantPool.putConstant(new Constant(Constant.Kind.CLASS, readBytes(2)));
                case Instructions.ConstantTag.FIELD_REF ->
                        constantPool.putConstant(new Constant(Constant.Kind.FIELD_REF, readBytes(4)));
                case Instructions.ConstantTag.METHOD_REF ->
                        constantPool.putConstant(new Constant(Constant.Kind.METHOD_REF, readBytes(4)));
                case Instructions.ConstantTag.INTERFACE_METHOD_REF ->
                        constantPool.putConstant(new Constant(Constant.Kind.INTERFACE_METHOD_REF, readBytes(4)));
                case Instructions.ConstantTag.NAME_AND_TYPE ->
                        constantPool.putConstant(new Constant(Constant.Kind.NAME_AND_TYPE, readBytes(4)));
                case Instructions.ConstantTag.STRING ->
                        constantPool.putConstant(new Constant(Constant.Kind.STRING, readBytes(2)));
                case Instructions.ConstantTag.METHOD_HANDLE ->
                        constantPool.putConstant(new Constant(Constant.Kind.METHOD_HANDLE, readBytes(3)));
                case Instructions.ConstantTag.METHOD_TYPE ->
                        constantPool.putConstant(new Constant(Constant.Kind.METHOD_TYPE, readBytes(2)));
                case Instructions.ConstantTag.DYNAMIC ->
                        constantPool.putConstant(new Constant(Constant.Kind.DYNAMIC, readBytes(4)));
                case Instructions.ConstantTag.INVOKE_DYNAMIC ->
                        constantPool.putConstant(new Constant(Constant.Kind.INVOKE_DYNAMIC, readBytes(4)));
                case Instructions.ConstantTag.MODULE ->
                        constantPool.putConstant(new Constant(Constant.Kind.MODULE, readBytes(2)));
                case Instructions.ConstantTag.PACKAGE ->
                        constantPool.putConstant(new Constant(Constant.Kind.PACKAGE, readBytes(2)));
                case Instructions.ConstantTag.INTEGER ->
                        constantPool.putConstant(new Constant(Constant.Kind.INTEGER, readBytes(4)));
                case Instructions.ConstantTag.FLOAT ->
                        constantPool.putConstant(new Constant(Constant.Kind.FLOAT, readBytes(4)));
                case Instructions.ConstantTag.LONG -> {
                    constantPool.putConstant(new Constant(Constant.Kind.LONG, readBytes(8)));
                    i++;
                }
                case Instructions.ConstantTag.DOUBLE -> {
                    constantPool.putConstant(new Constant(Constant.Kind.DOUBLE, readBytes(8)));
                    i++;
                }
            }
        }

        return constantPool;
    }

    /**
     * Reads and returns the next byte from the byte content.
     * @return the next byte
     */
    private byte readByte() {
        return content[index++];
    }

    /**
     * Reads and returns the next short from the byte content.
     * @return the next short
     */
    private short readShort() {
        return (short) ((readByte() & 0xFF) << 8
                | (readByte() & 0xFF));
    }

    /**
     * Reads and returns the next integer from the byte content.
     * @return the next integer
     */
    private int readInt() {
        return (readByte() & 0xFF) << 24
                | (readByte() & 0xFF) << 16
                | (readByte() & 0xFF) << 8
                | (readByte() & 0xFF);
    }

    /**
     * Reads and returns the next given amount of bytes from the byte content.
     * @param amount the byte amount
     * @return the next bytes
     */
    private byte[] readBytes(int amount) {
        byte[] bytes = new byte[amount];

        for(int i = 0; i < amount; i++)
            bytes[i] = readByte();

        return bytes;
    }

    /**
     * Skips the given amount of bytes in the byte content.
     * @param amount the byte amount
     */
    private void skipBytes(int amount) {
        index += amount;
    }
}
