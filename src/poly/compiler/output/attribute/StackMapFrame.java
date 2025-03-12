package poly.compiler.output.attribute;

import poly.compiler.generator.LocalTable;
import poly.compiler.generator.OperandStack;
import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

/**
 * The StackMapFrame class. This class represents a stack map frame, as described by
 * the JVM specification.
 * @author Vincent Philippe (@vincent64)
 */
public class StackMapFrame implements Byteable {
    private final Kind kind;
    private final short offset;
    private final byte[] content;

    /**
     * Constructs a stack map frame with the given kind, offset and byte content.
     * @param kind the frame kind
     * @param offset the frame offset
     * @param content the frame content
     */
    private StackMapFrame(Kind kind, short offset, byte[] content) {
        this.kind = kind;
        this.offset = offset;
        this.content = content;
    }

    /**
     * Constructs a stack map frame with the given kind and offset.
     * @param kind the frame kind
     * @param offset the frame offset
     */
    private StackMapFrame(Kind kind, short offset) {
        this(kind, offset, new byte[0]);
    }

    /**
     * Creates and returns a full stack map frame from the given operand stack, local table and offset.
     * @param operandStack the operand stack
     * @param localTable the local table
     * @param offset the frame offset
     * @return a full stack map frame
     */
    public static StackMapFrame createFullFrame(OperandStack operandStack, LocalTable localTable, int offset) {
        ByteArray byteArray = new ByteArray();

        //Add operand stack types
        byteArray.add((short) localTable.getCount());
        for(VerificationType type : localTable.getLocalTypes())
            byteArray.add(type.getBytes());

        //Add local types
        byteArray.add((short) operandStack.getSize());
        for(VerificationType type : operandStack.getStackTypes())
            byteArray.add(type.getBytes());

        return new StackMapFrame(Kind.FULL_FRAME, (short) offset, byteArray.getBytes());
    }

    /**
     * Creates and returns a same stack map frame from the given offset.
     * @param offset the frame offset
     * @return a same stack map frame
     */
    public static StackMapFrame createSameFrame(int offset) {
        return new StackMapFrame(Kind.SAME_FRAME, (short) offset);
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        switch(kind) {
            case FULL_FRAME -> {
                byteArray.add(kind.getTag());
                byteArray.add(offset);
                byteArray.add(content);
            }

            case SAME_FRAME -> {
                byteArray.add((byte) (kind.getTag() + offset));
                byteArray.add(content);
            }
        }

        return byteArray.getBytes();
    }

    /**
     * The StackMapFrame.Kind enum. This enum contains every kind of
     * stack map frame there is and their associated tag.
     */
    public enum Kind {
        SAME_FRAME(0),
        SAME_LOCALS_1_STACK_ITEM_FRAME(64),
        SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED(247),
        CHOP_FRAME(248),
        SAME_FRAME_EXTENDED(251),
        APPEND_FRAME(252),
        FULL_FRAME(255);

        private final byte tag;

        Kind(int tag) {
            this.tag = (byte) tag;
        }

        public byte getTag() {
            return tag;
        }
    }
}
