package poly.compiler.resolver;

import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.PackageSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ClassName;
import poly.compiler.util.PackageName;

/**
 * The ProjectClasses class. This class contains every symbol of the project.
 * @author Vincent Philippe (@vincent64)
 */
public class ProjectClasses {
    /** The project source root symbol. */
    private static final PackageSymbol rootSymbol = new PackageSymbol("");

    private ProjectClasses() { }

    /**
     * Generates the package from the given package name.
     * @param packageName the package name
     * @return the generated package
     */
    public static PackageSymbol generatePackage(PackageName packageName) {
        return rootSymbol.generatePackage(packageName);
    }

    /**
     * Returns the symbol from the given symbol name.
     * @param symbolName the symbol name
     * @return the symbol (null if none found)
     */
    public static Symbol findSymbol(String symbolName) {
        return rootSymbol.findSymbol(symbolName);
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public static ClassSymbol findClass(ClassName className) {
        return rootSymbol.findClass(className);
    }

    /**
     * Returns the root package symbol.
     * @return the root symbol
     */
    public static PackageSymbol getRootSymbol() {
        return rootSymbol;
    }
}
