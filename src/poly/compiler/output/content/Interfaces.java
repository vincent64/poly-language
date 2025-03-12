package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

public class Interfaces implements Byteable {
    private final List<Short> interfaceIndices;
    private short interfaceCount;

    public Interfaces() {
        //Initialize interfaces list
        interfaceIndices = new ArrayList<>();
    }

    public void addInterface(short index) {
        interfaceIndices.add(index);
        interfaceCount++;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add interface count
        byteArray.add(interfaceCount);

        //Add every interface content
        for(short interfaceIndex : interfaceIndices)
            byteArray.add(interfaceIndex);

        return byteArray.getBytes();
    }

    public short getInterfaceCount() {
        return interfaceCount;
    }
}
