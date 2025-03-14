package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The Interfaces class. This class represents the list of interfaces in a class file.
 * Every interface is referenced by its constant pool index.
 * @author Vincent Philippe (@vincent64)
 */
public class Interfaces implements Byteable {
    private final List<Short> interfaceIndices;
    private short interfaceCount;

    public Interfaces() {
        //Initialize interfaces list
        interfaceIndices = new ArrayList<>();
    }

    /**
     * Adds an interface with the given constant pool index.
     * @param index the index
     */
    public void addInterface(short index) {
        interfaceIndices.add(index);
        interfaceCount++;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add interfaces count
        byteArray.add(interfaceCount);

        //Add every interface content
        for(short interfaceIndex : interfaceIndices)
            byteArray.add(interfaceIndex);

        return byteArray.getBytes();
    }

    /**
     * Returns the interface count.
     * @return the interface count
     */
    public short getInterfaceCount() {
        return interfaceCount;
    }
}
