package poly.compiler.output.attribute;

import poly.compiler.analyzer.type.Primitive;
import poly.compiler.output.Byteable;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.util.ByteArray;

import java.util.Arrays;

/**
 * The VerificationType class. This class represents a type used for verification by
 * the JVM during runtime in stack map frames.
 * @author Vincent Philippe (@vincent64)
 */
public class VerificationType implements Byteable {
    private final Kind kind;
    private final byte[] content;

    /**
     * Constructs a verification type with the given kind and content.
     * @param kind the type kind
     * @param content the type content
     */
    private VerificationType(Kind kind, byte[] content) {
        this.kind = kind;
        this.content = content;
    }

    /**
     * Constructs a verification type with the given kind.
     * @param kind the type kind
     */
    private VerificationType(Kind kind) {
        this(kind, new byte[0]);
    }

    /**
     * Returns the verification type for the given primitive.
     * @param primitive the primitive
     * @return the verification type for a primitive
     */
    public static VerificationType forPrimitive(Primitive primitive) {
        return new VerificationType(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> Kind.INTEGER;
            case LONG -> Kind.LONG;
            case FLOAT -> Kind.FLOAT;
            case DOUBLE -> Kind.DOUBLE;
        });
    }

    /**
     * Returns the verification type for an integer.
     * @return the verification type for an integer
     */
    public static VerificationType forInteger() {
        return new VerificationType(Kind.INTEGER);
    }

    /**
     * Returns the verification type for a long.
     * @return the verification type for a long
     */
    public static VerificationType forLong() {
        return new VerificationType(Kind.LONG);
    }

    /**
     * Returns the verification type for a float.
     * @return the verification type for a float
     */
    public static VerificationType forFloat() {
        return new VerificationType(Kind.FLOAT);
    }

    /**
     * Returns the verification type for a double.
     * @return the verification type for a double
     */
    public static VerificationType forDouble() {
        return new VerificationType(Kind.DOUBLE);
    }

    /**
     * Returns the verification type for a top variable.
     * @return the verification type for a top variable
     */
    public static VerificationType forTop() {
        return new VerificationType(Kind.TOP);
    }

    /**
     * Returns the verification type for a null reference.
     * @return the verification type for a null reference
     */
    public static VerificationType forNullReference() {
        return new VerificationType(Kind.NULL);
    }

    /**
     * Returns the verification type for an uninitialized this reference.
     * @return the verification type for an uninitialized this reference
     */
    public static VerificationType forUninitializedThisReference() {
        return new VerificationType(Kind.UNINITIALIZED_THIS);
    }

    /**
     * Returns the verification type for an uninitialized object.
     * @param programCounter the program counter of the new instruction
     * @return the verification type for an uninitialized object
     */
    public static VerificationType forUninitializedObject(short programCounter) {
        return new VerificationType(Kind.UNINITIALIZED, ByteArray.getShortAsByteArray(programCounter));
    }

    /**
     * Returns the verification type for an object.
     * @param index the class constant index in the constant pool
     * @return the verification type for an object
     */
    public static VerificationType forObject(short index) {
        return new VerificationType(Kind.OBJECT, ByteArray.getShortAsByteArray(index));
    }

    /**
     * Returns the verification type for an object.
     * @param classSymbol the class symbol
     * @param constantPool the constant pool
     * @return the verification type for an object
     */
    public static VerificationType forObject(ClassSymbol classSymbol, ConstantPool constantPool) {
        return forObject((short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName()));
    }

    /**
     * Returns whether the verification type takes up two entries.
     * @return true if the type takes up two entries
     */
    public boolean isDoubleEntry() {
        return kind == Kind.LONG || kind == Kind.DOUBLE;
    }

    /**
     * Returns the verification type kind.
     * @return the type kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the verification type byte content.
     * @return the type content
     */
    public byte[] getContent() {
        return content;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof VerificationType that))
            return false;

        return kind == that.kind && Arrays.equals(content, that.content);
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add kind tag and content
        byteArray.add(kind.getTag());
        byteArray.add(content);

        return byteArray.getBytes();
    }

    /**
     * The VerificationType.Kind enum. This enum contains every kind of
     * verification type there exists and their associated tag.
     */
    public enum Kind {
        TOP(0),
        INTEGER(1),
        FLOAT(2),
        LONG(4),
        DOUBLE(3),
        NULL(5),
        UNINITIALIZED_THIS(6),
        OBJECT(7),
        UNINITIALIZED(8);

        private final byte tag;

        Kind(int tag) {
            this.tag = (byte) tag;
        }

        public byte getTag() {
            return tag;
        }
    }
}
