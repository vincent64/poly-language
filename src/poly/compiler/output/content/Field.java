package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.output.jvm.Instructions;
import poly.compiler.util.ByteArray;

/**
 * The Field class. This class represents a field in the class file.
 * According to the JVM specification, a field has the following structure :
 * <pre>
 *      field_info {
 *          u2              access_flags;
 *          u2              name_index;
 *          u2              descriptor_index;
 *          u2              attributes_count;
 *          attribute_info  attributes[attributes_count];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public class Field implements Byteable {
    private final short accessFlag;
    private final short nameIndex;
    private final short descriptorIndex;
    private final Attributes attributes;

    /**
     * Constructs a field with the given access flag, name index, descriptor index and attributes.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     * @param attributes the attributes
     */
    public Field(short accessFlag, short nameIndex, short descriptorIndex, Attributes attributes) {
        this.accessFlag = accessFlag;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }

    /**
     * Constructs a field with the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public Field(short accessFlag, short nameIndex, short descriptorIndex) {
        this(accessFlag, nameIndex, descriptorIndex, new Attributes());
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add field content
        byteArray.add(accessFlag);
        byteArray.add(nameIndex);
        byteArray.add(descriptorIndex);

        //Add attributes content
        byteArray.add(attributes.getBytes());

        return byteArray.getBytes();
    }

    /**
     * Returns the field access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return AccessModifier.fromFieldAccessFlag(accessFlag);
    }

    /**
     * Returns whether the field is static.
     * @return true if the field is static
     */
    public boolean isStatic() {
        return (accessFlag & Instructions.FieldAccessFlag.STATIC) != 0;
    }

    /**
     * Returns whether the field is constant.
     * @return true if the field is constant
     */
    public boolean isConstant() {
        return (accessFlag & Instructions.FieldAccessFlag.FINAL) != 0;
    }

    /**
     * Returns the field name index.
     * @return the name index
     */
    public short getNameIndex() {
        return nameIndex;
    }

    /**
     * Returns the field descriptor index.
     * @return the descriptor index
     */
    public short getDescriptorIndex() {
        return descriptorIndex;
    }

    /**
     * Returns the field attributes.
     * @return the attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }
}
