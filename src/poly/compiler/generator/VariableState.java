package poly.compiler.generator;

/**
 * The VariableState class. This class represents the current state of
 * local variables at any given point in the code.
 * @author Vincent Philippe (@vincent64)
 */
final class VariableState {
    private final int programCounter;
    private final int localCount, variableCount;

    /**
     * Constructs a variable state with the given program counter, locals count and variables count.
     * @param programCounter the program counter
     * @param localCount the local count
     * @param variableCount the variable count
     */
    public VariableState(int programCounter, int localCount, int variableCount) {
        this.programCounter = programCounter;
        this.localCount = localCount;
        this.variableCount = variableCount;
    }

    /**
     * Returns the program counter.
     * @return the program counter
     */
    public int getProgramCounter() {
        return programCounter;
    }

    /**
     * Returns the locals count.
     * @return the local count
     */
    public int getLocalCount() {
        return localCount;
    }

    /**
     * Returns the variables count.
     * @return the variable count
     */
    public int getVariableCount() {
        return variableCount;
    }
}
