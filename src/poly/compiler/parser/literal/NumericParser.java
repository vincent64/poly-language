package poly.compiler.parser.literal;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.Literal;
import poly.compiler.tokenizer.Token;

/**
 * The NumericParser class. This class is used to parse literal numeric values.
 * Numeric values that are accepted are integer, long, float and double.
 * @author Vincent Philippe (@vincent64)
 */
public class NumericParser {
    private final Token token;
    private final char[] content;

    public NumericParser(Token token) {
        this.token = token;
        content = token.getContent().toCharArray();
    }

    /**
     * Parses the token and returns the corresponding literal node.
     * @return the literal node
     */
    public Literal parse() {
        //Parse binary and hexadecimal numbers
        if(content.length > 2 && content[0] == '0'
                && (content[1] == 'x' || content[1] == 'X' || content[1] == 'b' || content[1] == 'B')) {
            //Parse integer or long according to last character
            char lastCharacter = content[content.length - 1];
            if(lastCharacter == 'l' || lastCharacter == 'L')
                return new Literal.Long(Node.Meta.fromLeadingToken(token),
                        new IntParser(token).parseLong());
            else
                return new Literal.Integer(Node.Meta.fromLeadingToken(token),
                        new IntParser(token).parseInt());
        }

        return parseDecimal();
    }

    private Literal parseDecimal() {
        //Parse floating point number
        if(hasDecimalSeparator()) {
            //Parse float or double according to last character
            char lastCharacter = content[content.length - 1];
            if(lastCharacter == 'f' || lastCharacter == 'F')
                return new Literal.Float(Node.Meta.fromLeadingToken(token),
                        new FloatParser(token).parseFloat());
            else
                return new Literal.Double(Node.Meta.fromLeadingToken(token),
                        new FloatParser(token).parseDouble());
        }

        char lastCharacter = content[content.length - 1];

        //Parse long literal
        if(lastCharacter == 'l' || lastCharacter == 'L')
            return new Literal.Long(Node.Meta.fromLeadingToken(token),
                    new IntParser(token).parseLong());

        //Parse float literal
        if(lastCharacter == 'f' || lastCharacter == 'F')
            return new Literal.Float(Node.Meta.fromLeadingToken(token),
                    new FloatParser(token).parseFloat());

        //Parse double literal
        if(lastCharacter == 'd' || lastCharacter == 'D')
            return new Literal.Double(Node.Meta.fromLeadingToken(token),
                    new FloatParser(token).parseDouble());

        return new Literal.Integer(Node.Meta.fromLeadingToken(token),
                new IntParser(token).parseInt());
    }

    private boolean hasDecimalSeparator() {
        for(char c : content) {
            if(c == '.')
                return true;
        }

        return false;
    }
}
