package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The StatementBlock class. This class represents a block containing any amount of statement.
 * There may be zero, one or multiple statements in the statement block.
 * @author Vincent Philippe (@vincent64)
 */
public class StatementBlock extends Node {
    private Node[] statements;

    public StatementBlock(Meta meta) {
        super(meta);

        //Initialize statements array
        statements = new Node[0];
    }

    public void addStatement(Node node) {
        statements = Node.add(statements, node);
    }

    public void addFirstStatement(Node node) {
        statements = Node.addFirst(statements, node);
    }

    public Node[] getStatements() {
        return statements;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitStatementBlock(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitStatementBlock(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("StatementBlock");
        string.addString("Statements:");
        string.addNodes(statements);

        return string.toString();
    }
}
