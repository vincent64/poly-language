package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;

/**
 * The SimpleName class. This class represents a simple name node, i.e. a single
 * class, field or method name.
 * @author Vincent Philippe (@vincent64)
 */
public class SimpleName extends Expression {
    private String name;

    public SimpleName(Meta meta) {
        super(meta);
    }

    public void setName(Token token) {
        setName(token.getContent());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(char[] name) {
        this.name = String.valueOf(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitSimpleName(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitSimpleName(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
