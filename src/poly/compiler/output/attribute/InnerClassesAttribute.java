package poly.compiler.output.attribute;

import poly.compiler.output.content.ConstantPool;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The InnerClassesAttribute class. This class represents an attribute containing the
 * list of inner classes of a class, and extends from the Attribute class.
 * According to the JVM specification, an inner classes attribute has the following structure :
 * <pre>
 *      InnerClasses_attribute {
 *          u2 attribute_name_index;
 *          u4 attribute_length;
 *          u2 number_of_classes;
 *          {   u2 inner_class_info_index;
 *              u2 outer_class_info_index;
 *              u2 inner_name_index;
 *              u2 inner_class_access_flags;
 *          } classes[number_of_classes];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public final class InnerClassesAttribute extends Attribute {
    public static final String NAME = "InnerClasses";
    private final List<InnerClass> innerClasses;

    /**
     * Constructs an inner classes attribute.
     * @param constantPool the constant pool
     */
    public InnerClassesAttribute(ConstantPool constantPool) {
        super((short) constantPool.addUTF8Constant(NAME), 0);

        //Initialize inner classes list
        innerClasses = new ArrayList<>();
    }

    /**
     * Adds an inner class with the given access flag, name index, class index and outer class index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param classIndex the class index
     * @param outerClassIndex the outer class index
     */
    public void addInnerClass(short accessFlag, short nameIndex, short classIndex, short outerClassIndex) {
        innerClasses.add(new InnerClass(accessFlag, nameIndex, classIndex, outerClassIndex));
    }

    /**
     * Returns the list of inner classes.
     * @return the inner classes list
     */
    public List<InnerClass> getInnerClasses() {
        return List.copyOf(innerClasses);
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add attribute content
        byteArray.add(nameIndex);
        byteArray.add(innerClasses.size() * 8 + 2);
        byteArray.add((short) innerClasses.size());

        //Add every inner class content
        for(InnerClass innerClass : innerClasses)
            byteArray.add(innerClass.getBytes());

        return byteArray.getBytes();
    }
}
