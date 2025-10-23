package poly.compiler.generator;

/**
 * The VariableState class. This class represents the current state of
 * local variables at any given point in the code.
 * @author Vincent Philippe (@vincent64)
 */
final class VariableState {
    private final int localCount, variableCount;

    /**
     * Constructs a variable state with the given locals count and variables count.
     * @param localCount the local count
     * @param variableCount the variable count
     */
    public VariableState(int localCount, int variableCount) {
        this.localCount = localCount;
        this.variableCount = variableCount;
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
