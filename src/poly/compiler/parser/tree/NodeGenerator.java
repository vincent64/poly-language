package poly.compiler.parser.tree;

import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.parser.tree.expression.MemberAccess;
import poly.compiler.parser.tree.variable.Parameter;

/**
 * The NodeGenerator class. This class is used to generate nodes outside the parser.
 * This class contains many methods to generate implicit node to the AST after
 * the parsing process and before the code generation process.
 * @author Vincent Philippe (@vincent64)
 */
public class NodeGenerator {
    private NodeGenerator() { }

    /**
     * Generates an empty member access node with the given member expression and accessor expression.
     * @param member the member expression
     * @param accessor the accessor expression
     * @return the member access node
     */
    public static MemberAccess forMemberAccess(Expression member, Expression accessor) {
        MemberAccess memberAccess = new MemberAccess(null);
        memberAccess.setMember(member);
        memberAccess.setAccessor(accessor);

        return memberAccess;
    }

    /**
     * Generates an empty parameter node with the given node type.
     * @param type the node type
     * @return the parameter node
     */
    public static Parameter forEmptyParameter(Expression type) {
        Parameter parameter = new Parameter(null);
        parameter.setName("");
        parameter.setType(type);

        return parameter;
    }
}
