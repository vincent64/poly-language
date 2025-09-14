package poly.compiler.analyzer;

import poly.compiler.analyzer.content.OperatorMethod;
import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.analyzer.table.Variable;
import poly.compiler.analyzer.table.VariableTable;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Void;
import poly.compiler.analyzer.type.*;
import poly.compiler.error.AnalyzingError;
import poly.compiler.parser.tree.*;
import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.statement.*;
import poly.compiler.parser.tree.variable.*;
import poly.compiler.resolver.ClassDefinition;
import poly.compiler.resolver.Classes;
import poly.compiler.resolver.LibraryClasses;
import poly.compiler.resolver.symbol.*;
import poly.compiler.util.ClassName;
import poly.compiler.warning.AnalyzingWarning;

import java.util.List;

/**
 * The Analyzer class. This class is used to analyze the AST produced by the parser.
 * This step includes determining the type of the expressions, making sure they are valid,
 * building the local variables table, transforming primitive operations involving objects
 * into operator overloaded method calls, making sure statements and expressions are
 * correctly used, and much more. Most compile-time errors will come from this class,
 * as its use is precisely to analyze and make sure everything is valid.
 * This class can, and in the vast majority of cases, will change the provided AST in order
 * to make sure the code generation phase will have the sole prupose of generating the code.
 * However, the analyzer will not optimize the code. If the optimization flag is enabled,
 * a second traversal of the AST will be performed by the Optimizer class to optimize it.
 * @author Vincent Philippe (@vincent64)
 */
public final class Analyzer implements NodeModifier {
    private final Transformer transformer;
    private final ImportTable importTable;
    private final ClassDeclaration classDeclaration;
    private final ClassSymbol classSymbol;
    private final VariableTable variableTable;
    private MethodDeclaration currentMethod;
    private int currentLoopLevel;
    private boolean isInitialized;
    private boolean isStaticContext;

    private Analyzer(ImportTable importTable, ClassDefinition classDefinition) {
        this.importTable = importTable;

        classDeclaration = classDefinition.getClassDeclaration();
        classSymbol = classDefinition.getClassSymbol();
        isInitialized = true;

        //Initialize transformer
        transformer = new Transformer(this, classSymbol);

        //Initialize variables table
        variableTable = new VariableTable();
    }

    public static Analyzer getInstance(ImportTable importTable, ClassDefinition classDefinition) {
        return new Analyzer(importTable, classDefinition);
    }

    /**
     * Analyzes the AST.
     */
    public void analyze() {
        //Visit the class declaration
        classDeclaration.accept(this);
    }

    @Override
    public Node visitClassDeclaration(ClassDeclaration classDeclaration) {
        //Visit every field
        for(Node node : classDeclaration.getFields())
            node.accept(this);

        //Visit every method
        for(Node node : classDeclaration.getMethods())
            node.accept(this);

        //Visit every enum constant
        if(classDeclaration.isEnum())
            classDeclaration.setConstantList(classDeclaration.getConstantList().accept(this));

        return classDeclaration;
    }

    @Override
    public Node visitFieldDeclaration(FieldDeclaration fieldDeclaration) {
        isStaticContext = fieldDeclaration.isStatic() || classSymbol.isStatic();

        //Clear local variables table
        variableTable.clear();

        //Visit field declaration
        fieldDeclaration.setVariable(fieldDeclaration.getVariable().accept(this));

        return fieldDeclaration;
    }

    @Override
    public Node visitMethodDeclaration(MethodDeclaration methodDeclaration) {
        currentMethod = methodDeclaration;
        isStaticContext = methodDeclaration.isStatic() || classSymbol.isStatic();
        isInitialized = true;

        //Make sure the method is empty if class is interface
        if(!classDeclaration.isInterface() && methodDeclaration.isEmpty())
            new AnalyzingError.MissingMethodBody(methodDeclaration);

        //Nothing to analyze if empty
        if(methodDeclaration.isEmpty())
            return methodDeclaration;

        //Clear local variables table
        variableTable.clear();

        //Visit parameters list
        methodDeclaration.setParameterList(methodDeclaration.getParameterList().accept(this));

        //Visit method body
        methodDeclaration.setStatementBlock(methodDeclaration.getStatementBlock().accept(this));

        StatementBlock statementBlock = (StatementBlock) methodDeclaration.getStatementBlock();
        List<Statement> statements = statementBlock.getStatements();

        boolean hasReturned = false;
        for(Node statement : statements) {
            if(statement instanceof ReturnStatement) {
                hasReturned = true;
                break;
            }
        }

        //Make sure there is a return statement
        if(methodDeclaration.getReturnType() != null && !hasReturned)
            new AnalyzingError.MissingReturnStatement(methodDeclaration);

        //Skip if method is not constructor or class is an enum
        if(!methodDeclaration.isConstructor()
                || classSymbol.isEnum())
            return methodDeclaration;

        //Check whether the constructor should call super
        boolean shouldCallSuper = classSymbol.getSuperclassSymbol() != null
                && ((ClassSymbol) classSymbol.getSuperclassSymbol())
                    .findConstructor(new Type[0], classSymbol, methodDeclaration) == null;

        //Check whether the constructor has a constructor call
        boolean hasSuperStatement = !statements.isEmpty() && statements.getFirst() instanceof SuperStatement;
        boolean hasThisStatement = !statements.isEmpty() && statements.getFirst() instanceof ThisStatement;

        //Make sure there is a constructor call
        if(shouldCallSuper && !hasSuperStatement && !hasThisStatement)
            new AnalyzingError.MissingConstructorCall(methodDeclaration);

        //Add implicit super constructor call
        if(!shouldCallSuper && !hasThisStatement && !hasSuperStatement) {
            SuperStatement superStatement = new SuperStatement(methodDeclaration.getMeta());
            superStatement.setArgumentList(new ArgumentList(methodDeclaration.getMeta()));
            statementBlock.addFirstStatement(superStatement);
        }

        return methodDeclaration;
    }

    @Override
    public Statement visitVariableDeclaration(VariableDeclaration variableDeclaration) {
        //Make sure the variable name is not already used
        if(variableTable.isAlreadyDefined(variableDeclaration.getName()))
            new AnalyzingError.DuplicateVariable(variableDeclaration);

        Node type = variableDeclaration.getType();
        Type variableType = getTypeFromNode(variableDeclaration.getType());

        //Make sure the type is valid
        if(variableType == null)
            new AnalyzingError.UnresolvableClass(type, type.toString());

        //Make sure the type is accessible if it is an object
        if(variableType instanceof Object object
                && !object.getClassSymbol().isAccessibleFrom(classSymbol))
            new AnalyzingError.UnresolvableClass(type, type.toString());

        Expression expression = variableDeclaration.getInitializationExpression();

        if(expression != null) {
            //Infer type if possible
            inferType(expression, variableType);

            //Visit initialization expression
            variableDeclaration.setInitializationExpression(expression.accept(this));
            expression = variableDeclaration.getInitializationExpression();

            //Make sure the type is not a primitive if the expression is null
            if(isNullExpression(expression)) {
                if(variableType.getKind() == Type.Kind.PRIMITIVE)
                    new AnalyzingError.TypeConversion(expression,
                            expression.getExpressionType(), variableType);
            }

            //Make sure the expression type is assignable to the variable type
            else if(!expression.getExpressionType().isAssignableTo(variableType))
                new AnalyzingError.TypeConversion(expression,
                        expression.getExpressionType(), variableType);
        }

        //Add the variable to the table
        Variable variable = variableTable.addVariable(variableType,
                variableDeclaration.getName(),
                variableDeclaration.isConstant());

        //Set the variable as assigned
        if(expression != null)
            variable.setAsAssigned();

        return variableDeclaration;
    }



    // Analyze statements

    @Override
    public Statement visitStatementBlock(StatementBlock statementBlock) {
        //Get the amount of previous local variables
        int previousVariableCount = variableTable.getVariableCount();

        List<Statement> statements = statementBlock.getStatements();

        //Warn if statement block is empty
        if(statements.isEmpty())
            new AnalyzingWarning.EmptyBody(statementBlock);

        //Visit every statement
        boolean hasJumped = false;
        for(int i = 0; i < statements.size(); i++) {
            //Make sure there is no statement after the jump
            if(hasJumped)
                new AnalyzingError.UnreachableStatement(statements.get(i));

            //Visit statement
            statements.set(i, statements.get(i).accept(this));

            Node statement = statements.get(i);
            hasJumped = statement instanceof ReturnStatement
                    || statement instanceof BreakStatement
                    || statement instanceof ContinueStatement
                    || statement instanceof ThrowStatement;
        }

        //Remove local variables added in this statement block
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        return statementBlock;
    }

    @Override
    public Statement visitIfStatement(IfStatement ifStatement) {
        //Visit condition expression
        ifStatement.setCondition(ifStatement.getCondition().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(ifStatement.getCondition());

        //Visit statement body
        ifStatement.setStatementBlock(ifStatement.getStatementBlock().accept(this));

        //Visit else statement body
        if(ifStatement.getElseStatementBlock() != null)
            ifStatement.setElseStatementBlock(ifStatement.getElseStatementBlock().accept(this));

        return ifStatement;
    }

    @Override
    public Statement visitForStatement(ForStatement forStatement) {
        //Get the amount of previous local variables
        int previousVariableCount = variableTable.getVariableCount();

        //Visit expressions and statement
        forStatement.setStatement(forStatement.getStatement().accept(this));
        forStatement.setCondition(forStatement.getCondition().accept(this));
        forStatement.setExpression(forStatement.getExpression().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(forStatement.getCondition());

        //Visit statement body
        currentLoopLevel++;
        forStatement.setStatementBlock(forStatement.getStatementBlock().accept(this));
        currentLoopLevel--;

        //Remove local variables added in this for-statement
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        return forStatement;
    }

    @Override
    public Statement visitWhileStatement(WhileStatement whileStatement) {
        //Visit condition expression
        whileStatement.setCondition(whileStatement.getCondition().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(whileStatement.getCondition());

        //Visit statement body
        currentLoopLevel++;
        whileStatement.setStatementBlock(whileStatement.getStatementBlock().accept(this));
        currentLoopLevel--;

        return whileStatement;
    }

    @Override
    public Statement visitDoStatement(DoStatement doStatement) {
        //Visit condition expression
        doStatement.setCondition(doStatement.getCondition().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(doStatement.getCondition());

        //Visit statement block
        currentLoopLevel++;
        doStatement.setStatementBlock(doStatement.getStatementBlock().accept(this));
        currentLoopLevel--;

        return doStatement;
    }

    @Override
    public Statement visitSwitchStatement(SwitchStatement switchStatement) {
        //Visit expression
        switchStatement.setExpression(switchStatement.getExpression().accept(this));

        Expression expression = switchStatement.getExpression();

        //Make sure the expression is valid
        if(!(expression.getExpressionType() instanceof Primitive primitive && primitive.isIntegerType()))
            new AnalyzingError.InvalidSwitchExpression(expression);

        List<Statement> cases = switchStatement.getCases();

        //Visit every case statement
        for(int i = 0; i < cases.size(); i++) {
            //Visit case statement
            cases.set(i, cases.get(i).accept(this));

            CaseStatement caseStatement = (CaseStatement) cases.get(i);
            Expression caseExpression = caseStatement.getExpression();

            //Make sure the case expression is constant
            if(!isConstantExpression(caseExpression))
                new AnalyzingError.ExpectedConstantExpression(caseExpression);

            //Make sure the case expression type is valid
            if(!caseExpression.getExpressionType().equals(expression.getExpressionType()))
                new AnalyzingError.TypeConversion(caseExpression,
                        caseExpression.getExpressionType(), expression.getExpressionType());
        }

        //Visit optional else case statement
        if(switchStatement.getElseCase() != null)
            switchStatement.setElseCase(switchStatement.getElseCase().accept(this));

        return switchStatement;
    }

    @Override
    public Statement visitMatchStatement(MatchStatement matchStatement) {
        List<Statement> cases = matchStatement.getCases();

        //Visit every case statement
        for(int i = 0; i < cases.size(); i++) {
            //Visit case statement
            cases.set(i, cases.get(i).accept(this));

            //Make sure the case expression type is a boolean expression
            matchBooleanExpression(((CaseStatement) cases.get(i)).getExpression());
        }

        //Visit optional else case statement
        if(matchStatement.getElseCase() != null)
            matchStatement.setElseCase(matchStatement.getElseCase().accept(this));

        return matchStatement;
    }

    @Override
    public Statement visitAssertStatement(AssertStatement assertStatement) {
        //Visit condition expression
        assertStatement.setCondition(assertStatement.getCondition().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(assertStatement.getCondition());

        //Analyze exception from expression
        if(assertStatement.getExceptionExpression() != null) {
            //Visit exception expression
            assertStatement.setExceptionExpression(assertStatement.getExceptionExpression().accept(this));

            Expression exceptionExpression = assertStatement.getExceptionExpression();
            Type type = exceptionExpression.getExpressionType();

            //Make sure the expression type is a subclass of throwable
            if(!isThrowableExpression(exceptionExpression))
                new AnalyzingError.TypeConversion(exceptionExpression, type, new Object(LibraryClasses.findClass(ClassName.THROWABLE)));
        }

        //Analyze assertion exception
        else {
            ClassSymbol classSymbol = LibraryClasses.findClass(ClassName.ASSERTION_ERROR);

            //Make sure the string class exists
            if(classSymbol == null)
                new AnalyzingError.UnresolvableClass(assertStatement, ClassName.ASSERTION_ERROR.toString());
        }

        return assertStatement;
    }

    @Override
    public Statement visitTryStatement(TryStatement tryStatement) {
        //Visit statement block
        tryStatement.setStatementBlock(tryStatement.getStatementBlock().accept(this));

        //Get the amount of previous local variables
        int previousVariableCount = variableTable.getVariableCount();

        //Visit exception parameter
        tryStatement.setExceptionParameter(tryStatement.getExceptionParameter().accept(this));

        ClassSymbol classSymbol = LibraryClasses.findClass(ClassName.THROWABLE);

        //Make sure the throwable class exists
        if(classSymbol == null)
            new AnalyzingError.UnresolvableClass(tryStatement, ClassName.THROWABLE.toString());

        Parameter parameter = (Parameter) tryStatement.getExceptionParameter();
        Type type = getTypeFromNode(parameter.getType());

        //Make sure the exception type is a subclass of throwable
        if(!(type instanceof Object object && object.getClassSymbol().isSubtypeOf(classSymbol)))
            new AnalyzingError.TypeConversion(parameter, type, new Object(classSymbol));

        //Visit catch statement block
        tryStatement.setCatchStatementBlock(tryStatement.getCatchStatementBlock().accept(this));

        //Remove local variables added in this try-statement
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        return tryStatement;
    }

    @Override
    public Statement visitThrowStatement(ThrowStatement throwStatement) {
        //Visit throw expression
        throwStatement.setExpression(throwStatement.getExpression().accept(this));

        Expression expression = throwStatement.getExpression();
        Type type = expression.getExpressionType();

        //Make sure the expression type is a subclass of throwable
        if(!isThrowableExpression(expression))
            new AnalyzingError.TypeConversion(expression, type, new Object(LibraryClasses.findClass(ClassName.THROWABLE)));

        return throwStatement;
    }

    @Override
    public Statement visitReturnStatement(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();

        //Make sure the return statement returns nothing when not expected
        if(expression != null && currentMethod.getReturnType() == null)
            new AnalyzingError.UnexpectedReturnValue(returnStatement);

        //Make sure the return statement returns something when expected
        if(expression == null && currentMethod.getReturnType() != null)
            new AnalyzingError.MissingReturnValue(returnStatement);

        if(expression != null) {
            Type returnType = getTypeFromNode(currentMethod.getReturnType());

            //Infer type if possible
            inferType(expression, returnType);

            //Visit return expression
            returnStatement.setExpression(returnStatement.getExpression().accept(this));

            expression = returnStatement.getExpression();

            Type expressionType = expression.getExpressionType();

            //Make sure the expression type is valid if null
            if(isNullExpression(expression)) {
                if(returnType.getKind() != Type.Kind.OBJECT)
                    new AnalyzingError.TypeConversion(expression,
                            expressionType, returnType);

                return returnStatement;
            }

            //Make sure the expression type is assignable to the variable type
            if(!expression.getExpressionType().isAssignableTo(returnType))
                new AnalyzingError.TypeConversion(expression,
                        expression.getExpressionType(), returnType);
        }

        return returnStatement;
    }

    @Override
    public Statement visitBreakStatement(BreakStatement breakStatement) {
        //Make sure the statement is used inside a loop
        if(currentLoopLevel == 0)
            new AnalyzingError.BreakOutsideLoop(breakStatement);

        return breakStatement;
    }

    @Override
    public Statement visitContinueStatement(ContinueStatement continueStatement) {
        //Make sure the statement is used inside a loop
        if(currentLoopLevel == 0)
            new AnalyzingError.ContinueOutsideLoop(continueStatement);

        return continueStatement;
    }

    @Override
    public Statement visitThisStatement(ThisStatement thisStatement) {
        //Make sure the statement is used inside a constructor
        if(!currentMethod.isConstructor())
            new AnalyzingError.InvalidConstructorCall(thisStatement);

        //Visit argument list
        isInitialized = false;
        thisStatement.setArgumentList(thisStatement.getArgumentList().accept(this));
        isInitialized = true;

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) thisStatement.getArgumentList());

        //Find constructor in current class
        MethodSymbol constructor = classSymbol.findConstructor(argumentTypes, classSymbol, thisStatement);

        //Make sure there is a valid constructor
        if(constructor == null)
            new AnalyzingError.UnresolvableConstructor(thisStatement, argumentTypes);


        return thisStatement;
    }

    @Override
    public Statement visitSuperStatement(SuperStatement superStatement) {
        //Make sure the statement is used inside a constructor
        if(!currentMethod.isConstructor())
            new AnalyzingError.InvalidConstructorCall(superStatement);

        //Make sure the class is not an enum
        if(classSymbol.isEnum())
            new AnalyzingError.InvalidEnumSuperCall(superStatement);

        //Visit argument list
        isInitialized = false;
        superStatement.setArgumentList(superStatement.getArgumentList().accept(this));
        isInitialized = true;

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) superStatement.getArgumentList());

        ClassSymbol superclassSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

        //Make sure the class has a superclass
        if(classSymbol.isRoot())
            new AnalyzingError.UnknownSuperReference(superStatement);

        //Find constructor in superclass
        MethodSymbol constructor = superclassSymbol.findConstructor(argumentTypes, classSymbol, superStatement);

        //Make sure there is a valid constructor
        if(constructor == null)
            new AnalyzingError.UnresolvableConstructor(superStatement, argumentTypes);

        return superStatement;
    }

    @Override
    public Statement visitExpressionStatement(ExpressionStatement expressionStatement) {
        //Visit expression
        expressionStatement.setExpression(expressionStatement.getExpression().accept(this));

        Expression expression = expressionStatement.getExpression();

        //Make sure the expression is a valid statement
        if(!isExpressionStatement(expression))
            new AnalyzingError.NotAStatement(expressionStatement);

        return expressionStatement;
    }



    // Analyze expressions

    @Override
    public Expression visitLiteral(Literal literal) {
        //Define literal expression type
        if(literal instanceof Literal.Boolean)
            literal.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
        else if(literal instanceof Literal.Integer)
            literal.setExpressionType(new Primitive(Primitive.Kind.INTEGER));
        else if(literal instanceof Literal.Long)
            literal.setExpressionType(new Primitive(Primitive.Kind.LONG));
        else if(literal instanceof Literal.Float)
            literal.setExpressionType(new Primitive(Primitive.Kind.FLOAT));
        else if(literal instanceof Literal.Double)
            literal.setExpressionType(new Primitive(Primitive.Kind.DOUBLE));
        else if(literal instanceof Literal.Char)
            literal.setExpressionType(new Primitive(Primitive.Kind.CHAR));
        else if(literal instanceof Literal.Null)
            literal.setExpressionType(Object.NULL_REFERENCE);

        //Analyze string literal
        else if(literal instanceof Literal.String) {
            ClassSymbol classSymbol = LibraryClasses.findClass(ClassName.STRING);

            //Make sure the string class exists
            if(classSymbol == null)
                new AnalyzingError.UnresolvableClass(literal, ClassName.STRING.toString());

            literal.setExpressionType(new Object(classSymbol));
        }

        //Analyze array literal
        else if(literal instanceof Literal.Array array) {
            //Visit every element
            List<Expression> elements = array.getElements();
            for(int i = 0; i < elements.size(); i++)
                elements.set(i, elements.get(i).accept(this));

            Type type = elements.getFirst().getExpressionType();

            for(int i = 1; i < elements.size(); i++) {
                Expression element = elements.get(i);

                //Make sure the elements have the same type
                if(!type.equals(element.getExpressionType()))
                    new AnalyzingError.TypeConversion(element, element.getExpressionType(), type);
            }

            //Define resulting type
            array.setExpressionType(new Array(type));
        }

        return literal;
    }

    @Override
    public Expression visitUnaryExpression(UnaryExpression unaryExpression) {
        //Visit the expression
        unaryExpression.setExpression(unaryExpression.getExpression().accept(this));

        Expression expression = unaryExpression.getExpression();

        //Make sure the expression is not void
        if(isVoidExpression(expression))
            new AnalyzingError.InvalidUnaryOperation(unaryExpression);

        //Analyze operation kind and expression type
        if(expression.getExpressionType() instanceof Primitive primitive) {
            //Widen the narrow integer type
            if(primitive.isNarrowIntegerType())
                primitive = new Primitive(Primitive.Kind.INTEGER);

            switch(unaryExpression.getKind()) {
                case PRE_INCREMENT, POST_INCREMENT, PRE_DECREMENT, POST_DECREMENT -> {
                    //Make sure the type is numerical
                    if(!primitive.isNumericalType())
                        new AnalyzingError.InvalidUnaryOperation(unaryExpression);

                    //Make sure the variable is a variable expression
                    if(!isVariableExpression(expression))
                        new AnalyzingError.ExpectedVariableExpression(expression);

                    //Visit variable expression
                    visitVariableExpression(expression);

                    //Define resulting type
                    unaryExpression.setExpressionType(expression.getExpressionType());
                }

                case OPERATION_NEGATE -> {
                    //Make sure the type is numerical
                    if(!primitive.isNumericalType())
                        new AnalyzingError.InvalidUnaryOperation(unaryExpression);

                    //Define resulting type
                    unaryExpression.setExpressionType(expression.getExpressionType());
                }

                case LOGICAL_NOT -> {
                    //Make sure the type is boolean
                    if(!primitive.isBooleanType())
                        new AnalyzingError.InvalidUnaryOperation(unaryExpression);

                    //Define resulting type
                    unaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
                }

                case BITWISE_NOT -> {
                    //Make sure the type is integer
                    if(!primitive.isIntegerType())
                        new AnalyzingError.InvalidUnaryOperation(unaryExpression);

                    //Define resulting type
                    unaryExpression.setExpressionType(primitive);
                }
            }
        }

        //Analyze object operation
        else if(expression.getExpressionType() instanceof Object) {
            return visitUnaryOperationOverload(unaryExpression);
        }

        else new AnalyzingError.InvalidUnaryOperation(unaryExpression);

        return unaryExpression;
    }

    @Override
    public Expression visitBinaryExpression(BinaryExpression binaryExpression) {
        //Analyze type equality operation
        if(binaryExpression.getKind() == BinaryExpression.Kind.TYPE_EQUAL
                || binaryExpression.getKind() == BinaryExpression.Kind.TYPE_NOT_EQUAL) {
            //Visit the first expression only
            binaryExpression.setFirst(binaryExpression.getFirst().accept(this));

            Expression first = binaryExpression.getFirst();
            Expression second = binaryExpression.getSecond();

            //Make sure the type to check is an object
            if(first.getExpressionType().getKind() != Type.Kind.OBJECT)
                new AnalyzingError.InvalidBinaryOperation(binaryExpression);

            Type type = getTypeFromNode(second);

            //Make sure the type exists
            if(type == null)
                new AnalyzingError.UnresolvableClass(second, second.toString());

            //Define resulting type
            binaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));

            return binaryExpression;
        }

        //Visit the two expressions
        binaryExpression.setFirst(binaryExpression.getFirst().accept(this));
        binaryExpression.setSecond(binaryExpression.getSecond().accept(this));

        Expression first = binaryExpression.getFirst();
        Expression second = binaryExpression.getSecond();

        //Make sure the expressions are not void
        if(isVoidExpression(first) || isVoidExpression(second))
            new AnalyzingError.InvalidBinaryOperation(binaryExpression);

        //Analyze operation kind and expression types
        if(first.getExpressionType() instanceof Primitive primitive1
                && second.getExpressionType() instanceof Primitive primitive2) {
            //Widen the narrow integer types
            if(primitive1.isNarrowIntegerType())
                primitive1 = new Primitive(Primitive.Kind.INTEGER);
            if(primitive2.isNarrowIntegerType())
                primitive2 = new Primitive(Primitive.Kind.INTEGER);

            switch(binaryExpression.getKind()) {
                case OPERATION_ADDITION, OPERATION_SUBTRACTION, OPERATION_MULTIPLICATION,
                        OPERATION_DIVISION, OPERATION_MODULO -> {
                    //Make sure the types are both numericals
                    if(!primitive1.isNumericalType() || !primitive2.isNumericalType())
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(Primitive.getWidestPrimitiveBetween(primitive1, primitive2));
                }

                case COMPARISON_SPACESHIP -> {
                    //Make sure the types are both numericals
                    if(!primitive1.isNumericalType() || !primitive2.isNumericalType())
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(Primitive.Kind.INTEGER));
                }

                case EQUALITY_EQUAL, EQUALITY_NOT_EQUAL -> {
                    //Make sure the types are both numerical or boolean
                    if((primitive1.isNumericalType() && !primitive2.isNumericalType())
                            || (!primitive1.isNumericalType() && primitive2.isNumericalType()))
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
                }

                case COMPARISON_GREATER, COMPARISON_LESS,
                        COMPARISON_GREATER_EQUAL, COMPARISON_LESS_EQUAL -> {
                    //Make sure the types are both numerical
                    if(!primitive1.isNumericalType() || !primitive2.isNumericalType())
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
                }

                case BITWISE_AND, BITWISE_XOR, BITWISE_OR,
                        BITWISE_SHIFT_LEFT, BITWISE_SHIFT_RIGHT, BITWISE_SHIFT_RIGHT_ARITHMETIC -> {
                    //Make sure the types are integers
                    if(!primitive1.isIntegerType() || !primitive2.isIntegerType())
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(primitive1.getPrimitiveKind()));

                }

                case LOGICAL_AND, LOGICAL_OR -> {
                    //Make sure the types are booleans
                    if(!primitive1.isBooleanType() || !primitive2.isBooleanType())
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
                }

                default ->
                    new AnalyzingError.InvalidBinaryOperation(binaryExpression);
            }
        }

        //Analyze object operation
        else if(first.getExpressionType() instanceof Object || first.getExpressionType() instanceof Array) {
            Type firstType = first.getExpressionType();
            Type secondType = second.getExpressionType();

            switch(binaryExpression.getKind()) {
                case REFERENCE_EQUAL, REFERENCE_NOT_EQUAL -> {
                    //Make sure the second expression is an object
                    if(secondType.getKind() != Type.Kind.OBJECT
                            && secondType.getKind() != Type.Kind.ARRAY)
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(new Primitive(Primitive.Kind.BOOLEAN));
                }

                case COMPARISON_NULL -> {
                    //Make sure the second expression is an object
                    if(secondType.getKind() != Type.Kind.OBJECT
                            && secondType.getKind() != Type.Kind.ARRAY)
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Make sure both objects have the same type
                    if(!firstType.equals(secondType))
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    //Define resulting type
                    binaryExpression.setExpressionType(firstType);
                }

                default -> {
                    //Make sure the operator overload type is not an array
                    if(firstType instanceof Array)
                        new AnalyzingError.InvalidBinaryOperation(binaryExpression);

                    return visitBinaryOperationOverload(binaryExpression);
                }
            }
        }

        else new AnalyzingError.InvalidBinaryOperation(binaryExpression);

        return binaryExpression;
    }

    @Override
    public Expression visitCastExpression(CastExpression castExpression) {
        //Visit cast expression
        castExpression.setExpression(castExpression.getExpression().accept(this));

        Expression expression = castExpression.getExpression();

        //Make sure the expression is not void
        if(isVoidExpression(expression))
            new AnalyzingError.TypeCast(castExpression);

        Type castType = getTypeFromNode(castExpression.getCastType());

        //Make sure the type exists
        if(castType == null)
            new AnalyzingError.UnresolvableType(castExpression.getCastType());

        //Analyze primitive casting
        if(expression.getExpressionType() instanceof Primitive primitive
                && castType instanceof Primitive primitiveCast) {
            //Make sure the type are numerical
            if(!primitive.isNumericalType() || !primitiveCast.isNumericalType())
                new AnalyzingError.TypeCast(castExpression);

            //Warn if types are the same
            if(primitive.equals(primitiveCast))
                new AnalyzingWarning.RedundantCasting(castExpression);

            //Define resulting type
            castExpression.setExpressionType(castType);
        }

        //Analyze object casting
        else if(expression.getExpressionType() instanceof Object object
                && castType instanceof Object castObject) {
            ClassSymbol objectSymbol = object.getClassSymbol();

            //Make sure the object can be cast
            if(objectSymbol != null
                    && (!objectSymbol.isSubtypeOf(castObject.getClassSymbol())
                    && !objectSymbol.isSupertypeOf(castObject.getClassSymbol())))
                new AnalyzingError.TypeCast(castExpression);

            //Warn if types are the same
            if(objectSymbol != null && objectSymbol.equals(castObject.getClassSymbol()))
                new AnalyzingWarning.RedundantCasting(castExpression);

            //Define resulting type
            castExpression.setExpressionType(castType);

        }

        //Analyze array casting
        else if(expression.getExpressionType() instanceof Array && castType instanceof Array) {
            //Define resulting type
            castExpression.setExpressionType(castType);
        }

        else new AnalyzingError.TypeCast(castExpression);

        return castExpression;
    }

    @Override
    public Expression visitSimpleName(SimpleName simpleName) {
        //Analyze variable
        if(variableTable.isAlreadyDefined(simpleName.getName())) {
            //Find variable
            Variable variable = variableTable.findVariableWithName(simpleName.getName());

            //Define resulting type
            simpleName.setExpressionType(variable.getType());
        }

        //Analyze field
        else {
            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), classSymbol);

            //Make sure the field exists
            if(fieldSymbol == null)
                new AnalyzingError.UnresolvableSymbol(simpleName, simpleName.getName());

            //Make sure the static context is valid
            if(isStaticContext && !fieldSymbol.isStatic())
                new AnalyzingError.UnresolvableSymbol(simpleName, fieldSymbol.getName());

            //Make sure the class is initialized
            if(!isInitialized)
                new AnalyzingError.UninitializedThisReference(simpleName);

            //Define resulting type
            simpleName.setExpressionType(fieldSymbol.getType());
        }

        return simpleName;
    }

    @Override
    public Expression visitMethodCall(MethodCall methodCall) {
        //Visit arguments list
        methodCall.setArgumentList(methodCall.getArgumentList().accept(this));

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

        //Analyze method call
        if(methodCall.getMethod() == null) {
            //Find method in current class
            MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, classSymbol, methodCall);

            //Analyze member call
            if(methodSymbol == null)
                return visitMethodCallOperationOverload(methodCall);

            //Make sure the method call is
            if(isStaticContext && !methodSymbol.isStatic())
                new AnalyzingError.UnresolvableMethod(methodCall, methodSymbol.getName(), methodSymbol.getParameterTypes());

            //Make sure the class is initialized
            if(!isInitialized)
                new AnalyzingError.UninitializedThisReference(methodCall);

            //Define resulting type
            methodCall.setExpressionType(methodSymbol.getReturnType());
        }

        //Analyze expression call
        else {
            return visitExpressionCallOperationOverload(methodCall);
        }

        return methodCall;
    }

    @Override
    public Expression visitMemberAccess(MemberAccess memberAccess) {
        //Analyze method call accessor
        if(memberAccess.getAccessor() instanceof MethodCall methodCall) {
            //Visit arguments list
            methodCall.setArgumentList(methodCall.getArgumentList().accept(this));

            //Get the type of each argument
            Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

            String methodName = methodCall.getMethodName();

            ClassSymbol classSymbol = null;
            boolean isStaticContext = false;

            Type type = getTypeFromNode(memberAccess.getMember());

            //Analyze static method call
            if(type instanceof Object object) {
                classSymbol = object.getClassSymbol();
                isStaticContext = true;
            }

            //Analyze instance method call
            else if(type == null) {
                //Visit member
                memberAccess.setMember(memberAccess.getMember().accept(this));

                Type memberType = (memberAccess.getMember()).getExpressionType();

                //Make sure the member is an object
                if(memberType.getKind() != Type.Kind.OBJECT)
                    new AnalyzingError.InvalidMemberAccess(memberAccess, memberType);

                classSymbol = ((Object) memberType).getClassSymbol();
            } else
                new AnalyzingError.InvalidMemberAccess(memberAccess, type);

            //Find method in class symbol
            MethodSymbol methodSymbol = classSymbol.findMethod(methodName, argumentTypes, this.classSymbol, methodCall);

            if(methodSymbol != null) {
                //Make sure the method is valid
                if(methodSymbol.isStatic() != isStaticContext)
                    new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

                //Define resulting type
                memberAccess.setExpressionType(methodSymbol.getReturnType());

                return memberAccess;
            }

            //Make sure the current context is not static
            if(isStaticContext)
                new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(methodName, classSymbol);

            //Make sure the field exists
            if(fieldSymbol == null || fieldSymbol.getType().getKind() != Type.Kind.OBJECT)
                new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

            ClassSymbol fieldClassSymbol = ((Object) fieldSymbol.getType()).getClassSymbol();

            methodSymbol = fieldClassSymbol.findMethod(OperatorMethod.Name.METHOD_INVOCATION, argumentTypes, this.classSymbol, methodCall);

            if(methodSymbol == null)
                new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

            //Define resulting type
            memberAccess.setExpressionType(methodSymbol.getReturnType());

            return memberAccess;
        }

        //Visit member
        memberAccess.setMember(memberAccess.getMember().accept(this));

        Type memberType = memberAccess.getMember().getExpressionType();

        //Make sure the member is not a primitive type
        if(memberType.getKind() == Type.Kind.PRIMITIVE)
            new AnalyzingError.InvalidMemberAccess(memberAccess, memberType);

        if(memberType.getKind() == Type.Kind.ARRAY) {
            //Make sure the only array field is size
            if(!(memberAccess.getAccessor() instanceof SimpleName simpleName)
                    || !simpleName.getName().equals(Array.SIZE))
                new AnalyzingError.InvalidMemberAccess(memberAccess, memberType);

            //Define resulting type
            memberAccess.setExpressionType(new Primitive(Primitive.Kind.INTEGER));

            return memberAccess;
        }

        ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();

        //Analyse simple name accessor
        if(memberAccess.getAccessor() instanceof SimpleName simpleName) {
            String name = simpleName.getName();

            //Find field in class symbol
            FieldSymbol fieldSymbol = classSymbol.findField(name, classSymbol);

            //Make sure the field exists
            if(fieldSymbol == null)
                new AnalyzingError.UnresolvableSymbol(memberAccess.getAccessor(), name);

            //Define resulting type
            memberAccess.setExpressionType(fieldSymbol.getType());
        }

        //Analyze class creation accessor
        else if(memberAccess.getAccessor() instanceof ClassCreation classCreation) {
            if(classCreation.getType() == null) {
                //Make sure the type can be infered if no type provided
                if(classCreation.getExpressionType() == null)
                    new AnalyzingError.TypeInference(classCreation);
            } else {
                Type type = getTypeFromNode(classCreation.getType());

                //Make sure the class exists
                if(type == null)
                    new AnalyzingError.UnresolvableClass(classCreation.getType(), classCreation.getType().toString());

                //Define resulting type
                classCreation.setExpressionType(type);
            }

            Type type = classCreation.getExpressionType();

            //Make sure the type is an object
            if(!(type instanceof Object))
                new AnalyzingError.InvalidTypeCreation(classCreation, type);

            ClassSymbol innerClassSymbol = ((Object) type).getClassSymbol();

            //Make sure the class is an inner class
            if(!innerClassSymbol.isInner())
                new AnalyzingError.InvalidInnerCreation(classCreation);

            //Make sure the class is a valid inner class
            if(!innerClassSymbol.isInnerClassOf(classSymbol))
                new AnalyzingError.InvalidInnerCreation(classCreation);

            //Visit argument list
            classCreation.setArgumentList(classCreation.getArgumentList().accept(this));

            classCreation = (ClassCreation) transformer.transformInnerClassCreation(memberAccess, classCreation);

            Type[] argumentTypes = getTypesFromArguments((ArgumentList) classCreation.getArgumentList());

            //Find constructor in class creation symbol
            MethodSymbol constructor = innerClassSymbol.findConstructor(argumentTypes, classSymbol, memberAccess);

            //Make sure the constructor exists
            if(constructor == null)
                new AnalyzingError.UnresolvableConstructor(classCreation, argumentTypes);

            //Define resulting type
            classCreation.setExpressionType(type);

            return classCreation;
        }

        return memberAccess;
    }

    @Override
    public Expression visitQualifiedName(QualifiedName qualifiedName) {
        //Visit qualified name as node
        visitQualifiedName((Node) qualifiedName);

        return qualifiedName;
    }

    @Override
    public Expression visitClassCreation(ClassCreation classCreation) {
        if(classCreation.getType() == null) {
            //Make sure the type can be infered if no type provided
            if(classCreation.getExpressionType() == null)
                new AnalyzingError.TypeInference(classCreation);
        } else {
            Type type = getTypeFromNode(classCreation.getType());

            //Make sure the class exists
            if(type == null)
                new AnalyzingError.UnresolvableClass(classCreation.getType(), classCreation.getType().toString());

            //Define resulting type
            classCreation.setExpressionType(type);
        }

        Type type = classCreation.getExpressionType();

        //Make sure the type is an object
        if(type.getKind() != Type.Kind.OBJECT)
            new AnalyzingError.InvalidTypeCreation(classCreation, type);

        ClassSymbol classSymbol = ((Object) type).getClassSymbol();

        //Make sure the class is accessible
        if(!classSymbol.isAccessibleFrom(this.classSymbol))
            new AnalyzingError.UnresolvableClass(classCreation, classSymbol.getName());

        //Make sure the class is not an interface
        if(classSymbol.isInterface())
            new AnalyzingError.InterfaceCreation(classCreation);

        //Make sure the class is not an enum
        if(classSymbol.isEnum())
            new AnalyzingError.EnumCreation(classCreation);

        //Make sure the class is not static
        if(classSymbol.isStatic())
            new AnalyzingError.StaticCreation(classCreation);

        //Make sure the class is not an inner class
        if(classSymbol.isInner())
            new AnalyzingError.InnerCreation(classCreation);

        //Visit arguments list
        classCreation.setArgumentList(classCreation.getArgumentList().accept(this));

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) classCreation.getArgumentList());

        //Find constructor in class symbol
        MethodSymbol constructor = classSymbol.findConstructor(argumentTypes, this.classSymbol, classCreation);

        //Make sure the constructor exists
        if(constructor == null)
            new AnalyzingError.UnresolvableConstructor(classCreation, argumentTypes);

        return classCreation;
    }

    @Override
    public Expression visitArrayCreation(ArrayCreation arrayCreation) {
        if(arrayCreation.getType() == null) {
            //Make sure the type can be infered if no type provided
            if(arrayCreation.getExpressionType() == null)
                new AnalyzingError.TypeInference(arrayCreation);
        } else {
            Type type = getTypeFromNode(arrayCreation.getType());

            //Make sure the array type is valid
            if(type == null)
                new AnalyzingError.UnresolvableSymbol(arrayCreation.getType(), arrayCreation.getType().toString());

            //Define resulting type
            arrayCreation.setExpressionType(new Array(type));
        }

        //Visit the initialization expression
        arrayCreation.setInitializationExpression(arrayCreation.getInitializationExpression().accept(this));

        Expression initializationExpression = arrayCreation.getInitializationExpression();

        //Make sure the initialization expression is an integer
        if(!(initializationExpression.getExpressionType() instanceof Primitive primitive)
                || primitive.getPrimitiveKind() != Primitive.Kind.INTEGER)
            new AnalyzingError.TypeConversion(initializationExpression,
                    initializationExpression.getExpressionType(), new Primitive(Primitive.Kind.INTEGER));

        return arrayCreation;
    }

    @Override
    public Expression visitArrayAccess(ArrayAccess arrayAccess) {
        //Visit array and expression
        arrayAccess.setArray(arrayAccess.getArray().accept(this));
        arrayAccess.setAccessExpression(arrayAccess.getAccessExpression().accept(this));

        Expression arrayExpression = arrayAccess.getArray();
        Expression accessExpression = arrayAccess.getAccessExpression();

        //Make sure the array expression is not void or null
        if(isVoidExpression(arrayExpression) || isNullExpression(arrayExpression))
            new AnalyzingError.ExpectedArrayType(arrayAccess);

        //Make sure the access expression is an integer
        if(!(accessExpression.getExpressionType() instanceof Primitive primitive)
                || primitive.getPrimitiveKind() != Primitive.Kind.INTEGER)
            new AnalyzingError.TypeConversion(accessExpression,
                    accessExpression.getExpressionType(), new Primitive(Primitive.Kind.INTEGER));

        //Make sure the array is not a primitive
        if(arrayExpression.getExpressionType().getKind() == Type.Kind.PRIMITIVE)
            new AnalyzingError.ExpectedArrayType(arrayAccess);

        //Analyze array as array
        if(arrayExpression.getExpressionType() instanceof Array array) {
            //Define resulting type
            arrayAccess.setExpressionType(array.getType());
        }

        //Analyze array as object
        else if(arrayExpression.getExpressionType() instanceof Object) {
            return visitArrayAccessOperationOverload(arrayAccess);
        }

        return arrayAccess;
    }

    @Override
    public Expression visitAssignmentExpression(AssignmentExpression assignmentExpression) {
        //Visit variable
        assignmentExpression.setVariable(assignmentExpression.getVariable().accept(this));

        Expression variableExpression = assignmentExpression.getVariable();
        Expression expression = assignmentExpression.getExpression();

        //Make sure the variable is a variable expression
        if(!isVariableExpression(variableExpression))
            new AnalyzingError.ExpectedVariableExpression(variableExpression);

        //Visit variable expression
        visitVariableExpression(variableExpression);

        Type variableType = variableExpression.getExpressionType();

        //Analyze simple assignment
        if(assignmentExpression.getKind() == AssignmentExpression.Kind.ASSIGNMENT) {
            //Infer type if possible
            inferType(expression, variableType);

            //Visit assignment expression
            assignmentExpression.setExpression(assignmentExpression.getExpression().accept(this));
            expression = assignmentExpression.getExpression();

            //Make sure the type is not a primitive if the expression is null
            if(isNullExpression(expression)) {
                if(variableType.getKind() == Type.Kind.PRIMITIVE)
                    new AnalyzingError.TypeConversion(expression,
                            expression.getExpressionType(), variableType);

                return assignmentExpression;
            }

            //Make sure the expression type is assignable to the variable type
            if(!expression.getExpressionType().isAssignableTo(variableType))
                new AnalyzingError.TypeConversion(expression,
                        expression.getExpressionType(), variableType);

            //Define resulting type
            assignmentExpression.setExpressionType(variableType);
        }

        //Analyze augmented assignment
        else {
            //Visit assignment expression
            assignmentExpression.setExpression(assignmentExpression.getExpression().accept(this));
            expression = assignmentExpression.getExpression();

            Type expressionType = expression.getExpressionType();

            //Make sure the variable is not an array
            if(variableType instanceof Array)
                new AnalyzingError.InvalidAssignment(assignmentExpression, variableType);

            //Analyze primitive assignment
            if(variableType instanceof Primitive primitive) {
                switch(assignmentExpression.getKind()) {
                    case ASSIGNMENT_ADDITION, ASSIGNMENT_SUBTRACTION, ASSIGNMENT_MULTIPLICATION,
                         ASSIGNMENT_DIVISION, ASSIGNMENT_MODULO -> {
                        //Make sure the type is numerical
                        if(!primitive.isNumericalType())
                            new AnalyzingError.InvalidAssignment(assignmentExpression, variableType);

                        //Make sure the types are equal
                        if(!variableType.equals(expressionType))
                            new AnalyzingError.TypeConversion(assignmentExpression,
                                    expressionType, variableType);

                        //Define resulting type
                        assignmentExpression.setExpressionType(variableType);
                    }

                    case ASSIGNMENT_BITWISE_AND, ASSIGNMENT_BITWISE_XOR, ASSIGNMENT_BITWISE_OR,
                         ASSIGNMENT_SHIFT_LEFT, ASSIGNMENT_SHIFT_RIGHT, ASSIGNMENT_SHIFT_RIGHT_ARITHMETIC -> {
                        //Make sure the type is integer
                        if(!primitive.isIntegerType())
                            new AnalyzingError.InvalidAssignment(assignmentExpression, variableType);

                        //Make sure the types are equal
                        if(!variableType.equals(expressionType))
                            new AnalyzingError.TypeConversion(assignmentExpression,
                                    expressionType, variableType);

                        //Define resulting type
                        assignmentExpression.setExpressionType(variableType);
                    }
                }
            }

            //Analyze object assignment
            else if(variableType instanceof Object) {
                return visitAssignmentOperationOverload(assignmentExpression);
            }
        }

        return assignmentExpression;
    }

    @Override
    public Expression visitIfExpression(IfExpression ifExpression) {
        //Visit condition
        ifExpression.setCondition(ifExpression.getCondition().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(ifExpression.getCondition());

        //Visit expressions
        ifExpression.setExpression(ifExpression.getExpression().accept(this));
        ifExpression.setElseExpression(ifExpression.getElseExpression().accept(this));

        Expression expression = ifExpression.getExpression();
        Expression elseExpression = ifExpression.getElseExpression();

        //Make sure expression types are equal
        if(!expression.getExpressionType().equals(elseExpression.getExpressionType()))
            new AnalyzingError.TypeConversion(elseExpression,
                    elseExpression.getExpressionType(), expression.getExpressionType());

        //Define resulting type
        ifExpression.setExpressionType(expression.getExpressionType());

        return ifExpression;
    }

    @Override
    public Expression visitSumExpression(SumExpression sumExpression) {
        //Get the amount of previous local variables
        int previousVariableCount = variableTable.getVariableCount();

        //Visit sum-expression expressions
        sumExpression.setVariableInitialization(sumExpression.getVariableInitialization().accept(this));
        sumExpression.setCondition(sumExpression.getCondition().accept(this));
        sumExpression.setIncrementExpression(sumExpression.getIncrementExpression().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(sumExpression.getCondition());

        //Visit expression body
        currentLoopLevel++;
        sumExpression.setExpression(sumExpression.getExpression().accept(this));
        currentLoopLevel--;

        //Remove local variables added in this sum-expression
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        Expression expression = sumExpression.getExpression();

        //Make sure the expression type is a numerical expression
        if(!(expression.getExpressionType() instanceof Primitive primitive && primitive.isNumericalType()))
            new AnalyzingError.ExpectedNumericalExpression(expression);

        //Define resulting type
        sumExpression.setExpressionType(expression.getExpressionType());

        return sumExpression;
    }

    @Override
    public Expression visitProdExpression(ProdExpression prodExpression) {
        //Get the amount of previous local variables
        int previousVariableCount = variableTable.getVariableCount();

        //Visit prod-expression expressions
        prodExpression.setVariableInitialization(prodExpression.getVariableInitialization().accept(this));
        prodExpression.setCondition(prodExpression.getCondition().accept(this));
        prodExpression.setIncrementExpression(prodExpression.getIncrementExpression().accept(this));

        //Make sure the condition is a boolean expression
        matchBooleanExpression(prodExpression.getCondition());

        //Visit expression body
        currentLoopLevel++;
        prodExpression.setExpression(prodExpression.getExpression().accept(this));
        currentLoopLevel--;

        //Remove local variables added in this prod-expression
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        Expression expression = prodExpression.getExpression();

        //Make sure the expression type is a numerical expression
        if(!(expression.getExpressionType() instanceof Primitive primitive && primitive.isNumericalType()))
            new AnalyzingError.ExpectedNumericalExpression(expression);

        //Define resulting type
        prodExpression.setExpressionType(expression.getExpressionType());

        return prodExpression;
    }

    @Override
    public Expression visitThisExpression(ThisExpression thisExpression) {
        //Make sure the method is not static
        if(isStaticContext)
            new AnalyzingError.StaticThisReference(thisExpression);

        //Make sure the class is initialized
        if(!isInitialized)
            new AnalyzingError.UninitializedThisReference(thisExpression);

        //Define type as current class
        thisExpression.setExpressionType(new Object(classSymbol));

        return thisExpression;
    }

    @Override
    public Expression visitSuperExpression(SuperExpression superExpression) {
        //Make sure the method is not static
        if(isStaticContext)
            new AnalyzingError.StaticSuperReference(superExpression);

        //Make sure the class is initialized
        if(!isInitialized)
            new AnalyzingError.UninitializedSuperReference(superExpression);

        ClassSymbol superclassSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

        //Make sure the superclass exists
        if(classSymbol.isRoot())
            new AnalyzingError.UnknownSuperReference(superExpression);

        //Define type as superclass
        superExpression.setExpressionType(new Object(superclassSymbol));

        return superExpression;
    }

    @Override
    public Expression visitOuterExpression(OuterExpression outerExpression) {
        //Make sure the method is not static
        if(isStaticContext)
            new AnalyzingError.StaticOuterReference(outerExpression);

        //Make sure the class is initialized
        if(!isInitialized)
            new AnalyzingError.UninitializedOuterReference(outerExpression);

        //Make sure the class is inner
        if(!classDeclaration.isInner())
            new AnalyzingError.InvalidOuterReference(outerExpression);

        ClassSymbol outerClassSymbol = (ClassSymbol) classSymbol.getOwnerSymbol();

        //Define resulting type
        outerExpression.setExpressionType(new Object(outerClassSymbol));

        return outerExpression;
    }

    @Override
    public Expression visitPrimitiveAttribute(PrimitiveAttribute primitiveAttribute) {
        //Make sure the attribute is valid
        if(primitiveAttribute.getKind() == null)
            new AnalyzingError.InvalidPrimitiveAttribute(primitiveAttribute);

        return primitiveAttribute.toLiteral().accept(this);
    }

    @Override
    public Node visitArgumentList(ArgumentList argumentList) {
        //Visit every argument
        List<Expression> arguments = argumentList.getArguments();
        for(int i = 0; i < arguments.size(); i++)
            arguments.set(i, arguments.get(i).accept(this));

        return argumentList;
    }

    @Override
    public Node visitParameterList(ParameterList parameterList) {
        //Visit every parameter
        List<Node> parameters = parameterList.getParameters();
        for(int i = 0; i < parameters.size(); i++)
            parameters.set(i, parameters.get(i).accept(this));

        return parameterList;
    }

    @Override
    public Node visitParameter(Parameter parameter) {
        String parameterName = parameter.getName();

        //Make sure the variable name is not already defined
        if(variableTable.isAlreadyDefined(parameterName))
            new AnalyzingError.DuplicateVariable(parameter);

        Type type = getTypeFromNode(parameter.getType());

        //Add the variable to the table
        variableTable.addVariable(type, parameterName, parameter.isConstant());

        if(parameter.isAttribute()) {
            FieldSymbol fieldSymbol = classSymbol.findField(parameterName, classSymbol);

            //Make sure there is a corresponding field with the same name
            if(fieldSymbol == null || !fieldSymbol.getClassSymbol().equals(classSymbol))
                new AnalyzingError.UnresolvableField(parameter, parameterName);

            //Make sure the field is not constant if method is not constructor
            if(fieldSymbol.isConstant() && !currentMethod.isConstructor())
                new AnalyzingError.InvalidConstantAssignment(parameter, parameterName);
        }

        return parameter;
    }

    @Override
    public Node visitEnumConstantList(EnumConstantList constantList) {
        isStaticContext = true;

        //Visit every constant
        List<Node> constants = constantList.getConstants();
        for(int i = 0; i < constants.size(); i++)
            constants.set(i, constants.get(i).accept(this));

        return constantList;
    }

    @Override
    public Node visitEnumConstant(EnumConstant constant) {
        //Visit arguments list
        if(constant.getArgumentList() != null)
            constant.setArgumentList(constant.getArgumentList().accept(this));

        ArgumentList argumentList = (ArgumentList) constant.getArgumentList();
        Type[] types = argumentList != null ? getTypesFromArguments(argumentList) : new Type[0];

        //Find corresponding enum constructor
        MethodSymbol constructorSymbol = classSymbol.findEnumConstructor(types, classSymbol, constant);

        //Make sure the constructor exists
        if(constructorSymbol == null)
            new AnalyzingError.UnresolvableConstructor(constant, types);

        return constant;
    }

    @Override
    public Statement visitCaseStatement(CaseStatement caseStatement) {
        //Visit expression
        caseStatement.setExpression(caseStatement.getExpression().accept(this));

        //Visit statement block
        caseStatement.setStatementBlock(caseStatement.getStatementBlock().accept(this));

        return caseStatement;
    }



    //Alternative visit methods

    /**
     * Visit the given unary expression node as an operation overload and returns the transformed node.
     * @param unaryExpression the unary expression
     * @return the transformed node
     */
    private Expression visitUnaryOperationOverload(UnaryExpression unaryExpression) {
        Expression expression = unaryExpression.getExpression();
        Object object = (Object) expression.getExpressionType();

        //Make sure the expression is not null
        if(isNullExpression(expression))
            new AnalyzingError.InvalidUnaryOperation(unaryExpression);

        //Get operation overload name from expression
        String methodName = OperatorMethod.getNameFromUnaryExpression(unaryExpression.getKind());

        //Find operator overload method
        MethodSymbol methodSymbol = object.getClassSymbol().findMethod(methodName, new Type[0], classSymbol, unaryExpression);

        //Make sure the object supports the operation
        if(methodSymbol == null)
            new AnalyzingError.InvalidUnaryOperation(unaryExpression);

        return transformer.transformOperationOverload(unaryExpression, expression, null, methodName);
    }

    /**
     * Visits the given binary expression as an operation overload and returns the transformed node.
     * @param binaryExpression the binary expression
     * @return the transformed node
     */
    private Expression visitBinaryOperationOverload(BinaryExpression binaryExpression) {
        Expression first = binaryExpression.getFirst();
        Expression second = binaryExpression.getSecond();

        Object object = (Object) first.getExpressionType();
        ClassSymbol classSymbol = object.getClassSymbol();

        //Make sure the first expression is not null
        if(isNullExpression(first))
            new AnalyzingError.InvalidBinaryOperation(binaryExpression);

        //Transform string binary operation
        if(classSymbol.getClassName().equals(ClassName.STRING)) {
            //Transform string concatenation
            if(binaryExpression.getKind() == BinaryExpression.Kind.OPERATION_ADDITION)
                return transformer.transformStringConcatenation(binaryExpression);

            //Transform string repeating
            if(binaryExpression.getKind() == BinaryExpression.Kind.OPERATION_MULTIPLICATION)
                return transformer.transformStringRepeating(binaryExpression);
        }

        //Get operation overload name from expression
        String methodName = OperatorMethod.getNameFromBinaryExpression(binaryExpression.getKind());

        //Make sure the operator can be overloaded
        if(methodName == null)
            new AnalyzingError.InvalidBinaryOperation(binaryExpression);

        Type[] parameterTypes = { second.getExpressionType() };

        //Find operator overload method
        MethodSymbol methodSymbol = classSymbol.findMethod(methodName, parameterTypes, this.classSymbol, binaryExpression);

        //Make sure the object supports the operation
        if(methodSymbol == null)
            new AnalyzingError.InvalidBinaryOperation(binaryExpression);

        return transformer.transformOperationOverload(binaryExpression, first, second, methodName);
    }

    /**
     * Visits the given array access as operation overload and returns the transformed node.
     * @param arrayAccess the array access
     * @return the transformed node
     */
    private Expression visitArrayAccessOperationOverload(ArrayAccess arrayAccess) {
        Expression arrayExpression = arrayAccess.getArray();
        Expression accessExpression = arrayAccess.getAccessExpression();

        Object object = (Object) arrayExpression.getExpressionType();
        ClassSymbol classSymbol = object.getClassSymbol();

        //Transform string character access
        if(classSymbol.getClassName().equals(ClassName.STRING))
            return transformer.transformStringCharacterAccess(arrayAccess);

        Type[] parameterTypes = { accessExpression.getExpressionType() };

        //Find access overload method
        MethodSymbol methodSymbol = classSymbol.findMethod(OperatorMethod.Name.ARRAY_ACCESS, parameterTypes, this.classSymbol, arrayAccess);

        //Make sure the method exists
        if(methodSymbol == null)
            new AnalyzingError.InvalidArrayAccess(arrayAccess);

        return transformer.transformOperationOverload(arrayAccess, arrayExpression, accessExpression, OperatorMethod.Name.ARRAY_ACCESS);
    }

    /**
     * Visits the given assignment expression as an operation overload and returns the transformed node.
     * @param assignmentExpression the assignment expression
     * @return the transformed node
     */
    private Expression visitAssignmentOperationOverload(AssignmentExpression assignmentExpression) {
        Expression variableExpression = assignmentExpression.getVariable();
        Expression expression = assignmentExpression.getExpression();

        Type expressionType = expression.getExpressionType();
        Object object = (Object) variableExpression.getExpressionType();
        ClassSymbol classSymbol = object.getClassSymbol();

        //Make sure the expression is not null
        if(isNullExpression(variableExpression))
            new AnalyzingError.InvalidAssignment(assignmentExpression, object);

        //Get assignment overload name from expression
        String methodName = OperatorMethod.getNameFromAssignmentExpression(assignmentExpression.getKind());

        Type[] argumentTypes = new Type[] { expressionType };

        //Find assignment overload method
        MethodSymbol methodSymbol = classSymbol.findMethod(methodName, argumentTypes, this.classSymbol, assignmentExpression);

        //Make sure the object supports the assignment
        if(methodSymbol == null)
            new AnalyzingError.InvalidAssignment(assignmentExpression, object);

        return transformer.transformOperationOverload(assignmentExpression, variableExpression, expression, methodName);
    }

    /**
     * Visits the given method call as an operation overload method and returns the transformed node.
     * @param methodCall the method call
     * @return the transformed node
     */
    private Expression visitMethodCallOperationOverload(MethodCall methodCall) {
        String methodName = methodCall.getMethodName();
        Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

        ClassSymbol classSymbol;

        //Find local variable
        Variable variable = variableTable.findVariableWithName(methodName);

        //Analyze variable call
        if(variable == null) {
            //Find field in current class
            FieldSymbol fieldSymbol = this.classSymbol.findField(methodName, this.classSymbol);

            //Make sure the field exists
            if(fieldSymbol == null || fieldSymbol.getType().getKind() != Type.Kind.OBJECT)
                new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

            classSymbol = ((Object) fieldSymbol.getType()).getClassSymbol();
        }

        //Analyze field call
        else {
            //Make sure the variable is an object
            if(variable.getType().getKind() != Type.Kind.OBJECT)
                new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

            classSymbol = ((Object) variable.getType()).getClassSymbol();
        }

        //Find invocation overload method
        MethodSymbol methodSymbol = classSymbol.findMethod(OperatorMethod.Name.METHOD_INVOCATION, argumentTypes, this.classSymbol, methodCall);

        //Make sure the method exists
        if(methodSymbol == null)
            new AnalyzingError.UnresolvableMethod(methodCall, methodName, argumentTypes);

        //Transform expression to method call
        MethodCall methodInvokation = new MethodCall(methodCall.getMeta());
        methodInvokation.setMethodName(OperatorMethod.Name.METHOD_INVOCATION);
        methodInvokation.setArgumentList(methodCall.getArgumentList());

        //Transform method call to member access
        MemberAccess memberAccess = new MemberAccess(methodCall.getMeta());
        SimpleName simpleName = new SimpleName(methodCall.getMeta());
        simpleName.setName(methodName);
        memberAccess.setMember(simpleName);
        memberAccess.setAccessor(methodInvokation);

        return memberAccess.accept(this);
    }

    /**
     * Visits the given expression call as an operation overload method and returns the transformed node.
     * @param methodCall the expression call
     * @return the transformed node
     */
    private Expression visitExpressionCallOperationOverload(MethodCall methodCall) {
        //Visit member
        methodCall.setMethod(methodCall.getMethod().accept(this));

        Expression member = methodCall.getMethod();
        Type type = member.getExpressionType();

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

        //Make sure the method type is an object
        if(type.getKind() != Type.Kind.OBJECT)
            new AnalyzingError.InvalidObjectCall(methodCall, argumentTypes);

        ClassSymbol classSymbol = ((Object) type).getClassSymbol();

        //Find invocation overload method
        MethodSymbol methodSymbol = classSymbol.findMethod(OperatorMethod.Name.METHOD_INVOCATION, argumentTypes, this.classSymbol, methodCall);

        //Make sure the method exists
        if(methodSymbol == null)
            new AnalyzingError.InvalidObjectCall(methodCall, argumentTypes);

        //Transform expression to method call
        MethodCall methodInvocation = new MethodCall(methodCall.getMeta());
        methodInvocation.setMethodName(OperatorMethod.Name.METHOD_INVOCATION);
        methodInvocation.setArgumentList(methodCall.getArgumentList());

        //Transform method call to member access
        MemberAccess memberAccess = new MemberAccess(methodCall.getMeta());
        memberAccess.setMember(member);
        memberAccess.setAccessor(methodInvocation);

        return memberAccess.accept(this);
    }

    /**
     * Visits the given qualified name node and returns the corresponding symbol.
     * This method recursiverly set the expression type and the corresponding symbol,
     * which can be null if the qualified name is not static.
     * @param node the qualified name node
     * @return the corresponding symbol
     */
    private Symbol visitQualifiedName(Node node) {
        //Visit simple name
        if(node instanceof SimpleName simpleName) {
            String name = simpleName.getName();

            //Find variable in variable table
            Variable variable = variableTable.findVariableWithName(name);

            if(variable != null) {
                //Define resulting type
                simpleName.setExpressionType(variable.getType());
                return null;
            }

            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(name, classSymbol);

            if(fieldSymbol != null) {
                //Define resulting type
                simpleName.setExpressionType(fieldSymbol.getType());
                return null;
            }

            //Find class name
            Type type = getTypeFromNode(simpleName);

            if(type != null)
                return ((Object) type).getClassSymbol();

            //Find symbol name
            Symbol symbol = Classes.findSymbol(name);

            //Make sure the symbol exists
            if(symbol == null)
                new AnalyzingError.UnresolvableSymbol(node, name);

            return symbol;
        }

        //Visit qualified name
        else if(node instanceof QualifiedName qualifiedName) {
            //Get object type from qualified name
            Object objectType = (Object) getTypeFromNode(qualifiedName);

            //Return class symbol if qualified name is a type
            if(objectType != null)
                return objectType.getClassSymbol();

            Symbol symbol = visitQualifiedName(qualifiedName.getQualifiedName());
            String name = qualifiedName.getName();

            Type type = qualifiedName.getQualifiedName().getExpressionType();

            //Analyze instance access
            if(type != null) {
                //Visit field access
                if(type instanceof Object object) {
                    ClassSymbol classSymbol = object.getClassSymbol();

                    //Find field in class symbol
                    FieldSymbol fieldSymbol = classSymbol.findField(name, this.classSymbol);

                    //Make sure the field exists
                    if(fieldSymbol == null)
                        new AnalyzingError.UnresolvableSymbol(node, name);

                    //Make sure the field is not static
                    if(fieldSymbol.isStatic())
                        new AnalyzingError.UnresolvableSymbol(node, name);

                    //Define resulting type
                    qualifiedName.setExpressionType(fieldSymbol.getType());
                    return null;
                }

                //Visit array access
                else if(type.getKind() == Type.Kind.ARRAY && name.equals(Array.SIZE)) {
                    //Define resulting type
                    qualifiedName.setExpressionType(new Primitive(Primitive.Kind.INTEGER));
                    return null;
                }

                else new AnalyzingError.UnresolvableSymbol(qualifiedName, name);

            }

            //Analyze static access
            else {
                //Visit package symbol
                if(symbol instanceof PackageSymbol packageSymbol) {
                    Symbol subSymbol = packageSymbol.findSymbol(name);

                    //Make sure the symbol exists
                    if(subSymbol == null)
                        new AnalyzingError.UnresolvableSymbol(qualifiedName, name);

                    return subSymbol;
                }

                //Visit class symbol
                else if(symbol instanceof ClassSymbol classSymbol) {
                    ClassSymbol innerClassSymbol = classSymbol.findClass(name);

                    //Make sure the inner class symbol exists
                    if(innerClassSymbol != null)
                        return innerClassSymbol;

                    //Find field in class symbol
                    FieldSymbol fieldSymbol = classSymbol.findField(name, this.classSymbol);

                    //Make sure the field exists and is static
                    if(fieldSymbol == null || !fieldSymbol.isStatic())
                        new AnalyzingError.UnresolvableSymbol(qualifiedName, name);

                    //Define resulting type
                    qualifiedName.setExpressionType(fieldSymbol.getType());
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Visits the given variable expression node.
     * This method makes sure the variable is not constant and can be assigned.
     * @param variableExpression the variable expression
     */
    private void visitVariableExpression(Node variableExpression) {
        //Analyze simple name variable
        if(variableExpression instanceof SimpleName simpleName) {
            Variable variable = variableTable.findVariableWithName(simpleName.getName());

            if(variable != null) {
                //Make sure the variable isn't already assigned if constant
                if(variable.isConstant() && variable.isAssigned())
                    new AnalyzingError.InvalidConstantAssignment(variableExpression, simpleName.getName());

            } else {
                //Find field in current class
                FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), classSymbol);

                //Make sure the field is not assigned outside constructor if constant
                if(fieldSymbol.isConstant() && currentMethod != null && !currentMethod.isConstructor())
                    new AnalyzingError.InvalidConstantAssignment(variableExpression, simpleName.getName());
            }
        }

        //Analyze member access variable
        else if(variableExpression instanceof MemberAccess memberAccess) {
            SimpleName simpleName = (SimpleName) memberAccess.getAccessor();
            Type type = memberAccess.getMember().getExpressionType();

            //Make sure the variable is not the array size
            if(type.getKind() == Type.Kind.ARRAY)
                new AnalyzingError.InvalidConstantAssignment(variableExpression, simpleName.getName());

            ClassSymbol classSymbol = ((Object) type).getClassSymbol();
            FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), this.classSymbol);

            //Make sure the field is not assigned outside constructor if constant
            if(fieldSymbol.isConstant() && currentMethod != null && !currentMethod.isConstructor())
                new AnalyzingError.InvalidConstantAssignment(variableExpression, simpleName.getName());
        }

        //Analyze qualified name variable
        else if(variableExpression instanceof QualifiedName qualifiedName) {
            //Make sure the qualified name is a variable
            if(qualifiedName.getExpressionType() == null)
                new AnalyzingError.ExpectedVariableExpression(variableExpression);

            Type type;
            if((type = getTypeFromNode(qualifiedName.getQualifiedName())) == null)
                type = qualifiedName.getQualifiedName().getExpressionType();

            //Make sure the variable is not the array size
            if(type.getKind() == Type.Kind.ARRAY)
                new AnalyzingError.InvalidConstantAssignment(variableExpression, qualifiedName.getName());

            ClassSymbol classSymbol = ((Object) type).getClassSymbol();
            FieldSymbol fieldSymbol = classSymbol.findField(qualifiedName.getName(), this.classSymbol);

            //Make sure the field is not assigned outside constructor if constant
            if(fieldSymbol.isConstant() && currentMethod != null && !currentMethod.isConstructor())
                new AnalyzingError.InvalidConstantAssignment(variableExpression, qualifiedName.getName());
        }
    }



    //Matching and validating methods

    /**
     * Infers the given type to the given expression if the expression type
     * can be inferred.
     * @param expression the expression
     * @param type the type
     */
    private void inferType(Expression expression, Type type) {
        //Infer type to empty class creation
        if(expression instanceof ClassCreation classCreation
                && type.getKind() == Type.Kind.OBJECT) {
            if(classCreation.getType() == null)
                classCreation.setExpressionType(type);
        }

        //Infer type to empty array creation
        if(expression instanceof ArrayCreation arrayCreation
                && type.getKind() == Type.Kind.ARRAY) {
            if(arrayCreation.getType() == null)
                arrayCreation.setExpressionType(type);
        }

        //Infer type to empty member class creation
        if(expression instanceof MemberAccess memberAccess
                && memberAccess.getAccessor() instanceof ClassCreation classCreation
                && type.getKind() == Type.Kind.OBJECT) {
            if(classCreation.getType() == null)
                classCreation.setExpressionType(type);
        }
    }

    /**
     * Returns the type from the given type node.
     * @param node the type node
     * @return the type
     */
    private Type getTypeFromNode(Node node) {
        return Type.fromTypeNode(node, classSymbol, importTable);
    }

    /**
     * Returns the array of types from the given argument list node.
     * @param argumentList the argument list node
     * @return the types array
     */
    private Type[] getTypesFromArguments(ArgumentList argumentList) {
        List<Expression> arguments = argumentList.getArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        for(int i = 0; i < arguments.size(); i++)
            argumentTypes[i] = arguments.get(i).getExpressionType();

        return argumentTypes;
    }

    /**
     * Returns whether the given node is an expression statement.
     * @param node the node
     * @return true if the node is an expression statement
     */
    private boolean isExpressionStatement(Node node) {
        return node instanceof MethodCall
                || node instanceof AssignmentExpression
                || node instanceof ClassCreation
                || (node instanceof MemberAccess memberAccess
                    && memberAccess.getAccessor() instanceof MethodCall)
                || (node instanceof UnaryExpression unaryExpression
                    && (unaryExpression.getKind().isIncrement()
                    || unaryExpression.getKind().isDecrement()));
    }

    /**
     * Returns whether the given node is a variable expression.
     * @param node the node
     * @return true if the node is a variable expression
     */
    private boolean isVariableExpression(Node node) {
        return node instanceof SimpleName
                || node instanceof ArrayAccess
                || node instanceof QualifiedName
                || (node instanceof MemberAccess memberAccess
                    && memberAccess.getAccessor() instanceof SimpleName);
    }

    /**
     * Returns whether the given expression is a constant expression.
     * @param expression the expression
     * @return true if the expression is a constant expression
     */
    private boolean isConstantExpression(Expression expression) {
        return expression instanceof Literal;
    }

    /**
     * Matches the given node as a boolean expression, and throw an error
     * if the node is not a boolean expression.
     * @param node the node
     */
    private void matchBooleanExpression(Node node) {
        if(!isBooleanExpression((Expression) node))
            new AnalyzingError.ExpectedBooleanExpression(node);
    }

    /**
     * Returns whether the given expression node is a boolean expression.
     * @param node the expression node
     * @return true if the node is a boolean expression
     */
    private boolean isBooleanExpression(Expression node) {
        return node.getExpressionType() instanceof Primitive primitive
                && primitive.getPrimitiveKind() == Primitive.Kind.BOOLEAN;
    }

    /**
     * Returns whether the given expression is a null object reference.
     * @param expression the expression node
     * @return true if the expression is null
     */
    private boolean isNullExpression(Expression expression) {
        return expression.getExpressionType() instanceof Object object
                && object.getClassSymbol() == null;
    }

    /**
     * Returns whether the given expression is a void expression.
     * A void expression is an expression which has the return type of void method.
     * @param expression the expression node
     * @return true if the expression is void
     */
    private boolean isVoidExpression(Expression expression) {
        return expression.getExpressionType() instanceof Void;
    }

    /**
     * Returns whether the given exception is a throwable expression.
     * An expression is throwable if its resulting type is assignable to Throwable.
     * @param expression the expression node
     * @return true if the expression is throwable
     */
    private boolean isThrowableExpression(Expression expression) {
        Type type = expression.getExpressionType();
        ClassSymbol classSymbol = LibraryClasses.findClass(ClassName.THROWABLE);

        //Make sure the throwable class exists
        if(classSymbol == null)
            new AnalyzingError.UnresolvableClass(expression, ClassName.THROWABLE.toString());

        return type.isAssignableTo(new Object(classSymbol));
    }
}
