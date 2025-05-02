package poly.compiler.generator;

import poly.compiler.output.Byteable;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The ExceptionTable class. This class represents an exception table and contains
 * the exceptions caught by a try-statement in the code.
 * @author Vincent Philippe (@vincent64)
 */
public class ExceptionTable implements Byteable {
    private final List<Entry> entries;

    /**
     * Constructs an exception table.
     */
    public ExceptionTable() {
        //Initialize entries list
        entries = new ArrayList<>();
    }

    /**
     * Adds a new exception entry with the given start, end and handler program counters
     * and exception catch type.
     * @param startProgramCounter the start program counter
     * @param endProgramCounter the end program counter
     * @param handlerProgramCounter the handler program counter
     * @param catchType the exception catch type
     */
    public void addEntry(int startProgramCounter, int endProgramCounter, int handlerProgramCounter, short catchType) {
        entries.add(new Entry((short) startProgramCounter, (short) endProgramCounter, (short) handlerProgramCounter, catchType));
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        byteArray.add((short) entries.size());

        //Add every entry
        for(Entry entry : entries)
            byteArray.add(entry.getBytes());

        return byteArray.getBytes();
    }

    /**
     * The ExceptionTable.Entry class. This class represents an exception entry, as described
     * by the JVM specification.
     */
    private static class Entry implements Byteable {
        private final short startProgramCounter, endProgramCounter;
        private final short handlerProgramCounter;
        private final short catchType;

        /**
         * Constructs an exception entry with the given start, end and handler program counters
         * and exception catch type.
         * @param startProgramCounter the start program counter
         * @param endProgramCounter the end program counter
         * @param handlerProgramCounter the handler program counter
         * @param catchType the exception catch type
         */
        public Entry(short startProgramCounter, short endProgramCounter, short handlerProgramCounter, short catchType) {
            this.startProgramCounter = startProgramCounter;
            this.endProgramCounter = endProgramCounter;
            this.handlerProgramCounter = handlerProgramCounter;
            this.catchType = catchType;
        }

        @Override
        public byte[] getBytes() {
            ByteArray byteArray = new ByteArray();

            //Add program counters and catch type
            byteArray.add(startProgramCounter);
            byteArray.add(endProgramCounter);
            byteArray.add(handlerProgramCounter);
            byteArray.add(catchType);

            return byteArray.getBytes();
        }
    }
}
