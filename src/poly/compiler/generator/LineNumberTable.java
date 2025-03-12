package poly.compiler.generator;

import poly.compiler.output.Byteable;
import poly.compiler.output.attribute.LineNumberEntry;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The LineNumberTable class. This class represents a line number table and contains
 * the code line numbers associated with the corresponding program counter.
 * @author Vincent Philippe (@vincent64)
 */
public class LineNumberTable implements Byteable {
    private final List<LineNumberEntry> entries;
    private int currentProgramCounter, currentLineNumber;

    /**
     * Constructs a line number table.
     */
    public LineNumberTable() {
        currentProgramCounter = -1;
        currentLineNumber = -1;

        //Initialize entries list
        entries = new ArrayList<>();
    }

    /**
     * Generates and adds a new entry with the given program counter and line number.
     * @param programCounter the program counter
     * @param lineNumber the line number
     */
    public void addEntry(int programCounter, int lineNumber) {
        if(programCounter > currentProgramCounter && lineNumber > currentLineNumber) {
            entries.add(new LineNumberEntry((short) programCounter, (short) lineNumber));
            currentProgramCounter = programCounter;
            currentLineNumber = lineNumber;
        }
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        byteArray.add((short) entries.size());

        //Add every entry
        for(LineNumberEntry entry : entries)
            byteArray.add(entry.getBytes());

        return byteArray.getBytes();
    }
}
