package poly.compiler.output.jvm;

/**
 * The Instructions class. This class contains the value of every JVM instruction there exist.
 * The instructions are saved in static variables whose identifier is its mnemonic code.
 * The instruction descriptions are the same as the one provided by the JVM specification.
 * @author Vincent Philippe (@vincent64)
 */
public class Instructions {
    /** Java magic code. */
    public static final int MAGIC = 0xCAFEBABE;

    /**
     * Java version codes (major and minor combined).
     */
    public static class Version {
        public static final int JAVA_8 = 0x0000_0034;
        public static final int JAVA_9 = 0x0000_0035;
        public static final int JAVA_10 = 0x0000_0036;
        public static final int JAVA_11 = 0x0000_0037;
        public static final int JAVA_12 = 0x0000_0038;
        public static final int JAVA_13 = 0x0000_0039;
        public static final int JAVA_14 = 0x0000_003A;
        public static final int JAVA_15 = 0x0000_003B;
        public static final int JAVA_16 = 0x0000_003C;
        public static final int JAVA_17 = 0x0000_003D;
        public static final int JAVA_18 = 0x0000_003E;
        public static final int JAVA_19 = 0x0000_003F;
        public static final int JAVA_20 = 0x0000_0040;
        public static final int JAVA_21 = 0x0000_0041;
        public static final int JAVA_22 = 0x0000_0042;
        public static final int JAVA_23 = 0x0000_0043;
        public static final int JAVA_24 = 0x0000_0044;
        public static final int JAVA_25 = 0x0000_0045;
    }

    /** No operation. */
    public static final byte NOP = (byte) 0x00;

    /** Push the null object reference. */
    public static final byte ACONST_NULL = (byte) 0x01;
    /** Push int constant value -1. */
    public static final byte ICONST_M1 = (byte) 0x02;
    /** Push int constant value 0. */
    public static final byte ICONST_0 = (byte) 0x03;
    /** Push int constant value 1. */
    public static final byte ICONST_1 = (byte) 0x04;
    /** Push int constant value 2. */
    public static final byte ICONST_2 = (byte) 0x05;
    /** Push int constant value 3. */
    public static final byte ICONST_3 = (byte) 0x06;
    /** Push int constant value 4. */
    public static final byte ICONST_4 = (byte) 0x07;
    /** Push int constant value 5. */
    public static final byte ICONST_5 = (byte) 0x08;
    /** Push long constant value 0. */
    public static final byte LCONST_0 = (byte) 0x09;
    /** Push long constant value 1. */
    public static final byte LCONST_1 = (byte) 0x0A;
    /** Push float constant value 0. */
    public static final byte FCONST_0 = (byte) 0x0B;
    /** Push float constant value 1. */
    public static final byte FCONST_1 = (byte) 0x0C;
    /** Push float constant value 2. */
    public static final byte FCONST_2 = (byte) 0x0D;
    /** Push double constant value 0. */
    public static final byte DCONST_0 = (byte) 0x0E;
    /** Push double constant value 1. */
    public static final byte DCONST_1 = (byte) 0x0F;
    /** Push byte value. */
    public static final byte BIPUSH = (byte) 0x10;
    /** Push short value. */
    public static final byte SIPUSH = (byte) 0x11;
    /** Push item from run-time constant pool. */
    public static final byte LDC = (byte) 0x12;
    /** Push item from run-time constant pool (wide index). */
    public static final byte LDC_W = (byte) 0x13;
    /** Push long or double from run-time constant pool (wide index). */
    public static final byte LDC2_W = (byte) 0x14;

    /** Load int from local variable. */
    public static final byte ILOAD = (byte) 0x15;
    /** Load long from local variable. */
    public static final byte LLOAD = (byte) 0x16;
    /** Load float from local variable. */
    public static final byte FLOAD = (byte) 0x17;
    /** Load double from local variable. */
    public static final byte DLOAD = (byte) 0x18;
    /** Load reference from local variable. */
    public static final byte ALOAD = (byte) 0x19;
    /** Load int from local variable 0. */
    public static final byte ILOAD_0 = (byte) 0x1A;
    /** Load int from local variable 1. */
    public static final byte ILOAD_1 = (byte) 0x1B;
    /** Load int from local variable 2. */
    public static final byte ILOAD_2 = (byte) 0x1C;
    /** Load int from local variable 3. */
    public static final byte ILOAD_3 = (byte) 0x1D;
    /** Load long from local variable 0. */
    public static final byte LLOAD_0 = (byte) 0x1E;
    /** Load long from local variable 1. */
    public static final byte LLOAD_1 = (byte) 0x1F;
    /** Load long from local variable 2. */
    public static final byte LLOAD_2 = (byte) 0x20;
    /** Load long from local variable 3. */
    public static final byte LLOAD_3 = (byte) 0x21;
    /** Load float from local variable 0. */
    public static final byte FLOAD_0 = (byte) 0x22;
    /** Load float from local variable 1. */
    public static final byte FLOAD_1 = (byte) 0x23;
    /** Load float from local variable 2. */
    public static final byte FLOAD_2 = (byte) 0x24;
    /** Load float from local variable 3. */
    public static final byte FLOAD_3 = (byte) 0x25;
    /** Load double from local variable 0. */
    public static final byte DLOAD_0 = (byte) 0x26;
    /** Load double from local variable 1. */
    public static final byte DLOAD_1 = (byte) 0x27;
    /** Load double from local variable 2. */
    public static final byte DLOAD_2 = (byte) 0x28;
    /** Load double from local variable 3. */
    public static final byte DLOAD_3 = (byte) 0x29;
    /** Load reference from local variable 0. */
    public static final byte ALOAD_0 = (byte) 0x2A;
    /** Load reference from local variable 1. */
    public static final byte ALOAD_1 = (byte) 0x2B;
    /** Load reference from local variable 2. */
    public static final byte ALOAD_2 = (byte) 0x2C;
    /** Load reference from local variable 3. */
    public static final byte ALOAD_3 = (byte) 0x2D;
    /** Load int from array. */
    public static final byte IALOAD = (byte) 0x2E;
    /** Load long from array. */
    public static final byte LALOAD = (byte) 0x2F;
    /** Load float from array. */
    public static final byte FALOAD = (byte) 0x30;
    /** Load double from array. */
    public static final byte DALOAD = (byte) 0x31;
    /** Load reference from array. */
    public static final byte AALOAD = (byte) 0x32;
    /** Load byte or boolean from array. */
    public static final byte BALOAD = (byte) 0x33;
    /** Load char from array. */
    public static final byte CALOAD = (byte) 0x34;
    /** Load short from array. */
    public static final byte SALOAD = (byte) 0x35;

    /** Store int into local variable. */
    public static final byte ISTORE = (byte) 0x36;
    /** Store long into local variable. */
    public static final byte LSTORE = (byte) 0x37;
    /** Store float into local variable. */
    public static final byte FSTORE = (byte) 0x38;
    /** Store double into local variable. */
    public static final byte DSTORE = (byte) 0x39;
    /** Store reference into local variable. */
    public static final byte ASTORE = (byte) 0x3A;
    /** Store int into local variable 0. */
    public static final byte ISTORE_0 = (byte) 0x3B;
    /** Store int into local variable 1. */
    public static final byte ISTORE_1 = (byte) 0x3C;
    /** Store int into local variable 2. */
    public static final byte ISTORE_2 = (byte) 0x3D;
    /** Store int into local variable 3. */
    public static final byte ISTORE_3 = (byte) 0x3E;
    /** Store long into local variable 0. */
    public static final byte LSTORE_0 = (byte) 0x3F;
    /** Store long into local variable 1. */
    public static final byte LSTORE_1 = (byte) 0x40;
    /** Store long into local variable 2. */
    public static final byte LSTORE_2 = (byte) 0x41;
    /** Store long into local variable 3. */
    public static final byte LSTORE_3 = (byte) 0x42;
    /** Store float into local variable 0. */
    public static final byte FSTORE_0 = (byte) 0x43;
    /** Store float into local variable 1. */
    public static final byte FSTORE_1 = (byte) 0x44;
    /** Store float into local variable 2. */
    public static final byte FSTORE_2 = (byte) 0x45;
    /** Store float into local variable 3. */
    public static final byte FSTORE_3 = (byte) 0x46;
    /** Store double into local variable 0. */
    public static final byte DSTORE_0 = (byte) 0x47;
    /** Store double into local variable 1. */
    public static final byte DSTORE_1 = (byte) 0x48;
    /** Store double into local variable 2. */
    public static final byte DSTORE_2 = (byte) 0x49;
    /** Store double into local variable 3. */
    public static final byte DSTORE_3 = (byte) 0x4A;
    /** Store reference into local variable 0. */
    public static final byte ASTORE_0 = (byte) 0x4B;
    /** Store reference into local variable 1. */
    public static final byte ASTORE_1 = (byte) 0x4C;
    /** Store reference into local variable 2. */
    public static final byte ASTORE_2 = (byte) 0x4D;
    /** Store reference into local variable 3. */
    public static final byte ASTORE_3 = (byte) 0x4E;
    /** Store into int array. */
    public static final byte IASTORE = (byte) 0x4F;
    /** Store into long array. */
    public static final byte LASTORE = (byte) 0x50;
    /** Store into float array. */
    public static final byte FASTORE = (byte) 0x51;
    /** Store into double array. */
    public static final byte DASTORE = (byte) 0x52;
    /** Store into reference array. */
    public static final byte AASTORE = (byte) 0x53;
    /** Store into byte or boolean array. */
    public static final byte BASTORE = (byte) 0x54;
    /** Store into char array. */
    public static final byte CASTORE = (byte) 0x55;
    /** Store into short array. */
    public static final byte SASTORE = (byte) 0x56;

    /** Pop the top operand stack value. */
    public static final byte POP = (byte) 0x57;
    /** Pop the top one or two operand stack values. */
    public static final byte POP_2 = (byte) 0x58;
    /** Duplicate the top operand stack value. */
    public static final byte DUP = (byte) 0x59;
    /** Duplicate the top operand stack value and insert two values down. */
    public static final byte DUP_X1 = (byte) 0x5A;
    /** Duplicate the top operand stack value and insert two or three values down. */
    public static final byte DUP_X2 = (byte) 0x5B;
    /** Duplicate the top one or two operand stack values. */
    public static final byte DUP2 = (byte) 0x5C;
    /** Duplicate the top one or two operand stack values and insert two or three values down. */
    public static final byte DUP2_X1 = (byte) 0x5D;
    /** Duplicate the top one or two operand stack values and insert two, three or four values down. */
    public static final byte DUP2_X2 = (byte) 0x5E;
    /** Swap the top two operand stack values. */
    public static final byte SWAP = (byte) 0x5F;

    /** Add int. */
    public static final byte IADD = (byte) 0x60;
    /** Add long. */
    public static final byte LADD = (byte) 0x61;
    /** Add float. */
    public static final byte FADD = (byte) 0x62;
    /** Add double. */
    public static final byte DADD = (byte) 0x63;
    /** Subtract int. */
    public static final byte ISUB = (byte) 0x64;
    /** Subtract long. */
    public static final byte LSUB = (byte) 0x65;
    /** Subtract float. */
    public static final byte FSUB = (byte) 0x66;
    /** Subtract double. */
    public static final byte DSUB = (byte) 0x67;
    /** Multiply int. */
    public static final byte IMUL = (byte) 0x68;
    /** Multiply long. */
    public static final byte LMUL = (byte) 0x69;
    /** Multiply float. */
    public static final byte FMUL = (byte) 0x6A;
    /** Multiply double. */
    public static final byte DMUL = (byte) 0x6B;
    /** Divide int. */
    public static final byte IDIV = (byte) 0x6C;
    /** Divide long. */
    public static final byte LDIV = (byte) 0x6D;
    /** Divide float. */
    public static final byte FDIV = (byte) 0x6E;
    /** Divide double. */
    public static final byte DDIV = (byte) 0x6F;
    /** Remainder int. */
    public static final byte IREM = (byte) 0x70;
    /** Remainder long. */
    public static final byte LREM = (byte) 0x71;
    /** Remainder float. */
    public static final byte FREM = (byte) 0x72;
    /** Remainder double. */
    public static final byte DREM = (byte) 0x73;
    /** Negate int. */
    public static final byte INEG = (byte) 0x74;
    /** Negate long. */
    public static final byte LNEG = (byte) 0x75;
    /** Negate float. */
    public static final byte FNEG = (byte) 0x76;
    /** Negate double. */
    public static final byte DNEG = (byte) 0x77;
    /** Shift left int. */
    public static final byte ISHL = (byte) 0x78;
    /** Shift left long. */
    public static final byte LSHL = (byte) 0x79;
    /** Shift right int. */
    public static final byte ISHR = (byte) 0x7A;
    /** Shift right long. */
    public static final byte LSHR = (byte) 0x7B;
    /** Shift right logical int. */
    public static final byte IUSHR = (byte) 0x7C;
    /** Shift right logical long. */
    public static final byte LUSHR = (byte) 0x7D;
    /** Bitwise and int. */
    public static final byte IAND = (byte) 0x7E;
    /** Bitwise and long. */
    public static final byte LAND = (byte) 0x7F;
    /** Bitwise or int. */
    public static final byte IOR = (byte) 0x80;
    /** Bitwise or long. */
    public static final byte LOR = (byte) 0x81;
    /** Bitwise xor int. */
    public static final byte IXOR = (byte) 0x82;
    /** Bitwise xor long. */
    public static final byte LXOR = (byte) 0x83;
    /** Increment local variable by constant. */
    public static final byte IINC = (byte) 0x84;

    /** Convert int to long. */
    public static final byte I2L = (byte) 0x85;
    /** Convert int to float. */
    public static final byte I2F = (byte) 0x86;
    /** Convert int to double. */
    public static final byte I2D = (byte) 0x87;
    /** Convert long to int. */
    public static final byte L2I = (byte) 0x88;
    /** Convert long to float. */
    public static final byte L2F = (byte) 0x89;
    /** Convert long to double. */
    public static final byte L2D = (byte) 0x8A;
    /** Convert float to int. */
    public static final byte F2I = (byte) 0x8B;
    /** Convert float to long. */
    public static final byte F2L = (byte) 0x8C;
    /** Convert float to double. */
    public static final byte F2D = (byte) 0x8D;
    /** Convert double to int. */
    public static final byte D2I = (byte) 0x8E;
    /** Convert double to long. */
    public static final byte D2L = (byte) 0x8F;
    /** Convert double to float. */
    public static final byte D2F = (byte) 0x90;
    /** Convert int to byte. */
    public static final byte I2B = (byte) 0x91;
    /** Convert int to char. */
    public static final byte I2C = (byte) 0x92;
    /** Convert int to short. */
    public static final byte I2S = (byte) 0x93;

    /** Compare long. */
    public static final byte LCMP = (byte) 0x94;
    /** Compare float. */
    public static final byte FCMPL = (byte) 0x95;
    /** Compare float. */
    public static final byte FCMPG = (byte) 0x96;
    /** Compare double. */
    public static final byte DCMPL = (byte) 0x97;
    /** Compare double. */
    public static final byte DCMPG = (byte) 0x98;
    /** Branch if int is equal to zero. */
    public static final byte IFEQ = (byte) 0x99;
    /** Branch if int is not equal to zero. */
    public static final byte IFNE = (byte) 0x9A;
    /** Branch if int is less than zero. */
    public static final byte IFLT = (byte) 0x9B;
    /** Branch if int is greater or equal than zero. */
    public static final byte IFGE = (byte) 0x9C;
    /** Branch if int is greater than zero. */
    public static final byte IFGT = (byte) 0x9D;
    /** Branch if int is less or equal than zero. */
    public static final byte IFLE = (byte) 0x9E;
    /** Branch if int values are equal. */
    public static final byte IF_ICMPEQ = (byte) 0x9F;
    /** Branch if int values are not equal. */
    public static final byte IF_ICMPNE = (byte) 0xA0;
    /** Branch if int value 1 is less than value 2. */
    public static final byte IF_ICMPLT = (byte) 0xA1;
    /** Branch if int value 1 is greater or equal than value 2. */
    public static final byte IF_ICMPGE = (byte) 0xA2;
    /** Branch if int value 1 is greater than value 2. */
    public static final byte IF_ICMPGT = (byte) 0xA3;
    /** Branch if int value 1 is less or equal than value 2. */
    public static final byte IF_ICMPLE = (byte) 0xA4;
    /** Branch if references are equal. */
    public static final byte IF_ACMPEQ = (byte) 0xA5;
    /** Branch if references are not equal. */
    public static final byte IF_ACMPNE = (byte) 0xA6;

    /** Branch unconditionnally. */
    public static final byte GOTO = (byte) 0xA7;
    /** Jump subroutine (deprecated). */
    public static final byte JSR = (byte) 0xA8;
    /** Branch unconditionnally (deprecated). */
    public static final byte RET = (byte) 0xA9;
    /** Access jump table by index and jump. */
    public static final byte TABLESWITCH = (byte) 0xAA;
    /** Access jump table by key match and jump. */
    public static final byte LOOKUPSWITCH = (byte) 0xAB;
    /** Return int from method. */
    public static final byte IRETURN = (byte) 0xAC;
    /** Return long from method. */
    public static final byte LRETURN = (byte) 0xAD;
    /** Return float from method. */
    public static final byte FRETURN = (byte) 0xAE;
    /** Return double from method. */
    public static final byte DRETURN = (byte) 0xAF;
    /** Return reference from method. */
    public static final byte ARETURN = (byte) 0xB0;
    /** Return void from method. */
    public static final byte RETURN = (byte) 0xB1;

    /** Get static field from class. */
    public static final byte GETSTATIC = (byte) 0xB2;
    /** Set static field in class. */
    public static final byte PUTSTATIC = (byte) 0xB3;
    /** Get field from object. */
    public static final byte GETFIELD = (byte) 0xB4;
    /** Set field in object. */
    public static final byte PUTFIELD = (byte) 0xB5;
    /** Invoke instance method. */
    public static final byte INVOKEVIRTUAL = (byte) 0xB6;
    /** Invoke instance method. */
    public static final byte INVOKESPECIAL = (byte) 0xB7;
    /** Invoke static method. */
    public static final byte INVOKESTATIC = (byte) 0xB8;
    /** Invoke interface method. */
    public static final byte INVOKEINTERFACE = (byte) 0xB9;
    /** Invoke dynamically-computed call site. */
    public static final byte INVOKEDYNAMIC = (byte) 0xBA;
    /** Create new object. */
    public static final byte NEW = (byte) 0xBB;
    /** Create new array. */
    public static final byte NEWARRAY = (byte) 0xBC;
    /** Create new array of reference. */
    public static final byte ANEWARRAY = (byte) 0xBD;
    /** Get length of array. */
    public static final byte ARRAYLENGTH = (byte) 0xBE;
    /** Throw exception or error. */
    public static final byte ATHROW = (byte) 0xBF;
    /** Check whether object is of given type. */
    public static final byte CHECKCAST = (byte) 0xC0;
    /** Determine if object is of given type. */
    public static final byte INSTANCEOF = (byte) 0xC1;
    /** Enter monitor for object. */
    public static final byte MONITORENTER = (byte) 0xC2;
    /** Exit monitor for object. */
    public static final byte MONITOREXIT = (byte) 0xC3;

    /** Extend local variable index by additional bytes. */
    public static final byte WIDE = (byte) 0xC4;
    /** Create new multidimensional array. */
    public static final byte MULTIANEWARRAY = (byte) 0xC5;
    /** Branch if reference is null. */
    public static final byte IFNULL = (byte) 0xC6;
    /** Branch if reference is not null. */
    public static final byte IFNONNULL = (byte) 0xC7;
    /** Branch unconditionnally (wide index). */
    public static final byte GOTO_W = (byte) 0xC8;
    /** Jump subroutine (wide index) (deprecated). */
    public static final byte JSR_W = (byte) 0xC9;


    /**
     * Class access modifier flags.
     * Important note: The PRIVATE, PROTECTED and STATIC access flags should only
     * be used for nested and inner classes, as described by the JVM specification.
     */
    public static class ClassAccessFlag {
        public static final short PUBLIC = 0x0001;
        public static final short PRIVATE = 0x0002;
        public static final short PROTECTED = 0x0004;
        public static final short STATIC = 0x0008;
        public static final short FINAL = 0x0010;
        public static final short SUPER = 0x0020;
        public static final short INTERFACE = 0x0200;
        public static final short ABSTRACT = 0x0400;
        public static final short SYNTHETIC = 0x1000;
        public static final short ANNOTATION = 0x2000;
        public static final short ENUM = 0x4000;
    }

    /**
     * Method access modifier flags.
     */
    public static class MethodAccessFlag {
        public static final short PUBLIC = 0x0001;
        public static final short PRIVATE = 0x0002;
        public static final short PROTECTED = 0x0004;
        public static final short STATIC = 0x0008;
        public static final short FINAL = 0x0010;
        public static final short SYNCHRONIZED = 0x0020;
        public static final short BRIDGE = 0x0040;
        public static final short VARARGS = 0x0080;
        public static final short NATIVE = 0x0100;
        public static final short ABSTRACT = 0x0400;
        public static final short STRICT = 0x0800;
        public static final short SYNTHETIC = 0x1000;
    }

    /**
     * Field access modifier flags.
     */
    public static class FieldAccessFlag {
        public static final short PUBLIC = 0x0001;
        public static final short PRIVATE = 0x0002;
        public static final short PROTECTED = 0x0004;
        public static final short STATIC = 0x0008;
        public static final short FINAL = 0x0010;
        public static final short VOLATILE = 0x0040;
        public static final short TRANSIENT = 0x0080;
        public static final short SYNTHETIC = 0x1000;
        public static final short ENUM = 0x4000;
    }

    /**
     * Constant pool tags.
     */
    public static class ConstantTag {
        public static final byte UTF8 = 1;
        public static final byte INTEGER = 3;
        public static final byte FLOAT = 4;
        public static final byte LONG = 5;
        public static final byte DOUBLE = 6;
        public static final byte CLASS = 7;
        public static final byte STRING = 8;
        public static final byte FIELD_REF = 9;
        public static final byte METHOD_REF = 10;
        public static final byte INTERFACE_METHOD_REF = 11;
        public static final byte NAME_AND_TYPE = 12;
        public static final byte METHOD_HANDLE = 15;
        public static final byte METHOD_TYPE = 16;
        public static final byte DYNAMIC = 17;
        public static final byte INVOKE_DYNAMIC = 18;
        public static final byte MODULE = 19;
        public static final byte PACKAGE = 20;
    }
}
