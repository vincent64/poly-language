package poly.compiler.output.attribute;

import poly.compiler.output.Byteable;

/**
 * The Attribute abstract class. This class represents an attribute in the class file.
 * According to the JVM specification, an attribute has the following structure :
 * <pre>
 *      attribute_info {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u1 info[attribute_length];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Attribute implements Byteable {
    protected final short nameIndex;
    protected final int length;

    /**
     * Constructs an attribute with the given name index and length.
     * @param nameIndex the name index
     * @param length the length
     */
    public Attribute(short nameIndex, int length) {
        this.nameIndex = nameIndex;
        this.length = length;
    }

    /**
     * Returns the byte array making up the attribute.
     * @return the attribute bytes array
     */
    @Override
    public abstract byte[] getBytes();

    /**
     * Returns the attribute name index.
     * @return the name index
     */
    public short getNameIndex() {
        return nameIndex;
    }

    /**
     * Returns the attribute length.
     * @return the length
     */
    public int getLength() {
        return length;
    }
}
