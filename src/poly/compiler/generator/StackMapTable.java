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
    private List<VerificationType> previousLastLocalTypes;
    private int lastOffset;
    private int previousLastOffset;

    /**
     * Constructs a stack map table.
     */
    public StackMapTable() {
        //Initialize frames list
        frames = new ArrayList<>();
        lastLocalTypes = new ArrayList<>();
        previousLastLocalTypes = new ArrayList<>();
    }

    /**
     * Generates and adds a new frame to the table from the given operand stack, local table and program counter.
     * @param operandStack the operand stack
     * @param localTable the local table
     * @param programCounter the program counter
     */
    public void addFrame(OperandStack operandStack, LocalTable localTable, int programCounter) {
        //Add new stack map frame
        if(programCounter >= lastOffset) {
            int offset = programCounter - lastOffset;

            //Generate frame according to previous frame
            StackMapFrame frame = operandStack.isEmpty()
                    && localTable.getLocalTypes().equals(lastLocalTypes) && offset < 64
                    ? StackMapFrame.createSameFrame(offset)
                    : StackMapFrame.createFullFrame(operandStack, localTable, offset);

            //Add the frame to the table
            frames.add(frame);

            //Update previous frame values
            previousLastLocalTypes = lastLocalTypes;
            lastLocalTypes = List.copyOf(localTable.getLocalTypes());
            previousLastOffset = lastOffset;
            lastOffset = programCounter + 1;
        }

        //Replace last stack map frame
        else if(programCounter == lastOffset - 1 && lastOffset > 0) {
            int offset = programCounter - previousLastOffset;

            //Generate frame according to previous frame
            StackMapFrame frame = operandStack.isEmpty()
                    && localTable.getLocalTypes().equals(previousLastLocalTypes) && offset < 64
                    ? StackMapFrame.createSameFrame(offset)
                    : StackMapFrame.createFullFrame(operandStack, localTable, offset);

            //Replace the last frame in the table
            frames.set(frames.size() - 1, frame);

            //Update previous frame values
            lastLocalTypes = List.copyOf(localTable.getLocalTypes());
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
