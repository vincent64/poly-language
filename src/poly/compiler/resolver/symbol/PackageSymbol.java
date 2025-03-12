package poly.compiler.resolver.symbol;

import poly.compiler.output.content.AccessModifier;
import poly.compiler.util.ClassName;
import poly.compiler.util.PackageName;

import java.util.ArrayList;
import java.util.List;

/**
 * The PackageSymbol class. This class is the symbol representation of a package.
 * A package symbol contains several symbols, which can be either subpackages or classes.
 * @author Vincent Philippe (@vincent64)
 */
public final class PackageSymbol extends Symbol {
    private final List<Symbol> symbols;

    /**
     * Constructs the package with the given package name.
     * @param name the package name
     */
    public PackageSymbol(String name) {
        super(Kind.PACKAGE, AccessModifier.PUBLIC, name, false, false);

        //Initialize symbols list
        symbols = new ArrayList<>();
    }

    /**
     * Adds the given symbol to the package symbol if it was not already present,
     * and returns whether the given symbol was already present.
     * The symbol can either be a class or a subpackage.
     * @param symbol the symbol
     * @return true if the class symbol already contained the symbol
     */
    public boolean addSymbol(Symbol symbol) {
        if(symbols.contains(symbol))
            return false;

        return symbols.add(symbol);
    }

    /**
     * Generates the package from the given package name.
     * @param packageName the package name
     * @return the generated package
     */
    public PackageSymbol generatePackage(PackageName packageName) {
        if(packageName.isEmpty())
            return this;

        //Get subpackage if it already exists
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(packageName.getFirst())
                    && symbol instanceof PackageSymbol packageSymbol)
                return packageSymbol.generatePackage(packageName.withoutFirst());
        }

        //Generate new subpackage
        PackageSymbol packageSymbol = new PackageSymbol(packageName.getFirst());
        addSymbol(packageSymbol);

        return packageSymbol.generatePackage(packageName.withoutFirst());
    }

    /**
     * Returns the symbol from the given symbol name.
     * @param symbolName the symbol name
     * @return the symbol (null if none found)
     */
    public Symbol findSymbol(String symbolName) {
        //Find class in current package first
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(symbolName)
                    && symbol instanceof ClassSymbol classSymbol)
                return classSymbol;
        }

        //Find class in a subpackage
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(symbolName)
                    && symbol instanceof PackageSymbol packageSymbol)
                return packageSymbol;
        }

        return null;
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public ClassSymbol findClass(String className) {
        //Find class in current package
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(className)
                    && symbol instanceof ClassSymbol classSymbol)
                return classSymbol;
        }

        return null;
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public ClassSymbol findClass(ClassName className) {
        //Find class in current package first
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(className.getFirst())
                    && symbol instanceof ClassSymbol classSymbol)
                return classSymbol.findClass(className.withoutFirst());
        }

        //Find class in a subpackage
        for(Symbol symbol : symbols) {
            if(symbol.getName().equals(className.getFirst())
                    && symbol instanceof PackageSymbol packageSymbol)
                return packageSymbol.findClass(className.withoutFirst());
        }

        return null;
    }

    /**
     * Returns whether the given package has the same name as the current one.
     * @param object the package symbol
     * @return true if the symbols have the same
     */
    @Override
    public boolean equals(Object object) {
        if(!(object instanceof PackageSymbol packageSymbol))
            return false;

        return name.equals(packageSymbol.name);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("PackageSymbol(" + name + "):\n");

        //Append every symbol
        for(Symbol symbol : symbols)
            string.append(symbol.toString().indent(4));

        return string.toString();
    }
}
