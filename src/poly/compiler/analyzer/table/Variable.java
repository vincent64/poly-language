package poly.compiler.analyzer.table;

import poly.compiler.analyzer.type.Type;

/**
 * The Variable class. This class represents a local variable in a method.
 * @author Vincent Philippe (@vincent64)
 */
public class Variable {
    private final Type type;
    private final String name;
    private final boolean isConstant;
    private final int index;
    private boolean isAssigned;

    /**
     * Constructs a local variable with the given type, name, whether it is constant
     * and its index in the local variables table.
     * @param type the variable type
     * @param name the variable name
     * @param isConstant whether it is constant
     * @param index the variable index
     */
    public Variable(Type type, String name, boolean isConstant, int index) {
        this.type = type;
        this.name = name;
        this.isConstant = isConstant;
        this.index = index;
    }

    /**
     * Sets the variable has being assigned to a value.
     */
    public void setAsAssigned() {
        isAssigned = true;
    }

    /**
     * Returns the variable type.
     * @return the variable type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the variable name.
     * @return the variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the variable is constant.
     * @return true if the variable is constant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Returns the variable index in the variables table.
     * @return the variable index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns whether the variable has already been assigned a value.
     * @return true if the variable is already assigned
     */
    public boolean isAssigned() {
        return isAssigned;
    }
}
