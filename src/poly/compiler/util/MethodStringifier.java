package poly.compiler.util;

import poly.compiler.analyzer.type.Type;
import poly.compiler.resolver.symbol.MethodSymbol;

/**
 * The MethodStringifier class. This class is used to create a string representation of a method,
 * often used in error and warning messages.
 * @author Vincent Philippe (@vincent64)
 */
public class MethodStringifier {
    private MethodStringifier() { }

    /**
     * Returns the stringified method with the given name and parameter types.
     * @param name the method name
     * @param parameterTypes the parameter types
     * @return the stringified method
     */
    public static String stringify(String name, Type[] parameterTypes) {
        return name + stringify(parameterTypes);
    }

    /**
     * Returns the stringified method from the given method symbol.
     * @param methodSymbol the method symbol
     * @return the stringified method
     */
    public static String stringify(MethodSymbol methodSymbol) {
        return stringify(methodSymbol.getName(), methodSymbol.getParameterTypes());
    }

    /**
     * Returns the stringified types from the given types.
     * @param types the types array
     * @return the stringified types
     */
    public static String stringify(Type[] types) {
        StringBuilder string = new StringBuilder();

        //Add types list
        string.append("(");
        for(int i = 0; i < types.length; i++) {
            if(i == 0) string.append(types[i]);
            else string.append(", ").append(types[i]);
        }
        string.append(")");

        return string.toString();
    }
}
