package poly.compiler.parser.tree;

import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.statement.*;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.parser.tree.variable.VariableDeclaration;

/**
 * The NodeVisitor interface. This interface is used to visit each node in the AST.
 * This interface follows the visitor design pattern, and is used by
 * the Generator class to generate the output bytecode.
 * Every method contains a default empty body, to provide the implementer the
 * flexibility by not being constrained to override every method of this interface.
 * @author Vincent Philippe (@vincent64)
 */
public interface NodeVisitor {
    //General nodes
    default void visitContentNode(ContentNode contentNode) {}
    default void visitClassDeclaration(ClassDeclaration classDeclaration) {}
    default void visitFieldDeclaration(FieldDeclaration fieldDeclaration) {}
    default void visitMethodDeclaration(MethodDeclaration methodDeclaration) {}

    //Statement nodes
    default void visitAssertStatement(AssertStatement assertStatement) {}
    default void visitBreakStatement(BreakStatement breakStatement) {}
    default void visitCaseStatement(CaseStatement caseStatement) {}
    default void visitContinueStatement(ContinueStatement continueStatement) {}
    default void visitDoStatement(DoStatement doStatement) {}
    default void visitExpressionStatement(ExpressionStatement expressionStatement) {}
    default void visitForStatement(ForStatement forStatement) {}
    default void visitIfStatement(IfStatement ifStatement) {}
    default void visitImportStatement(ImportStatement importStatement) {}
    default void visitMatchStatement(MatchStatement matchStatement) {}
    default void visitReturnStatement(ReturnStatement returnStatement) {}
    default void visitStatementBlock(StatementBlock statementBlock) {}
    default void visitSuperStatement(SuperStatement superStatement) {}
    default void visitSwitchStatement(SwitchStatement switchStatement) {}
    default void visitThisStatement(ThisStatement thisStatement) {}
    default void visitThrowStatement(ThrowStatement throwStatement) {}
    default void visitTryStatement(TryStatement tryStatement) {}
    default void visitVariableDeclaration(VariableDeclaration variableDeclaration) {}
    default void visitWhileStatement(WhileStatement whileStatement) {}

    //Expression nodes
    default void visitArrayAccess(ArrayAccess arrayAccess) {}
    default void visitArrayCreation(ArrayCreation arrayCreation) {}
    default void visitArrayType(ArrayType arrayType) {}
    default void visitAssignmentExpression(AssignmentExpression assignmentExpression) {}
    default void visitBinaryExpression(BinaryExpression binaryExpression) {}
    default void visitCastExpression(CastExpression castExpression) {}
    default void visitClassCreation(ClassCreation classCreation) {}
    default void visitIfExpression(IfExpression ifExpression) {}
    default void visitLiteral(Literal literal) {}
    default void visitMemberAccess(MemberAccess memberAccess) {}
    default void visitMethodCall(MethodCall methodCall) {}
    default void visitOuterExpression(OuterExpression outerExpression) {}
    default void visitPrimitiveAttribute(PrimitiveAttribute primitiveAttribute) {}
    default void visitPrimitiveType(PrimitiveType primitiveType) {}
    default void visitProdExpression(ProdExpression prodExpression) {}
    default void visitQualifiedName(QualifiedName qualifiedName) {}
    default void visitSimpleName(SimpleName simpleName) {}
    default void visitSumExpression(SumExpression sumExpression) {}
    default void visitSuperExpression(SuperExpression superExpression) {}
    default void visitThisExpression(ThisExpression thisExpression) {}
    default void visitUnaryExpression(UnaryExpression unaryExpression) {}

    //Variable nodes
    default void visitArgumentList(ArgumentList argumentList) {}
    default void visitParameter(Parameter parameter) {}
    default void visitParameterList(ParameterList parameterList) {}
}
