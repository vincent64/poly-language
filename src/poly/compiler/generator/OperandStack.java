package poly.compiler.generator;

import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Primitive;
import poly.compiler.analyzer.type.Type;
import poly.compiler.output.attribute.VerificationType;
import poly.compiler.output.content.Constant;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.output.content.Descriptor;
import poly.compiler.output.jvm.Instruction;
import poly.compiler.util.ByteArray;
import poly.compiler.util.ClassName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static poly.compiler.output.jvm.Instructions.*;

/**
 * The OperandStack class. This class represents the operand stack at any
 * given moment during the runtime process. This class is used only during
 * the code generation for the JVM verifications.
 * When a double entry type is pushed onto the stack, the stack count is
 * increased by 2 instead of 1. When this type is later popped off the stack,
 * the stack count is decreased back by 2 instead of 1.
 * @author Vincent Philippe (@vincent64)
 */
public class OperandStack {
    private final List<VerificationType> stackTypes;
    private final ConstantPool constantPool;
    private final LocalTable localTable;
    private int stackSize;
    private int maxStack;

    /**
     * Constructs an operand stack with the given constant pool and local table.
     * @param constantPool the constant pool
     * @param localTable the local table
     */
    public OperandStack(ConstantPool constantPool, LocalTable localTable) {
        this.constantPool = constantPool;
        this.localTable = localTable;

        //Initialize stack types list
        stackTypes = new ArrayList<>();
    }

    /**
     * Pushes the given verification type on the stack.
     * @param type the verification type
     */
    private void push(VerificationType type) {
        stackTypes.add(type);
        stackSize += type.isDoubleEntry() ? 2 : 1;

        //Increase max stack size
        if(stackSize > maxStack)
            maxStack = stackSize;
    }

    /**
     * Inserts the given verification type at the given index in the stack.
     * @param index the index
     * @param type the verification type
     */
    private void insert(int index, VerificationType type) {
        stackTypes.add(index, type);
        stackSize += type.isDoubleEntry() ? 2 : 1;

        //Increase max stack size
        if(stackSize > maxStack)
            maxStack = stackSize;
    }

    /**
     * Pushes the given type on the stack.
     * @param type the type
     */
    protected void push(Type type) {
        //Push primitive type
        if(type instanceof Primitive primitive)
            push(VerificationType.forPrimitive(primitive));

        //Push object type
        else if(type instanceof Object object)
            push(VerificationType.forObject(object.getClassSymbol(), constantPool));

        //Push array type
        else {
            String descriptor = String.valueOf(Descriptor.getDescriptorFromType(type));
            push(VerificationType.forObject((short) constantPool.addClassConstant(descriptor)));
        }
    }

    /**
     * Pops the given amount of operands from the stack.
     * @param count the operand count
     */
    protected void pop(int count) {
        for(int i = 0; i < count; i++) {
            VerificationType type = stackTypes.removeLast();
            stackSize -= type.isDoubleEntry() ? 2 : 1;
        }
    }

    /**
     * Returns the topmost operand from the stack.
     * @return the topmost operand
     */
    private VerificationType peek() {
        return stackTypes.getLast();
    }

    /**
     * Returns the operand at the given count from the top of the stack.
     * @param count the location count
     * @return the operand
     */
    private VerificationType peek(int count) {
        return stackTypes.get(stackTypes.size() - count - 1);
    }

    /**
     * Updates the stack from the given instruction and program counter.
     * Every time the operand stack is updated with a given instruction,
     * it will perform every obvious operation on the stack.
     * This does not include variable stack operations, such as pushing the return type
     * of a method call. These operation are directly provided by the generator.
     * @param instruction the instruction
     * @param programCounter the program counter
     */
    public void update(Instruction instruction, int programCounter) {
        byte code = instruction.getCode();
        byte[] bytes = Arrays.copyOfRange(instruction.getBytes(), 1, instruction.getSize());

        //Get original wide instruction
        if(code == WIDE) {
            code = bytes[1];
            bytes = Arrays.copyOfRange(instruction.getBytes(), 2, instruction.getSize());
        }

        switch(code) {
            case ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
                    ILOAD, ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3, BIPUSH, SIPUSH ->
                push(VerificationType.forInteger());

            case LCONST_0, LCONST_1, LLOAD, LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3 ->
                push(VerificationType.forLong());

            case FCONST_0, FCONST_1, FCONST_2, FLOAD, FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3 ->
                push(VerificationType.forFloat());

            case DCONST_0, DCONST_1, DLOAD, DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3 ->
                push(VerificationType.forDouble());

            case ACONST_NULL ->
                push(VerificationType.forNullReference());

            case ALOAD ->
                push(localTable.getLocal(bytes[0]));

            case ALOAD_0 ->
                push(localTable.getLocal(0));

            case ALOAD_1 ->
                push(localTable.getLocal(1));

            case ALOAD_2 ->
                push(localTable.getLocal(2));

            case ALOAD_3 ->
                push(localTable.getLocal(3));

            case LDC, LDC_W, LDC2_W -> {
                Constant constant = constantPool.getConstant(code != LDC
                        ? ByteArray.getShortFromByteArray(bytes)
                        : bytes[0]);

                switch(constant.getKind()) {
                    case INTEGER -> push(VerificationType.forInteger());
                    case LONG -> push(VerificationType.forLong());
                    case FLOAT -> push(VerificationType.forFloat());
                    case DOUBLE -> push(VerificationType.forDouble());
                    case STRING -> push(VerificationType.forObject(
                            (short) constantPool.addClassConstant(ClassName.STRING.toInternalQualifiedName())));
                }
            }

            case NEW ->
                push(VerificationType.forUninitializedObject((short) (programCounter - 3)));

            case CHECKCAST, ANEWARRAY -> {
                pop(1);
                push(VerificationType.forObject(ByteArray.getShortFromByteArray(bytes)));
            }

            case IADD, ISUB, IMUL, IDIV, IREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR,
                    LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IALOAD, BALOAD, SALOAD, CALOAD -> {
                pop(2);
                push(VerificationType.forInteger());
            }

            case LADD, LSUB, LMUL, LDIV, LREM, LSHL, LSHR, LUSHR, LAND, LOR, LXOR, LALOAD -> {
                pop(2);
                push(VerificationType.forLong());
            }

            case FADD, FSUB, FMUL, FDIV, FREM, FALOAD -> {
                pop(2);
                push(VerificationType.forFloat());
            }

            case DADD, DSUB, DMUL, DDIV, DREM, DALOAD -> {
                pop(2);
                push(VerificationType.forDouble());
            }

            case ISTORE, ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3,
                    LSTORE, LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3,
                    FSTORE, FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3,
                    DSTORE, DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3,
                    ASTORE, ASTORE_0, ASTORE_1, ASTORE_2, ASTORE_3,
                    POP, IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE,
                    IFNULL, IFNONNULL, TABLESWITCH, LOOKUPSWITCH,
                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKEINTERFACE,
                    IRETURN, LRETURN, FRETURN, DRETURN, ARETURN,
                    PUTSTATIC, GETFIELD, NEWARRAY, ATHROW ->
                pop(1);

            case IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
                    IF_ACMPEQ, IF_ACMPNE, AALOAD, PUTFIELD ->
                pop(2);

            case IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE ->
                pop(3);

            case DUP ->
                push(stackTypes.getLast());

            case DUP_X1 ->
                insert(stackTypes.size() - 2, stackTypes.getLast());

            case DUP_X2 -> {
                if(!peek(1).isDoubleEntry()) {
                    insert(stackTypes.size() - 3, stackTypes.getLast());
                } else {
                    insert(stackTypes.size() - 2, stackTypes.getLast());
                }
            }

            case DUP2 -> {
                if(!peek().isDoubleEntry()) {
                    push(stackTypes.get(stackTypes.size() - 2));
                    push(stackTypes.get(stackTypes.size() - 2));
                } else {
                    push(stackTypes.getLast());
                }
            }

            case DUP2_X1 -> {
                if(!peek().isDoubleEntry()) {
                    insert(stackTypes.size() - 3, stackTypes.getLast());
                    insert(stackTypes.size() - 4, stackTypes.get(stackTypes.size() - 2));
                } else {
                    insert(stackTypes.size() - 2, stackTypes.getLast());
                }
            }

            case DUP2_X2 -> {
                if(peek().isDoubleEntry() && peek(1).isDoubleEntry()) {
                    insert(stackTypes.size() - 2, stackTypes.getLast());
                } else if(peek().isDoubleEntry()) {
                    insert(stackTypes.size() - 3, stackTypes.getLast());
                } else if(peek(3).isDoubleEntry()) {
                    insert(stackTypes.size() - 3, stackTypes.getLast());
                    insert(stackTypes.size() - 4, stackTypes.get(stackTypes.size() - 2));
                } else {
                    insert(stackTypes.size() - 4, stackTypes.getLast());
                    insert(stackTypes.size() - 5, stackTypes.get(stackTypes.size() - 2));
                }
            }

            case L2I, F2I, D2I, I2B, I2S, I2C, ARRAYLENGTH -> {
                pop(1);
                push(VerificationType.forInteger());
            }

            case I2L, F2L, D2L -> {
                pop(1);
                push(VerificationType.forLong());
            }

            case I2F, L2F, D2F -> {
                pop(1);
                push(VerificationType.forFloat());
            }

            case I2D, L2D, F2D -> {
                pop(1);
                push(VerificationType.forDouble());
            }
        }
    }

    /**
     * Returns the stack types.
     * @return the stack types
     */
    public List<VerificationType> getStackTypes() {
        return stackTypes;
    }

    /**
     * Returns the operand stack size.
     * @return the stack size
     */
    public int getSize() {
        return stackTypes.size();
    }

    /**
     * Returns whether the operand stack is empty.
     * @return true if the stack is empty
     */
    public boolean isEmpty() {
        return stackTypes.isEmpty();
    }

    /**
     * Returns the maximum stack size.
     * @return the max stack size
     */
    public int getMaxStack() {
        return maxStack;
    }
}
