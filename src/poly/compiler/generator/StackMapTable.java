package poly.compiler.generator;

import poly.compiler.output.Byteable;
import poly.compiler.output.attribute.StackMapFrame;
import poly.compiler.output.attribute.VerificationType;
import poly.compiler.util.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * The StackMapTable class. This class represents a stack map table and contains
 * the various stack map frames generated alongside the code.
 * @author Vincent Philippe (@vincent64)
 */
public class StackMapTable implements Byteable {
    private final List<StackMapFrame> frames;
    private List<VerificationType> lastLocalTypes;
    private int lastVariableCount;
    private int lastOffset;

    /**
     * Constructs a stack map table.
     */
    public StackMapTable() {
        //Initialize frames list
        frames = new ArrayList<>();
        lastLocalTypes = new ArrayList<>();
    }

    /**
     * Generates and adds a new frame to the table from the given operand stack, local table and program counter.
     * @param operandStack the operand stack
     * @param localTable the local table
     * @param programCounter the program counter
     */
    public void addFrame(OperandStack operandStack, LocalTable localTable, int programCounter) {
        if(programCounter >= lastOffset) {
            int offset = programCounter - lastOffset;

            //Add the frame according to previous frame
            if(operandStack.isEmpty() && localTable.getLocalTypes().equals(lastLocalTypes) && offset < 64) {
                frames.add(StackMapFrame.createSameFrame(offset));
            } else {
                frames.add(StackMapFrame.createFullFrame(operandStack, localTable, offset));
            }

            //Set previous frame to current frame values
            lastLocalTypes = List.copyOf(localTable.getLocalTypes());
            lastVariableCount = localTable.getCount();
            lastOffset = programCounter + 1;
        }
    }

    @Override
    public byte[] getBytes() {
        ByteArray byteArray = new ByteArray();

        //Add frame size
        byteArray.add((short) frames.size());

        //Add every stack map frame
        for(StackMapFrame frame : frames)
            byteArray.add(frame.getBytes());

        return byteArray.getBytes();
    }
}
