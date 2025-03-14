package poly.compiler.generator;

import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.analyzer.table.Variable;
import poly.compiler.analyzer.table.VariableTable;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Void;
import poly.compiler.analyzer.type.*;
import poly.compiler.output.attribute.CodeAttribute;
import poly.compiler.output.attribute.LineNumberTableAttribute;
import poly.compiler.output.attribute.StackMapTableAttribute;
import poly.compiler.output.content.Attributes;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.output.content.Descriptor;
import poly.compiler.output.jvm.Instruction;
import poly.compiler.parser.tree.*;
import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.statement.*;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.parser.tree.variable.VariableDeclaration;
import poly.compiler.resolver.ClassDefinition;
import poly.compiler.resolver.LibraryClasses;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.FieldSymbol;
import poly.compiler.resolver.symbol.MethodSymbol;
import poly.compiler.tokenizer.content.Keyword;
import poly.compiler.util.ByteArray;
import poly.compiler.util.ClassName;

import java.util.*;

import static poly.compiler.output.jvm.Instructions.*;

/**
 * The CodeGenerator class. This class is used to generate the bytecode content of the method declarations
 * in the AST analyzed by the Analyzer. This step includes making conditional branches,
 * keep track of the instructions, program counter, local variables and operand stack, and much more.
 * @author Vincent Philippe (@vincent64)
 */
public class CodeGenerator implements NodeVisitor {
    private final ImportTable importTable;
    private final ConstantPool constantPool;
    private final List<Instruction> instructions;
    private final LineNumberTable lineNumberTable;
    private final StackMapTable stackMapTable;
    private final ClassDeclaration classDeclaration;
    private final ClassSymbol classSymbol;
    private final VariableTable variableTable;
    private final LocalTable localTable;
    private final OperandStack operandStack;
    private final List<Branching> loopStack;
    private int programCounter;

    private CodeGenerator(ClassDefinition classDefinition, ConstantPool constantPool, ImportTable importTable) {
        this.constantPool = constantPool;
        this.importTable = importTable;

        classDeclaration = classDefinition.getClassDeclaration();
        classSymbol = classDefinition.getClassSymbol();

        //Initialize instructions list
        instructions = new ArrayList<>();

        //Initialize stack map and line number tables
        lineNumberTable = new LineNumberTable();
        stackMapTable = new StackMapTable();

        //Initialize variables table
        variableTable = new VariableTable();

        //Initialize local table and operand stack
        localTable = new LocalTable(constantPool);
        operandStack = new OperandStack(constantPool, localTable);

        //Initialize loops stack
        loopStack = new ArrayList<>();
    }

    public static CodeGenerator getInstance(ClassDefinition classDefinition, ConstantPool constantPool, ImportTable importTable) {
        return new CodeGenerator(classDefinition, constantPool, importTable);
    }

    /**
     * Adds the given instruction to the instructions list.
     * This method will also update the program counter.
     * @param instruction the instruction
     */
    private void addInstruction(Instruction instruction) {
        instructions.add(instruction);
        programCounter += instruction.getSize();

        //Update the operand stack with new instruction
        operandStack.update(instruction, programCounter);
    }

    /**
     * Adds the given operation code as instruction in the instructions list.
     * @param code the operation code
     */
    private void addInstruction(byte code) {
        addInstruction(new Instruction(code));
    }

    /**
     * Adds the line number entry to the table from the given node.
     * @param node the node
     */
    private void addLineNumber(Node node) {
        if(node.getMeta() != null)
            lineNumberTable.addEntry(programCounter, node.getMeta().getLine());
    }

    /**
     * Generates and returns the bytecode for the given method declaration.
     * @param methodDeclaration the method declaration
     * @return the bytecode
     */
    public CodeAttribute generate(MethodDeclaration methodDeclaration) {
        //Visit method declaration
        methodDeclaration.accept(this);

        //Generate bytes from instructions
        ByteArray byteArray = new ByteArray();
        for(Instruction instruction : instructions)
            byteArray.add(instruction.getBytes());
        byte[] bytes = byteArray.getBytes();

        Attributes attributes = new Attributes();

        //Generate stack map table attribute
        StackMapTableAttribute stackMapTableAttribute = new StackMapTableAttribute(constantPool, stackMapTable);
        attributes.addAttribute(stackMapTableAttribute);

        //Generate line number table attribute
        LineNumberTableAttribute lineNumberTableAttribute = new LineNumberTableAttribute(constantPool, lineNumberTable);
        attributes.addAttribute(lineNumberTableAttribute);

        return new CodeAttribute(constantPool,
                (short) operandStack.getMaxStack(),
                (short) localTable.getMaxCount(),
                bytes,
                (short) 0,
                attributes);
    }

    @Override
    public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {
        //Clear local variables table
        variableTable.clear();

        //Add instance this variable
        if((!classSymbol.isStatic() && !methodDeclaration.isStatic())
                || (classSymbol.isStatic() && methodDeclaration.isConstructor())) {
            Type thisType = new Object(classSymbol);
            variableTable.addVariable(thisType, "this", true);

            if(methodDeclaration.isConstructor()) {
                localTable.addUninitializedThis();
            } else {
                localTable.addLocal(thisType);
            }
        }

        //Visit static field declarations
        if(methodDeclaration.isStaticConstructor())
            visitFieldDeclarations(true);

        StatementBlock statementBlock = (StatementBlock) methodDeclaration.getStatementBlock();
        Node[] statements = statementBlock.getStatements();

        //Visit parameters list
        methodDeclaration.getParameterList().accept(this);

        //Set outer reference field
        if(classSymbol.isInner() && methodDeclaration.isConstructor()) {
            addInstruction(ALOAD_0);
            addInstruction(ALOAD_1);
            generatePutInstanceField(classSymbol.findField(String.valueOf(Keyword.EXPRESSION_OUTER), classSymbol));
        }

        //Visit method body
        methodDeclaration.getStatementBlock().accept(this);

        //Generate empty return instruction
        if(statements.length == 0 || !(statements[statements.length - 1] instanceof ReturnStatement))
            addInstruction(RETURN);
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
        addLineNumber(variableDeclaration);

        Type variableType = getTypeFromNode(variableDeclaration.getType());

        //Add variable to variable table
        Variable variable = variableTable.addVariable(variableType, variableDeclaration.getName(), variableDeclaration.isConstant());

        Expression expression = (Expression) variableDeclaration.getInitializationExpression();

        if(expression != null) {
            //Visit initialization expression
            visitExpression(expression);
        } else {
            //Generate default variable initialization value
            addInstruction(variableType instanceof Primitive primitive
                    ? Instruction.forConstantZero(primitive)
                    : new Instruction(ACONST_NULL));
        }

        //Generate instructions
        addInstruction(Instruction.forStoring(variable));

        localTable.addLocal(variableType);
    }

    @Override
    public void visitStatementBlock(StatementBlock statementBlock) {
        addLineNumber(statementBlock);

        //Get the amount of previous local variables
        int previousLocalCount = localTable.getCount();
        int previousVariableCount = variableTable.getVariableCount();

        //Visit every statement
        for(Node node : statementBlock.getStatements())
            node.accept(this);

        //Remove local variables added in this statement block
        localTable.remove(localTable.getCount() - previousLocalCount);
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        addLineNumber(ifStatement);

        Branching branching = new Branching();

        //Visit condition expression
        visitCondition((Expression) ifStatement.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit statement body
        ifStatement.getStatementBlock().accept(this);

        if(ifStatement.getElseStatementBlock() != null) {
            branching.addJumpIndex(instructions.size(), programCounter);
            addInstruction(Instruction.forUnconditionalJump(programCounter));
        }

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);

        //Visit else statement body
        if(ifStatement.getElseStatementBlock() != null)
            ifStatement.getElseStatementBlock().accept(this);

        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
    }

    @Override
    public void visitForStatement(ForStatement forStatement) {
        addLineNumber(forStatement);

        //Get the amount of previous local variables
        int previousLocalCount = localTable.getCount();
        int previousVariableCount = variableTable.getVariableCount();

        //Visit variable initialization
        forStatement.getVariableInitialization().accept(this);

        Branching branching = new Branching();

        int jumpOffset = programCounter;
        generateStackMapFrame();

        //Visit condition expression
        visitCondition((Expression) forStatement.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit statement body
        loopStack.add(branching);
        forStatement.getStatementBlock().accept(this);
        loopStack.removeLast();

        //Resolve jumps to neutral-clause
        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);

        //Visit increment expression
        forStatement.getIncrementExpression().accept(this);

        addInstruction(Instruction.forUnconditionalJump(jumpOffset - programCounter));

        //Remove local variables added in this statement block
        localTable.remove(localTable.getCount() - previousLocalCount);
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        addLineNumber(whileStatement);

        Branching branching = new Branching();

        int jumpOffset = programCounter;
        generateStackMapFrame();

        //Visit condition expression
        visitCondition((Expression) whileStatement.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit statement body
        loopStack.add(branching);
        whileStatement.getStatementBlock().accept(this);
        loopStack.removeLast();

        //Resolve jumps to neutral-clause
        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);

        addInstruction(Instruction.forUnconditionalJump(jumpOffset - programCounter));

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
    }

    @Override
    public void visitDoStatement(DoStatement doStatement) {
        addLineNumber(doStatement);

        Branching branching = new Branching();

        int jumpOffset = programCounter;
        generateStackMapFrame();

        //Visit statement body
        loopStack.add(branching);
        doStatement.getStatementBlock().accept(this);
        loopStack.removeLast();

        //Resolve jumps to neutral-clause
        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);

        //Visit condition expression
        visitCondition((Expression) doStatement.getCondition(), branching, true);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, jumpOffset);

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
    }

    @Override
    public void visitSwitchStatement(SwitchStatement switchStatement) {
        addLineNumber(switchStatement);

        //Visit switch expression
        switchStatement.getExpression().accept(this);

        Branching branching = new Branching();
        Map<Integer, Node> caseValues = new TreeMap<>();
        SequencedMap<Integer, Integer> caseBranches = new LinkedHashMap<>();

        for(Node node : switchStatement.getCases()) {
            CaseStatement caseStatement = (CaseStatement) node;
            Literal.Integer value = (Literal.Integer) caseStatement.getExpression();

            //Add literal value to values
            caseValues.put(value.getValue(), caseStatement.getStatementBlock());
        }

        //Generate instruction
        generateStackMapFrame();
        int switchOffset = programCounter;
        addInstruction(LOOKUPSWITCH);

        //Add instruction padding
        int padding = (4 - (programCounter % 4)) % 4;
        for(int i = 0; i < padding; i++)
            addInstruction((byte) 0);

        //Add default jump
        int instructionOffset = instructions.size();
        addInstruction(new Instruction.Builder(4).build());

        //Add cases count
        addInstruction(new Instruction.Builder(4)
                .add(caseValues.size())
                .build());

        //Add cases branches
        for(Map.Entry<Integer, Node> value : caseValues.entrySet()) {
            addInstruction(new Instruction.Builder(4)
                    .add(value.getKey())
                    .build());
            caseBranches.put(instructions.size(), switchOffset);
            addInstruction(new Instruction.Builder(4).build());
        }

        //Resolve cases branches and generate cases
        for(Map.Entry<Integer, Node> value : caseValues.entrySet()) {
            //Resolve jump index
            generateStackMapFrame();
            Map.Entry<Integer, Integer> entry = caseBranches.pollFirstEntry();
            instructions.set(entry.getKey(), new Instruction.Builder(4)
                    .add(programCounter - entry.getValue())
                    .build());

            //Visit case statement block
            value.getValue().accept(this);

            branching.addJumpIndex(instructions.size(), programCounter);
            addInstruction(Instruction.forUnconditionalJump(programCounter));
        }

        //Resolve default jumps
        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
        instructions.set(instructionOffset, new Instruction.Builder(4)
                .add(programCounter - switchOffset)
                .build());
    }

    @Override
    public void visitMatchStatement(MatchStatement matchStatement) {
        addLineNumber(matchStatement);

        Branching branching = new Branching();

        Node[] cases = matchStatement.getCases();

        for(Node node : cases) {
            CaseStatement caseStatement = (CaseStatement) node;

            //Visit condition expression
            visitCondition((Expression) caseStatement.getExpression(), branching);

            //Resolve jumps to true-clause
            generateStackMapFrame();
            branching.resolveTrueJump(instructions, programCounter);

            //Visit case statement body
            caseStatement.getStatementBlock().accept(this);

            generateStackMapFrame();
            branching.addJumpIndex(instructions.size(), programCounter);
            addInstruction(Instruction.forUnconditionalJump(programCounter));

            //Resolve jumps to false-clause
            generateStackMapFrame();
            branching.resolveFalseJump(instructions, programCounter);
        }

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);

        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
    }

    @Override
    public void visitAssertStatement(AssertStatement assertStatement) {
        addLineNumber(assertStatement);

        Branching branching = new Branching();

        //Visit condition expression
        visitCondition((Expression) assertStatement.getCondition(), branching, true, true);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        ClassSymbol classSymbol = LibraryClasses.findClass(ClassName.ASSERTION_ERROR);

        //Add class name to the constant pool
        short index = (short) constantPool.addClassConstant(ClassName.ASSERTION_ERROR.toInternalQualifiedName());

        //Generate instructions
        addInstruction(new Instruction.Builder(NEW, 3)
                .add(index)
                .build());
        addInstruction(DUP);

        MethodSymbol methodSymbol = classSymbol.findConstructor(new Type[0], this.classSymbol, assertStatement);

        //Generate instructions
        generateCallSpecialMethod(methodSymbol);
        addInstruction(ATHROW);
        operandStack.pop(1);

        generateStackMapFrame();
        branching.addJumpIndex(instructions.size(), programCounter);
        addInstruction(Instruction.forUnconditionalJump(programCounter));

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
        branching.resolveJumps(instructions, programCounter);
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        addLineNumber(returnStatement);

        //Generate empty return statement
        if(returnStatement.getExpression() == null) {
            addInstruction(RETURN);
            return;
        }

        Expression expression = (Expression) returnStatement.getExpression();

        //Visit return expression
        visitExpression(expression);

        //Generate return instructions
        if(expression.getExpressionType() instanceof Primitive primitive) {
            switch(primitive.getPrimitiveKind()) {
                case BOOLEAN, BYTE, SHORT, CHAR, INTEGER -> addInstruction(IRETURN);
                case LONG -> addInstruction(LRETURN);
                case FLOAT -> addInstruction(FRETURN);
                case DOUBLE -> addInstruction(DRETURN);
            }
        } else {
            addInstruction(ARETURN);
        }
    }

    @Override
    public void visitBreakStatement(BreakStatement breakStatement) {
        addLineNumber(breakStatement);

        //Retrieve branching from current loop
        Branching branching = loopStack.getLast();

        //Add jump to false-clause
        branching.addFalseJumpIndex(instructions.size(), programCounter);
        addInstruction(Instruction.forUnconditionalJump(0));
    }

    @Override
    public void visitContinueStatement(ContinueStatement continueStatement) {
        addLineNumber(continueStatement);

        //Retrieve branching from current loop
        Branching branching = loopStack.getLast();

        //Add jump to neutral-clause
        branching.addJumpIndex(instructions.size(), programCounter);
        addInstruction(Instruction.forUnconditionalJump(0));
    }

    @Override
    public void visitThisStatement(ThisStatement thisStatement) {
        addLineNumber(thisStatement);

        //Retrieve this reference
        addInstruction(ALOAD_0);

        //Visit arguments list
        thisStatement.getArgumentList().accept(this);

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) thisStatement.getArgumentList());

        //Find constructor in current class
        MethodSymbol constructorSymbol = classSymbol.findConstructor(argumentTypes, classSymbol, thisStatement);

        //Generate instructions
        generateCallSpecialMethod(constructorSymbol);

        //Update current class reference
        localTable.setInitializedThis(classSymbol);
    }

    @Override
    public void visitSuperStatement(SuperStatement superStatement) {
        addLineNumber(superStatement);

        //Retrieve this reference
        addInstruction(ALOAD_0);

        //Visit arguments list
        superStatement.getArgumentList().accept(this);

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) superStatement.getArgumentList());

        ClassSymbol superclassSymbol = (ClassSymbol) classSymbol.getSuperclassSymbol();

        //Find constructor in superclass
        MethodSymbol constructorSymbol = superclassSymbol.findConstructor(argumentTypes, this.classSymbol, superStatement);

        //Generate instructions
        generateCallSpecialMethod(constructorSymbol);

        //Update current class reference
        localTable.setInitializedThis(classSymbol);

        //Visit instance field declarations
        visitFieldDeclarations(false);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        addLineNumber(expressionStatement);

        Expression expression = (Expression) expressionStatement.getExpression();

        //Visit assignement expression
        if(expression instanceof AssignementExpression assignementExpression) {
            if(assignementExpression.getVariable() instanceof SimpleName simpleName)
                visitSimpleNameAssignement(assignementExpression, simpleName, false);
            else if(assignementExpression.getVariable() instanceof MemberAccess memberAccess)
                visitMemberAccessAssignement(assignementExpression, memberAccess, false);
            else if(assignementExpression.getVariable() instanceof QualifiedName qualifiedName)
                visitQualifiedNameAssignement(assignementExpression, qualifiedName, false);
            else if(assignementExpression.getVariable() instanceof ArrayAccess arrayAccess)
                visitArrayAccessAssignement(assignementExpression, arrayAccess, false);
            return;
        }

        //Visit increment expression
        else if(expression instanceof UnaryExpression unaryExpression
                && (unaryExpression.getKind().isIncrement() || unaryExpression.getKind().isDecrement())) {
            if(unaryExpression.getExpression() instanceof SimpleName simpleName)
                visitSimpleNameIncrement(unaryExpression, simpleName, false);
            else if(unaryExpression.getExpression() instanceof MemberAccess memberAccess)
                visitMemberAccessIncrement(unaryExpression, memberAccess, false);
            else if(unaryExpression.getExpression() instanceof QualifiedName qualifiedName)
                visitQualifiedNameIncrement(unaryExpression, qualifiedName, false);
            else if(unaryExpression.getExpression() instanceof ArrayAccess arrayAccess)
                visitArrayAccessIncrement(unaryExpression, arrayAccess, false);
            return;
        }

        //Visit expression
        expression.accept(this);

        //Check method call
        if(expression instanceof MethodCall methodCall) {
            Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

            //Find method in current class
            MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, this.classSymbol, methodCall);

            //Generate instructions
            if(!(methodSymbol.getReturnType() instanceof Void))
                addInstruction(POP);
        }

        //Check access method call
        else if(expression instanceof MemberAccess memberAccess
                && memberAccess.getAccessor() instanceof MethodCall methodCall) {
            Expression member = (Expression) memberAccess.getMember();

            Type type = member.getExpressionType() == null
                    ? getTypeFromNode(memberAccess.getMember())
                    : member.getExpressionType();
            ClassSymbol classSymbol = ((Object) type).getClassSymbol();

            Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

            //Find method in class
            MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, this.classSymbol, memberAccess);

            //Generate instructions
            if(!(methodSymbol.getReturnType() instanceof Void))
                addInstruction(POP);
        }

        //Check class creation
        else if(expression instanceof ClassCreation) {
            addInstruction(POP);
        }
    }

    @Override
    public void visitLiteral(Literal literal) {
        addLineNumber(literal);

        //Generate instructions for constant literal
        if(literal instanceof Literal.Integer value)
            addInstruction(Instruction.forConstantInteger(value.getValue(), constantPool));
        else if(literal instanceof Literal.Long value)
            addInstruction(Instruction.forConstantLong(value.getValue(), constantPool));
        else if(literal instanceof Literal.Float value)
            addInstruction(Instruction.forConstantFloat(value.getValue(), constantPool));
        else if(literal instanceof Literal.Double value)
            addInstruction(Instruction.forConstantDouble(value.getValue(), constantPool));
        else if(literal instanceof Literal.Boolean value)
            addInstruction(value.getValue() ? ICONST_1 : ICONST_0);
        else if(literal instanceof Literal.Null)
            addInstruction(ACONST_NULL);
        else if(literal instanceof Literal.Char value) {
            addInstruction(Instruction.forConstantInteger(value.getValue(), constantPool));
            addInstruction(I2C);
        } else if(literal instanceof Literal.String string) {
            //Add string constant to constant pool
            short index = (short) constantPool.addStringConstant(String.valueOf(string.getValue()));

            //Generate instructions
            addInstruction(new Instruction.Builder(LDC_W, 3)
                    .add(index)
                    .build());
        }
    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) {
        addLineNumber(unaryExpression);

        Expression expression = (Expression) unaryExpression.getExpression();

        //Visit increment expression
        if(unaryExpression.getKind().isIncrement() || unaryExpression.getKind().isDecrement()) {
            if(expression instanceof SimpleName simpleName)
                visitSimpleNameIncrement(unaryExpression, simpleName, true);
            else if(expression instanceof MemberAccess memberAccess)
                visitMemberAccessIncrement(unaryExpression, memberAccess, true);
            else if(expression instanceof QualifiedName qualifiedName)
                visitQualifiedNameIncrement(unaryExpression, qualifiedName, true);
            else if(expression instanceof ArrayAccess arrayAccess)
                visitArrayAccessIncrement(unaryExpression, arrayAccess, true);
            return;
        }

        //Visit the expression
        expression.accept(this);

        Primitive resultType = (Primitive) unaryExpression.getExpressionType();
        Primitive primitive = (Primitive) expression.getExpressionType();

        //Promote primitive if not equal to result type
        if(!primitive.equals(resultType))
            promotePrimitive(primitive, resultType);

        //Generate instructions
        switch(unaryExpression.getKind()) {
            case OPERATION_NEGATE ->
                    addInstruction(Instruction.forNegationOperation(resultType));

            case BITWISE_NOT -> {
                switch(resultType.getPrimitiveKind()) {
                    case INTEGER, BYTE, SHORT, CHAR -> {
                        addInstruction(ICONST_M1);
                        addInstruction(IXOR);
                    }
                    case LONG -> {
                        addInstruction(Instruction.forConstantLong(-1, constantPool));
                        addInstruction(LXOR);
                    }
                }
            }
        }
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        addLineNumber(binaryExpression);

        //Visit comparison null elsewhere
        if(binaryExpression.getKind() == BinaryExpression.Kind.COMPARISON_NULL) {
            visitComparisonNull(binaryExpression);
            return;
        }

        Expression first = (Expression) binaryExpression.getFirst();
        Expression second = (Expression) binaryExpression.getSecond();

        Primitive primitive1 = (Primitive) first.getExpressionType();
        Primitive primitive2 = (Primitive) second.getExpressionType();

        Primitive operationType = Primitive.getWidestPrimitiveBetween(primitive1, primitive2);

        //Visit the first expression
        binaryExpression.getFirst().accept(this);

        //Promote primitive if not equal to result type
        if(!primitive1.equals(operationType))
            promotePrimitive(primitive1, operationType);

        //Visit the second expression
        binaryExpression.getSecond().accept(this);

        //Promote primitive if not equal to result type
        if(!primitive2.equals(operationType))
            promotePrimitive(primitive2, operationType);

        //Generate instructions
        switch(binaryExpression.getKind()) {
            case OPERATION_ADDITION ->
                    addInstruction(Instruction.forAdditionOperation(operationType));
            case OPERATION_SUBTRACTION ->
                    addInstruction(Instruction.forSubtractionOperation(operationType));
            case OPERATION_MULTIPLICATION ->
                    addInstruction(Instruction.forMultiplicationOperation(operationType));
            case OPERATION_DIVISION ->
                    addInstruction(Instruction.forDivisionOperation(operationType));
            case OPERATION_MODULO ->
                    addInstruction(Instruction.forModuloOperation(operationType));
            case BITWISE_AND ->
                    addInstruction(Instruction.forBitwiseAndOperation(operationType));
            case BITWISE_XOR ->
                    addInstruction(Instruction.forBitwiseXorOperation(operationType));
            case BITWISE_OR ->
                    addInstruction(Instruction.forBitwiseOrOperation(operationType));
            case BITWISE_SHIFT_LEFT ->
                    addInstruction(Instruction.forShiftLeftOperation(operationType));
            case BITWISE_SHIFT_RIGHT ->
                    addInstruction(Instruction.forShiftRightOperation(operationType));
            case BITWISE_SHIFT_RIGHT_ARITHMETIC ->
                    addInstruction(Instruction.forShiftRightArithmeticOperation(operationType));

            case COMPARISON_SPACESHIP -> {
                switch(operationType.getPrimitiveKind()) {
                    case INTEGER, BYTE, SHORT, CHAR -> {
                        addInstruction(I2L);
                        addInstruction(DUP2_X1);
                        addInstruction(POP_2);
                        addInstruction(I2L);
                        addInstruction(LCMP);
                        addInstruction(INEG);
                    }
                    case LONG -> addInstruction(LCMP);
                    case FLOAT -> addInstruction(FCMPG);
                    case DOUBLE -> addInstruction(DCMPG);
                }
            }
        }
    }

    @Override
    public void visitCastExpression(CastExpression castExpression) {
        addLineNumber(castExpression);

        //Visit cast expression
        castExpression.getExpression().accept(this);

        Expression expression = (Expression) castExpression.getExpression();

        Type expressionType = expression.getExpressionType();
        Type castType = castExpression.getExpressionType();

        //Generate primitive casting
        if(expressionType instanceof Primitive primitive) {
            Primitive.Kind kind = primitive.getPrimitiveKind();
            Primitive.Kind castKind = ((Primitive) castType).getPrimitiveKind();

            //Generate instructions
            switch(kind) {
                case INTEGER, BYTE, SHORT, CHAR -> {
                    switch(castKind) {
                        case LONG -> addInstruction(I2L);
                        case FLOAT -> addInstruction(I2F);
                        case DOUBLE -> addInstruction(I2D);
                    }
                }

                case LONG -> {
                    switch(castKind) {
                        case INTEGER, BYTE, SHORT, CHAR -> addInstruction(L2I);
                        case FLOAT -> addInstruction(L2F);
                        case DOUBLE -> addInstruction(L2D);
                    }
                }

                case FLOAT -> {
                    switch(castKind) {
                        case INTEGER, BYTE, SHORT, CHAR -> addInstruction(F2I);
                        case LONG -> addInstruction(F2L);
                        case DOUBLE -> addInstruction(F2D);
                    }
                }

                case DOUBLE -> {
                    switch(castKind) {
                        case INTEGER, BYTE, SHORT, CHAR -> addInstruction(D2I);
                        case LONG -> addInstruction(D2L);
                        case FLOAT -> addInstruction(D2F);
                    }
                }
            }

            //Generate instructions for byte, short, char
            if(castKind == Primitive.Kind.BYTE
                    || castKind == Primitive.Kind.SHORT
                    || castKind == Primitive.Kind.CHAR) {
                switch(castKind) {
                    case BYTE -> addInstruction(I2B);
                    case SHORT -> addInstruction(I2S);
                    case CHAR -> addInstruction(I2C);
                }
            }
        }

        //Generate object casting
        else if(expressionType instanceof Object) {
            ClassSymbol classSymbol = ((Object) castType).getClassSymbol();

            //Generate instructions
            addInstruction(new Instruction.Builder(CHECKCAST, 3)
                    .add((short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName()))
                    .build());
        }

        //Generate array casting
        else {
            Array array = (Array) expressionType;

            //Generate array descriptor
            String arrayDescriptor = String.valueOf(Descriptor.getDescriptorFromType(array));

            //Generate instructions
            addInstruction(new Instruction.Builder(CHECKCAST, 3)
                    .add((short) constantPool.addClassConstant(arrayDescriptor))
                    .build());
        }
    }

    @Override
    public void visitSimpleName(SimpleName simpleName) {
        addLineNumber(simpleName);

        Variable variable = variableTable.findVariableWithName(simpleName.getName());

        //Generate variable loading instructions
        if(variable != null) {
            addInstruction(Instruction.forLoading(variable));
        }

        //Generate field loading instructions
        else {
            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), classSymbol);

            //Generate instructions
            if(fieldSymbol.isStatic()) {
                generateGetStaticField(fieldSymbol);
            } else {
                addInstruction(ALOAD_0);
                generateGetInstanceField(fieldSymbol);
            }
        }
    }

    @Override
    public void visitMethodCall(MethodCall methodCall) {
        addLineNumber(methodCall);

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

        //Find method in current class
        MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, classSymbol, methodCall);

        if(!methodSymbol.isStatic())
            addInstruction(ALOAD_0);

        //Visit arguments list
        methodCall.getArgumentList().accept(this);

        //Generate instructions
        if(methodSymbol.getClassSymbol().isInterface()) {
            generateCallInterfaceMethod(methodSymbol);
        } else if(methodSymbol.isStatic()) {
            generateCallStaticMethod(methodSymbol);
        } else {
            generateCallVirtualMethod(methodSymbol);
        }
    }

    @Override
    public void visitMemberAccess(MemberAccess memberAccess) {
        addLineNumber(memberAccess);

        Expression member = (Expression) memberAccess.getMember();

        //Generate static access
        if(member.getExpressionType() == null) {
            Type memberType = getTypeFromNode(member);
            ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();

            //Generate field access
            if(memberAccess.getAccessor() instanceof SimpleName simpleName) {
                //Find field in class
                FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), this.classSymbol);

                //Generate instructions
                generateGetStaticField(fieldSymbol);
            }

            //Generate method call
            else if(memberAccess.getAccessor() instanceof MethodCall methodCall) {
                //Visit arguments list
                methodCall.getArgumentList().accept(this);

                Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

                //Find method in member class
                MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, this.classSymbol, methodCall);

                //Generate instructions
                generateCallStaticMethod(methodSymbol);
            }
        }

        //Generate instance access
        else {
            //Visit member
            member.accept(this);

            Type memberType = member.getExpressionType();

            //Generate array size field
            if(memberType.getKind() == Type.Kind.ARRAY) {
                addInstruction(ARRAYLENGTH);
                return;
            }

            ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();

            //Generate field access
            if(memberAccess.getAccessor() instanceof SimpleName simpleName) {
                //Find field in current class
                FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), this.classSymbol);

                //Generate instructions
                generateGetInstanceField(fieldSymbol);
            }

            //Generate method call
            else if(memberAccess.getAccessor() instanceof MethodCall methodCall) {
                //Visit arguments list
                methodCall.getArgumentList().accept(this);

                Type[] argumentTypes = getTypesFromArguments((ArgumentList) methodCall.getArgumentList());

                //Find method in member class
                MethodSymbol methodSymbol = classSymbol.findMethod(methodCall.getMethodName(), argumentTypes, this.classSymbol, methodCall);

                boolean isSpecial = member instanceof ThisExpression
                        || member instanceof SuperExpression;

                //Generate instructions
                if(methodSymbol.getClassSymbol().isInterface()) {
                    generateCallInterfaceMethod(methodSymbol);
                } else if(isSpecial) {
                    generateCallSpecialMethod(methodSymbol);
                } else {
                    generateCallVirtualMethod(methodSymbol);
                }
            }
        }
    }

    @Override
    public void visitQualifiedName(QualifiedName qualifiedName) {
        addLineNumber(qualifiedName);

        Expression member = (Expression) qualifiedName.getQualifiedName();

        //Generate static access
        if(member.getExpressionType() == null) {
            Type memberType = getTypeFromNode(member);
            ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();

            //Find field in class
            FieldSymbol fieldSymbol = classSymbol.findField(qualifiedName.getName(), this.classSymbol);

            //Generate instructions
            generateGetStaticField(fieldSymbol);
        }

        //Generate instance access
        else {
            //Visit member
            member.accept(this);

            Type memberType = member.getExpressionType();

            //Generate array size field
            if(memberType.getKind() == Type.Kind.ARRAY) {
                addInstruction(ARRAYLENGTH);
                return;
            }

            ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();

            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(qualifiedName.getName(), this.classSymbol);

            //Generate instructions
            generateGetInstanceField(fieldSymbol);
        }
    }

    @Override
    public void visitClassCreation(ClassCreation classCreation) {
        addLineNumber(classCreation);

        Type classType = classCreation.getExpressionType();
        ClassSymbol classSymbol = ((Object) classType).getClassSymbol();

        //Add class name to the constant pool
        short index = (short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName());

        //Load and create class object
        addInstruction(new Instruction.Builder(NEW, 3)
                .add(index)
                .build());

        //Duplicate reference
        addInstruction(DUP);

        //Visit arguments list
        classCreation.getArgumentList().accept(this);

        Type[] argumentTypes = getTypesFromArguments((ArgumentList) classCreation.getArgumentList());

        //Find constructor in class symbol
        MethodSymbol constructorSymbol = classSymbol.findConstructor(argumentTypes, this.classSymbol, classCreation);

        //Generate instructions
        generateCallSpecialMethod(constructorSymbol);
    }

    @Override
    public void visitArrayCreation(ArrayCreation arrayCreation) {
        addLineNumber(arrayCreation);

        //Visit initialization expression
        arrayCreation.getInitializationExpression().accept(this);

        Type arrayType = ((Array) arrayCreation.getExpressionType()).getType();

        //Generate array of primitive
        if(arrayType instanceof Primitive primitive) {
            //Generate instructions
            addInstruction(new Instruction.Builder(NEWARRAY, 2)
                    .add((byte) switch(primitive.getPrimitiveKind()) {
                        case BOOLEAN -> 4;
                        case CHAR -> 5;
                        case FLOAT -> 6;
                        case DOUBLE -> 7;
                        case BYTE -> 8;
                        case SHORT -> 9;
                        case INTEGER -> 10;
                        case LONG -> 11;
                    })
                    .build());

            //Push primitive array to stack
            operandStack.push(arrayCreation.getExpressionType());
        }

        //Generate array of object
        else if(arrayType instanceof Object object) {
            ClassSymbol classSymbol = object.getClassSymbol();

            //Generate instructions
            addInstruction(new Instruction.Builder(ANEWARRAY, 3)
                    .add((short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName()))
                    .build());
        }

        //Generate array of array
        else {
            //Generate array descriptor
            String arrayDescriptor = String.valueOf(Descriptor.getDescriptorFromType(arrayType));

            //Generate instructions
            addInstruction(new Instruction.Builder(ANEWARRAY, 3)
                    .add((short) constantPool.addClassConstant(arrayDescriptor))
                    .build());
        }
    }

    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {
        addLineNumber(arrayAccess);

        //Visit array and access expression
        arrayAccess.getArray().accept(this);
        arrayAccess.getAccessExpression().accept(this);

        Type variableType = arrayAccess.getExpressionType();

        //Generate instructions
        addInstruction(Instruction.forLoadingFromArray(variableType));
        if(variableType.getKind() == Type.Kind.OBJECT
                || variableType.getKind() == Type.Kind.ARRAY)
            operandStack.push(variableType);
    }

    @Override
    public void visitVariableAssignement(AssignementExpression assignementExpression) {
        addLineNumber(assignementExpression);

        //Visit variable as simple name
        if(assignementExpression.getVariable() instanceof SimpleName simpleName)
            visitSimpleNameAssignement(assignementExpression, simpleName, true);

        //Visit variable as member access
        else if(assignementExpression.getVariable() instanceof MemberAccess memberAccess)
            visitMemberAccessAssignement(assignementExpression, memberAccess, true);

        //Visit variable as qualified name
        else if(assignementExpression.getVariable() instanceof QualifiedName qualifiedName)
            visitQualifiedNameAssignement(assignementExpression, qualifiedName, true);

        //Visit variable as array access
        else if(assignementExpression.getVariable() instanceof ArrayAccess arrayAccess)
            visitArrayAccessAssignement(assignementExpression, arrayAccess, true);
    }

    @Override
    public void visitIfExpression(IfExpression ifExpression) {
        addLineNumber(ifExpression);

        Branching branching = new Branching();

        //Visit condition expression
        visitCondition((Expression) ifExpression.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit expression
        visitExpression((Expression) ifExpression.getExpression());

        operandStack.pop(1);
        branching.addJumpIndex(instructions.size(), programCounter);
        addInstruction(Instruction.forUnconditionalJump(programCounter));

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);

        //Visit else expression
        visitExpression((Expression) ifExpression.getElseExpression());

        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
    }

    @Override
    public void visitSumExpression(SumExpression sumExpression) {
        addLineNumber(sumExpression);

        //Get the amount of previous local variables
        int previousLocalCount = localTable.getCount();
        int previousVariableCount = variableTable.getVariableCount();

        //Visit variable initialization
        sumExpression.getVariableInitialization().accept(this);

        Branching branching = new Branching();

        Type type = sumExpression.getExpressionType();
        Primitive primitive = (Primitive) type;

        //Generate instructions
        addInstruction(Instruction.forConstantZero(primitive));

        generateStackMapFrame();
        int jumpOffset = programCounter;

        //Visit condition expression
        visitCondition((Expression) sumExpression.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit expression
        sumExpression.getExpression().accept(this);

        //Generate operation instructions
        addInstruction(Instruction.forAdditionOperation(primitive));

        //Visit increment expression
        sumExpression.getIncrementExpression().accept(this);

        addInstruction(Instruction.forUnconditionalJump(jumpOffset - programCounter));

        //Remove local variables added in this statement block
        localTable.remove(localTable.getCount() - previousLocalCount);
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
    }

    @Override
    public void visitProdExpression(ProdExpression prodExpression) {
        addLineNumber(prodExpression);

        //Get the amount of previous local variables
        int previousLocalCount = localTable.getCount();
        int previousVariableCount = variableTable.getVariableCount();

        //Visit variable initialization
        prodExpression.getVariableInitialization().accept(this);

        Branching branching = new Branching();

        Type type = prodExpression.getExpressionType();
        Primitive primitive = (Primitive) type;

        addInstruction(Instruction.forConstantOne(primitive));

        generateStackMapFrame();
        int jumpOffset = programCounter;

        //Visit condition expression
        visitCondition((Expression) prodExpression.getCondition(), branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Visit expression
        prodExpression.getExpression().accept(this);

        //Generate operation instructions
        addInstruction(Instruction.forMultiplicationOperation(primitive));

        //Visit increment expression
        prodExpression.getIncrementExpression().accept(this);

        addInstruction(Instruction.forUnconditionalJump(jumpOffset - programCounter));

        //Remove local variables added in this statement block
        localTable.remove(localTable.getCount() - previousLocalCount);
        variableTable.removeVariables(variableTable.getVariableCount() - previousVariableCount);

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);
    }

    @Override
    public void visitThisExpression(ThisExpression thisExpression) {
        //Generate instructions
        addInstruction(ALOAD_0);
    }

    @Override
    public void visitSuperExpression(SuperExpression superExpression) {
        //Generate instructions
        addInstruction(ALOAD_0);
    }

    @Override
    public void visitOuterExpression(OuterExpression outerExpression) {
        FieldSymbol fieldSymbol = classSymbol.findField(String.valueOf(Keyword.EXPRESSION_OUTER), classSymbol);

        //Generate instructions
        addInstruction(ALOAD_0);
        generateGetInstanceField(fieldSymbol);
    }

    @Override
    public void visitArgumentList(ArgumentList argumentList) {
        //Visit every argument
        for(Node node : argumentList.getArguments())
            visitExpression((Expression) node);
    }

    @Override
    public void visitParameterList(ParameterList parameterList) {
        //Visit every parameter
        for(Node node : parameterList.getParameters())
            node.accept(this);
    }

    @Override
    public void visitParameter(Parameter parameter) {
        Type type = getTypeFromNode(parameter.getType());

        //Add the variable to the table
        variableTable.addVariable(type, parameter.getName(), parameter.isConstant());
        localTable.addLocal(type);
    }

    @Override
    public void visitCaseStatement(CaseStatement caseStatement) {
        //Visit statements block
        caseStatement.getStatementBlock().accept(this);
    }



    //Alternative visit methods

    /**
     * Visits the given expression.
     * @param expression the expression
     */
    private void visitExpression(Expression expression) {
        if(expression.getExpressionType() instanceof Primitive primitive && primitive.isBooleanType()) {
            visitBooleanExpression(expression);
        } else {
            expression.accept(this);
        }
    }

    /**
     * Visits the given boolean expression.
     * @param expression the boolean expression
     */
    private void visitBooleanExpression(Expression expression) {
        //Visit boolean expression
        if(!(expression instanceof BinaryExpression) && !(expression instanceof UnaryExpression)) {
            expression.accept(this);
            return;
        }

        Branching branching = new Branching();

        //Visit condition expression
        visitCondition(expression, branching);

        //Resolve jumps to true-clause
        generateStackMapFrame();
        branching.resolveTrueJump(instructions, programCounter);

        //Generate instructions
        addInstruction(ICONST_1);

        operandStack.pop(1);
        branching.addJumpIndex(instructions.size(), programCounter);
        addInstruction(Instruction.forUnconditionalJump(programCounter));

        //Resolve jumps to false-clause
        generateStackMapFrame();
        branching.resolveFalseJump(instructions, programCounter);

        //Generate instructions
        addInstruction(ICONST_0);

        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
    }

    /**
     * Visits the given boolean expression with the given branching.
     * @param expression the boolean expression
     * @param branching the branching
     */
    private void visitCondition(Expression expression, Branching branching) {
        visitCondition(expression, branching, false);
    }

    /**
     * Visits the given boolean expression with the given branching.
     * @param expression the boolean expression
     * @param branching the branching
     */
    private void visitCondition(Expression expression, Branching branching, boolean jumpOnTrue) {
        visitCondition(expression, branching, jumpOnTrue, false);
    }

    /**
     * Visits the given boolean expression with the given branching.
     * @param expression the boolean expression
     * @param branching the branching
     * @param jumpOnTrue whether to jump (aka. short-circuit) on true
     * @param isInverted whether the condition is inverted
     */
    private void visitCondition(Expression expression, Branching branching, boolean jumpOnTrue, boolean isInverted) {
        //Visit binary expression
        if(expression instanceof BinaryExpression binaryExpression) {
            //Generate logical AND expression
            if(binaryExpression.getKind() == BinaryExpression.Kind.LOGICAL_AND) {
                visitCondition((Expression) binaryExpression.getFirst(), branching, false, isInverted);
                visitCondition((Expression) binaryExpression.getSecond(), branching, jumpOnTrue, isInverted);
            }

            //Generate logical OR expression
            else if(binaryExpression.getKind() == BinaryExpression.Kind.LOGICAL_OR) {
                visitCondition((Expression) binaryExpression.getFirst(), branching, true, isInverted);
                visitCondition((Expression) binaryExpression.getSecond(), branching, jumpOnTrue, isInverted);
            }

            //Generate type equality
            else if(binaryExpression.getKind() == BinaryExpression.Kind.TYPE_EQUAL
                    || binaryExpression.getKind() == BinaryExpression.Kind.TYPE_NOT_EQUAL) {
                visitTypeEquality(binaryExpression, branching, jumpOnTrue, isInverted);
            }

            //Generate reference equality
            else if(binaryExpression.getKind() == BinaryExpression.Kind.REFERENCE_EQUAL
                    || binaryExpression.getKind() == BinaryExpression.Kind.REFERENCE_NOT_EQUAL) {
                visitReferenceEquality(binaryExpression, branching, jumpOnTrue, isInverted);
            }

            //Generate binary expressions
            else {
                visitComparison(binaryExpression, branching, jumpOnTrue, isInverted);
            }
        }

        //Visit unary expression
        else if(expression instanceof UnaryExpression unaryExpression) {
            visitCondition((Expression) unaryExpression.getExpression(), branching, !jumpOnTrue, !isInverted);
        }

        //Visit boolean expression
        else {
            //Visit expression
            expression.accept(this);

            branching.addJumpIndex(instructions.size(), programCounter, jumpOnTrue, isInverted);

            //Generate instructions
            byte code = jumpOnTrue ? IFNE : IFEQ;
            addInstruction(new Instruction.Builder(code, 3).build());
        }
    }

    /**
     * Visits the given comparison expression with the given branching.
     * @param binaryExpression the comparison expression
     * @param branching the branching
     * @param jumpOnTrue whether to jump (aka. short-circuit) on true
     * @param isInverted whether the condition is inverted
     */
    private void visitComparison(BinaryExpression binaryExpression, Branching branching, boolean jumpOnTrue, boolean isInverted) {
        Expression first = (Expression) binaryExpression.getFirst();
        Expression second = (Expression) binaryExpression.getSecond();

        Primitive primitive1 = (Primitive) first.getExpressionType();
        Primitive primitive2 = (Primitive) second.getExpressionType();

        Primitive operationType = Primitive.getWidestPrimitiveBetween(primitive1, primitive2);

        //Visit the first expression
        binaryExpression.getFirst().accept(this);

        //Promote primitive if not equal to result type
        if(!primitive1.equals(operationType))
            promotePrimitive(primitive1, operationType);

        //Visit the second expression
        binaryExpression.getSecond().accept(this);

        //Promote primitive if not equal to result type
        if(!primitive2.equals(operationType))
            promotePrimitive(primitive2, operationType);

        //Generate comparison instructions
        switch(operationType.getPrimitiveKind()) {
            case LONG -> addInstruction(LCMP);
            case FLOAT -> addInstruction(FCMPG);
            case DOUBLE -> addInstruction(DCMPG);
        }

        branching.addJumpIndex(instructions.size(), programCounter, jumpOnTrue, isInverted);

        //Generate branching instructions
        byte code = switch(binaryExpression.getKind()) {
            case EQUALITY_EQUAL ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPEQ : IF_ICMPNE)
                            : (jumpOnTrue ? IFEQ : IFNE);

            case EQUALITY_NOT_EQUAL ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPNE : IF_ICMPEQ)
                            : (jumpOnTrue ? IFNE : IFEQ);

            case COMPARISON_GREATER ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPGT : IF_ICMPLE)
                            : (jumpOnTrue ? IFGT : IFLE);

            case COMPARISON_LESS ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPLT : IF_ICMPGE)
                            : (jumpOnTrue ? IFLT : IFGE);

            case COMPARISON_GREATER_EQUAL ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPGE : IF_ICMPLT)
                            : (jumpOnTrue ? IFGE : IFLT);

            case COMPARISON_LESS_EQUAL ->
                    operationType.getPrimitiveKind() == Primitive.Kind.INTEGER
                            ? (jumpOnTrue ? IF_ICMPLE : IF_ICMPGT)
                            : (jumpOnTrue ? IFLE : IFGT);

            default -> NOP;
        };

        addInstruction(new Instruction.Builder(code, 3).build());
    }

    /**
     * Visits the given type equality expression with the given branching.
     * @param binaryExpression the type equality expression
     * @param branching the branching
     * @param jumpOnTrue whether to jump (aka. short-circuit) on true
     * @param isInverted whether the condition is inverted
     */
    private void visitTypeEquality(BinaryExpression binaryExpression, Branching branching, boolean jumpOnTrue, boolean isInverted) {
        //Visit first expression
        binaryExpression.getFirst().accept(this);

        Type type = getTypeFromNode(binaryExpression.getSecond());
        ClassSymbol classSymbol = ((Object) type).getClassSymbol();

        //Add class reference to constant pool
        short classReference = (short) constantPool.addClassConstant(classSymbol.getClassInternalQualifiedName());

        //Generate instruction
        addInstruction(new Instruction.Builder(INSTANCEOF, 3)
                .add(classReference)
                .build());

        branching.addJumpIndex(instructions.size(), programCounter, jumpOnTrue, isInverted);

        //Generate instructions
        if(binaryExpression.getKind() == BinaryExpression.Kind.TYPE_EQUAL) {
            byte instruction = jumpOnTrue ? IFNE : IFEQ;
            addInstruction(new Instruction.Builder(instruction, 3).build());
        } else {
            byte instruction = jumpOnTrue ? IFEQ : IFNE;
            addInstruction(new Instruction.Builder(instruction, 3).build());
        }
    }

    /**
     * Visits the given reference equality expression with the given branching.
     * @param binaryExpression the reference equality expression
     * @param branching the branching
     * @param jumpOnTrue whether to jump (aka. short-circuit) on true
     * @param isInverted whether the condition is inverted
     */
    private void visitReferenceEquality(BinaryExpression binaryExpression, Branching branching, boolean jumpOnTrue, boolean isInverted) {
        //Visit expressions
        binaryExpression.getFirst().accept(this);
        binaryExpression.getSecond().accept(this);

        branching.addJumpIndex(instructions.size(), programCounter, jumpOnTrue, isInverted);

        //Generate instructions
        byte code = binaryExpression.getKind() == BinaryExpression.Kind.REFERENCE_EQUAL
                ? (jumpOnTrue ? IF_ACMPEQ : IF_ACMPNE)
                : (jumpOnTrue ? IF_ACMPNE : IF_ACMPEQ);

        addInstruction(new Instruction.Builder(code, 3).build());
    }

    /**
     * Visits the given binary expression as null coalescing operation.
     * @param binaryExpression the binary expression
     */
    private void visitComparisonNull(BinaryExpression binaryExpression) {
        //Visit the first expression
        binaryExpression.getFirst().accept(this);

        //Duplicate result
        addInstruction(DUP);

        Branching branching = new Branching();

        //Set jump
        branching.addJumpIndex(instructions.size(), programCounter);

        //Generate branching instruction
        addInstruction(new Instruction.Builder(IFNONNULL, 3).build());

        //Remove null result
        addInstruction(POP);

        //Visit the second expression
        binaryExpression.getSecond().accept(this);

        //Resolve jump
        generateStackMapFrame();
        branching.resolveJumps(instructions, programCounter);
    }

    /**
     * Visits the given assignement expression and generate augmented assignement.
     * @param assignementExpression the assignement expression
     * @param type the assignement type
     * @param isResultNeeded whether the result value is needed
     * @param isDoubleStackDepth whether the stack depth is two values deep (for array assignement)
     */
    private void visitAssignement(AssignementExpression assignementExpression, Type type,
                                  boolean isResultNeeded, boolean isDoubleStackDepth) {
        //Visit assignement expression
        visitExpression((Expression) assignementExpression.getExpression());

        //Perform augmented assignement operation
        if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT)
            generateAssignementOperation(assignementExpression, (Primitive) type);

        //Duplicate result after assignement
        if(isResultNeeded) {
            addInstruction(type instanceof Primitive primitive && primitive.isWideType()
                    ? isDoubleStackDepth ? DUP2_X2 : DUP2
                    : isDoubleStackDepth ? DUP_X2 : DUP);
        }
    }

    /**
     * Visits the given assignement expression as a simple name variable or field.
     * @param assignementExpression the assignement expression
     * @param simpleName the simple name
     * @param isResultNeeded whether the result value is needed
     */
    private void visitSimpleNameAssignement(AssignementExpression assignementExpression, SimpleName simpleName, boolean isResultNeeded) {
        Variable variable = variableTable.findVariableWithName(simpleName.getName());

        //Generate variable assignement
        if(variable != null) {
            //Load variable if augmented assignement
            if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT)
                addInstruction(Instruction.forLoading(variable));

            //Visit assignement
            visitAssignement(assignementExpression, variable.getType(), isResultNeeded, false);

            //Generate instructions
            addInstruction(Instruction.forStoring(variable));
        }

        //Generate field assignement
        else {
            FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), classSymbol);

            //Generate instructions
            addInstruction(ALOAD_0);

            //Load field if augmented assignement
            if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT) {
                addInstruction(DUP);
                generateGetField(fieldSymbol);
            }

            //Visit assignement
            visitAssignement(assignementExpression, fieldSymbol.getType(), isResultNeeded, false);

            //Generate instructions
            generatePutInstanceField(fieldSymbol);
        }
    }

    /**
     * Visits the given assignement expression as a member access field.
     * @param assignementExpression the assignement expression
     * @param memberAccess the member access
     * @param isResultNeeded whether the result value is needed
     */
    private void visitMemberAccessAssignement(AssignementExpression assignementExpression, MemberAccess memberAccess, boolean isResultNeeded) {
        Expression member = (Expression) memberAccess.getMember();
        SimpleName simpleName = (SimpleName) memberAccess.getAccessor();

        //Visit member
        member.accept(this);

        Type memberType = member.getExpressionType();
        ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();
        FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), this.classSymbol);

        //Load field if augmented assignement
        if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT) {
            addInstruction(DUP);
            generateGetInstanceField(fieldSymbol);
        }

        //Visit assignement
        visitAssignement(assignementExpression, fieldSymbol.getType(), isResultNeeded, false);

        //Generate instructions
        generatePutInstanceField(fieldSymbol);
    }

    /**
     * Visits the given assignement expression as a qualified name field.
     * @param assignementExpression the assignement expression
     * @param qualifiedName the qualified name
     * @param isResultNeeded whether the result value is needed
     */
    private void visitQualifiedNameAssignement(AssignementExpression assignementExpression, QualifiedName qualifiedName, boolean isResultNeeded) {
        Expression member = (Expression) qualifiedName.getQualifiedName();

        Type memberType = member.getExpressionType();

        if(memberType == null) {
            memberType = getTypeFromNode(member);
        } else {
            //Visit member
            member.accept(this);
        }

        ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();
        FieldSymbol fieldSymbol = classSymbol.findField(qualifiedName.getName(), this.classSymbol);

        //Load field if augmented assignement
        if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT) {
            //Generate instructions
            if(fieldSymbol.isStatic()) {
                generateGetStaticField(fieldSymbol);
            } else {
                addInstruction(DUP);
                generateGetInstanceField(fieldSymbol);
            }
        }

        //Visit assignement
        visitAssignement(assignementExpression, fieldSymbol.getType(), isResultNeeded, false);

        //Generate instructions
        generatePutField(fieldSymbol);
    }

    /**
     * Visits the given assignement expression as an array access.
     * @param assignementExpression the assignement expression
     * @param arrayAccess the array access
     * @param isResultNeeded whether the result value is needed
     */
    private void visitArrayAccessAssignement(AssignementExpression assignementExpression, ArrayAccess arrayAccess, boolean isResultNeeded) {
        //Visit array and access expression
        arrayAccess.getArray().accept(this);
        arrayAccess.getAccessExpression().accept(this);

        Expression expression = (Expression) assignementExpression.getExpression();

        //Load array if augmented assignement
        if(assignementExpression.getKind() != AssignementExpression.Kind.ASSIGNEMENT) {
            addInstruction(DUP2);
            addInstruction(Instruction.forLoadingFromArray(expression.getExpressionType()));
        }

        //Visit assignement
        visitAssignement(assignementExpression, expression.getExpressionType(), isResultNeeded, true);

        //Generate instructions
        addInstruction(Instruction.forStoringInArray(expression.getExpressionType()));
    }

    /**
     * Visits the given unary expression and generate increment expression.
     * @param unaryExpression the unary expression
     * @param primitive the increment primitive
     * @param isResultNeeded whether the result value is needed
     * @param isDoubleStackDepth whether the stack depth is two values deep (for array increment)
     */
    private void visitIncrement(UnaryExpression unaryExpression, Primitive primitive,
                                boolean isResultNeeded, boolean isDoubleStackDepth) {
        //Duplicate value before increment
        if(isResultNeeded) {
            if(unaryExpression.getKind() == UnaryExpression.Kind.POST_INCREMENT
                    || unaryExpression.getKind() == UnaryExpression.Kind.POST_DECREMENT)
                addInstruction(primitive.isWideType()
                        ? isDoubleStackDepth ? DUP2_X2 : DUP2
                        : isDoubleStackDepth ? DUP_X2 : DUP);
        }

        //Generate instructions
        addInstruction(Instruction.forConstantOne(primitive));

        //Perform increment
        addInstruction(unaryExpression.getKind().isIncrement()
                ? Instruction.forAdditionOperation(primitive)
                : Instruction.forSubtractionOperation(primitive));

        //Duplicate result after increment
        if(isResultNeeded) {
            if(unaryExpression.getKind() == UnaryExpression.Kind.PRE_INCREMENT
                    || unaryExpression.getKind() == UnaryExpression.Kind.PRE_DECREMENT)
                addInstruction(primitive.isWideType()
                        ? isDoubleStackDepth ? DUP2_X2 : DUP2
                        : isDoubleStackDepth ? DUP_X2 : DUP);
        }
    }

    /**
     * Visits the given increment expression as a simple name variable or field.
     * @param unaryExpression the increment expression
     * @param simpleName the simple name
     * @param isResultNeeded whether the result value is needed
     */
    private void visitSimpleNameIncrement(UnaryExpression unaryExpression, SimpleName simpleName, boolean isResultNeeded) {
        Variable variable = variableTable.findVariableWithName(simpleName.getName());

        //Generate variable increment
        if(variable != null) {
            Primitive primitive = (Primitive) variable.getType();

            //Load variable
            addInstruction(Instruction.forLoading(variable));

            //Visit increment
            visitIncrement(unaryExpression, primitive, isResultNeeded, false);

            //Generate instruction
            addInstruction(Instruction.forStoring(variable));
        }

        //Generate field increment
        else {
            FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), classSymbol);

            //Load field
            if(fieldSymbol.isStatic()) {
                generateGetStaticField(fieldSymbol);
            } else {
                addInstruction(ALOAD_0);
                generateGetInstanceField(fieldSymbol);
            }

            //Visit increment
            visitIncrement(unaryExpression, (Primitive) fieldSymbol.getType(), isResultNeeded, false);

            //Generate instructions
            addInstruction(ALOAD_0);
            addInstruction(SWAP);
            generatePutInstanceField(fieldSymbol);
        }
    }

    /**
     * Visits the given increment expression as a member access field.
     * @param unaryExpression the increment expression
     * @param memberAccess the member access
     * @param isResultNeeded whether the result value is needed
     */
    private void visitMemberAccessIncrement(UnaryExpression unaryExpression, MemberAccess memberAccess, boolean isResultNeeded) {
        Expression member = (Expression) memberAccess.getMember();
        SimpleName simpleName = (SimpleName) memberAccess.getAccessor();

        //Visit member
        member.accept(this);

        Type memberType = member.getExpressionType();
        ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();
        FieldSymbol fieldSymbol = classSymbol.findField(simpleName.getName(), this.classSymbol);

        //Load field
        generateGetInstanceField(fieldSymbol);

        //Visit increment
        visitIncrement(unaryExpression, (Primitive) fieldSymbol.getType(), isResultNeeded, false);

        //Generate instructions
        generatePutInstanceField(fieldSymbol);
    }

    /**
     * Visits the given increment expression as a qualified name field.
     * @param unaryExpression the increment expression
     * @param qualifiedName the qualified name
     * @param isResultNeeded whether the result value is needed
     */
    private void visitQualifiedNameIncrement(UnaryExpression unaryExpression, QualifiedName qualifiedName, boolean isResultNeeded) {
        Expression member = (Expression) qualifiedName.getQualifiedName();

        Type memberType = member.getExpressionType();

        if(memberType == null) {
            memberType = getTypeFromNode(member);
        } else {
            //Visit member
            member.accept(this);
        }

        ClassSymbol classSymbol = ((Object) memberType).getClassSymbol();
        FieldSymbol fieldSymbol = classSymbol.findField(qualifiedName.getName(), this.classSymbol);

        //Load field
        if(fieldSymbol.isStatic()) {
            generateGetStaticField(fieldSymbol);
        } else {
            addInstruction(DUP);
            generateGetInstanceField(fieldSymbol);
        }

        //Visit increment
        visitIncrement(unaryExpression, (Primitive) fieldSymbol.getType(), isResultNeeded, false);

        //Generate instructions
        generatePutField(fieldSymbol);
    }

    /**
     * Visits the given increment expression as an array access.
     * @param unaryExpression the increment expression
     * @param arrayAccess the array access
     * @param isResultNeeded whether the result is needed
     */
    private void visitArrayAccessIncrement(UnaryExpression unaryExpression, ArrayAccess arrayAccess, boolean isResultNeeded) {
        //Visit array and access expression
        arrayAccess.getArray().accept(this);
        arrayAccess.getAccessExpression().accept(this);

        Expression expression = (Expression) unaryExpression.getExpression();

        //Load array
        addInstruction(DUP2);
        addInstruction(Instruction.forLoadingFromArray(expression.getExpressionType()));

        //Visit increment
        visitIncrement(unaryExpression, (Primitive) expression.getExpressionType(), isResultNeeded, true);

        //Generate instructions
        addInstruction(Instruction.forStoringInArray(expression.getExpressionType()));
    }

    /**
     * Visits and generates the field declarations.
     * @param isStaticContext whether the current context is static
     */
    private void visitFieldDeclarations(boolean isStaticContext) {
        for(Node node : classDeclaration.getFields()) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) node;

            //Skip fields in wrong context
            if(isStaticContext != (fieldDeclaration.isStatic() || classSymbol.isStatic()))
                continue;

            VariableDeclaration variableDeclaration = (VariableDeclaration) fieldDeclaration.getVariable();

            //Skip fields with no initialization expression
            if(variableDeclaration.getInitializationExpression() == null)
                continue;

            //Generate this reference
            if(!isStaticContext)
                addInstruction(ALOAD_0);

            //Visit field initialization expression
            variableDeclaration.getInitializationExpression().accept(this);

            //Find field in current class
            FieldSymbol fieldSymbol = classSymbol.findField(variableDeclaration.getName(), classSymbol);

            //Generate instructions
            if(isStaticContext) {
                generatePutStaticField(fieldSymbol);
            } else {
                generatePutInstanceField(fieldSymbol);
            }
        }
    }



    //Utility methods

    /**
     * Promotes the given primitive value to the given expected primitive value
     * using primitive type casting instructions.
     * @param primitive the primitive value
     * @param expected the expected primitive value
     */
    private void promotePrimitive(Primitive primitive, Primitive expected) {
        switch(primitive.getPrimitiveKind()) {
            case INTEGER, BYTE, SHORT, CHAR -> {
                switch(expected.getPrimitiveKind()) {
                    case LONG -> addInstruction(I2L);
                    case FLOAT -> addInstruction(I2F);
                    case DOUBLE -> addInstruction(I2D);
                }
            }

            case LONG -> {
                switch(expected.getPrimitiveKind()) {
                    case FLOAT -> addInstruction(L2F);
                    case DOUBLE -> addInstruction(L2D);
                }
            }

            case FLOAT -> addInstruction(I2D);
        }
    }

    private void generateStackMapFrame() {
        stackMapTable.addFrame(operandStack, localTable, programCounter);
    }

    /**
     * Generates the instruction for the given assignement expression according to the given primitive.
     * @param assignementExpression the assignement expression
     * @param primitive the primitive
     */
    private void generateAssignementOperation(AssignementExpression assignementExpression, Primitive primitive) {
        switch(assignementExpression.getKind()) {
            case ASSIGNEMENT_ADDITION ->
                    addInstruction(Instruction.forAdditionOperation(primitive));
            case ASSIGNEMENT_SUBTRACTION ->
                    addInstruction(Instruction.forSubtractionOperation(primitive));
            case ASSIGNEMENT_MULTIPLICATION ->
                    addInstruction(Instruction.forMultiplicationOperation(primitive));
            case ASSIGNEMENT_DIVISION ->
                    addInstruction(Instruction.forDivisionOperation(primitive));
            case ASSIGNEMENT_MODULO ->
                    addInstruction(Instruction.forModuloOperation(primitive));
            case ASSIGNEMENT_BITWISE_AND ->
                    addInstruction(Instruction.forBitwiseAndOperation(primitive));
            case ASSIGNEMENT_BITWISE_XOR ->
                    addInstruction(Instruction.forBitwiseXorOperation(primitive));
            case ASSIGNEMENT_BITWISE_OR ->
                    addInstruction(Instruction.forBitwiseOrOperation(primitive));
            case ASSIGNEMENT_SHIFT_LEFT ->
                    addInstruction(Instruction.forShiftLeftOperation(primitive));
            case ASSIGNEMENT_SHIFT_RIGHT ->
                    addInstruction(Instruction.forShiftRightOperation(primitive));
            case ASSIGNEMENT_SHIFT_RIGHT_ARITHMETIC ->
                    addInstruction(Instruction.forShiftRightArithmeticOperation(primitive));
        }
    }

    /**
     * Generates the instructions for getting the given field symbol.
     * @param fieldSymbol the field symbol
     */
    private void generateGetInstanceField(FieldSymbol fieldSymbol) {
        addInstruction(new Instruction.Builder(GETFIELD, 3)
                .add(getFieldReference(fieldSymbol))
                .build());

        //Add field type to stack
        operandStack.push(fieldSymbol.getType());
    }

    /**
     * Generates the instructions for getting the given static field symbol.
     * @param fieldSymbol the field symbol
     */
    private void generateGetStaticField(FieldSymbol fieldSymbol) {
        addInstruction(new Instruction.Builder(GETSTATIC, 3)
                .add(getFieldReference(fieldSymbol))
                .build());

        //Add field type to stack
        operandStack.push(fieldSymbol.getType());
    }

    private void generateGetField(FieldSymbol fieldSymbol) {
        if(fieldSymbol.isStatic()) {
            generateGetStaticField(fieldSymbol);
        } else {
            generateGetInstanceField(fieldSymbol);
        }
    }

    /**
     * Generates the instructions for putting the given field symbol.
     * @param fieldSymbol the field symbol
     */
    private void generatePutInstanceField(FieldSymbol fieldSymbol) {
        addInstruction(new Instruction.Builder(PUTFIELD, 3)
                .add(getFieldReference(fieldSymbol))
                .build());
    }

    /**
     * Generates the instructions for putting the given static field symbol.
     * @param fieldSymbol the field symbol
     */
    private void generatePutStaticField(FieldSymbol fieldSymbol) {
        addInstruction(new Instruction.Builder(PUTSTATIC, 3)
                .add(getFieldReference(fieldSymbol))
                .build());
    }

    private void generatePutField(FieldSymbol fieldSymbol) {
        if(fieldSymbol.isStatic()) {
            generatePutStaticField(fieldSymbol);
        } else {
            generatePutInstanceField(fieldSymbol);
        }
    }

    /**
     * Generates the instructions for calling the given virtual method symbol.
     * @param methodSymbol the method symbol
     */
    private void generateCallVirtualMethod(MethodSymbol methodSymbol) {
        addInstruction(new Instruction.Builder(INVOKEVIRTUAL, 3)
                .add(getMethodReference(methodSymbol))
                .build());

        //Remove arguments from stack
        operandStack.pop(methodSymbol.getParameterCount());
        //Add return value to stack
        if(!(methodSymbol.getReturnType() instanceof Void))
            operandStack.push(methodSymbol.getReturnType());
    }

    /**
     * Generates the instructions for calling the given special method symbol.
     * @param methodSymbol the method symbol
     */
    private void generateCallSpecialMethod(MethodSymbol methodSymbol) {
        addInstruction(new Instruction.Builder(INVOKESPECIAL, 3)
                .add(getMethodReference(methodSymbol))
                .build());

        //Remove arguments from stack
        operandStack.pop(methodSymbol.getParameterCount());
        //Add return value to stack
        if(!(methodSymbol.getReturnType() instanceof Void))
            operandStack.push(methodSymbol.getReturnType());
    }

    /**
     * Generates the instructions for calling the given static method symbol.
     * @param methodSymbol the method symbol
     */
    private void generateCallStaticMethod(MethodSymbol methodSymbol) {
        addInstruction(new Instruction.Builder(INVOKESTATIC, 3)
                .add(getMethodReference(methodSymbol))
                .build());

        //Remove arguments from stack
        operandStack.pop(methodSymbol.getParameterCount());
        //Add return value to stack
        if(!(methodSymbol.getReturnType() instanceof Void))
            operandStack.push(methodSymbol.getReturnType());
    }

    /**
     * Generates the instructions for calling the given interface method symbol.
     * @param methodSymbol the method symbol
     */
    private void generateCallInterfaceMethod(MethodSymbol methodSymbol) {
        //Compute parameters count
        int parameterCount = methodSymbol.getParameterCount();
        for(Type type : methodSymbol.getParameterTypes()) {
            if(type instanceof Primitive primitive && primitive.isWideType())
                parameterCount++;
        }

        addInstruction(new Instruction.Builder(INVOKEINTERFACE, 5)
                .add(getInterfaceMethodReference(methodSymbol))
                .add((byte) (parameterCount + 1))
                .add((byte) 0)
                .build());

        //Remove arguments from stack
        operandStack.pop(methodSymbol.getParameterCount());
        //Add return value to stack
        if(!(methodSymbol.getReturnType() instanceof Void))
            operandStack.push(methodSymbol.getReturnType());
    }

    /**
     * Returns the field reference index in the constant pool of the given field symbol.
     * @param fieldSymbol the field symbol
     * @return the field reference index
     */
    private short getFieldReference(FieldSymbol fieldSymbol) {
        return (short) constantPool.addFieldRefConstant(
                fieldSymbol.getClassSymbol().getClassInternalQualifiedName(),
                fieldSymbol.getName(),
                String.valueOf(Descriptor.generateFieldDescriptor(fieldSymbol)));
    }

    /**
     * Returns the method reference index in the constant pool of the given method symbol.
     * @param methodSymbol the method symbol
     * @return the method reference index
     */
    private short getMethodReference(MethodSymbol methodSymbol) {
        return (short) constantPool.addMethodRefConstant(
                methodSymbol.getClassSymbol().getClassInternalQualifiedName(),
                methodSymbol.getName(),
                String.valueOf(Descriptor.generateMethodDescriptor(methodSymbol)));
    }

    /**
     * Returns the interface method reference index in the constant pool of the given method symbol.
     * @param methodSymbol the method symbol
     * @return the interface method reference index
     */
    private short getInterfaceMethodReference(MethodSymbol methodSymbol) {
        return (short) constantPool.addInterfaceMethodRefConstant(
                methodSymbol.getClassSymbol().getClassInternalQualifiedName(),
                methodSymbol.getName(),
                String.valueOf(Descriptor.generateMethodDescriptor(methodSymbol)));
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
        Node[] arguments = argumentList.getArguments();
        Type[] argumentTypes = new Type[arguments.length];

        for(int i = 0; i < arguments.length; i++)
            argumentTypes[i] = ((Expression) arguments[i]).getExpressionType();

        return argumentTypes;
    }
}
