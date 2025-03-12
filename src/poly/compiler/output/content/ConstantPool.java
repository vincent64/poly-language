package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The ConstantPool class. This class represents the constant pool structure of
 * a class file, as described by the JVM specification.
 * It contains its byte content as well as the entries count.
 * A constant entry has the following structure :
 * <pre>
 *      cp_info {
 *          u1  tag;
 *          u1  info[];
 *      }
 * </pre>
 * The class contains several methods to add the various kind of constants
 * in the constant pool. Note that these methods return the index of the
 * created constant in the constant pool. If the constant pool already had
 * the created constant, it will not duplicate it, and will return the
 * index of the preexisting one.
 * @author Vincent Philippe (@vincent64)
 */
public class ConstantPool implements Byteable {
    private final List<Constant> constants;
    private short entryCount;

    public ConstantPool() {
        //Initialize contants list
        constants = new ArrayList<>();
    }

    /**
     * Adds the given constant in the constant pool and returns its index in
     * the constant pool. If the constant is already present in the constant pool,
     * the method will only return its index in it.
     * @param constant the constant
     * @return the index of the constant in the constant pool
     */
    public int addConstant(Constant constant) {
        //Add the constant if it doesn't exist yet
        if(!constants.contains(constant)) {
            constants.add(constant);

            int constantIndex = ++entryCount;

            //Add empty constant if constant takes two entries
            if(constant.isDoubleEntry()) {
                constants.add(null);
                entryCount++;
            }

            return constantIndex;
        }

        return constants.indexOf(constant) + 1;
    }

    /**
     * Puts the given constant in the constant pool.
     * As opposed to the addConstant method, this method adds the constant
     * whether it is already present in the constant pool, and does not return its index.
     * @param constant the constant
     */
    public void putConstant(Constant constant) {
        constants.add(constant);

        entryCount++;

        //Add empty constant if constant takes two entries
        if(constant.isDoubleEntry()) {
            constants.add(null);
            entryCount++;
        }
    }

    /**
     * Returns the constant at the given index in the constant pool.
     * @param index the constant index
     * @return the constant at the index
     */
    public Constant getConstant(int index) {
        return constants.get(index - 1);
    }

    /**
     * Returns the amount of constant entries the constant pool has.
     * @return the entry count
     */
    public int getEntryCount() {
        return constants.size();
    }

    /**
     * Adds a UTF-8 constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The UTF-8 constant has the following structure :
     * <pre>
     *      CONSTANT_Utf8_info {
     *          u1 tag;
     *          u2 length;
     *          u1 bytes[length];
     *      }
     * </pre>
     * @param value the string
     * @return the index of the constant in the constant pool
     */
    public int addUTF8Constant(String value) {
        //Get UTF-8 byte content from char array
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.UTF8.getTag());
        byteArray.add((short) utf8.length);
        byteArray.add(utf8);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.UTF8, bytes));
    }

    /**
     * Adds a class constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The class constant has the following structure :
     * <pre>
     *      CONSTANT_Class_info {
     *          u1 tag;
     *          u2 name_index;
     *      }
     * </pre>
     * @param name the class name
     * @return the index of the constant in the constant pool
     */
    public int addClassConstant(String name) {
        //Add the class name as constant
        int utf8Index = addUTF8Constant(name);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.CLASS.getTag());
        byteArray.add((short) utf8Index);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.CLASS, bytes));
    }

    /**
     * Adds a NameAndType constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The NameAndType constant has the following structure :
     * <pre>
     *      CONSTANT_NameAndType_info {
     *          u1 tag;
     *          u2 name_index;
     *          u2 descriptor_index;
     *      }
     * </pre>
     * @param name the name
     * @param descriptor the type descriptor
     * @return the index of the constant in the constant pool
     */
    public int addNameAndTypeConstant(String name, String descriptor) {
        //Add name and type strings as constants
        int nameIndex = addUTF8Constant(name);
        int descriptorIndex = addUTF8Constant(descriptor);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.NAME_AND_TYPE.getTag());
        byteArray.add((short) nameIndex);
        byteArray.add((short) descriptorIndex);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.NAME_AND_TYPE, bytes));
    }

    /**
     * Adds a field reference constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The field reference constant has the following structure :
     * <pre>
     *      CONSTANT_Fieldref_info {
     *          u1 tag;
     *          u2 class_index;
     *          u2 name_and_type_index;
     *      }
     * </pre>
     * @param className the class name
     * @param name the field name
     * @param descriptor the field descriptor
     * @return the index of the constant in the constant pool
     */
    public int addFieldRefConstant(String className, String name, String descriptor) {
        //Add class and name-type constants
        int classIndex = addClassConstant(className);
        int nameAndTypeIndex = addNameAndTypeConstant(name, descriptor);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.FIELD_REF.getTag());
        byteArray.add((short) classIndex);
        byteArray.add((short) nameAndTypeIndex);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.FIELD_REF, bytes));
    }

    /**
     * Adds a method reference constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The method reference constant has the following structure :
     * <pre>
     *      CONSTANT_Methodref_info {
     *          u1 tag;
     *          u2 class_index;
     *          u2 name_and_type_index;
     *      }
     * </pre>
     * @param className the class name
     * @param name the method name
     * @param descriptor the method descriptor
     * @return the index of the constant in the constant pool
     */
    public int addMethodRefConstant(String className, String name, String descriptor) {
        //Add class and name-type constants
        int classIndex = addClassConstant(className);
        int nameAndTypeIndex = addNameAndTypeConstant(name, descriptor);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.METHOD_REF.getTag());
        byteArray.add((short) classIndex);
        byteArray.add((short) nameAndTypeIndex);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.METHOD_REF, bytes));
    }

    /**
     * Adds an interface method reference constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The interface method reference constant has the following structure :
     * <pre>
     *      CONSTANT_InterfaceMethodref_info {
     *          u1 tag;
     *          u2 class_index;
     *          u2 name_and_type_index;
     *      }
     * </pre>
     * @param className the class name
     * @param name the method name
     * @param descriptor the method descriptor
     * @return the index of the constant in the constant pool
     */
    public int addInterfaceMethodRefConstant(String className, String name, String descriptor) {
        //Add class and name-type constants
        int classIndex = addClassConstant(className);
        int nameAndTypeIndex = addNameAndTypeConstant(name, descriptor);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.INTERFACE_METHOD_REF.getTag());
        byteArray.add((short) classIndex);
        byteArray.add((short) nameAndTypeIndex);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.INTERFACE_METHOD_REF, bytes));
    }

    /**
     * Adds an integer value constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The integer value constant has the following structure :
     * <pre>
     *      CONSTANT_Integer_info {
     *          u1 tag;
     *          u4 bytes;
     *      }
     * </pre>
     * @param value the integer value
     * @return the index of the constant in the constant pool
     */
    public int addIntegerConstant(int value) {
        //Create byte array containing the integer value
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.INTEGER.getTag());
        byteArray.add(ByteArray.getIntegerAsByteArray(value));

        return addConstant(new Constant(Constant.Kind.INTEGER, byteArray.getBytes()));
    }

    /**
     * Adds a float value constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The float value constant has the following structure :
     * <pre>
     *      CONSTANT_Float_info {
     *          u1 tag;
     *          u4 bytes;
     *      }
     * </pre>
     * @param value the float value
     * @return the index of the constant in the constant pool
     */
    public int addFloatConstant(float value) {
        //Create byte array containing the float value
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.FLOAT.getTag());
        byteArray.add(ByteArray.getFloatAsByteArray(value));

        return addConstant(new Constant(Constant.Kind.FLOAT, byteArray.getBytes()));
    }

    /**
     * Adds a long value constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The long value constant has the following structure :
     * <pre>
     *      CONSTANT_Long_info {
     *          u1 tag;
     *          u4 high_bytes;
     *          u4 low_bytes;
     *      }
     * </pre>
     * @param value the long value
     * @return the index of the constant in the constant pool
     */
    public int addLongConstant(long value) {
        //Create byte array containing the long value
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.LONG.getTag());
        byteArray.add(ByteArray.getLongAsByteArray(value));

        return addConstant(new Constant(Constant.Kind.LONG, byteArray.getBytes()));
    }

    /**
     * Adds a double value constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The double value constant has the following structure :
     * <pre>
     *      CONSTANT_Double_info {
     *          u1 tag;
     *          u4 high_bytes;
     *          u4 low_bytes;
     *      }
     * </pre>
     * @param value the double value
     * @return the index of the constant in the constant pool
     */
    public int addDoubleConstant(double value) {
        //Create byte array containing the double value
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.DOUBLE.getTag());
        byteArray.add(ByteArray.getDoubleAsByteArray(value));

        return addConstant(new Constant(Constant.Kind.DOUBLE, byteArray.getBytes()));
    }

    /**
     * Adds a string constant in the constant pool and returns its
     * index in the constant pool. If the constant is already present
     * in the constant pool, it will only return its index in it.
     * The string constant has the following structure :
     * <pre>
     *      CONSTANT_String_info {
     *          u1 tag;
     *          u2 string_index;
     *      }
     * </pre>
     * @param string the string content
     * @return the index of the constant in the constant pool
     */
    public int addStringConstant(String string) {
        //Add the string as constant
        int utf8Index = addUTF8Constant(string);

        //Create byte array containing the constant content
        ByteArray byteArray = new ByteArray();
        byteArray.add(Constant.Kind.STRING.getTag());
        byteArray.add((short) utf8Index);
        byte[] bytes = byteArray.getBytes();

        return addConstant(new Constant(Constant.Kind.STRING, bytes));
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add entry count
        byteArray.add((short) (entryCount + 1));

        //Add every constant content
        for(Constant constant : constants) {
            if(constant != null)
                byteArray.add(constant.getContent());
        }

        return byteArray.getBytes();
    }
}
