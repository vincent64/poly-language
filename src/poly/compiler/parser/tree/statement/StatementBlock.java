package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.util.NodeStringifier;

/**
 * The StatementBlock class. This class represents a block containing any amount of statement.
 * There may be zero, one or multiple statements in the statement block.
 * @author Vincent Philippe (@vincent64)
 */
public class StatementBlock extends Statement {
    private Statement[] statements;

    public StatementBlock(Meta meta) {
        super(meta);

        //Initialize statements array
        statements = new Statement[0];
    }

    public void addStatement(Statement node) {
        statements = (Statement[]) add(statements, node);
    }

    public void addFirstStatement(Statement node) {
        statements = (Statement[]) addFirst(statements, node);
    }

    public Statement[] getStatements() {
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
