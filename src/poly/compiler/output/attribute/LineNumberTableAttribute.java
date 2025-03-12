package poly.compiler.output.attribute;

import poly.compiler.generator.LineNumberTable;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

/**
 * The LineNumberTableAttribute class. This class represents an attribute pairing the
 * line numbers in the code to instructions offset, and extends from the Attribute class.
 * According to the JVM specification, a line number table attribute has the following structure :
 * <pre>
 *      LineNumberTable_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 line_number_table_length;
 *          {   u2 start_pc;
 *              u2 line_number;
 *          }   line_number_table[line_number_table_length];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class LineNumberTableAttribute extends Attribute {
    private static final String NAME = "LineNumberTable";
    private final LineNumberTable lineNumberTable;

    /**
     * Constructs a line number table attribute with the given line number table.
     * @param constantPool the constant pool
     * @param lineNumberTable the line number table
     */
    public LineNumberTableAttribute(ConstantPool constantPool, LineNumberTable lineNumberTable) {
        super((short) constantPool.addUTF8Constant(NAME), 0);
        this.lineNumberTable = lineNumberTable;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        byte[] tableBytes = lineNumberTable.getBytes();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(tableBytes.length);

        //Add line number table content
        byteArray.add(tableBytes);

        return byteArray.getBytes();
    }
}
