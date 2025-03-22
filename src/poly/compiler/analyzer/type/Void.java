package poly.compiler.analyzer.type;

/**
 * The Void class. This class represents a void type.
 * @author Vincent Philippe (@vincent64)
 */
public final class Void extends Type {
    /**
     * Constructs a void type.
     */
    public Void() {
        super(null);
    }

    @Override
    public boolean equals(java.lang.Object object) {
        return object instanceof Void;
    }

    @Override
    public String toString() {
        return "void";
    }
}
