package poly.compiler.output.content;

import poly.compiler.output.Byteable;
import poly.compiler.output.attribute.Attribute;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The Attributes class. This class represents the attributes structure of
 * a class file, as described by the JVM specification.
 * It contains its byte content as well as the attributes count.
 * An attribute has the following structure :
 * <pre>
 *      attribute_info {
 *          u2  attribute_name_index;
 *          u4  attribute_length;
 *          u1  info[attribute_length];
 *      }
 * </pre>
 * @author Vincent Philippe (@vincent64)
 */
public class Attributes implements Byteable {
    private final List<Attribute> attributes;
    private short attributeCount;

    /**
     * Constructs attributes.
     */
    public Attributes() {
        //Initialize attributes list
        attributes = new ArrayList<>();
    }

    /**
     * Adds the given attribute to the attributes.
     * @param attribute the attribute
     */
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
        attributeCount++;
    }

    /**
     * Returns the amount of attributes.
     * @return the attribute count
     */
    public short getAttributeCount() {
        return attributeCount;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add attribute count
        byteArray.add(attributeCount);

        //Add every attribute content
        for(Attribute attribute : attributes)
            byteArray.add(attribute.getBytes());

        return byteArray.getBytes();
    }
}
