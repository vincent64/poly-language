package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.output.jvm.Instructions;
import poly.compiler.util.ByteArray;

/**
 * The Method class. This class represents a method in the class file.
 * According to the JVM specification, a method has the following structure :
 * <pre>
 *      method_info {
 *          u2              access_flags;
 *          u2              name_index;
 *          u2              descriptor_index;
 *          u2              attributes_count;
 *          attribute_info  attributes[attributes_count];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public class Method implements Byteable {
    private final short accessFlag;
    private final short nameIndex;
    private final short descriptorIndex;
    private final Attributes attributes;

    /**
     * Constructs a method with the given access flag, name index, descriptor index and attributes.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     * @param attributes the attributes
     */
    public Method(short accessFlag, short nameIndex, short descriptorIndex, Attributes attributes) {
        this.accessFlag = accessFlag;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }

    /**
     * Constructs a method with the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public Method(short accessFlag, short nameIndex, short descriptorIndex) {
        this(accessFlag, nameIndex, descriptorIndex, new Attributes());
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add method content
        byteArray.add(accessFlag);
        byteArray.add(nameIndex);
        byteArray.add(descriptorIndex);

        //Add attributes content
        byteArray.add(attributes.getBytes());

        return byteArray.getBytes();
    }

    /**
     * Returns the method access modifier.
     * @return the access modifier
     */
    public AccessModifier getAccessModifier() {
        return AccessModifier.fromMethodAccessFlag(accessFlag);
    }

    /**
     * Returns whether the method is static.
     * @return true if the method is static
     */
    public boolean isStatic() {
        return (accessFlag & Instructions.MethodAccessFlag.STATIC) != 0;
    }

    /**
     * Returns whether the method is constant.
     * @return true if the method is constant
     */
    public boolean isConstant() {
        return (accessFlag & Instructions.MethodAccessFlag.FINAL) != 0;
    }

    /**
     * Returns whether the method has a body (i.e. if it is abstract or not).
     * @return true if the method is empty
     */
    public boolean isEmpty() {
        return (accessFlag & Instructions.MethodAccessFlag.ABSTRACT) != 0;
    }

    /**
     * Returns the method name index.
     * @return the name index
     */
    public short getNameIndex() {
        return nameIndex;
    }

    /**
     * Returns the method descriptor index.
     * @return the descriptor index
     */
    public short getDescriptorIndex() {
        return descriptorIndex;
    }

    /**
     * Returns the method attributes.
     * @return the attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }
}
