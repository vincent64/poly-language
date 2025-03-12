package poly.compiler.output.attribute;

import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

/**
 * The SourceFileAttribute class. This class represents an attribute containing the
 * source code file information, and extends from the Attribute class.
 * According to the JVM specification, a source file attribute has the following structure :
 * <pre>
 *      SourceFile_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 sourcefile_index;
 *      }
 * </pre>
 */
public final class SourceFileAttribute extends Attribute {
    private static final String NAME = "SourceFile";
    private final short sourceFileIndex;

    /**
     * Constructs a source file attribute with the given source file name.
     * @param constantPool the constant pool
     * @param sourceFileName the source file name
     */
    public SourceFileAttribute(ConstantPool constantPool, String sourceFileName) {
        super((short) constantPool.addUTF8Constant(NAME), 2);

        //Add source file name to constant pool
        sourceFileIndex = (short) constantPool.addUTF8Constant(sourceFileName);
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(length);

        //Add source file attribute content
        byteArray.add(sourceFileIndex);

        return byteArray.getBytes();
    }
}
