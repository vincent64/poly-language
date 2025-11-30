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
        switch(token.getContent()) {
            case Keyword.PRIMITIVE_BOOLEAN -> kind = Primitive.Kind.BOOLEAN;
            case Keyword.PRIMITIVE_BYTE -> kind = Primitive.Kind.BYTE;
            case Keyword.PRIMITIVE_SHORT -> kind = Primitive.Kind.SHORT;
            case Keyword.PRIMITIVE_CHAR -> kind = Primitive.Kind.CHAR;
            case Keyword.PRIMITIVE_INTEGER -> kind = Primitive.Kind.INTEGER;
            case Keyword.PRIMITIVE_LONG -> kind = Primitive.Kind.LONG;
            case Keyword.PRIMITIVE_FLOAT -> kind = Primitive.Kind.FLOAT;
            case Keyword.PRIMITIVE_DOUBLE -> kind = Primitive.Kind.DOUBLE;
        }
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
