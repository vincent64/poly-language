package poly.compiler.parser.tree;

import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.statement.*;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.parser.tree.variable.Parameter;
import poly.compiler.parser.tree.variable.ParameterList;
import poly.compiler.parser.tree.variable.VariableDeclaration;

/**
 * The NodeModifier interface. This interface is used to visit each node in the AST.
 * Unlike the NodeVisitor interface, this interface requires every method to return
 * a node - be it the node given in the argument itself, or a new node.
 * This interface, like the NodeVisitor interface, follows the visitor design pattern,
 * and is used by the Analyzer and Optimizer classes to visit and update the AST.
 * Every method returns by default the node provided in the method argument, thus
 * providing the implementer the flexibility by not being constrained to override
 * every method of this interface.
 * @author Vincent Philippe (@vincent64)
 */
public interface NodeModifier {
    //General nodes
    default Node visitContentNode(ContentNode contentNode) { return contentNode; }
    default Node visitClassDeclaration(ClassDeclaration classDeclaration) { return classDeclaration; }
    default Node visitFieldDeclaration(FieldDeclaration fieldDeclaration) { return fieldDeclaration; }
    default Node visitMethodDeclaration(MethodDeclaration methodDeclaration) { return methodDeclaration; }

    //Statement nodes
    default Statement visitAssertStatement(AssertStatement assertStatement) { return assertStatement; }
    default Statement visitBreakStatement(BreakStatement breakStatement) { return breakStatement; }
    default Statement visitCaseStatement(CaseStatement caseStatement) { return caseStatement; }
    default Statement visitContinueStatement(ContinueStatement continueStatement) { return continueStatement; }
    default Statement visitDoStatement(DoStatement doStatement) { return doStatement; }
    default Statement visitExpressionStatement(ExpressionStatement expressionStatement) { return expressionStatement; }
    default Statement visitForStatement(ForStatement forStatement) { return forStatement; }
    default Statement visitIfStatement(IfStatement ifStatement) { return ifStatement; }
    default Statement visitImportStatement(ImportStatement importStatement) { return importStatement; }
    default Statement visitMatchStatement(MatchStatement matchStatement) { return matchStatement; }
    default Statement visitReturnStatement(ReturnStatement returnStatement) { return returnStatement; }
    default Statement visitStatementBlock(StatementBlock statementBlock) { return statementBlock; }
    default Statement visitSuperStatement(SuperStatement superStatement) { return superStatement; }
    default Statement visitSwitchStatement(SwitchStatement switchStatement) { return switchStatement; }
    default Statement visitThisStatement(ThisStatement thisStatement) { return thisStatement; }
    default Statement visitThrowStatement(ThrowStatement throwStatement) { return throwStatement; }
    default Statement visitTryStatement(TryStatement tryStatement) { return tryStatement; }
    default Statement visitVariableDeclaration(VariableDeclaration variableDeclaration) { return variableDeclaration; }
    default Statement visitWhileStatement(WhileStatement whileStatement) { return whileStatement; }

    //Expression nodes
    default Expression visitArrayAccess(ArrayAccess arrayAccess) { return arrayAccess; }
    default Expression visitArrayCreation(ArrayCreation arrayCreation) { return arrayCreation; }
    default Expression visitArrayType(ArrayType arrayType) { return arrayType; }
    default Expression visitAssignmentExpression(AssignmentExpression assignmentExpression) { return assignmentExpression; }
    default Expression visitBinaryExpression(BinaryExpression binaryExpression) { return binaryExpression; }
    default Expression visitCastExpression(CastExpression castExpression) { return castExpression; }
    default Expression visitClassCreation(ClassCreation classCreation) { return classCreation; }
    default Expression visitIfExpression(IfExpression ifExpression) { return ifExpression; }
    default Expression visitLiteral(Literal literal) { return literal; }
    default Expression visitMemberAccess(MemberAccess memberAccess) { return memberAccess; }
    default Expression visitMethodCall(MethodCall methodCall) { return methodCall; }
    default Expression visitOuterExpression(OuterExpression outerExpression) { return outerExpression; }
    default Expression visitPrimitiveAttribute(PrimitiveAttribute primitiveAttribute) { return primitiveAttribute; }
    default Expression visitPrimitiveType(PrimitiveType primitiveType) { return primitiveType; }
    default Expression visitProdExpression(ProdExpression prodExpression) { return prodExpression; }
    default Expression visitQualifiedName(QualifiedName qualifiedName) { return qualifiedName; }
    default Expression visitSimpleName(SimpleName simpleName) { return simpleName; }
    default Expression visitSumExpression(SumExpression sumExpression) { return sumExpression; }
    default Expression visitSuperExpression(SuperExpression superExpression) { return superExpression; }
    default Expression visitThisExpression(ThisExpression thisExpression) { return thisExpression; }
    default Expression visitUnaryExpression(UnaryExpression unaryExpression) { return unaryExpression; }

    //Variable nodes
    default Node visitArgumentList(ArgumentList argumentList) { return argumentList; }
    default Node visitParameter(Parameter parameter) { return parameter; }
    default Node visitParameterList(ParameterList parameterList) { return parameterList; }
}
