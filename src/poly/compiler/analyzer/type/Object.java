package poly.compiler.analyzer.type;

import poly.compiler.resolver.symbol.ClassSymbol;

/**
 * The Object class. This class represents an object type, and contains the
 * class symbol of the object instance.
 * @author Vincent Philippe (@vincent64)
 */
public final class Object extends Type {
    /** The null reference object. */
    public static final Object NULL_REFERENCE = new Object(null);
    private final ClassSymbol classSymbol;

    /**
     * Constructs an object with the given class symbol.
     * @param classSymbol the class symbol
     */
    public Object(ClassSymbol classSymbol) {
        super(Kind.OBJECT);
        this.classSymbol = classSymbol;
    }

    /**
     * Returns the object class symbol.
     * @return the class symbol
     */
    public ClassSymbol getClassSymbol() {
        return classSymbol;
    }

    @Override
    public boolean equals(java.lang.Object object) {
        if(!(object instanceof Object obj))
            return false;

        if(classSymbol == null || obj.classSymbol == null)
            return true;

        return classSymbol.equals(obj.classSymbol);
    }

    @Override
    public String toString() {
        return classSymbol != null ? classSymbol.getClassQualifiedName() : "null";
    }
}
