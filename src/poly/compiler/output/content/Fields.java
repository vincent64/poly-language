package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The Fields class. This class represents the list of fields in a class file.
 * @author Vincent Philippe (@vincent64)
 */
public class Fields implements Byteable {
    private final List<Field> fields;
    private short fieldCount;

    public Fields() {
        //Initialize fields list
        fields = new ArrayList<>();
    }

    /**
     * Adds a field with the given access flag, name index and descriptor index.
     * @param accessFlag the access flag
     * @param nameIndex the name index
     * @param descriptorIndex the descriptor index
     */
    public void addField(short accessFlag, short nameIndex, short descriptorIndex) {
        fields.add(new Field(accessFlag, nameIndex, descriptorIndex));
        fieldCount++;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add fields count
        byteArray.add(fieldCount);

        //Add every field content
        for(Field field : fields)
            byteArray.add(field.getBytes());

        return byteArray.getBytes();
    }

    /**
     * Returns the list of fields.
     * @return the fields list
     */
    public List<Field> getFields() {
        return List.copyOf(fields);
    }

    /**
     * Returns the field count.
     * @return the field count
     */
    public short getFieldCount() {
        return fieldCount;
    }
}
