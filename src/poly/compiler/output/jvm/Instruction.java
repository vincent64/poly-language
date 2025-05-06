package poly.compiler.output.jvm;

import poly.compiler.analyzer.table.Variable;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Primitive;
import poly.compiler.analyzer.type.Type;
import poly.compiler.output.Byteable;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.output.content.Descriptor;
import poly.compiler.resolver.symbol.ClassSymbol;

import static poly.compiler.output.jvm.Instructions.*;

/**
 * The Instruction class. This class represents a JVM instruction composed of bytecode bytes.
 * This class also contains several methods that are used to generate instructions for
 * redundant logic, such as loading or storing primitives.
 * @author Vincent Philippe (@vincent64)
 */
public class Instruction implements Byteable {
    private final byte[] bytes;

    /**
     * Constructs an instruction with the given bytes content.
     * @param bytes the bytes content
     */
    private Instruction(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Constructs an instruction with the single given operation code byte.
     * @param code the operation code
     */
    public Instruction(byte code) {
        //Create single-byte instruction array
        bytes = new byte[1];
        bytes[0] = code;
    }

    /**
     * Returns the operation code of the instruction.
     * @return the operation code
     */
    public byte getCode() {
        return bytes[0];
    }

    /**
     * Returns the bytes content of the instruction.
     * @return the instruction content
     */
    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Returns the size of the instruction in bytes.
     * @return the instruction size
     */
    public int getSize() {
        return bytes.length;
    }

    /**
     * Returns the instruction for loading the given constant integer value.
     * @param value the integer value
     * @param constantPool the constant pool
     * @return the instruction for loading a constant integer
     */
    public static Instruction forConstantInteger(int value, ConstantPool constantPool) {
        if(value <= 5 && value >= -1) {
            //Return pushing immediatly available constant
            return switch(value) {
                case -1 -> new Instruction(ICONST_M1);
                case 1 -> new Instruction(ICONST_1);
                case 2 -> new Instruction(ICONST_2);
                case 3 -> new Instruction(ICONST_3);
                case 4 -> new Instruction(ICONST_4);
                case 5 -> new Instruction(ICONST_5);
                default -> new Instruction(ICONST_0);
            };
        } else if(value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
            //Return pushing literal byte
            return new Builder(BIPUSH, 2)
                    .add((byte) value)
                    .build();
        } else if(value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
            //Return pushing literal short
            return new Builder(SIPUSH, 3)
                    .add((short) value)
                    .build();
        } else {
            //Return pushing from constant pool
            return new Builder(LDC_W, 3)
                    .add((short) constantPool.addIntegerConstant(value))
                    .build();
        }
    }

    /**
     * Returns the instruction for loading the given constant long value.
     * @param value the long value
     * @param constantPool the constant pool
     * @return the instruction for loading a constant long
     */
    public static Instruction forConstantLong(long value, ConstantPool constantPool) {
        if(value == 0L) {
            //Return pushing immediatly available constant
            return new Instruction(LCONST_0);
        } else if(value == 1L) {
            //Return pushing immediatly available constant
            return new Instruction(LCONST_1);
        } else {
            //Return pushing from constant pool
            return new Builder(LDC2_W, 3)
                    .add((short) constantPool.addLongConstant(value))
                    .build();
        }
    }

    /**
     * Returns the instruction for loading the given constant float value.
     * @param value the float value
     * @param constantPool the constant pool
     * @return the instruction for loading a constant float
     */
    public static Instruction forConstantFloat(float value, ConstantPool constantPool) {
        if(value == 0f) {
            //Return pushing immediatly available constant
            return new Instruction(FCONST_0);
        } else if(value == 1f) {
            //Return pushing immediatly available constant
            return new Instruction(FCONST_1);
        } else if(value == 2f) {
            //Return pushing immediatly available constant
            return new Instruction(FCONST_2);
        } else {
            //Return pushing from constant pool
            return new Builder(LDC_W, 3)
                    .add((short) constantPool.addFloatConstant(value))
                    .build();
        }
    }

    /**
     * Returns the instruction for loading the given constant double value.
     * @param value the double value
     * @param constantPool the constant pool
     * @return the instruction for loading a constant double
     */
    public static Instruction forConstantDouble(double value, ConstantPool constantPool) {
        if(value == 0f) {
            //Return pushing immediatly available constant
            return new Instruction(DCONST_0);
        } else if(value == 1f) {
            //Return pushing immediatly available constant
            return new Instruction(DCONST_1);
        } else {
            //Return pushing from constant pool
            return new Builder(LDC2_W, 3)
                    .add((short) constantPool.addDoubleConstant(value))
                    .build();
        }
    }

    /**
     * Returns the instruction for loading the constant 1 of the given primitive.
     * @param primitive the primitive
     * @return the instruction for loading the constant 1
     */
    public static Instruction forConstantOne(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> ICONST_1;
            case LONG -> LCONST_1;
            case FLOAT -> FCONST_1;
            case DOUBLE -> DCONST_1;
        });
    }

    /**
     * Returns the instruction for loading the constant 0 of the given primitive.
     * @param primitive the primitive
     * @return the instruction for loading the constant 0
     */
    public static Instruction forConstantZero(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> ICONST_0;
            case LONG -> LCONST_0;
            case FLOAT -> FCONST_0;
            case DOUBLE -> DCONST_0;
        });
    }

    /**
     * Returns the instruction for loading the integer variable at the given index.
     * @param index the variable index
     * @return the instruction for loading an integer variable
     */
    public static Instruction forLoadingInteger(int index) {
        return switch(index) {
            case 0 -> new Instruction(ILOAD_0);
            case 1 -> new Instruction(ILOAD_1);
            case 2 -> new Instruction(ILOAD_2);
            case 3 -> new Instruction(ILOAD_3);
            default -> new Builder(ILOAD, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for loading the long variable at the given index.
     * @param index the variable index
     * @return the instruction for loading a long variable
     */
    public static Instruction forLoadingLong(int index) {
        return switch(index) {
            case 0 -> new Instruction(LLOAD_0);
            case 1 -> new Instruction(LLOAD_1);
            case 2 -> new Instruction(LLOAD_2);
            case 3 -> new Instruction(LLOAD_3);
            default -> new Builder(LLOAD, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for loading the float variable at the given index.
     * @param index the variable index
     * @return the instruction for loading a float variable
     */
    public static Instruction forLoadingFloat(int index) {
        return switch(index) {
            case 0 -> new Instruction(FLOAD_0);
            case 1 -> new Instruction(FLOAD_1);
            case 2 -> new Instruction(FLOAD_2);
            case 3 -> new Instruction(FLOAD_3);
            default -> new Builder(FLOAD, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for loading the double variable at the given index.
     * @param index the variable index
     * @return the instruction for loading a double variable
     */
    public static Instruction forLoadingDouble(int index) {
        return switch(index) {
            case 0 -> new Instruction(DLOAD_0);
            case 1 -> new Instruction(DLOAD_1);
            case 2 -> new Instruction(DLOAD_2);
            case 3 -> new Instruction(DLOAD_3);
            default -> new Builder(DLOAD, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for loading the reference variable at the given index.
     * @param index the variable index
     * @return the instruction for loading a reference variable
     */
    public static Instruction forLoadingReference(int index) {
        return switch(index) {
            case 0 -> new Instruction(ALOAD_0);
            case 1 -> new Instruction(ALOAD_1);
            case 2 -> new Instruction(ALOAD_2);
            case 3 -> new Instruction(ALOAD_3);
            default -> new Builder(ALOAD, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for loading the given variable.
     * @param variable the variable
     * @return the instruction for loading a variable
     */
    public static Instruction forLoading(Variable variable) {
        int index = variable.getIndex();

        //Generate instruction according to variable type
        if(variable.getType() instanceof Primitive primitive) {
            return switch(primitive.getPrimitiveKind()) {
                case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> forLoadingInteger(index);
                case LONG -> forLoadingLong(index);
                case FLOAT -> forLoadingFloat(index);
                case DOUBLE -> forLoadingDouble(index);
            };
        } else {
            return forLoadingReference(index);
        }
    }

    /**
     * Returns the instruction for loading the given type from an array.
     * @param type the type
     * @return the instruction for loading from an array
     */
    public static Instruction forLoadingFromArray(Type type) {
        if(type instanceof Primitive primitive) {
            return new Instruction(switch(primitive.getPrimitiveKind()) {
                case BYTE, BOOLEAN -> BALOAD;
                case SHORT -> SALOAD;
                case CHAR -> CALOAD;
                case INTEGER -> IALOAD;
                case LONG -> LALOAD;
                case FLOAT -> FALOAD;
                case DOUBLE -> DALOAD;
            });
        } else {
            return new Instruction(AALOAD);
        }
    }

    /**
     * Returns the instruction for storing an integer variable at the given index.
     * @param index the variable index
     * @return the instruction for storing an integer variable
     */
    public static Instruction forStoringInteger(int index) {
        return switch(index) {
            case 0 -> new Instruction(ISTORE_0);
            case 1 -> new Instruction(ISTORE_1);
            case 2 -> new Instruction(ISTORE_2);
            case 3 -> new Instruction(ISTORE_3);
            default -> new Builder(ISTORE, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for storing a long variable at the given index.
     * @param index the variable index
     * @return the instruction for storing a long variable
     */
    public static Instruction forStoringLong(int index) {
        return switch(index) {
            case 0 -> new Instruction(LSTORE_0);
            case 1 -> new Instruction(LSTORE_1);
            case 2 -> new Instruction(LSTORE_2);
            case 3 -> new Instruction(LSTORE_3);
            default -> new Builder(LSTORE, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for storing a float variable at the given index.
     * @param index the variable index
     * @return the instruction for storing a float variable
     */
    public static Instruction forStoringFloat(int index) {
        return switch(index) {
            case 0 -> new Instruction(FSTORE_0);
            case 1 -> new Instruction(FSTORE_1);
            case 2 -> new Instruction(FSTORE_2);
            case 3 -> new Instruction(FSTORE_3);
            default -> new Builder(FSTORE, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for storing a double variable at the given index.
     * @param index the variable index
     * @return the instruction for storing a double variable
     */
    public static Instruction forStoringDouble(int index) {
        return switch(index) {
            case 0 -> new Instruction(DSTORE_0);
            case 1 -> new Instruction(DSTORE_1);
            case 2 -> new Instruction(DSTORE_2);
            case 3 -> new Instruction(DSTORE_3);
            default -> new Builder(DSTORE, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for storing a reference variable at the given index.
     * @param index the variable index
     * @return the instruction for storing a reference variable
     */
    public static Instruction forStoringReference(int index) {
        return switch(index) {
            case 0 -> new Instruction(ASTORE_0);
            case 1 -> new Instruction(ASTORE_1);
            case 2 -> new Instruction(ASTORE_2);
            case 3 -> new Instruction(ASTORE_3);
            default -> new Builder(ASTORE, 2)
                    .add((byte) index)
                    .build();
        };
    }

    /**
     * Returns the instruction for storing the given variable.
     * @param variable the variable
     * @return the instruction for storing a variable
     */
    public static Instruction forStoring(Variable variable) {
        int index = variable.getIndex();

        //Generate instruction according to variable type
        if(variable.getType() instanceof Primitive primitive) {
            return switch(primitive.getPrimitiveKind()) {
                case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> forStoringInteger(index);
                case LONG -> forStoringLong(index);
                case FLOAT -> forStoringFloat(index);
                case DOUBLE -> forStoringDouble(index);
            };
        } else {
            return forStoringReference(index);
        }
    }

    /**
     * Returns the instruction for storing the given type in an array.
     * @param type the type
     * @return the instruction for storing in an array
     */
    public static Instruction forStoringInArray(Type type) {
        if(type instanceof Primitive primitive) {
            return new Instruction(switch(primitive.getPrimitiveKind()) {
                case BYTE, BOOLEAN -> BASTORE;
                case SHORT -> SASTORE;
                case CHAR -> CASTORE;
                case INTEGER -> IASTORE;
                case LONG -> LASTORE;
                case FLOAT -> FASTORE;
                case DOUBLE -> DASTORE;
            });
        } else {
            return new Instruction(AASTORE);
        }
    }

    /**
     * Returns the instruction for the addition operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the addition operation
     */
    public static Instruction forAdditionOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> IADD;
            case LONG -> LADD;
            case FLOAT -> FADD;
            case DOUBLE -> DADD;
        });
    }

    /**
     * Returns the instruction for the subtraction operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the subtraction operation
     */
    public static Instruction forSubtractionOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> ISUB;
            case LONG -> LSUB;
            case FLOAT -> FSUB;
            case DOUBLE -> DSUB;
        });
    }

    /**
     * Returns the instruction for the multiplication operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the multiplication operation
     */
    public static Instruction forMultiplicationOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> IMUL;
            case LONG -> LMUL;
            case FLOAT -> FMUL;
            case DOUBLE -> DMUL;
        });
    }

    /**
     * Returns the instruction for the division operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the division operation
     */
    public static Instruction forDivisionOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> IDIV;
            case LONG -> LDIV;
            case FLOAT -> FDIV;
            case DOUBLE -> DDIV;
        });
    }

    /**
     * Returns the instruction for the modulo operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the modulo operation
     */
    public static Instruction forModuloOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> IREM;
            case LONG -> LREM;
            case FLOAT -> FREM;
            case DOUBLE -> DREM;
        });
    }

    /**
     * Returns the instruction for the bitwise AND operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the bitwise AND operation
     */
    public static Instruction forBitwiseAndOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> IAND;
            case LONG -> LAND;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the bitwise XOR operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the bitwise XOR operation
     */
    public static Instruction forBitwiseXorOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> IXOR;
            case LONG -> LXOR;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the bitwise OR operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the bitwise OR operation
     */
    public static Instruction forBitwiseOrOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> IOR;
            case LONG -> LOR;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the left shift operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the left shift operation
     */
    public static Instruction forShiftLeftOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> ISHL;
            case LONG -> LSHL;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the right shift operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the right shift operation
     */
    public static Instruction forShiftRightOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> IUSHR;
            case LONG -> LUSHR;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the arithmetic right shift operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the arithmetic right shift operation
     */
    public static Instruction forShiftRightArithmeticOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> ISHR;
            case LONG -> LSHR;
            default -> NOP;
        });
    }

    /**
     * Returns the instruction for the negation operation of the given primitive.
     * @param primitive the primitive
     * @return the instruction for the negation operation
     */
    public static Instruction forNegationOperation(Primitive primitive) {
        return new Instruction(switch(primitive.getPrimitiveKind()) {
            case INTEGER, BOOLEAN, BYTE, SHORT, CHAR -> INEG;
            case LONG -> LNEG;
            case FLOAT -> FNEG;
            case DOUBLE -> DNEG;
        });
    }

    /**
     * Returns the instruction for an unconditional jump to the given branch index.
     * @param index the branch index
     * @return the instruction for an unconditional jump
     */
    public static Instruction forUnconditionalJump(int index) {
        if(index <= Short.MAX_VALUE) {
            return new Builder(GOTO, 3)
                    .add((short) index)
                    .build();
        } else {
            return new Builder(GOTO_W, 5)
                    .add(index)
                    .build();
        }
    }

    /**
     * Returns the instruction for popping the last operand from the operand stack.
     * @param type the operand type
     * @return the instruction for popping the last operand
     */
    public static Instruction forPoppingFromStack(Type type) {
        return new Instruction(type instanceof Primitive primitive && primitive.isWideType() ? POP_2 : POP);
    }

    /**
     * Returns the instruction for creating a new array with the given type.
     * @param type the array type
     * @param constantPool the constant pool
     * @return the instruction for creating a new array
     */
    public static Instruction forNewArray(Type type, ConstantPool constantPool) {
        //Generate array of primitive
        if(type instanceof Primitive primitive) {
            return new Instruction.Builder(NEWARRAY, 2)
                    .add((byte) switch(primitive.getPrimitiveKind()) {
                        case BOOLEAN -> 4;
                        case CHAR -> 5;
                        case FLOAT -> 6;
                        case DOUBLE -> 7;
                        case BYTE -> 8;
                        case SHORT -> 9;
                        case INTEGER -> 10;
                        case LONG -> 11;
                    }).build();
        }

        //Generate array of object
        else if(type instanceof Object object) {
            ClassSymbol classSymbol = object.getClassSymbol();

            return new Instruction.Builder(ANEWARRAY, 3)
                    .add((short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName()))
                    .build();
        }

        //Generate array of array
        else {
            String descriptor = String.valueOf(Descriptor.getDescriptorFromType(type));

            return new Instruction.Builder(ANEWARRAY, 3)
                    .add((short) constantPool.addClassConstant(descriptor))
                    .build();
        }
    }

    /**
     * The Instruction.Builder class. This class is used to build an instruction
     * using bytes, shorts and integers.
     */
    public static class Builder {
        private final byte[] bytes;
        private int index;

        /**
         * Constructs an instruction builder with the given instruction and size.
         * @param instruction the instruction
         * @param size the instruction size
         */
        public Builder(byte instruction, int size) {
            this(size);

            //Set instruction as first byte
            add(instruction);
        }

        /**
         * Constructs an instruction builder with the given size.
         * @param size the instruction size
         */
        public Builder(int size) {
            bytes = new byte[size];
        }

        /**
         * Adds the given byte value to the instruction.
         * @param value the byte value
         * @return the instruction builder
         */
        public Builder add(byte value) {
            bytes[index++] = value;

            return this;
        }

        /**
         * Adds the given short value to the instruction.
         * @param value the short value
         * @return the instruction builder
         */
        public Builder add(short value) {
            add((byte) ((value >> 8) & 0xFF));
            add((byte) (value & 0xFF));

            return this;
        }

        /**
         * Adds the given int value to the instruction.
         * @param value the int value
         * @return the instruction builder
         */
        public Builder add(int value) {
            add((byte) ((value >> 24) & 0xFF));
            add((byte) ((value >> 16) & 0xFF));
            add((byte) ((value >> 8) & 0xFF));
            add((byte) (value & 0xFF));

            return this;
        }

        /**
         * Builds and returns the instruction.
         * @return the instruction
         */
        public Instruction build() {
            return new Instruction(bytes);
        }
    }
}
