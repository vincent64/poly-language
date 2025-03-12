package poly.compiler.output.content;

import poly.compiler.output.jvm.Instructions;

import java.util.Arrays;
import java.util.Objects;

/**
 * The Constant class. This class represents a constant entry in the constant pool.
 * It contains its constant kind, bytes content and size.
 * @author Vincent Philippe (@vincent64)
 */
public class Constant {
    private final Kind kind;
    private final byte[] content;
    private final int size;

    /**
     * Constructs a constant of the given kind with the given content and size.
     * @param kind the constant kind
     * @param content the constant content
     * @param size the content size
     */
    private Constant(Kind kind, byte[] content, int size) {
        this.kind = kind;
        this.content = content;
        this.size = size;
    }

    /**
     * Constructs a constant of the given kind with the given content.
     * @param kind the constant kind
     * @param content the constant content
     */
    public Constant(Kind kind, byte[] content) {
        this(kind, content, content.length);
    }

    /**
     * Returns whether the constant takes up two entries in the constant pool.
     * Only the long and double constants have this property.
     * @return true if the constant takes up two entries
     */
    public boolean isDoubleEntry() {
        return kind == Kind.LONG || kind == Kind.DOUBLE;
    }

    /**
     * Returns the constant kind.
     * @return the constant kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the constant content.
     * @return the constant content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the constant size.
     * @return the constant size
     */
    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;

        Constant constant = (Constant) object;

        return kind == constant.kind && Arrays.equals(content, constant.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(kind);
        result = 31 * result + Arrays.hashCode(content);

        return result;
    }

    /**
     * The Constant.Kind enum. This enum contains every kind of constant the JVM
     * constant pool can have and their associated tag.
     * @author Vincent Philippe (@vincent64)
     */
    public enum Kind {
        UTF8(Instructions.ConstantTag.UTF8),
        INTEGER(Instructions.ConstantTag.INTEGER),
        FLOAT(Instructions.ConstantTag.FLOAT),
        LONG(Instructions.ConstantTag.LONG),
        DOUBLE(Instructions.ConstantTag.DOUBLE),
        CLASS(Instructions.ConstantTag.CLASS),
        STRING(Instructions.ConstantTag.STRING),
        FIELD_REF(Instructions.ConstantTag.FIELD_REF),
        METHOD_REF(Instructions.ConstantTag.METHOD_REF),
        INTERFACE_METHOD_REF(Instructions.ConstantTag.INTERFACE_METHOD_REF),
        NAME_AND_TYPE(Instructions.ConstantTag.NAME_AND_TYPE),
        METHOD_HANDLE(Instructions.ConstantTag.METHOD_HANDLE),
        METHOD_TYPE(Instructions.ConstantTag.METHOD_TYPE),
        DYNAMIC(Instructions.ConstantTag.DYNAMIC),
        INVOKE_DYNAMIC(Instructions.ConstantTag.INVOKE_DYNAMIC),
        MODULE(Instructions.ConstantTag.MODULE),
        PACKAGE(Instructions.ConstantTag.PACKAGE);

        private final byte tag;

        Kind(byte tag) {
            this.tag = tag;
        }

        public byte getTag() {
            return tag;
        }
    }
}
