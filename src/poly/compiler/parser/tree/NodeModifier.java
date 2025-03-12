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
    default Node visitAssertStatement(AssertStatement assertStatement) { return assertStatement; }
    default Node visitBreakStatement(BreakStatement breakStatement) { return breakStatement; }
    default Node visitContinueStatement(ContinueStatement continueStatement) { return continueStatement; }
    default Node visitDoStatement(DoStatement doStatement) { return doStatement; }
    default Node visitExpressionStatement(ExpressionStatement expressionStatement) { return expressionStatement; }
    default Node visitForStatement(ForStatement forStatement) { return forStatement; }
    default Node visitIfStatement(IfStatement ifStatement) { return ifStatement; }
    default Node visitImportStatement(ImportStatement importStatement) { return importStatement; }
    default Node visitMatchStatement(MatchStatement matchStatement) { return matchStatement; }
    default Node visitReturnStatement(ReturnStatement returnStatement) { return returnStatement; }
    default Node visitStatementBlock(StatementBlock statementBlock) { return statementBlock; }
    default Node visitSuperStatement(SuperStatement superStatement) { return superStatement; }
    default Node visitSwitchStatement(SwitchStatement switchStatement) { return switchStatement; }
    default Node visitThisStatement(ThisStatement thisStatement) { return thisStatement; }
    default Node visitWhileStatement(WhileStatement whileStatement) { return whileStatement; }

    //Expression nodes
    default Node visitArrayAccess(ArrayAccess arrayAccess) { return arrayAccess; }
    default Node visitArrayCreation(ArrayCreation arrayCreation) { return arrayCreation; }
    default Node visitArrayType(ArrayType arrayType) { return arrayType; }
    default Node visitBinaryExpression(BinaryExpression binaryExpression) { return binaryExpression; }
    default Node visitCaseStatement(CaseStatement caseStatement) { return caseStatement; }
    default Node visitCastExpression(CastExpression castExpression) { return castExpression; }
    default Node visitClassCreation(ClassCreation classCreation) { return classCreation; }
    default Node visitIfExpression(IfExpression ifExpression) { return ifExpression; }
    default Node visitLiteral(Literal literal) { return literal; }
    default Node visitMemberAccess(MemberAccess memberAccess) { return memberAccess; }
    default Node visitMethodCall(MethodCall methodCall) { return methodCall; }
    default Node visitOuterExpression(OuterExpression outerExpression) { return outerExpression; }
    default Node visitPrimitiveAttribute(PrimitiveAttribute primitiveAttribute) { return primitiveAttribute; }
    default Node visitPrimitiveType(PrimitiveType primitiveType) { return primitiveType; }
    default Node visitProdExpression(ProdExpression prodExpression) { return prodExpression; }
    default Node visitQualifiedName(QualifiedName qualifiedName) { return qualifiedName; }
    default Node visitSimpleName(SimpleName simpleName) { return simpleName; }
    default Node visitSumExpression(SumExpression sumExpression) { return sumExpression; }
    default Node visitSuperExpression(SuperExpression superExpression) { return superExpression; }
    default Node visitThisExpression(ThisExpression thisExpression) { return thisExpression; }
    default Node visitUnaryExpression(UnaryExpression unaryExpression) { return unaryExpression; }

    //Variable nodes
    default Node visitArgumentList(ArgumentList argumentList) { return argumentList; }
    default Node visitParameter(Parameter parameter) { return parameter; }
    default Node visitParameterList(ParameterList parameterList) { return parameterList; }
    default Node visitVariableAssignement(AssignementExpression assignementExpression) { return assignementExpression; }
    default Node visitVariableDeclaration(VariableDeclaration variableDeclaration) { return variableDeclaration; }
}
