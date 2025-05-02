package poly.compiler.output.attribute;

import poly.compiler.generator.ExceptionTable;
import poly.compiler.output.content.Attributes;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

/**
 * The CodeAttribute class. This class represents a code attribute,
 * and extends from the Attribute class. Every non-empty method has this attribute.
 * According to the JVM specification, a code attribute has the following structure :
 * <pre>
 *      Code_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 max_stack;
 *          u2 max_locals;
 *          u4 code_length;
 *          u1 code[code_length];
 *          u2 exception_table_length;
 *          {   u2 start_pc;
 *              u2 end_pc;
 *              u2 handler_pc;
 *              u2 catch_type;
 *          } exception_table[exception_table_length];
 *          u2 attributes_count;
 *          attribute_info attributes[attributes_count];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class CodeAttribute extends Attribute {
    private static final String NAME = "Code";
    private final short maxStack;
    private final short maxLocals;
    private final int codeLength;
    private final byte[] code;
    private final ExceptionTable exceptionTable;
    private final Attributes attributes;

    /**
     * Constructs a code attribute with the given max stack count, max locals count,
     * code content, exception table and attributes.
     * @param constantPool the constant pool
     * @param maxStack the max stack count
     * @param maxLocals the max locals count
     * @param code the code content
     * @param exceptionTable the exception table
     * @param attributes the attributes
     */
    public CodeAttribute(ConstantPool constantPool, short maxStack, short maxLocals, byte[] code,
                         ExceptionTable exceptionTable, Attributes attributes) {
        super((short) constantPool.addUTF8Constant(NAME), code.length + 8);
        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.codeLength = code.length;
        this.exceptionTable = exceptionTable;
        this.code = code;
        this.attributes = attributes;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        byte[] exceptionBytes = exceptionTable.getBytes();
        byte[] attributeBytes = attributes.getBytes();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(length + exceptionBytes.length + attributeBytes.length);

        //Add code attribute content
        byteArray.add(maxStack);
        byteArray.add(maxLocals);
        byteArray.add(codeLength);
        byteArray.add(code);

        //Add exception table content
        byteArray.add(exceptionBytes);
        //Add attributes content
        byteArray.add(attributeBytes);

        return byteArray.getBytes();
    }
}
