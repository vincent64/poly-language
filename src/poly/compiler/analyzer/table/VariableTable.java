package poly.compiler.analyzer.table;

import poly.compiler.analyzer.type.Primitive;
import poly.compiler.analyzer.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * The VariableTable class. This class contains the local variables of a method that are
 * part of the current scope. The parameters of a method are included in the variable table.
 * @author Vincent Philippe (@vincent64)
 */
public class VariableTable {
    private final List<Variable> variables;
    private int variableCounter;

    /**
     * Constructs a variable table.
     */
    public VariableTable() {
        //Initialize local variables list
        variables = new ArrayList<>();
    }

    /**
     * Adds a new variable with the given type, name and whether it is constant
     * in the current variable table.
     * @param type the variable type
     * @param name the variable name
     * @param isConstant whether the variable is constant
     * @return the new variable
     */
    public Variable addVariable(Type type, String name, boolean isConstant) {
        Variable variable = new Variable(type, name, isConstant, variableCounter++);
        variables.add(variable);

        //Add empty variable if constant takes two indices
        if(type instanceof Primitive primitive && primitive.isWideType())
            variableCounter++;

        return variable;
    }

    /**
     * Removes the last given amount of variables.
     * @param amount the variables amount
     */
    public void removeVariables(int amount) {
        for(int i = 0; i < amount; i++)
            variables.removeLast();
    }

    /**
     * Returns the variable with the given name in the variable table.
     * @param name the variable name
     * @return the variable with the name (null if not found)
     */
    public Variable findVariableWithName(String name) {
        for(Variable variable : variables) {
            if(variable.getName().equals(name))
                return variable;
        }

        return null;
    }

    /**
     * Returns whether a variable with the given name is already defined.
     * @param name the variable name
     * @return true if the variable is already defined
     */
    public boolean isAlreadyDefined(String name) {
        for(Variable variable : variables) {
            if(variable.getName().equals(name) && !name.isEmpty())
                return true;
        }

        return false;
    }

    /**
     * Returns the variable at the given index in the variable table.
     * @param index the variable index
     * @return the variable
     */
    public Variable getVariableByIndex(int index) {
        return variables.get(index);
    }

    /**
     * Returns the variable that was last added to the variable table.
     * @return the variable
     */
    public Variable getLastVariable() {
        return variables.getLast();
    }

    /**
     * Removes every variable from the table and clears the counter.
     */
    public void clear() {
        variables.clear();
        variableCounter = 0;
    }

    /**
     * Returns the variable counter.
     * @return the variable counter
     */
    public int getVariableCounter() {
        return variableCounter;
    }

    /**
     * Returns the variable count.
     * @return the variable count
     */
    public int getVariableCount() {
        return variables.size();
    }
}
