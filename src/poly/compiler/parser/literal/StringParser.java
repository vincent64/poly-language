package poly.compiler.parser.literal;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.Literal;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.CharArray;
import poly.compiler.util.Character;

/**
 * The StringParser class. This class is used to parse a literal string token
 * as an array of characters to a string value.
 * @author Vincent Philippe (@vincent64)
 */
public class StringParser {
    private final Token token;
    private final char[] content;

    public StringParser(Token token) {
        this.token = token;
        content = token.getContent().toCharArray();
    }

    /**
     * Parses the string content and returns a string literal.
     * This method removes the quotes and parses escaping characters.
     * @return the string literal
     */
    public Literal parse() {
        //Get the string without quotes
        char[] string = Character.getSubstring(content, 1, content.length - 1);

        //Return small string
        if(string.length <= 1)
            return new Literal.String(Node.Meta.fromLeadingToken(token), string);

        CharArray charArray = new CharArray();
        boolean isEscaped = false;

        //Parse escaping characters
        for(int i = 0; i < string.length; i++) {
            char currentChar = string[i];

            //Add character according to escape
            if(isEscaped) {
                switch(currentChar) {
                    case '"' -> charArray.add('"');
                    case '\'' -> charArray.add('\'');
                    case '\\' -> charArray.add('\\');
                    case 'n' -> charArray.add(CharParser.ESCAPE_NEWLINE);
                    case 't' -> charArray.add(CharParser.ESCAPE_TAB);
                    case 'b' -> charArray.add(CharParser.ESCAPE_BACKSPACE);
                    case 'r' -> charArray.add(CharParser.ESCAPE_RETURN);
                    case 'f' -> charArray.add(CharParser.ESCAPE_FORMFEED);
                }
                isEscaped = false;
            } else if(currentChar == '\\') {
                isEscaped = true;
            } else {
                charArray.add(string[i]);
            }
        }

        return new Literal.String(Node.Meta.fromLeadingToken(token), charArray.getChars());
    }
}
