package poly.compiler.analyzer.type;

import poly.compiler.resolver.symbol.ClassSymbol;

/**
 * The Object class. This class represents an object type, and contains
 * the full name of its class.
 */
public class Object extends Type {
    public static final Object NULL_REFERENCE = new Object(null);
    private final ClassSymbol classSymbol;

    public Object(ClassSymbol classSymbol) {
        super(Kind.OBJECT);
        this.classSymbol = classSymbol;
    }

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
