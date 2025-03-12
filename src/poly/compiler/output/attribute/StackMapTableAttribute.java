package poly.compiler.output.attribute;

import poly.compiler.generator.StackMapTable;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

/**
 * The StackMapTableAttribute. This class represents an attribute containing stack map frame table,
 * and extends from the Attribute class. This attribute is mandatory for code attributes.
 * According to the JVM specification, a stack map table attribute has the following structure :
 * <pre>
 *      StackMapTable_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 number_of_entries;
 *          stack_map_frame entries[number_of_entries];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class StackMapTableAttribute extends Attribute {
    private static final String NAME = "StackMapTable";
    private final StackMapTable stackMapTable;

    /**
     * Constructs a stack map table attribute with the given stack map table.
     * @param constantPool the constant pool
     * @param stackMapTable the stack map table
     */
    public StackMapTableAttribute(ConstantPool constantPool, StackMapTable stackMapTable) {
        super((short) constantPool.addUTF8Constant(NAME), 0);
        this.stackMapTable = stackMapTable;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        byte[] tableBytes = stackMapTable.getBytes();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(tableBytes.length);

        //Add stack map table content
        byteArray.add(tableBytes);

        return byteArray.getBytes();
    }
}
