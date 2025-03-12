package poly.compiler.generator;

import poly.compiler.output.jvm.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Branching class. This class is used to manage branches when using conditional statements.
 * @author Vincent Philippe (@vincent64)
 */
public class Branching {
    private final Map<Integer, Integer> trueJumpIndices;
    private final Map<Integer, Integer> falseJumpIndices;
    private final Map<Integer, Integer> jumpIndices;

    public Branching() {
        //Initialize index maps
        trueJumpIndices = new HashMap<>();
        falseJumpIndices = new HashMap<>();
        jumpIndices = new HashMap<>();
    }

    /**
     * Adds the given jump index and jump offset to the jumps list according
     * to whether the jump should be on true- or false-clause.
     * @param jumpIndex the jump index
     * @param jumpOffset the jump offset
     * @param jumpOnTrue whether to jump on true
     * @param isInverted whether the condition is inverted
     */
    public void addJumpIndex(int jumpIndex, int jumpOffset, boolean jumpOnTrue, boolean isInverted) {
        if(jumpOnTrue ^ isInverted) {
            addTrueJumpIndex(jumpIndex, jumpOffset);
        } else {
            addFalseJumpIndex(jumpIndex, jumpOffset);
        }
    }

    /**
     * Adds the given jump index and jump offset to the jumps to true-clause list.
     * @param jumpIndex the jump index
     * @param jumpOffset the jump offset
     */
    public void addTrueJumpIndex(int jumpIndex, int jumpOffset) {
        trueJumpIndices.put(jumpIndex, jumpOffset);
    }

    /**
     * Adds the given jump index and jump offset to the jumps to false-clause list.
     * @param jumpIndex the jump index
     * @param jumpOffset the jump offset
     */
    public void addFalseJumpIndex(int jumpIndex, int jumpOffset) {
        falseJumpIndices.put(jumpIndex, jumpOffset);
    }

    /**
     * Adds the given jump index and jump offset to the unconditional jumps list.
     * @param jumpIndex the jump index
     * @param jumpOffset the jump offset
     */
    public void addJumpIndex(int jumpIndex, int jumpOffset) {
        jumpIndices.put(jumpIndex, jumpOffset);
    }

    /**
     * Resolves the jumps to true-clause with the given instructions list and program counter.
     * @param instructions the instructions list
     * @param programCounter the program counter
     */
    public void resolveTrueJump(List<Instruction> instructions, int programCounter) {
        resolveJump(instructions, programCounter, trueJumpIndices);
    }

    /**
     * Resolves the jumps to false-clause with the given instructions list and program counter.
     * @param instructions the instructions list
     * @param programCounter the program counter
     */
    public void resolveFalseJump(List<Instruction> instructions, int programCounter) {
        resolveJump(instructions, programCounter, falseJumpIndices);
    }

    /**
     * Resolves the unconditional jumps with the given instructions list and program counter.
     * @param instructions the instructions list
     * @param programCounter the program counter
     */
    public void resolveJumps(List<Instruction> instructions, int programCounter) {
        resolveJump(instructions, programCounter, jumpIndices);
    }

    /**
     * Resolves the given jump indices with the given instructions list and program counter.
     * @param instructions the instructions list
     * @param programCounter the program counter
     * @param jumpIndices the jump indices
     */
    private void resolveJump(List<Instruction> instructions, int programCounter, Map<Integer, Integer> jumpIndices) {
        for(Map.Entry<Integer, Integer> jumpIndex : jumpIndices.entrySet()) {
            Instruction instruction = instructions.get(jumpIndex.getKey());

            //Overwrite instruction with branching offset
            instruction = new Instruction.Builder(instruction.getBytes()[0], 3)
                    .add((short) (programCounter - jumpIndex.getValue()))
                    .build();

            instructions.set(jumpIndex.getKey(), instruction);
        }

        //Clear indices to avoid overwriting
        jumpIndices.clear();
    }
}
