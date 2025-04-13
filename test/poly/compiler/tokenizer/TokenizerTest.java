package poly.compiler.tokenizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTest {
    private static Token[] tokenize(String content) {
        Tokenizer tokenizer = Tokenizer.getInstance(null, content.toCharArray());
        return tokenizer.tokenize();
    }

    private static Token generateToken(String content, Token.Type type) {
        return new Token(content.toCharArray(), type, null);
    }

    @Test
    void testIntegerLiteral() {
        String code = "64";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("64", Token.Type.LITERAL_NUMERIC), tokens[0]);
    }

    @Test
    void testIntegerLiteralBinary() {
        String code = "0b1101 0B1101";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("0b1101", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("0B1101", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testIntegerLiteralHexadecimal() {
        String code = "0xDEADBEEFC 0Xdeadbeefc";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("0xDEADBEEFC", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("0Xdeadbeefc", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testLongLiteral() {
        String code = "256l 256L";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("256l", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("256L", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testLongLiteralBinary() {
        String code = "0b1101l 0B1101L";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("0b1101l", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("0B1101L", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testLongLiteralHexadecimal() {
        String code = "0xDEADBEEFCl 0XdeadbeefcL";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("0xDEADBEEFCl", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("0XdeadbeefcL", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testFloatLiteral() {
        String code = "1.61f 1.61F";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("1.61f", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("1.61F", Token.Type.LITERAL_NUMERIC), tokens[1]);
    }

    @Test
    void testDoubleLiteral() {
        String code = "3.14 3.14d 3.14D";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("3.14", Token.Type.LITERAL_NUMERIC), tokens[0]);
        assertEquals(generateToken("3.14d", Token.Type.LITERAL_NUMERIC), tokens[1]);
        assertEquals(generateToken("3.14D", Token.Type.LITERAL_NUMERIC), tokens[2]);
    }

    @Test
    void testCharacterLiteral() {
        String code = "'A'";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("'A'", Token.Type.LITERAL_CHAR), tokens[0]);
    }

    @Test
    void testCharacterLiteralEscape() {
        String code = "'\\n\\''";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("'\\n\\''", Token.Type.LITERAL_CHAR), tokens[0]);
    }

    @Test
    void testCharacterLiteralComment() {
        String code = "'A //b /*C*/ ¦ d'";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("'A //b /*C*/ ¦ d'", Token.Type.LITERAL_CHAR), tokens[0]);
    }

    @Test
    void testStringLiteral() {
        String code = "\"Hello world!\"";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("\"Hello world!\"", Token.Type.LITERAL_STRING), tokens[0]);
    }

    @Test
    void testStringLiteralEscape() {
        String code = "\"\\\"Hello\\nWorld!\\t\\\"\"";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("\"\\\"Hello\\nWorld!\\t\\\"\"", Token.Type.LITERAL_STRING), tokens[0]);
    }

    @Test
    void testSimpleStringComment() {
        String code = "\"Hello! //Bonjour! /*Hola!*/ ¦ Guten tag!\"";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("\"Hello! //Bonjour! /*Hola!*/ ¦ Guten tag!\"", Token.Type.LITERAL_STRING), tokens[0]);
    }

    @Test
    void testSingleLineComment() {
        String code = "//Hello \"sir\"!";
        Token[] tokens = tokenize(code);
        assertEquals(0, tokens.length);
    }

    @Test
    void testMultiLineComment() {
        String code = "/*Hello\n\"sir\"!\n*/";
        Token[] tokens = tokenize(code);
        assertEquals(0, tokens.length);
    }

    @Test
    void testDocstring() {
        String code = "¦ Hello \"sir\"!";
        Token[] tokens = tokenize(code);
        assertEquals(0, tokens.length);
    }

    @Test
    void testMathematicalExpression() {
        String code = "a*(b+ 16) -64/xyz";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("a", Token.Type.IDENTIFIER), tokens[0]);
        assertEquals(generateToken("*", Token.Type.OPERATOR), tokens[1]);
        assertEquals(generateToken("(", Token.Type.SEPARATOR), tokens[2]);
        assertEquals(generateToken("b", Token.Type.IDENTIFIER), tokens[3]);
        assertEquals(generateToken("+", Token.Type.OPERATOR), tokens[4]);
        assertEquals(generateToken("16", Token.Type.LITERAL_NUMERIC), tokens[5]);
        assertEquals(generateToken(")", Token.Type.SEPARATOR), tokens[6]);
        assertEquals(generateToken("-", Token.Type.OPERATOR), tokens[7]);
        assertEquals(generateToken("64", Token.Type.LITERAL_NUMERIC), tokens[8]);
        assertEquals(generateToken("/", Token.Type.OPERATOR), tokens[9]);
        assertEquals(generateToken("xyz", Token.Type.IDENTIFIER), tokens[10]);
    }

    @Test
    void testClassDeclaration() {
        String code = "class+ FMatrix(Matrix ): Matrix.Property, Equatable,Comparable {}";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("class", Token.Type.KEYWORD), tokens[0]);
        assertEquals(generateToken("+", Token.Type.OPERATOR), tokens[1]);
        assertEquals(generateToken("FMatrix", Token.Type.IDENTIFIER), tokens[2]);
        assertEquals(generateToken("(", Token.Type.SEPARATOR), tokens[3]);
        assertEquals(generateToken("Matrix", Token.Type.IDENTIFIER), tokens[4]);
        assertEquals(generateToken(")", Token.Type.SEPARATOR), tokens[5]);
        assertEquals(generateToken(":", Token.Type.SEPARATOR), tokens[6]);
        assertEquals(generateToken("Matrix", Token.Type.IDENTIFIER), tokens[7]);
        assertEquals(generateToken(".", Token.Type.SEPARATOR), tokens[8]);
        assertEquals(generateToken("Property", Token.Type.IDENTIFIER), tokens[9]);
        assertEquals(generateToken(",", Token.Type.SEPARATOR), tokens[10]);
        assertEquals(generateToken("Equatable", Token.Type.IDENTIFIER), tokens[11]);
        assertEquals(generateToken(",", Token.Type.SEPARATOR), tokens[12]);
        assertEquals(generateToken("Comparable", Token.Type.IDENTIFIER), tokens[13]);
        assertEquals(generateToken("{", Token.Type.SEPARATOR), tokens[14]);
        assertEquals(generateToken("}", Token.Type.SEPARATOR), tokens[15]);
    }

    @Test
    void testMethodDeclaration() {
        String code = "fn~ int getValue(int x, int y,int z ){ }";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("fn", Token.Type.KEYWORD), tokens[0]);
        assertEquals(generateToken("~", Token.Type.OPERATOR), tokens[1]);
        assertEquals(generateToken("int", Token.Type.KEYWORD), tokens[2]);
        assertEquals(generateToken("getValue", Token.Type.IDENTIFIER), tokens[3]);
        assertEquals(generateToken("(", Token.Type.SEPARATOR), tokens[4]);
        assertEquals(generateToken("int", Token.Type.KEYWORD), tokens[5]);
        assertEquals(generateToken("x", Token.Type.IDENTIFIER), tokens[6]);
        assertEquals(generateToken(",", Token.Type.SEPARATOR), tokens[7]);
        assertEquals(generateToken("int", Token.Type.KEYWORD), tokens[8]);
        assertEquals(generateToken("y", Token.Type.IDENTIFIER), tokens[9]);
        assertEquals(generateToken(",", Token.Type.SEPARATOR), tokens[10]);
        assertEquals(generateToken("int", Token.Type.KEYWORD), tokens[11]);
        assertEquals(generateToken("z", Token.Type.IDENTIFIER), tokens[12]);
        assertEquals(generateToken(")", Token.Type.SEPARATOR), tokens[13]);
        assertEquals(generateToken("{", Token.Type.SEPARATOR), tokens[14]);
        assertEquals(generateToken("}", Token.Type.SEPARATOR), tokens[15]);
    }

    @Test
    void testFieldDeclaration() {
        String code = "- const Vector xyz;";
        Token[] tokens = tokenize(code);
        assertEquals(generateToken("-", Token.Type.OPERATOR), tokens[0]);
        assertEquals(generateToken("const", Token.Type.KEYWORD), tokens[1]);
        assertEquals(generateToken("Vector", Token.Type.IDENTIFIER), tokens[2]);
        assertEquals(generateToken("xyz", Token.Type.IDENTIFIER), tokens[3]);
        assertEquals(generateToken(";", Token.Type.SEMICOLON), tokens[4]);
    }
}
