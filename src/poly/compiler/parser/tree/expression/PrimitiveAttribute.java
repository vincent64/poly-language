package poly.compiler.parser.tree.expression;

import poly.compiler.analyzer.type.Primitive;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The PrimitiveAttribute class. This class represents a primitive attribute,
 * containing the primitive kind and the attribute kind.
 * @author Vincent Philippe (@vincent64)
 */
public class PrimitiveAttribute extends Expression {
    private Kind kind;
    private Primitive.Kind primitiveKind;

    public PrimitiveAttribute(Meta meta) {
        super(meta);
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setPrimitiveKind(Primitive.Kind primitiveKind) {
        this.primitiveKind = primitiveKind;
    }

    public Kind getKind() {
        return kind;
    }

    public Primitive.Kind getPrimitiveKind() {
        return primitiveKind;
    }

    /**
     * Transforms and returns the current primitive attribute to a literal node.
     * @return the literal node
     */
    public Literal toLiteral() {
        Literal literal = new Literal.Integer(meta, 0);

        switch(kind) {
            case BYTES -> {
                switch(primitiveKind) {
                    case BYTE -> literal = new Literal.Integer(meta, Byte.BYTES);
                    case SHORT, CHAR -> literal = new Literal.Integer(meta, Short.BYTES);
                    case INTEGER, FLOAT -> literal = new Literal.Integer(meta, Integer.BYTES);
                    case LONG, DOUBLE -> literal = new Literal.Integer(meta, Long.BYTES);
                }
            }

            case BITS -> {
                switch(primitiveKind) {
                    case BYTE -> literal = new Literal.Integer(meta, Byte.SIZE);
                    case SHORT, CHAR -> literal = new Literal.Integer(meta, Short.SIZE);
                    case INTEGER, FLOAT -> literal = new Literal.Integer(meta, Integer.SIZE);
                    case LONG, DOUBLE -> literal = new Literal.Integer(meta, Long.SIZE);
                }
            }

            case MINIMUM -> {
                switch(primitiveKind) {
                    case BYTE -> literal = new Literal.Integer(meta, Byte.MIN_VALUE);
                    case SHORT -> literal = new Literal.Integer(meta, Short.MIN_VALUE);
                    case CHAR -> literal = new Literal.Char(meta, java.lang.Character.MIN_VALUE);
                    case INTEGER -> literal = new Literal.Integer(meta, Integer.MIN_VALUE);
                    case LONG -> literal = new Literal.Long(meta, Long.MIN_VALUE);
                    case FLOAT -> literal = new Literal.Float(meta, Float.MIN_VALUE);
                    case DOUBLE -> literal = new Literal.Double(meta, Double.MIN_VALUE);
                }
            }

            case MAXIMUM -> {
                switch(primitiveKind) {
                    case BYTE -> literal = new Literal.Integer(meta, Byte.MAX_VALUE);
                    case SHORT -> literal = new Literal.Integer(meta, Short.MAX_VALUE);
                    case CHAR -> literal = new Literal.Char(meta, java.lang.Character.MAX_VALUE);
                    case INTEGER -> literal = new Literal.Integer(meta, Integer.MAX_VALUE);
                    case LONG -> literal = new Literal.Long(meta, Long.MAX_VALUE);
                    case FLOAT -> literal = new Literal.Float(meta, Float.MAX_VALUE);
                    case DOUBLE -> literal = new Literal.Double(meta, Double.MAX_VALUE);
                }
            }
        }

        return literal;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitPrimitiveAttribute(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitPrimitiveAttribute(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("PrimitiveAttribute",
                "attributeKind=" + kind,
                "primitiveKind=" + primitiveKind);

        return string.toString();
    }

    /**
     * The PrimitiveAttribute.Kind enum. This enum contains every kind of
     * primitive attribute there exists, with their associated name.
     */
    public enum Kind {
        BYTES,
        BITS,
        MINIMUM,
        MAXIMUM
    }
}
