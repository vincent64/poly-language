package poly.compiler.output.attribute;

import poly.compiler.output.content.ConstantPool;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.util.ByteArray;

/**
 * The NestHostAttribute class. This class represents an attribute containing the
 * outer class of a nested class, and extends from the Attribute class.
 * According to the JVM specification, a nest host attribute has the following structure :
 * <pre>
 *      NestHost_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 host_class_index;
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class NestHostAttribute extends Attribute {
    private static final String NAME = "NestHost";
    private final short hostClassIndex;

    /**
     * Constructs a nest host attribute with the given class symbol.
     * @param constantPool the constant pool
     * @param classSymbol the class symbol
     */
    public NestHostAttribute(ConstantPool constantPool, ClassSymbol classSymbol) {
        super((short) constantPool.addUTF8Constant(NAME), 2);
        hostClassIndex = (short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName());
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(length);
        byteArray.add(hostClassIndex);

        return byteArray.getBytes();
    }
}
