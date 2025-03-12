package poly.compiler.tokenizer;

import poly.compiler.util.Character;

import java.util.Arrays;

/**
 * The Token class. This class represents a lexical token (i.e. an atomic string element with a meaning)
 * and its associated string content and type. The token also has a metadata attribute, which
 * contains information (often used as debugging information) such as the token line number.
 * @author Vincent Philippe (@vincent64)
 */
public final class Token {
    /** The token characters content. */
    private final char[] content;
    /** The token type. */
    private final Type type;
    /** The token metadata. */
    private final Meta meta;

    /**
     * Constructs a token with the given content, type and metadata information.
     * @param content the content
     * @param type the type
     * @param meta the metadata information
     */
    public Token(char[] content, Type type, Meta meta) {
        this.content = content;
        this.type = type;
        this.meta = meta;
    }

    /**
     * Returns the token content.
     * @return the token content
     */
    public char[] getContent() {
        return content;
    }

    /**
     * Returns the token type.
     * @return the token type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the token metadata.
     * @return the token metadata
     */
    public Meta getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof Token token))
            return false;

        if(type != token.type)
            return false;

        return Character.isSameString(content, token.content);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(content);
        result = 31 * result + (type != null ? type.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "Token(" + type + "): \"" + String.valueOf(content) + "\"";
    }

    /**
     * The Token.Type enum. This enum contains every type of token there exists.
     */
    public enum Type {
        KEYWORD,
        IDENTIFIER,
        LITERAL_NUMERIC,
        LITERAL_STRING,
        LITERAL_CHAR,
        SEPARATOR,
        OPERATOR,
        SEMICOLON
    }

    /**
     * The Token.Meta class. This class contains metadata information about a token,
     * mainly its location in the code file. These information can be useful when debugging.
     */
    public static class Meta {
        private final String fileName;
        private final int line;
        private final int character;

        public Meta(String fileName, int line, int character) {
            this.fileName = fileName;
            this.line = line;
            this.character = character;
        }

        public String getFileName() {
            return fileName;
        }

        public int getLine() {
            return line;
        }

        public int getCharacter() {
            return character;
        }

        @Override
        public String toString() {
            return "Token.Meta(fileName=" + fileName + ", line=" + line + ", character=" + character + ")";
        }
    }
}
