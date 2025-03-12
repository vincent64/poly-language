package poly.compiler.parser.literal;

import poly.compiler.error.ParsingError;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.Literal;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.Character;

/**
 * The CharParser class. This class is used to parse a literal character token
 * as an array of characters to a character value.
 * @author Vincent Philippe (@vincent64)
 */
public class CharParser {
    protected static final char ESCAPE_NEWLINE = '\n';
    protected static final char ESCAPE_TAB = '\t';
    protected static final char ESCAPE_BACKSPACE = '\b';
    protected static final char ESCAPE_RETURN = '\r';
    protected static final char ESCAPE_FORMFEED = '\f';
    private final Token token;
    private final char[] content;

    public CharParser(Token token) {
        this.token = token;
        content = token.getContent();
    }

    /**
     * Parses the characters content and returns a character literal.
     * This method removes the apostrophe and parses the escaping characters.
     * @return the character literal
     */
    public Literal parse() {
        //Get the string without apostrophe
        char[] string = Character.getSubstring(content, 1, content.length - 1);

        //Make sure there is no blank character
        if(string.length == 0)
            new ParsingError.InvalidCharacterValue(token);

        char character = string[0];

        //Parse escaping characters
        if(string.length > 1) {
            //Replace character according to escape
            if(string[0] == '\\') {
                switch(string[1]) {
                    case '"' -> character = '"';
                    case '\'' -> character = '\'';
                    case '\\' -> character = '\\';
                    case 'n' -> character = CharParser.ESCAPE_NEWLINE;
                    case 't' -> character = CharParser.ESCAPE_TAB;
                    case 'b' -> character = CharParser.ESCAPE_BACKSPACE;
                    case 'r' -> character = CharParser.ESCAPE_RETURN;
                    case 'f' -> character = CharParser.ESCAPE_FORMFEED;
                    default -> new ParsingError.InvalidCharacterValue(token);
                }
            } else new ParsingError.InvalidCharacterValue(token);
        }

        return new Literal.Char(Node.Meta.fromLeadingToken(token), character);
    }
}
