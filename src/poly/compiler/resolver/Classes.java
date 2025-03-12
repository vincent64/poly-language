package poly.compiler.resolver;

import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ClassName;

/**
 * The Classes class. This class is used to find a class symbol accross the project symbols
 * and library symbols.
 * @author Vincent Philippe (@vincent64)
 */
public class Classes {
    private Classes() { }

    /**
     * Returns the symbol from the given symbol name.
     * @param symbolName the symbol name
     * @return the symbol (null if none found)
     */
    public static Symbol findSymbol(String symbolName) {
        Symbol symbol = ProjectClasses.findSymbol(symbolName);

        if(symbol != null)
            return symbol;

        return LibraryClasses.findSymbol(symbolName);
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public static ClassSymbol findClass(ClassName className) {
        ClassSymbol classSymbol = ProjectClasses.findClass(className);

        if(classSymbol != null)
            return classSymbol;

        return LibraryClasses.findClass(className);
    }
}
