package poly.compiler.analyzer.type;

import poly.compiler.util.ClassName;

/**
 * The Array class. This class represents an array type, and contains
 * the type of the array.
 */
public class Array extends Type {
    public static final String SIZE = "size";
    private final Type type;

    public Array(Type type) {
        super(Kind.ARRAY);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(java.lang.Object object) {
        if(object instanceof Object array)
            return array.getClassSymbol().getClassInternalQualifiedName()
                    .equals(ClassName.OBJECT.toInternalQualifiedName());

        if(!(object instanceof Array array))
            return false;

        return type.equals(array.type);
    }

    @Override
    public String toString() {
        return type + "[]";
    }
}
