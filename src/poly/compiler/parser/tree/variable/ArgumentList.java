package poly.compiler.parser.tree.variable;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.parser.tree.expression.Expression;
import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The ArgumentList class. This class represents the list of arguments, or expression,
 * used when calling a method.
 * @author Vincent Philippe (@vincent64)
 */
public class ArgumentList extends Node {
    private List<Expression> arguments;

    public ArgumentList(Meta meta) {
        super(meta);

        //Initialize arguments list
        arguments = new ArrayList<>();
    }

    public void addArgument(Expression node) {
        arguments.add(node);
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitArgumentList(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitArgumentList(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ArgumentList");
        string.addString("Arguments:");
        string.addNodes(arguments);

        return string.toString();
    }
}
