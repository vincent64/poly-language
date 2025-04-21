package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The StatementBlock class. This class represents a block containing any amount of statement.
 * There may be zero, one or multiple statements in the statement block.
 * @author Vincent Philippe (@vincent64)
 */
public class StatementBlock extends Statement {
    private List<Statement> statements;

    public StatementBlock(Meta meta) {
        super(meta);

        //Initialize statements list
        statements = new ArrayList<>();
    }

    public void addStatement(Statement node) {
        statements.add(node);
    }

    public void addFirstStatement(Statement node) {
        statements.addFirst(node);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitStatementBlock(this);
    }

    @Override
    public Statement accept(NodeModifier modifier) {
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
