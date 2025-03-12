package poly.compiler.output.attribute;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

/**
 * The InnerClass class. This class represents an inner class in a class file attribute.
 * @author Vincent Philippe (@vincent64)
 */
public class InnerClass implements Byteable {
    private final short accessFlag;
    private final short nameIndex;
    private final short classIndex;
    private final short outerClassIndex;

    /**
     * Constructs an inner class with the given access flag, name index, class index and outer class index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param classIndex the class index
     * @param outerClassIndex the outer class index
     */
    public InnerClass(short accessFlag, short nameIndex, short classIndex, short outerClassIndex) {
        this.accessFlag = accessFlag;
        this.nameIndex = nameIndex;
        this.classIndex = classIndex;
        this.outerClassIndex = outerClassIndex;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add inner class content
        byteArray.add(classIndex);
        byteArray.add(outerClassIndex);
        byteArray.add(nameIndex);
        byteArray.add(accessFlag);

        return byteArray.getBytes();
    }
}
