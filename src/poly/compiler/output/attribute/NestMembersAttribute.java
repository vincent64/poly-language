package poly.compiler.output.attribute;

import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The NestMembersAttribute class. This class represents an attribute containing the
 * list of nested classes of a class, and extends from the Attribute class.
 * According to the JVM specification, a nested members attribute has the following structure :
 * <pre>
 *      NestMembers_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 number_of_classes;
 *          u2 classes[number_of_classes];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class NestMembersAttribute extends Attribute {
    private static final String NAME = "NestMembers";
    private final List<Short> nestedClassIndices;

    /**
     * Constructs a nested members attribute.
     * @param constantPool the constant pool
     */
    public NestMembersAttribute(ConstantPool constantPool) {
        super((short) constantPool.addUTF8Constant(NAME), 0);

        //Initialize nested classes list
        nestedClassIndices = new ArrayList<>();
    }

    /**
     * Adds a nested class with the given class index.
     * @param classIndex the class index
     */
    public void addNestedClass(short classIndex) {
        nestedClassIndices.add(classIndex);
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(nestedClassIndices.size() * 2 + 2);
        byteArray.add((short) nestedClassIndices.size());

        //Add every nested class content
        for(Short index : nestedClassIndices)
            byteArray.add(index);

        return byteArray.getBytes();
    }
}
