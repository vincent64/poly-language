package poly.compiler.parser;

import poly.compiler.analyzer.type.Primitive;
import poly.compiler.error.ParsingError;
import poly.compiler.parser.literal.CharParser;
import poly.compiler.parser.literal.NumericParser;
import poly.compiler.parser.literal.StringParser;
import poly.compiler.parser.tree.*;
import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.statement.*;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.parser.tree.variable.VariableDeclaration;
import poly.compiler.tokenizer.Token;
import poly.compiler.tokenizer.content.Operator;
import poly.compiler.tokenizer.content.Symbol;
import poly.compiler.util.Character;

import static poly.compiler.tokenizer.content.Attribute.*;
import static poly.compiler.tokenizer.content.Keyword.*;
import static poly.compiler.tokenizer.content.Operator.*;
import static poly.compiler.tokenizer.content.Symbol.*;
import static poly.compiler.util.Character.isSameString;

/**
 * The Parser class. This class is used to transform the list of tokens produced by
 * the tokenizer into an Abstract Syntax Tree (AST), which can then be used to create
 * the output class file. This class goes through every token one-by-one and make sure
 * they follow the language's grammar. If an unexpected token is found, a compile-time
 * UnexpectedToken error is thrown, indicating the location of the error in the code.
 * The output tree is made up of nodes, which can represent a statement, an expression,
 * a declaration, etc. Even though the parser will make sure the code follow the
 * language's grammar, not every syntactic error are detected during this phase.
 * @author Vincent Philippe (@vincent64)
 */
public final class Parser {
    private final Token[] tokens;
    private Token currentToken;
    private int currentTokenIndex;

    private Parser(Token[] tokens) {
        this.tokens = tokens;
    }

    public static Parser getInstance(Token[] tokens) {
        return new Parser(tokens);
    }

    /**
     * Parses the tokens list and returns the Abstract Syntax Tree.
     * @return the AST
     */
    public ContentNode parse() {
        //Make sure there is at least one token
        if(tokens.length == 0)
            return new ContentNode();

        //Set current starting token
        currentTokenIndex = 0;
        currentToken = tokens[currentTokenIndex];

        return parseContent();
    }

    /**
     * Advances to the next token, by setting the current token to be the next one.
     * This method should not be called if the current token is the last one of the code.
     * Therefore, if it does get called with the last token, an UnexpectedEndOfCode error
     * is thrown, saying the code ended abruptely where it should have not.
     */
    private void nextToken() {
        //Throw error if the code ended unexpectedly
        if(isPastLastToken()) new ParsingError.UnexpectedEndOfCode(currentToken);

        //Update current token
        currentToken = tokens[++currentTokenIndex];
    }

    /**
     * Takes a peek at and returns the next token in the list, like it goes forward
     * in time. Similarly to the nextToken, it throws an UnexpectedEndOfCode error
     * if the code ended too abruptely.
     * @return the next token in the list
     */
    private Token peekToken() {
        //Throw error if the next token is out of bounds
        if(isPastLastToken()) new ParsingError.UnexpectedEndOfCode(currentToken);

        return tokens[currentTokenIndex + 1];
    }

    /**
     * Returns whether the current token is the past the last token in the list
     * @return true if the current token is past the last one
     */
    private boolean isPastLastToken() {
        return currentTokenIndex >= tokens.length - 1;
    }

    /**
     * Matches the current token with the given string content,
     * and advances to the next token.
     * @param content the string content
     */
    private void match(char[] content) {
        if(!isSameString(currentToken.getContent(), content)) {
            //Throw missing token error
            if(isSameString(content, SEMICOLON) || isSameString(content, CLOSING_PARENTHESIS))
                new ParsingError.MissingToken(currentToken, content);

            //Throw unexpected token error
            new ParsingError.UnexpectedToken(currentToken);
        }

        if(!isPastLastToken()) nextToken();
        else currentTokenIndex++;
    }

    /**
     * Returns whether the current token is matching the given string content.
     * @param content the string content
     * @return true if the current token is matching the content
     */
    private boolean isMatching(char[] content) {
        return !isPastLastToken() && isSameString(currentToken.getContent(), content);
    }

    /**
     * Returns wether the current token is matching the given token type.
     * @param type the token type
     * @return true if the current token is matching the type
     */
    private boolean isMatchingType(Token.Type type) {
        return currentToken.getType() == type;
    }

    /**
     * Returns wether the current token is matching the given string content,
     * and advances to the next token if it is the case.
     * @param content the string content
     * @return true if the current token is matching the content
     */
    private boolean matches(char[] content) {
        if(isMatching(content)) {
            nextToken();
            return true;
        }

        return false;
    }



    //Parsing content and import statements

    private ContentNode parseContent() {
        ContentNode node = new ContentNode();

        //Parse import statements
        while(isMatching(IMPORT))
            node.addImport(parseImportStatement());

        //Parse class declarations
        while(isMatching(CLASS) || isMatching(CLASS_INTERFACE) || isMatching(CLASS_EXCEPTION))
            node.addClass(parseClassDeclaration());

        //Throw an error if there are tokens left
        if(currentTokenIndex < tokens.length)
            new ParsingError.UnexpectedEndOfCode(currentToken);

        return node;
    }

    private Node parseImportStatement() {
        ImportStatement node = new ImportStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match import keyword
        match(IMPORT);

        //Parse import alias name
        if(isMatchingType(Token.Type.IDENTIFIER) && Character.isSameString(peekToken().getContent(), COLON)) {
            node.setAliasName(currentToken);
            nextToken();
            match(COLON);
        }

        //Parse import package name
        node.setPackageName(parseQualifiedName());

        //Match semicolon at the end
        match(SEMICOLON);

        return node;
    }



    //Parsing declarations and initializations

    private Node parseClassDeclaration() {
        ClassDeclaration node = new ClassDeclaration(Node.Meta.fromLeadingToken(currentToken));

        //Match class, interface, inner or exception keyword
        if(matches(CLASS) || isMatching(CLASS_INTERFACE) || isMatching(CLASS_INNER) || isMatching(CLASS_EXCEPTION)) {
            if(matches(CLASS_INTERFACE))
                node.setInterface();

            if(matches(CLASS_INNER))
                node.setInner();

            if(matches(CLASS_EXCEPTION))
                node.setException();
        }

        //Parse class access modifier
        if(isAccessModifierSymbol(currentToken)) {
            node.setAccessModifier(currentToken);
            nextToken();
        }

        //Check if class is static
        if(matches(SHARP)) node.setStatic();

        //Check if class is constant
        if(matches(VAR_CONST)) node.setConstant();

        //Parse class name
        if(isMatchingType(Token.Type.IDENTIFIER)) {
            node.setName(currentToken);
            nextToken();
        } else new ParsingError.UnexpectedToken(currentToken);

        //Parse superclass name if there is one
        if(matches(OPENING_PARENTHESIS)) {
            node.setSuperclass(parseQualifiedName());
            match(CLOSING_PARENTHESIS);
        }

        //Parse interface names if there are some
        if(matches(COLON)) {
            do {
                node.addInterface(parseQualifiedName());
            } while(matches(COMMA));
        }

        //Match opening bracket
        match(OPENING_CURLY_BRACKET);

        //Parse class fields
        while(isAccessModifierSymbol(currentToken)
                || isMatching(VAR_CONST)
                || isMatching(SHARP)
                || isPrimitiveKeyword(currentToken)
                || isMatchingType(Token.Type.IDENTIFIER))
            node.addField(parseFieldDeclaration());

        //Parse class methods
        while(isMatching(METHOD) || isMatching(METHOD_OPERATOR))
            node.addMethod(parseMethodDeclaration());

        //Parse class innerclasses
        while(isMatching(CLASS) || isMatching(CLASS_INTERFACE) || isMatching(CLASS_INNER) || isMatching(CLASS_EXCEPTION))
            node.addNestedClass(parseClassDeclaration());

        //Match closing bracket
        match(CLOSING_CURLY_BRACKET);

        return node;
    }

    private Node parseFieldDeclaration() {
        FieldDeclaration node = new FieldDeclaration(Node.Meta.fromLeadingToken(currentToken));

        //Parse field access modifier
        if(isAccessModifierSymbol(currentToken)) {
            node.setAccessModifier(currentToken);
            nextToken();
        }

        //Check if field is static
        if(matches(SHARP)) node.setStatic();

        //Parse variable declaration
        node.setVariable(parseVariableDeclaration());

        //Match semicolon at the end
        match(SEMICOLON);

        return node;
    }

    private Node parseMethodDeclaration() {
        MethodDeclaration node = new MethodDeclaration(Node.Meta.fromLeadingToken(currentToken));

        //Match method keyword
        if(matches(METHOD) || isMatching(METHOD_OPERATOR)) {
            if(matches(METHOD_OPERATOR)) node.setOperator();
        }

        //Parse method access modifier
        if(isAccessModifierSymbol(currentToken)) {
            node.setAccessModifier(currentToken);
            nextToken();
        }

        //Check if method is static
        if(matches(SHARP)) node.setStatic();

        //Check if method is constant
        if(matches(VAR_CONST)) node.setConstant();

        //Check if method is a constructor
        if(matches(METHOD_CONSTRUCTOR) && !node.isOperator()) {
            node.setConstructor();
        } else {
            //Parse method return type
            if(!matches(METHOD_VOID))
                node.setReturnType(parseType());

            //Check if method is operator
            if(node.isOperator()) {
                if(isMethodOperator(currentToken)) {
                    node.setName(currentToken);
                    nextToken();
                } else new ParsingError.UnexpectedToken(currentToken);
            } else {
                //Parse method name
                if(isMatchingType(Token.Type.IDENTIFIER)) {
                    node.setName(currentToken);
                    nextToken();
                } else new ParsingError.UnexpectedToken(currentToken);
            }
        }

        //Parse method parameters
        match(OPENING_PARENTHESIS);
        node.setParameterList(parseParameterList());
        match(CLOSING_PARENTHESIS);

        //Check if method has empty body
        if(matches(SEMICOLON)) {
            node.setEmpty();

            return node;
        }

        //Parse method content
        node.setStatementBlock(parseStatementBlock());

        return node;
    }

    private Statement parseVariableDeclaration() {
        VariableDeclaration statement = new VariableDeclaration(Node.Meta.fromLeadingToken(currentToken));

        //Check if variable is constant
        if(matches(VAR_CONST)) statement.setConstant();

        //Parse variable type
        statement.setType(parseType());

        //Parse variable name
        if(isMatchingType(Token.Type.IDENTIFIER)) {
            statement.setName(currentToken);
            nextToken();
        } else new ParsingError.UnexpectedToken(currentToken);

        //Parse variable initialization
        if(matches(Symbol.EQUAL))
            statement.setInitializationExpression(parseExpression());

        return statement;
    }



    //Parsing statements

    private Statement parseStatementBlock() {
        StatementBlock statement = new StatementBlock(Node.Meta.fromLeadingToken(currentToken));

        //Parse block of statements
        if(matches(OPENING_CURLY_BRACKET)) {
            while(!isMatching(CLOSING_CURLY_BRACKET))
                statement.addStatement(parseStatement());

            match(CLOSING_CURLY_BRACKET);
        } else {
            statement.addStatement(parseStatement());
        }

        return statement;
    }

    private Statement parseStatement() {
        //Parse statements starting with a keyword
        if(isKeyword(currentToken)) {
            //Parse if-statement
            if(isMatching(STATEMENT_IF))
                return parseIfStatement();

            //Parse for-statement
            if(isMatching(STATEMENT_FOR))
                return parseForStatement();

            //Parse while-statement
            if(isMatching(STATEMENT_WHILE))
                return parseWhileStatement();

            //Parse do-statement
            if(isMatching(STATEMENT_DO))
                return parseDoStatement();

            //Parse switch-statement
            if(isMatching(STATEMENT_SWITCH))
                return parseSwitchStatement();

            //Parse match-statement
            if(isMatching(STATEMENT_MATCH))
                return parseMatchStatement();

            //Parse assert-statement
            if(isMatching(STATEMENT_ASSERT))
                return parseAssertStatement();

            //Parse try-statement
            if(isMatching(STATEMENT_TRY))
                return parseTryStatement();

            //Parse throw-statement
            if(isMatching(STATEMENT_THROW))
                return parseThrowStatement();

            //Parse return statement
            if(isMatching(STATEMENT_RETURN))
                return parseReturnStatement();

            //Parse break statement
            if(isMatching(STATEMENT_BREAK))
                return parseBreakStatement();

            //Parse continue statement
            if(isMatching(STATEMENT_CONTINUE))
                return parseContinueStatement();

            //Parse this statement
            if(isMatching(EXPRESSION_THIS))
                return parseThisStatement();

            //Parse super statement
            if(isMatching(EXPRESSION_SUPER))
                return parseSuperStatement();

            //Parse variable declaration
            if(isPrimitiveKeyword(currentToken) || isMatching(VAR_CONST)) {
                Statement statement = parseVariableDeclaration();
                match(SEMICOLON);

                return statement;
            }
        }

        //Parse dangling statements block
        if(isMatching(OPENING_CURLY_BRACKET))
            return parseStatementBlock();

        return parseVariableStatement();
    }

    private Statement parseVariableStatement() {
        //Parse statement starting with identifier
        if(isMatchingType(Token.Type.IDENTIFIER)) {
            Token nextToken = peekToken();

            //Parse variable declaration
            if(nextToken.getType() == Token.Type.IDENTIFIER) {
                Statement statement = parseVariableDeclaration();
                match(SEMICOLON);

                return statement;
            }

            //Parse expression
            Expression expression = parseExpression();

            //Parse variable declaration
            if((isMatchingType(Token.Type.IDENTIFIER) || isMatching(OPENING_SQUARE_BRACKET))
                    && (expression instanceof QualifiedName || expression instanceof SimpleName)) {
                //Parse array type
                while(matches(OPENING_SQUARE_BRACKET)) {
                    ArrayType arrayType = new ArrayType(Node.Meta.fromLeadingToken(currentToken));
                    arrayType.setType(expression);
                    expression = arrayType;

                    match(CLOSING_SQUARE_BRACKET);
                }

                VariableDeclaration variableDeclaration = new VariableDeclaration(Node.Meta.fromLeadingToken(currentToken));
                variableDeclaration.setType(expression);

                //Parse variable name
                if(isMatchingType(Token.Type.IDENTIFIER)) {
                    variableDeclaration.setName(currentToken);
                    nextToken();
                } else new ParsingError.UnexpectedToken(currentToken);

                //Parse variable initialization
                if(matches(Symbol.EQUAL))
                    variableDeclaration.setInitializationExpression(parseExpression());

                //Match semicolon at the end
                match(SEMICOLON);

                return variableDeclaration;
            }

            //Parse expression statement from expression
            ExpressionStatement expressionStatement = new ExpressionStatement(Node.Meta.fromLeadingToken(currentToken));
            expressionStatement.setExpression(expression);

            //Match semicolon at the end
            match(SEMICOLON);

            return expressionStatement;
        }

        return parseExpressionStatement();
    }

    private Statement parseIfStatement() {
        IfStatement statement = new IfStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match if keyword
        match(STATEMENT_IF);

        //Parse condition expression
        match(OPENING_PARENTHESIS);
        statement.setCondition(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Parse statement body
        statement.setStatementBlock(parseStatementBlock());

        //Parse else statement body
        if(matches(STATEMENT_ELSE))
            statement.setElseStatementBlock(parseStatementBlock());

        return statement;
    }

    private Statement parseForStatement() {
        ForStatement statement = new ForStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match for keyword and opening parenthesis
        match(STATEMENT_FOR);
        match(OPENING_PARENTHESIS);

        //Parse statement
        statement.setStatement(parseStatement());

        //Parse condition expression and semicolon
        statement.setCondition(parseExpression());
        match(SEMICOLON);

        //Parse expression as statement
        ExpressionStatement expressionStatement = new ExpressionStatement(Node.Meta.fromLeadingToken(currentToken));
        expressionStatement.setExpression(parseExpression());
        statement.setExpression(expressionStatement);

        //Match closing parenthesis
        match(CLOSING_PARENTHESIS);

        //Parse statement body
        statement.setStatementBlock(parseStatementBlock());

        return statement;
    }

    private Statement parseWhileStatement() {
        WhileStatement statement = new WhileStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match while keyword
        match(STATEMENT_WHILE);

        //Parse condition expression
        match(OPENING_PARENTHESIS);
        statement.setCondition(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Parse statement body
        statement.setStatementBlock(parseStatementBlock());

        return statement;
    }

    private Statement parseDoStatement() {
        DoStatement statement = new DoStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match do keyword
        match(STATEMENT_DO);

        //Parse statement body
        statement.setStatementBlock(parseStatementBlock());

        //Match while keyword
        match(STATEMENT_WHILE);

        //Parse condition expression
        match(OPENING_PARENTHESIS);
        statement.setCondition(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Match semicolon at the end
        match(SEMICOLON);

        return statement;
    }

    private Statement parseSwitchStatement() {
        SwitchStatement statement = new SwitchStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match switch keyword
        match(STATEMENT_SWITCH);

        //Parse switch expression
        match(OPENING_PARENTHESIS);
        statement.setExpression(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Match opening bracket
        match(OPENING_CURLY_BRACKET);

        //Parse case statements
        while(isMatching(STATEMENT_CASE))
            statement.addCase(parseCase());

        //Match closing bracket
        match(CLOSING_CURLY_BRACKET);

        return statement;
    }

    private Statement parseMatchStatement() {
        MatchStatement statement = new MatchStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match match keyword and opening bracket
        match(STATEMENT_MATCH);
        match(OPENING_CURLY_BRACKET);

        //Parse case statements
        while(isMatching(STATEMENT_CASE))
            statement.addCase(parseCase());

        //Match closing bracket
        match(CLOSING_CURLY_BRACKET);

        return statement;
    }

    private Statement parseAssertStatement() {
        AssertStatement statement = new AssertStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match assert keyword
        match(STATEMENT_ASSERT);

        //Parse assert expression
        match(OPENING_PARENTHESIS);
        statement.setCondition(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Match semicolon at the end
        match(SEMICOLON);

        return statement;
    }

    private Statement parseTryStatement() {
        TryStatement statement = new TryStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match try keyword
        match(STATEMENT_TRY);

        //Parse statement body
        statement.setStatementBlock(parseStatementBlock());

        //Match catch keyword
        match(STATEMENT_CATCH);

        //Parse exception parameter
        match(OPENING_PARENTHESIS);
        statement.setExceptionParameter(parseParameter());
        match(CLOSING_PARENTHESIS);

        //Parse catch statement body
        statement.setCatchStatementBlock(parseStatementBlock());

        return statement;
    }

    private Statement parseThrowStatement() {
        ThrowStatement statement = new ThrowStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match throw keyword
        match(STATEMENT_THROW);

        //Parse throw expression
        statement.setExpression(parseExpression());

        //Match semicolon at the end
        match(SEMICOLON);

        return statement;
    }

    private Statement parseReturnStatement() {
        ReturnStatement statement = new ReturnStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match return keyword
        match(STATEMENT_RETURN);

        //Parse return expression
        if(!isMatching(SEMICOLON))
            statement.setExpression(parseExpression());

        //Match semicolon at the end
        match(SEMICOLON);

        return statement;
    }

    private Statement parseBreakStatement() {
        BreakStatement statement = new BreakStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match break keyword and semicolon
        match(STATEMENT_BREAK);
        match(SEMICOLON);

        return statement;
    }

    private Statement parseContinueStatement() {
        ContinueStatement statement = new ContinueStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match continue keyword and semicolon
        match(STATEMENT_CONTINUE);
        match(SEMICOLON);

        return statement;
    }

    private Statement parseThisStatement() {
        if(Character.isSameString(peekToken().getContent(), OPENING_PARENTHESIS)) {
            ThisStatement statement = new ThisStatement(Node.Meta.fromLeadingToken(currentToken));

            //Match this keyword
            match(EXPRESSION_THIS);

            //Parse arguments list
            match(OPENING_PARENTHESIS);
            statement.setArgumentList(parseArgumentList());
            match(CLOSING_PARENTHESIS);

            //Match semicolon at the end
            match(SEMICOLON);

            return statement;
        }

        return parseExpressionStatement();
    }

    private Statement parseSuperStatement() {
        if(Character.isSameString(peekToken().getContent(), OPENING_PARENTHESIS)) {
            SuperStatement statement = new SuperStatement(Node.Meta.fromLeadingToken(currentToken));

            //Match super keyword
            match(EXPRESSION_SUPER);

            //Parse arguments list
            match(OPENING_PARENTHESIS);
            statement.setArgumentList(parseArgumentList());
            match(CLOSING_PARENTHESIS);

            //Match semicolon at the end
            match(SEMICOLON);

            return statement;
        }

        return parseExpressionStatement();
    }

    private Statement parseCase() {
        CaseStatement statement = new CaseStatement(Node.Meta.fromLeadingToken(currentToken));

        //Match case keyword
        match(STATEMENT_CASE);

        //Parse case expression
        match(OPENING_PARENTHESIS);
        statement.setExpression(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Parse case body
        statement.setStatementBlock(parseStatementBlock());

        return statement;
    }

    private Statement parseExpressionStatement() {
        ExpressionStatement statement = new ExpressionStatement(Node.Meta.fromLeadingToken(currentToken));

        //Parse expression
        statement.setExpression(parseExpression());

        //Match semicolon at the end
        match(SEMICOLON);

        return statement;
    }



    //Parsing expressions

    private Expression parseExpression() {
        //Parse if-expression
        if(isMatching(STATEMENT_IF))
            return parseIfExpression();

        //Parse sum-expression
        if(isMatching(EXPRESSION_SUM))
            return parseSumExpression();

        //Parse prod-expression
        if(isMatching(EXPRESSION_PROD))
            return parseProdExpression();

        return parseExpressionAssignment();
    }

    private Expression parseIfExpression() {
        IfExpression expression = new IfExpression(Node.Meta.fromLeadingToken(currentToken));

        //Match if keyword
        match(STATEMENT_IF);

        //Parse condition expression
        match(OPENING_PARENTHESIS);
        expression.setCondition(parseExpression());
        match(CLOSING_PARENTHESIS);

        //Parse expression
        expression.setExpression(parseExpression());

        //Match else keyword
        match(STATEMENT_ELSE);

        //Parse else expression
        expression.setElseExpression(parseExpression());

        return expression;
    }

    private Expression parseSumExpression() {
        SumExpression expression = new SumExpression(Node.Meta.fromLeadingToken(currentToken));

        //Match sum keyword and opening parenthesis
        match(EXPRESSION_SUM);
        match(OPENING_PARENTHESIS);

        //Parse variable initialiation
        expression.setVariableInitialization(parseVariableDeclaration());
        match(SEMICOLON);

        //Parse condition
        expression.setCondition(parseExpression());
        match(SEMICOLON);

        //Parse variable incrementation
        ExpressionStatement expressionStatement = new ExpressionStatement(Node.Meta.fromLeadingToken(currentToken));
        expressionStatement.setExpression(parseExpression());
        expression.setIncrementExpression(expressionStatement);

        //Match closing parenthesis
        match(CLOSING_PARENTHESIS);

        //Parse expression
        expression.setExpression(parseExpression());

        return expression;
    }

    private Expression parseProdExpression() {
        ProdExpression expression = new ProdExpression(Node.Meta.fromLeadingToken(currentToken));

        //Match prod keyword and opening parenthesis
        match(EXPRESSION_PROD);
        match(OPENING_PARENTHESIS);

        //Parse variable initialiation
        expression.setVariableInitialization(parseVariableDeclaration());
        match(SEMICOLON);

        //Parse condition
        expression.setCondition(parseExpression());
        match(SEMICOLON);

        //Parse variable incrementation
        ExpressionStatement expressionStatement = new ExpressionStatement(Node.Meta.fromLeadingToken(currentToken));
        expressionStatement.setExpression(parseExpression());
        expression.setIncrementExpression(expressionStatement);

        //Match closing parenthesis
        match(CLOSING_PARENTHESIS);

        //Parse expression
        expression.setExpression(parseExpression());

        return expression;
    }

    private Expression parseExpressionAssignment() {
        Expression expression = parseExpressionNullCoalescing();

        //Parse assignment expression
        if(isAssignOperator(currentToken)) {
            AssignmentExpression assignmentExpression = new AssignmentExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(Symbol.EQUAL))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT);
            if(matches(ASSIGN_ADD))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_ADDITION);
            if(matches(ASSIGN_SUB))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_SUBTRACTION);
            if(matches(ASSIGN_MUL))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_MULTIPLICATION);
            if(matches(ASSIGN_DIV))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_DIVISION);
            if(matches(ASSIGN_MOD))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_MODULO);
            if(matches(ASSIGN_BITWISE_AND))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_BITWISE_AND);
            if(matches(ASSIGN_BITWISE_XOR))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_BITWISE_XOR);
            if(matches(ASSIGN_BITWISE_OR))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_BITWISE_OR);
            if(matches(ASSIGN_SHIFT_LEFT))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_SHIFT_LEFT);
            if(matches(ASSIGN_SHIFT_RIGHT))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_SHIFT_RIGHT);
            if(matches(ASSIGN_SHIFT_RIGHT_ARITHMETIC))
                assignmentExpression.setKind(AssignmentExpression.Kind.ASSIGNMENT_SHIFT_RIGHT_ARITHMETIC);

            assignmentExpression.setVariable(expression);
            assignmentExpression.setExpression(parseExpression());
            expression = assignmentExpression;
        }

        return expression;
    }

    private Expression parseExpressionNullCoalescing() {
        Expression expression = parseExpressionLogicalOr();

        //Parse null coalescing operator expression
        if(matches(NULL_COALESCING)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_NULL);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionLogicalOr());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionLogicalOr() {
        Expression expression = parseExpressionLogicalAnd();

        //Parse logical OR expression
        while(matches(LOGICAL_OR)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.LOGICAL_OR);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionLogicalAnd());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionLogicalAnd() {
        Expression expression = parseExpressionBitwiseOr();

        //Parse logical AND expression
        while(matches(LOGICAL_AND)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.LOGICAL_AND);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionBitwiseOr());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionBitwiseOr() {
        Expression expression = parseExpressionBitwiseXor();

        //Parse bitwise OR expression
        while(matches(BITWISE_OR)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.BITWISE_OR);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionBitwiseXor());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionBitwiseXor() {
        Expression expression = parseExpressionBitwiseAnd();

        //Parse bitwise XOR expression
        while(matches(BITWISE_XOR)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.BITWISE_XOR);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionBitwiseAnd());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionBitwiseAnd() {
        Expression expression = parseExpressionEquality();

        //Parse bitwise AND expression
        while(matches(BITWISE_AND)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));
            binaryExpression.setKind(BinaryExpression.Kind.BITWISE_AND);
            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionEquality());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionEquality() {
        Expression expression = parseExpressionComparison();

        //Parse equality expression
        while(isMatching(Operator.EQUAL) || isMatching(NOT_EQUAL)
                || isMatching(REFERENCE_EQUAL) || isMatching(REFERENCE_NOT_EQUAL)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(Operator.EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.EQUALITY_EQUAL);
            if(matches(NOT_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.EQUALITY_NOT_EQUAL);
            if(matches(REFERENCE_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.REFERENCE_EQUAL);
            if(matches(REFERENCE_NOT_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.REFERENCE_NOT_EQUAL);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionComparison());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionComparison() {
        Expression expression = parseExpressionRelational();

        //Parse comparison expression
        while(isMatching(GREATER) || isMatching(LESS) || isMatching(GREATER_EQUAL) || isMatching(LESS_EQUAL)
                || isMatching(SPACESHIP)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(GREATER))
                binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_GREATER);
            if(matches(LESS))
                binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_LESS);
            if(matches(GREATER_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_GREATER_EQUAL);
            if(matches(LESS_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_LESS_EQUAL);
            if(matches(SPACESHIP))
                binaryExpression.setKind(BinaryExpression.Kind.COMPARISON_SPACESHIP);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionRelational());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionRelational() {
        Expression expression = parseExpressionShift();

        //Parse relational expression
        while(isMatching(TYPE_EQUAL) || isMatching(TYPE_NOT_EQUAL)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(TYPE_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.TYPE_EQUAL);
            if(matches(TYPE_NOT_EQUAL))
                binaryExpression.setKind(BinaryExpression.Kind.TYPE_NOT_EQUAL);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseQualifiedName());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionShift() {
        Expression expression = parseExpressionTerm();

        //Parse bit-shift expression
        while(isMatching(SHIFT_LEFT) || isMatching(SHIFT_RIGHT) || isMatching(SHIFT_RIGHT_ARITHMETIC)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(SHIFT_LEFT))
                binaryExpression.setKind(BinaryExpression.Kind.BITWISE_SHIFT_LEFT);
            if(matches(SHIFT_RIGHT))
                binaryExpression.setKind(BinaryExpression.Kind.BITWISE_SHIFT_RIGHT);
            if(matches(SHIFT_RIGHT_ARITHMETIC))
                binaryExpression.setKind(BinaryExpression.Kind.BITWISE_SHIFT_RIGHT_ARITHMETIC);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionTerm());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionTerm() {
        Expression expression = parseExpressionFactor();

        //Parse term expression
        while(isMatching(ADD) || isMatching(SUB)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(ADD))
                binaryExpression.setKind(BinaryExpression.Kind.OPERATION_ADDITION);
            if(matches(SUB))
                binaryExpression.setKind(BinaryExpression.Kind.OPERATION_SUBTRACTION);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionFactor());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionFactor() {
        Expression expression = parseExpressionUnary();

        //Parse factor expression
        while(isMatching(MUL) || isMatching(DIV) || isMatching(MOD)) {
            BinaryExpression binaryExpression = new BinaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(MUL))
                binaryExpression.setKind(BinaryExpression.Kind.OPERATION_MULTIPLICATION);
            if(matches(DIV))
                binaryExpression.setKind(BinaryExpression.Kind.OPERATION_DIVISION);
            if(matches(MOD))
                binaryExpression.setKind(BinaryExpression.Kind.OPERATION_MODULO);

            binaryExpression.setFirst(expression);
            binaryExpression.setSecond(parseExpressionUnary());
            expression = binaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionUnary() {
        //Parse unary expression
        if(isMatching(SUB) || isMatching(LOGICAL_NOT) || isMatching(BITWISE_NOT)
                || isMatching(INCREMENT) || isMatching(DECREMENT)) {
            UnaryExpression unaryExpression = new UnaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(SUB))
                unaryExpression.setType(UnaryExpression.Kind.OPERATION_NEGATE);
            if(matches(LOGICAL_NOT))
                unaryExpression.setType(UnaryExpression.Kind.LOGICAL_NOT);
            if(matches(BITWISE_NOT))
                unaryExpression.setType(UnaryExpression.Kind.BITWISE_NOT);
            if(matches(INCREMENT))
                unaryExpression.setType(UnaryExpression.Kind.PRE_INCREMENT);
            if(matches(DECREMENT))
                unaryExpression.setType(UnaryExpression.Kind.PRE_DECREMENT);

            unaryExpression.setExpression(parseExpressionUnary());

            return unaryExpression;
        } else {
            return parseExpressionPostUnary();
        }
    }

    private Expression parseExpressionPostUnary() {
        Expression expression = parseExpressionCast();

        //Parse post-unary expression
        if(isMatching(INCREMENT) || isMatching(DECREMENT)) {
            UnaryExpression unaryExpression = new UnaryExpression(Node.Meta.fromLeadingToken(currentToken));

            if(matches(INCREMENT))
                unaryExpression.setType(UnaryExpression.Kind.POST_INCREMENT);
            if(matches(DECREMENT))
                unaryExpression.setType(UnaryExpression.Kind.POST_DECREMENT);

            unaryExpression.setExpression(expression);
            expression = unaryExpression;
        }

        return expression;
    }

    private Expression parseExpressionCast() {
        Expression expression = parseExpressionPrimary();

        //Parse casting expression
        while(matches(COLON)) {
            CastExpression castExpression = new CastExpression(Node.Meta.fromLeadingToken(currentToken));
            castExpression.setExpression(expression);
            castExpression.setCastType(parseType());
            expression = castExpression;
        }

        return expression;
    }

    private Expression parseExpressionPrimary() {
        //Parse literal boolean expression
        if(isMatching(EXPRESSION_TRUE) || isMatching(EXPRESSION_FALSE)) {
            Expression expression = new Literal.Boolean(Node.Meta.fromLeadingToken(currentToken), isMatching(EXPRESSION_TRUE));
            nextToken();

            return expression;
        }

        //Parse literal numeric expression
        if(isMatchingType(Token.Type.LITERAL_NUMERIC)) {
            NumericParser numericParser = new NumericParser(currentToken);
            Expression expression = numericParser.parse();
            nextToken();

            return expression;
        }

        //Parse literal character expression
        if(isMatchingType(Token.Type.LITERAL_CHAR)) {
            CharParser charParser = new CharParser(currentToken);
            Expression expression = charParser.parse();
            nextToken();

            return expression;
        }

        //Parse literal null expression
        if(matches(EXPRESSION_NULL))
            return new Literal.Null(Node.Meta.fromLeadingToken(currentToken));

        //Parse primitive attribute
        if(isPrimitiveKeyword(currentToken) && !isMatching(PRIMITIVE_BOOLEAN))
            return parsePrimitiveAttribute();

        return parseExpressionMemberAccess();
    }

    private Expression parseExpressionMemberAccess() {
        Expression expression = parseExpressionMember();

        //Parse member access, array access or method call
        while(isMatching(DOT) || isMatching(OPENING_SQUARE_BRACKET) || isMatching(OPENING_PARENTHESIS)) {
            //Parse member access
            if(matches(DOT)) {
                //Retrieve next token
                Token nextToken = peekToken();

                if((expression instanceof QualifiedName || expression instanceof SimpleName)
                        && isMatchingType(Token.Type.IDENTIFIER)
                        && !isSameString(nextToken.getContent(), OPENING_PARENTHESIS)) {
                    //Parse qualified name
                    QualifiedName qualifiedName = new QualifiedName(Node.Meta.fromLeadingToken(currentToken));
                    qualifiedName.setQualifiedName(expression);
                    qualifiedName.setName(currentToken.getContent());
                    expression = qualifiedName;
                    nextToken();
                } else {
                    //Parse member access
                    MemberAccess memberAccess = new MemberAccess(Node.Meta.fromLeadingToken(currentToken));
                    memberAccess.setMember(expression);
                    memberAccess.setAccessor(parseExpressionAccess());
                    expression = memberAccess;
                }
            }

            //Parse array access
            else if(isMatching(OPENING_SQUARE_BRACKET)) {
                //Check if start of array declaration
                if(isSameString(peekToken().getContent(), CLOSING_SQUARE_BRACKET))
                    return expression;

                //Match opening bracket
                match(OPENING_SQUARE_BRACKET);

                //Parse array access
                ArrayAccess arrayAccess = new ArrayAccess(Node.Meta.fromLeadingToken(currentToken));
                arrayAccess.setArray(expression);
                arrayAccess.setAccessExpression(parseExpression());
                expression = arrayAccess;

                //Match closing bracket
                match(CLOSING_SQUARE_BRACKET);
            }

            //Parse method call
            else if(matches(OPENING_PARENTHESIS)) {
                //Parse method call
                MethodCall methodCall = new MethodCall(Node.Meta.fromLeadingToken(currentToken));
                methodCall.setMethod(expression);
                methodCall.setArgumentList(parseArgumentList());
                expression = methodCall;
                nextToken();
            }
        }

        return expression;
    }

    private Expression parseExpressionMember() {
        //Parse expression in parentheses
        if(matches(OPENING_PARENTHESIS)) {
            Expression expression = parseExpression();
            match(CLOSING_PARENTHESIS);

            return expression;
        }

        //Parse literal string expression
        if(isMatchingType(Token.Type.LITERAL_STRING)) {
            StringParser stringParser = new StringParser(currentToken);
            Expression expression = stringParser.parse();
            nextToken();

            return expression;
        }

        //Parse literal array expression
        if(isMatching(OPENING_SQUARE_BRACKET))
            return parseArrayLiteral();

        //Parse this expression
        if(isMatching(EXPRESSION_THIS))
            return parseExpressionThis();

        //Parse super expression
        if(isMatching(EXPRESSION_SUPER))
            return parseExpressionSuper();

        //Parse outer expression
        if(isMatching(EXPRESSION_OUTER))
            return parseExpressionOuter();

        return parseExpressionAccess();
    }

    private Expression parseExpressionAccess() {
        //Parse construction expression
        if(isMatching(CLASS_NEW))
            return parseExpressionCreation();

        //Throw an error if the token is not a valid identifier
        if(!isMatchingType(Token.Type.IDENTIFIER))
            new ParsingError.UnexpectedToken(currentToken);

        return parseIdentifier();
    }

    private Expression parseIdentifier() {
        //Parse method call
        if(Character.isSameString(peekToken().getContent(), OPENING_PARENTHESIS))
            return parseMethodCall();

        //Parse identifier
        SimpleName expression = new SimpleName(Node.Meta.fromLeadingToken(currentToken));
        expression.setName(currentToken);
        nextToken();

        return expression;
    }

    private Expression parseMethodCall() {
        MethodCall expression = new MethodCall(Node.Meta.fromLeadingToken(currentToken));

        //Parse method name
        expression.setMethodName(currentToken);
        nextToken();

        //Parse arguments list
        match(OPENING_PARENTHESIS);
        expression.setArgumentList(parseArgumentList());
        match(CLOSING_PARENTHESIS);

        return expression;
    }

    private Expression parseExpressionThis() {
        //Match this keyword
        match(EXPRESSION_THIS);

        return new ThisExpression(Node.Meta.fromLeadingToken(currentToken));
    }

    private Expression parseExpressionSuper() {
        //Match super keyword
        match(EXPRESSION_SUPER);

        return new SuperExpression(Node.Meta.fromLeadingToken(currentToken));
    }

    private Expression parseExpressionOuter() {
        //Match outer keyword
        match(EXPRESSION_OUTER);

        return new OuterExpression(Node.Meta.fromLeadingToken(currentToken));
    }

    private Expression parseExpressionCreation() {
        //Match new keyword
        match(CLASS_NEW);

        //Parse type if there is one
        Node type = null;
        if(isPrimitiveKeyword(currentToken)
                || isMatchingType(Token.Type.IDENTIFIER))
            type = parseTypeName();

        //Parse constructor arguments
        if(isMatching(OPENING_PARENTHESIS) || isMatching(OPENING_SQUARE_BRACKET)) {
            //Parse class creation
            if(matches(OPENING_PARENTHESIS)) {
                ClassCreation expression = new ClassCreation(Node.Meta.fromLeadingToken(currentToken));
                expression.setType(type);
                expression.setArgumentList(parseArgumentList());

                //Match closing parenthesis
                match(CLOSING_PARENTHESIS);

                return expression;
            }

            //Parse array creation
            if(matches(OPENING_SQUARE_BRACKET)) {
                ArrayCreation expression = new ArrayCreation(Node.Meta.fromLeadingToken(currentToken));
                expression.setType(type);

                //Parse array initialization
                expression.setInitializationExpression(parseExpression());

                //Match closing bracket
                match(CLOSING_SQUARE_BRACKET);

                return expression;
            }
        } else new ParsingError.UnexpectedToken(currentToken);

        return null;
    }

    private Expression parseArrayLiteral() {
        Literal.Array expression = new Literal.Array(Node.Meta.fromLeadingToken(currentToken));

        //Match opening bracket
        match(OPENING_SQUARE_BRACKET);

        //Parse elements
        do {
            expression.addElement(parseExpression());
        } while(matches(COMMA));

        //Match closing bracket
        match(CLOSING_SQUARE_BRACKET);

        return expression;
    }

    private Expression parsePrimitiveAttribute() {
        PrimitiveAttribute expression = new PrimitiveAttribute(Node.Meta.fromLeadingToken(currentToken));

        //Parse primitive type
        if(isPrimitiveKeyword(currentToken) && !isMatching(PRIMITIVE_BOOLEAN)) {
            if(matches(PRIMITIVE_BYTE))
                expression.setPrimitiveKind(Primitive.Kind.BYTE);
            if(matches(PRIMITIVE_SHORT))
                expression.setPrimitiveKind(Primitive.Kind.SHORT);
            if(matches(PRIMITIVE_CHAR))
                expression.setPrimitiveKind(Primitive.Kind.CHAR);
            if(matches(PRIMITIVE_INTEGER))
                expression.setPrimitiveKind(Primitive.Kind.INTEGER);
            if(matches(PRIMITIVE_LONG))
                expression.setPrimitiveKind(Primitive.Kind.LONG);
            if(matches(PRIMITIVE_FLOAT))
                expression.setPrimitiveKind(Primitive.Kind.FLOAT);
            if(matches(PRIMITIVE_DOUBLE))
                expression.setPrimitiveKind(Primitive.Kind.DOUBLE);
        } else new ParsingError.UnexpectedToken(currentToken);

        //Match dot
        match(DOT);

        //Parse attribute
        if(isAttribute(currentToken)) {
            if(matches(BITS))
                expression.setKind(PrimitiveAttribute.Kind.BITS);
            if(matches(BYTES))
                expression.setKind(PrimitiveAttribute.Kind.BYTES);
            if(matches(MINIMUM))
                expression.setKind(PrimitiveAttribute.Kind.MINIMUM);
            if(matches(MAXIMUM))
                expression.setKind(PrimitiveAttribute.Kind.MAXIMUM);
        } else new ParsingError.UnexpectedToken(currentToken);

        return expression;
    }

    private Expression parseQualifiedName() {
        Expression expression = parseSimpleName();

        //Parse qualified name
        while(matches(DOT)) {
            //Make sure there is an identifier after the dot
            if(!isMatchingType(Token.Type.IDENTIFIER))
                new ParsingError.UnexpectedToken(currentToken);

            QualifiedName qualifiedName = new QualifiedName(Node.Meta.fromLeadingToken(currentToken));
            qualifiedName.setQualifiedName(expression);
            qualifiedName.setName(currentToken.getContent());
            expression = qualifiedName;
            nextToken();
        }

        return expression;
    }

    private Expression parseSimpleName() {
        SimpleName expression = new SimpleName(Node.Meta.fromLeadingToken(currentToken));

        //Parse simple name
        if(isMatchingType(Token.Type.IDENTIFIER)) {
            expression.setName(currentToken);
            nextToken();
        } else new ParsingError.UnexpectedToken(currentToken);

        return expression;
    }

    private Node parseType() {
        Node node = parseTypeName();

        //Parse array type
        while(matches(OPENING_SQUARE_BRACKET)) {
            ArrayType arrayType = new ArrayType(Node.Meta.fromLeadingToken(currentToken));
            arrayType.setType(node);
            node = arrayType;

            match(CLOSING_SQUARE_BRACKET);
        }

        return node;
    }

    private Node parseTypeName() {
        //Parse primitive type
        if(isPrimitiveKeyword(currentToken)) {
            PrimitiveType node = new PrimitiveType(Node.Meta.fromLeadingToken(currentToken));
            node.setKind(currentToken);
            nextToken();

            return node;
        }

        return parseQualifiedName();
    }



    //Other expression and statement parsing

    private Node parseArgumentList() {
        ArgumentList node = new ArgumentList(Node.Meta.fromLeadingToken(currentToken));

        //Return empty argument list
        if(isMatching(CLOSING_PARENTHESIS))
            return node;

        //Parse arguments
        do {
            node.addArgument(parseExpression());
        } while(matches(COMMA));

        return node;
    }

    private Node parseParameterList() {
        ParameterList node = new ParameterList(Node.Meta.fromLeadingToken(currentToken));

        //Return empty parameter list
        if(isMatching(CLOSING_PARENTHESIS))
            return node;

        //Parse parameter list
        do {
            node.addParameter(parseParameter());
        } while(matches(COMMA));

        return node;
    }

    private Node parseParameter() {
        Parameter node = new Parameter(Node.Meta.fromLeadingToken(currentToken));

        //Check if parameter is constant
        if(matches(VAR_CONST))
            node.setConstant();

        //Parse parameter type
        node.setType(parseType());

        //Parse parameter name
        if(isMatchingType(Token.Type.IDENTIFIER)) {
            node.setName(currentToken);
            nextToken();
        } else new ParsingError.UnexpectedToken(currentToken);

        return node;
    }
}
