package poly.compiler.output.attribute;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

/**
 * The LineNumberEntry class. This class represents a line number entry, as described
 * by the JVM specification.
 * @author Vincent Philippe (@vincent64)
 */
public class LineNumberEntry implements Byteable {
    private final short startProgramCounter;
    private final short lineNumber;

    /**
     * Constructs a line number entry with the given starting program counter and line number.
     * @param startProgramCounter the starting program counter
     * @param lineNumber the line number
     */
    public LineNumberEntry(short startProgramCounter, short lineNumber) {
        this.startProgramCounter = startProgramCounter;
        this.lineNumber = lineNumber;
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add program counter and line number
        byteArray.add(startProgramCounter);
        byteArray.add(lineNumber);

        return byteArray.getBytes();
    }
}
