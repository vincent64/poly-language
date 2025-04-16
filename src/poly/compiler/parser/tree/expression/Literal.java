package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;

/**
 * The Literal class. This class represents a literal value, and is a terminal node.
 * There are several types of literal values, such as boolean, integer, or null.
 * These types are defined by the Type enum. Each type of literal has its
 * own subclass, containing its own literal value.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Literal extends Expression {
    private final Type type;

    public Literal(Meta meta, Type type) {
        super(meta);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitLiteral(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitLiteral(this);
    }

    @Override
    public abstract java.lang.String toString();

    /**
     * The Literal.Type enum. This enum contains every type
     * of literal value there exists.
     */
    public enum Type {
        NULL,
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        STRING
    }

    /**
     * The Literal.Null class. This class represents the null reference.
     */
    public static class Null extends Literal {
        public Null(Meta meta) {
            super(meta, Type.NULL);
        }

        @Override
        public java.lang.String toString() {
            return "Literal: null";
        }
    }

    /**
     * The Literal.Boolean class. This class represents a literal boolean value.
     */
    public static class Boolean extends Literal {
        private final boolean value;

        public Boolean(Meta meta, boolean value) {
            super(meta, Type.BOOLEAN);
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Boolean: " + value + "\n";
        }
    }

    /**
     * The Literal.Integer class. This class represents a literal integer value.
     */
    public static class Integer extends Literal {
        private final int value;

        public Integer(Meta meta, int value) {
            super(meta, Type.INTEGER);
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Integer: " + value + "\n";
        }
    }

    /**
     * The Literal.Long class. This class represents a literal long value.
     */
    public static class Long extends Literal {
        private final long value;

        public Long(Meta meta, long value) {
            super(meta, Type.LONG);
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Long: " + value + "\n";
        }
    }

    /**
     * The Literal.Float class. This class represents a literal float value.
     */
    public static class Float extends Literal {
        private final float value;

        public Float(Meta meta, float value) {
            super(meta, Type.FLOAT);
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Float: " + value + "\n";
        }
    }

    /**
     * The Literal.Double class. This class represents a literal double value.
     */
    public static class Double extends Literal {
        private final double value;

        public Double(Meta meta, double value) {
            super(meta, Type.DOUBLE);
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Double: " + value + "\n";
        }
    }

    /**
     * The Literal.String class. This class represents a literal string value.
     */
    public static class String extends Literal {
        private final char[] value;

        public String(Meta meta, char[] value) {
            super(meta, Type.STRING);
            this.value = value;
        }

        public char[] getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Literal: " + java.lang.String.valueOf(value) + "\n";
        }
    }

    /**
     * The Literal.Char class. This class represents a literal char value.
     */
    public static class Char extends Literal {
        private final char value;

        public Char(Meta meta, char value) {
            super(meta, Type.CHAR);
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        @Override
        public java.lang.String toString() {
            return "Literal: " + value + "\n";
        }
    }
}
