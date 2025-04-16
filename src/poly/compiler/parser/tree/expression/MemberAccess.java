package poly.compiler.parser.tree.expression;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The MemberAccess class. This class represents a member access, and contains
 * the node of the member being accessed, and the accessor node.
 * @author Vincent Philippe (@vincent64)
 */
public class MemberAccess extends Expression {
    private Expression member;
    private Expression accessor;

    public MemberAccess(Meta meta) {
        super(meta);
    }

    public void setMember(Expression node) {
        member = node;
    }

    public void setAccessor(Expression node) {
        accessor = node;
    }

    public Expression getMember() {
        return member;
    }

    public Expression getAccessor() {
        return accessor;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitMemberAccess(this);
    }

    @Override
    public Expression accept(NodeModifier modifier) {
        return modifier.visitMemberAccess(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("MemberAccess");
        string.addString("Member:");
        string.addNode(member);
        string.addString("Accessor:");
        string.addNode(accessor);

        return string.toString();
    }
}
