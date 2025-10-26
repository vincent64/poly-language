package poly.compiler.analyzer;

import poly.compiler.analyzer.content.OperatorMethod;
import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Primitive;
import poly.compiler.analyzer.type.Type;
import poly.compiler.error.AnalyzingError;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.*;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.util.ClassName;

/**
 * The Transformer class. This class is used by the Analyzer class to transform
 * some complex parts of the AST. This includes transforming operation overload.
 * @author Vincent Philippe (@vincent64)
 */
public final class Transformer {
    private final Analyzer analyzer;
    private final ClassSymbol classSymbol;

    Transformer(Analyzer analyzer, ClassSymbol classSymbol) {
        this.analyzer = analyzer;
        this.classSymbol = classSymbol;
    }

    /**
     * Transforms the given node to an operation overload method call with the given method name,
     * argument expression and the member expression.
     * @param node the operation node
     * @param expression the member expression
     * @param argumentExpression the argument expression
     * @param methodName the operation overload method name
     * @return the transformed node
     */
    Expression transformOperationOverload(Node node, Expression expression, Expression argumentExpression, String methodName) {
        //Generate operation method call
        MethodCall methodCall = new MethodCall(node.getMeta());
        methodCall.setMethodName(methodName);

        //Add argument expression to arguments list
        ArgumentList argumentList = new ArgumentList(node.getMeta());
        if(argumentExpression != null)
            argumentList.addArgument(argumentExpression);
        methodCall.setArgumentList(argumentList);

        //Transform operation to member access
        MemberAccess memberAccess = new MemberAccess(node.getMeta());
        memberAccess.setMember(expression);
        memberAccess.setAccessor(methodCall);

        return memberAccess.accept(analyzer);
    }

    /**
     * Transforms the given method call to an operation overload method invocation.
     * @param methodCall the method call
     * @return the transformed node
     */
    Expression transformMethodCallOperationOverload(MethodCall methodCall) {
        //Generate operation method invocation
        MethodCall methodInvocation = new MethodCall(methodCall.getMeta());
        methodInvocation.setMethodName(OperatorMethod.Name.METHOD_INVOCATION);
        methodInvocation.setArgumentList(methodCall.getArgumentList());

        //Transform operation to member access
        MemberAccess memberAccess = new MemberAccess(methodCall.getMeta());
        SimpleName simpleName = new SimpleName(methodCall.getMeta());
        simpleName.setName(methodCall.getMethodName());
        memberAccess.setMember(simpleName);
        memberAccess.setAccessor(methodInvocation);

        return memberAccess.accept(analyzer);
    }

    /**
     * Transforms the given method call to an operation overload method invocation.
     * @param methodCall the method call
     * @return the transformed node
     */
    Expression transformExpressionCallOperationOverload(MethodCall methodCall) {
        //Generate operation method invocation
        MethodCall methodInvocation = new MethodCall(methodCall.getMeta());
        methodInvocation.setMethodName(OperatorMethod.Name.METHOD_INVOCATION);
        methodInvocation.setArgumentList(methodCall.getArgumentList());

        //Transform operation to member access
        MemberAccess memberAccess = new MemberAccess(methodCall.getMeta());
        memberAccess.setMember(methodCall.getMethod());
        memberAccess.setAccessor(methodInvocation);

        return memberAccess.accept(analyzer);
    }

    /**
     * Transforms the given member access class creation node to an inner class creation node.
     * @param memberAccess the member access node
     * @param classCreation the class creation node
     * @return the transformed node
     */
    Expression transformInnerClassCreation(MemberAccess memberAccess, ClassCreation classCreation) {
        //Add member expression to arguments list
        ArgumentList argumentList = new ArgumentList(classCreation.getMeta());
        argumentList.addArgument(memberAccess.getMember());

        //Add arguments to arguments list
        for(Expression expression : ((ArgumentList) classCreation.getArgumentList()).getArguments())
            argumentList.addArgument(expression);

        //Transform class creation
        ClassCreation innerClassCreation = new ClassCreation(classCreation.getMeta());
        innerClassCreation.setArgumentList(argumentList);
        innerClassCreation.setType(classCreation.getType());

        return innerClassCreation;
    }

    /**
     * Transforms the given binary expression node to a string concatenation method call node.
     * @param binaryExpression the binary expression node
     * @return the transformed node
     */
    Expression transformStringConcatenation(BinaryExpression binaryExpression) {
        Expression first = binaryExpression.getFirst();
        Expression second = binaryExpression.getSecond();
        Type secondType = second.getExpressionType();

        //Add implicit string conversion
        if(!(secondType instanceof Object object)
                || !object.getClassSymbol().getClassInternalQualifiedName()
                .equals(ClassName.STRING.toInternalQualifiedName())) {
            //Generate method call
            ArgumentList argumentList = new ArgumentList(binaryExpression.getMeta());
            argumentList.addArgument(second);
            MethodCall methodCall = new MethodCall(binaryExpression.getMeta());
            methodCall.setMethodName("valueOf");
            methodCall.setArgumentList(argumentList);

            //Generate member access
            MemberAccess memberAccess = new MemberAccess(binaryExpression.getMeta());
            memberAccess.setMember(QualifiedName.fromClassName(ClassName.STRING));
            memberAccess.setAccessor(methodCall);
            binaryExpression.setSecond(memberAccess);
            second = memberAccess;
        }

        return transformOperationOverload(binaryExpression, first, second, "concat");
    }

    /**
     * Transforms the given binary expression node to a string repeating method call node.
     * @param binaryExpression the binary expression node
     * @return the transformed node
     */
    Expression transformStringRepeating(BinaryExpression binaryExpression) {
        Expression first = binaryExpression.getFirst();
        Expression second = binaryExpression.getSecond();
        Type secondType = second.getExpressionType();

        //Make sure the second expression is an integer
        if(!(secondType instanceof Primitive primitive)
                || primitive.getPrimitiveKind() != Primitive.Kind.INTEGER)
            new AnalyzingError.TypeConversion(binaryExpression, secondType, new Primitive(Primitive.Kind.INTEGER));

        return transformOperationOverload(binaryExpression, first, second, "repeat");
    }

    /**
     * Transforms the given array access node to a string character access method call node.
     * @param arrayAccess the array access node
     * @return the transformed node
     */
    Expression transformStringCharacterAccess(ArrayAccess arrayAccess) {
        Expression array = arrayAccess.getArray();
        Expression access = arrayAccess.getAccessExpression();
        Type accessType = access.getExpressionType();

        //Make sure the access expression is an integer
        if(!(accessType instanceof Primitive primitive)
                || primitive.getPrimitiveKind() != Primitive.Kind.INTEGER)
            new AnalyzingError.TypeConversion(arrayAccess, accessType, new Primitive(Primitive.Kind.INTEGER));

        return transformOperationOverload(arrayAccess, array, access, "charAt");
    }
}
