package poly.compiler.tokenizer;

import poly.compiler.tokenizer.content.Keyword;
import poly.compiler.tokenizer.content.Operator;
import poly.compiler.tokenizer.content.Symbol;
import poly.compiler.util.Character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Tokenizer class. This class' job is to transform the code content from a file written
 * in the Poly language to a list of tokens. The next step after computing the list of tokens is the parsing step.
 * This class goes through every character one-by-one and create a token each time
 * the character alphabet changes. Empty characters such as spaces, line breaks
 * and tabs are omitted, and simply jumped over. This class also entirely ignore
 * inline and multiline comments. It will also tokenize string and character literals.
 * The tokenizer will also attach a type to each token (e.g. keyword, separator),
 * in order to make the parser's work easier and improve performance.
 * Note: The tokenizer will currently ignore any docstring comments.
 * @author Vincent Philippe (@vincent64)
 */
public final class Tokenizer {
    private final String fileName;
    private final List<Token> tokens;
    private final char[] content;
    private int lineNumber;
    private int characterNumber;
    private int tokenLineNumber;
    private int tokenCharacterNumber;

    private Tokenizer(String fileName, char[] content) {
        this.fileName = fileName;
        this.content = content;

        //Initialize tokens list
        tokens = new ArrayList<>();
    }

    public static Tokenizer getInstance(String fileName, char[] content) {
        return new Tokenizer(fileName, content);
    }

    /**
     * Tokenizes the code content and returns an array of tokens.
     * @return an array of token
     */
    public Token[] tokenize() {
        tokens.clear();

        if(content.length == 0)
            return tokens.toArray(new Token[0]);

        //Set line and character numbering
        lineNumber = 1;
        characterNumber = 1;
        tokenLineNumber = lineNumber;
        tokenCharacterNumber = characterNumber;

        //Set the previous character to be the first
        char previousChar = 0;

        //Set escaping rules
        Escape escape = Escape.NONE;

        //Define current alphabet type
        Alphabet.Type currentAlphabetType = Alphabet.Type.EMPTY;

        //Set the current token starting index
        int tokenStartIndex = 0;

        for(int i = 0; i < content.length; i++) {
            char currentChar = content[i];

            //Increase line numbering when line breaks
            if(currentChar == '\n') {
                lineNumber++;
                characterNumber = 1;

                //Reset single-line comment
                if(escape == Escape.COMMENT || escape == Escape.DOCSTRING) {
                    tokenStartIndex = i + 1;
                    currentAlphabetType = Alphabet.Type.EMPTY;
                    escape = Escape.NONE;
                }
            } else characterNumber++;

            //Detect start of single-line comment
            if(currentChar == '/' && previousChar == '/' && escape == Escape.NONE)
                escape = Escape.COMMENT;

            //Detect start of multiple-line comment
            if(currentChar == '*' && previousChar == '/' && escape == Escape.NONE)
                escape = Escape.LONG_COMMENT;

            //Detect start of docstring comment
            if(currentChar == '¦' && escape == Escape.NONE)
                escape = Escape.DOCSTRING;

            //Reset multiple-line comment
            if(currentChar == '/' && previousChar == '*' && escape == Escape.LONG_COMMENT) {
                tokenStartIndex = i + 1;
                currentAlphabetType = Alphabet.Type.EMPTY;
                escape = Escape.NONE;
                continue;
            }

            //Skip tokenization if inside a comment
            if(escape.isComment()) {
                previousChar = currentChar;
                continue;
            }

            //Detect start and end of string literal
            if(currentChar == '"') {
                if(escape == Escape.NONE) {
                    if(currentAlphabetType != Alphabet.Type.EMPTY)
                        createToken(tokenStartIndex, i);

                    escape = Escape.STRING;
                    tokenStartIndex = i;
                } else if(escape == Escape.STRING && previousChar != '\\') {
                    //Create new token for string literal
                    createToken(tokenStartIndex, i + 1);

                    //Reset start index and alphabet type
                    tokenStartIndex = i + 1;
                    currentAlphabetType = Alphabet.Type.EMPTY;
                    escape = Escape.NONE;
                    continue;
                }
            }

            //Detect start and end of char literal
            if(currentChar == '\'') {
                if(escape == Escape.NONE) {
                    if(currentAlphabetType != Alphabet.Type.EMPTY)
                        createToken(tokenStartIndex, i);

                    escape = Escape.CHARACTER;
                    tokenStartIndex = i;
                } else if(escape == Escape.CHARACTER && previousChar != '\\') {
                    //Create new token for char literal
                    createToken(tokenStartIndex, i + 1);

                    //Reset start index and alphabet type
                    tokenStartIndex = i + 1;
                    currentAlphabetType = Alphabet.Type.EMPTY;
                    escape = Escape.NONE;
                    continue;
                }
            }

            //Skip tokenization if inside a string or char literal
            if(escape == Escape.STRING || escape == Escape.CHARACTER) {
                previousChar = currentChar;
                continue;
            }

            //Detect full alphabet change
            if(!currentAlphabetType.isInPrimaryAlphabet(currentChar)
                    && !currentAlphabetType.isInSecondaryAlphabet(currentChar)) {
                //Create new token if the alphabet is not the empty one
                if(currentAlphabetType != Alphabet.Type.EMPTY)
                    createToken(tokenStartIndex, i);

                //Reset token start index to current character
                tokenStartIndex = i;
                //Reset current alphabet type based on current character
                currentAlphabetType = Alphabet.getTypeFromMainCharacter(currentChar);
            } else {
                if(Alphabet.Type.EMPTY.isInPrimaryAlphabet(currentChar)) {
                    tokenStartIndex = i + 1;
                    resetTokenNumbering();
                }

                //Create new token if the current operator is broken
                if(currentAlphabetType == Alphabet.Type.OPERATOR) {
                    if(!Operator.isOperator(getSubstring(tokenStartIndex, i + 1))) {
                        createToken(tokenStartIndex, i);

                        //Reset token start index to current character
                        tokenStartIndex = i;
                    }
                }
            }

            //Add last token if there is one
            if(i == content.length - 1) {
                if(currentAlphabetType != Alphabet.Type.EMPTY)
                    createToken(tokenStartIndex, i + 1);
            }

            //Set current as previous char at the end
            previousChar = currentChar;
        }

        //Check if the code is reversed
        checkReverse();

        return tokens.toArray(new Token[0]);
    }

    private void resetTokenNumbering() {
        tokenLineNumber = lineNumber;
        tokenCharacterNumber = characterNumber;
    }

    /**
     * Creates and adds a new token in the tokens list. The given start and end index
     * represents the first token index (included) and the last token index (excluded)
     * in the file content. The specified line and character number describes the
     * location of the token in the file content, and are used as token metadata.
     * @param startIndex the starting character index (included)
     * @param endIndex the ending character index (excluded)
     */
    private void createToken(int startIndex, int endIndex) {
        //Extract the token content from the file content
        char[] tokenContent = getSubstring(startIndex, endIndex);

        //Get token type from its content
        Token.Type type = findType(tokenContent);

        //Create token metadata for debugging
        Token.Meta meta = new Token.Meta(fileName, tokenLineNumber, tokenCharacterNumber);

        //Create token with string content
        Token token = new Token(tokenContent, type, meta);
        //Add token to the tokens list
        tokens.add(token);

        //Reset token numbering values
        resetTokenNumbering();
    }

    /**
     * Finds and returns the type of the token given its string content.
     * @param tokenContent the token string content
     * @return the token type
     */
    private Token.Type findType(char[] tokenContent) {
        if(Keyword.isKeyword(tokenContent))
            return Token.Type.KEYWORD;
        if(Operator.isOperator(tokenContent))
            return Token.Type.OPERATOR;
        if(tokenContent.length == 1 && tokenContent[0] == ';')
            return Token.Type.SEMICOLON;

        switch(Alphabet.getTypeFromMainCharacter(tokenContent[0])) {
            case STRING -> {
                return Token.Type.IDENTIFIER;
            }
            case NUMERIC -> {
                return Token.Type.LITERAL_NUMERIC;
            }
        }

        if(tokenContent[0] == '"')
            return Token.Type.LITERAL_STRING;
        if(tokenContent[0] == '\'')
            return Token.Type.LITERAL_CHAR;

        return Token.Type.SEPARATOR;
    }

    /**
     * Returns a substring of the file content from the start character index (included)
     * to the end character index (excluded)
     * @param startIndex the starting character index (included)
     * @param endIndex the ending character index (excluded)
     * @return a substring of the file content
     */
    private char[] getSubstring(int startIndex, int endIndex) {
        char[] substring = new char[endIndex - startIndex];

        //Extract a substring from the file content
        for(int i = startIndex, j = 0; i < endIndex; i++, j++) {
            substring[j] = content[i];
        }

        return substring;
    }

    /**
     * Easter egg: if the first token is a closing curly bracket, it is assumed the code is reversed.
     * Therefore, it reverses back the tokens list, which means the reversed code still get compiled!
     */
    private void checkReverse() {
        if(!tokens.isEmpty() && tokens.getFirst().getContent().equals(Symbol.CLOSING_CURLY_BRACKET))
            Collections.reverse(tokens);
    }

    /**
     * The Tokenizer.Escape enum. This enum contains every kind
     * of token escape there exists.
     */
    private enum Escape {
        NONE, CHARACTER, STRING, COMMENT, LONG_COMMENT, DOCSTRING;

        boolean isComment() {
            return this == COMMENT || this == LONG_COMMENT || this == DOCSTRING;
        }
    }
}
