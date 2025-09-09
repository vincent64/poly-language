package poly.compiler.parser.tree.expression;

import poly.compiler.analyzer.type.Primitive;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;
import poly.compiler.tokenizer.content.Keyword;
import poly.compiler.util.Character;

/**
 * The PrimitiveType class. This class represents a primitive type node.
 * @author Vincent Philippe (@vincent64)
 */
public class PrimitiveType extends Expression {
    private Primitive.Kind kind;

    public PrimitiveType(Meta meta) {
        super(meta);
    }

    public void setKind(Token token) {
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_BOOLEAN))
            kind = Primitive.Kind.BOOLEAN;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_BYTE))
            kind = Primitive.Kind.BYTE;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_SHORT))
            kind = Primitive.Kind.SHORT;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_INTEGER))
            kind = Primitive.Kind.INTEGER;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_LONG))
            kind = Primitive.Kind.LONG;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_FLOAT))
            kind = Primitive.Kind.FLOAT;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_DOUBLE))
            kind = Primitive.Kind.DOUBLE;
        if(Character.isSameString(token.getContent(), Keyword.PRIMITIVE_CHAR))
            kind = Primitive.Kind.CHAR;
    }

    public void setKind(Primitive.Kind kind) {
        this.kind = kind;
    }

    public Primitive.Kind getKind() {
        return kind;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitPrimitiveType(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitPrimitiveType(this);
    }

    @Override
    public String toString() {
        return kind.toString();
    }
}
