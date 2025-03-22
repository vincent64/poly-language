package poly.compiler.analyzer;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.parser.tree.expression.MemberAccess;
import poly.compiler.parser.tree.expression.MethodCall;
import poly.compiler.parser.tree.variable.ArgumentList;
import poly.compiler.resolver.symbol.ClassSymbol;

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
    Node transformOperationOverload(Node node, Expression expression, Expression argumentExpression, String methodName) {
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
}
