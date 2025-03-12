package poly.compiler.analyzer.type;

/**
 * The Void class. This class represents a void type.
 * @author Vincent Philippe (@vincent64)
 */
public class Void extends Type {
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
