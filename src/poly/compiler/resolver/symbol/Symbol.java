package poly.compiler.resolver.symbol;

import poly.compiler.output.content.AccessModifier;

/**
 * The Symbol abstract class. This class represents a symbol (or reference) in the project.
 * The most common kinds of symbol are the class, field and method symbols.
 * Every symbol has a name, an access modifier and behavior modifiers.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Symbol {
    protected final Kind kind;
    protected final AccessModifier accessModifier;
    protected final String name;
    protected final boolean isStatic, isConstant;

    /**
     * Constructs a symbol with the given symbol kind, access modifier, name
     * and whether it is static and/or constant.
     * @param kind the symbol kind
     * @param accessModifier the access modifier
     * @param name the symbol name
     * @param isStatic whether the symbol is static
     * @param isConstant whether the symbol is constant
     */
    protected Symbol(Kind kind, AccessModifier accessModifier, String name, boolean isStatic, boolean isConstant) {
        this.kind = kind;
        this.accessModifier = accessModifier;
        this.name = name;
        this.isStatic = isStatic;
        this.isConstant = isConstant;
    }

    /**
     * Returns the symbol kind.
     * @return the symbol kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the symbol access modifier
     * @return the symbol access modifier
     */
    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    /**
     * Returns the symbol name.
     * @return the symbol name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the symbol is static.
     * @return true if the symbol is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Returns whether the symbol is constant.
     * @return true if the symbol is constant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * Returns whether the given symbol is equal to the current one.
     * This method may return true even if some attributes are not equal.
     * @param object the symbol
     * @return true if the symbols are equal
     */
    @Override
    public abstract boolean equals(Object object);

    /**
     * Returns the string representation of the symbol. Implementations of this method
     * may result in recursive calls to sub-symbols.
     * @return the string representation of the symbol
     */
    @Override
    public abstract String toString();

    /**
     * The Symbol.Kind enum. This enum contains every kind of symbol there is.
     */
    public enum Kind {
        CLASS,
        FIELD,
        METHOD,
        PACKAGE,
        TYPE,
        FILE
    }
}
