package poly.compiler.analyzer.type;

/**
 * The Primitive class. This class represents a primitive type.
 * @author Vincent Philippe (@vincent64)
 */
public class Primitive extends Type {
    private final Kind kind;

    public Primitive(Kind kind) {
        super(Type.Kind.PRIMITIVE);
        this.kind = kind;
    }

    public Kind getPrimitiveKind() {
        return kind;
    }

    /**
     * Returns whether the primitive type is an integral type.
     * @return true if the primitive is an integral type
     */
    public boolean isIntegerType() {
        return kind == Kind.BYTE || kind == Kind.CHAR || kind == Kind.SHORT
                || kind == Kind.INTEGER || kind == Kind.LONG;
    }

    /**
     * Returns whether the primitive type is a numerical type.
     * @return true if the primitive is a numerical type
     */
    public boolean isNumericalType() {
        return kind == Kind.BYTE || kind == Kind.CHAR || kind == Kind.SHORT
                || kind == Kind.INTEGER || kind == Kind.LONG || kind == Kind.FLOAT || kind == Kind.DOUBLE;
    }

    /**
     * Returns whether the primitive type is a narrow integral type.
     * @return true if the primitive is a narrow integral type
     */
    public boolean isNarrowIntegerType() {
        return kind == Kind.BYTE || kind == Kind.CHAR || kind == Kind.SHORT;
    }

    /**
     * Returns whether the primitive type is a wide type (i.e. a long or double).
     * @return true if the primitive is a wide type
     */
    public boolean isWideType() {
        return kind == Kind.LONG || kind == Kind.DOUBLE;
    }

    /**
     * Returns whether the primitive type is a boolean type.
     * @return true if the primitive is a boolean type
     */
    public boolean isBooleanType() {
        return kind == Kind.BOOLEAN;
    }

    public static Primitive getWidestPrimitiveBetween(Primitive primitive1, Primitive primitive2) {
        if(primitive1.getPrimitiveKind() == Primitive.Kind.DOUBLE
                || primitive2.getPrimitiveKind() == Primitive.Kind.DOUBLE)
            return new Primitive(Kind.DOUBLE);
        else if(primitive1.getPrimitiveKind() == Primitive.Kind.FLOAT
                || primitive2.getPrimitiveKind() == Primitive.Kind.FLOAT)
            return new Primitive(Kind.FLOAT);
        else if(primitive1.getPrimitiveKind() == Primitive.Kind.LONG
                || primitive2.getPrimitiveKind() == Primitive.Kind.LONG)
            return new Primitive(Kind.LONG);
        else
            return new Primitive(Kind.INTEGER);
    }

    @Override
    public boolean equals(java.lang.Object object) {
        if(!(object instanceof Primitive primitive))
            return false;

        return kind == primitive.kind;
    }

    @Override
    public String toString() {
        return kind.toString();
    }

    /**
     * The Primitive.Kind enum. This enum contains every kind of
     * primitive types there exists.
     */
    public enum Kind {
        BOOLEAN("bool"),
        BYTE("byte"),
        CHAR("char"),
        SHORT("short"),
        INTEGER("int"),
        LONG("long"),
        FLOAT("float"),
        DOUBLE("double");

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
