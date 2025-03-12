package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.output.attribute.CodeAttribute;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The Methods class. This class represents the list of methods in the class file.
 * @author Vincent Philippe (@vincent64)
 */
public class Methods implements Byteable {
    private final List<Method> methods;
    private short methodCount;

    public Methods() {
        //Initialize methods list
        methods = new ArrayList<>();
    }

    /**
     * Adds a method with the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public void addMethod(short accessFlag, short nameIndex, short descriptorIndex) {
        methods.add(new Method(accessFlag, nameIndex, descriptorIndex));
        methodCount++;
    }

    /**
     * Adds a method with the given access flag, name index, descriptor index and code attribute.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     * @param codeAttribute the code attribute
     */
    public void addMethod(short accessFlag, short nameIndex, short descriptorIndex, CodeAttribute codeAttribute) {
        if(codeAttribute != null) {
            //Add method with code attribute
            Attributes attributes = new Attributes();
            attributes.addAttribute(codeAttribute);
            methods.add(new Method(accessFlag, nameIndex, descriptorIndex, attributes));
            methodCount++;
        } else {
            addMethod(accessFlag, nameIndex, descriptorIndex);
        }
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add method count
        byteArray.add(methodCount);

        //Add every method content
        for(Method method : methods)
            byteArray.add(method.getBytes());

        return byteArray.getBytes();
    }

    /**
     * Returns the list of methods.
     * @return the methods list
     */
    public List<Method> getMethods() {
        return List.copyOf(methods);
    }

    /**
     * Returns the method count.
     * @return the method count
     */
    public short getMethodCount() {
        return methodCount;
    }
}
