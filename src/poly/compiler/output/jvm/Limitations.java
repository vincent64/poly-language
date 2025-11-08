package poly.compiler.output.jvm;

/**
 * The Limitations class. This class contains constant values representing the
 * limitations of the JVM, as described by the specification.
 * @author Vincent Philippe (@vincent64)
 */
public class Limitations {
    public static final int MAX_FIELDS_COUNT = 0xFFFF;
    public static final int MAX_METHODS_COUNT = 0xFFFF;
    public static final int MAX_INTERFACES_COUNT = 0xFFFF;
    public static final int MAX_IDENTIFIER_LENGTH = 0xFFFF;
    public static final int MAX_PARAMETERS_COUNT = 0xFF;
    public static final int MAX_CONSTANT_POOL_ENTRY_COUNT = 0xFFFF;
    public static final int MAX_LOCAL_VARIABLES_COUNT = 0xFFFF;
    public static final int MAX_OPERAND_STACK_SIZE = 0xFFFF;
    public static final int MAX_ARRAY_DIMENSION = 0xFF;

    private Limitations() { }
}
