package poly.compiler.parser.tree.statement;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.NodeModifier;
import poly.compiler.parser.tree.NodeVisitor;
import poly.compiler.tokenizer.Token;
import poly.compiler.util.NodeStringifier;

/**
 * The ImportStatement class. This class represents an importation statement,
 * containing the package name to be imported.
 * @author Vincent Philippe (@vincent64)
 */
public class ImportStatement extends Statement {
    private Node packageName;
    private String aliasName;

    public ImportStatement(Meta meta) {
        super(meta);
    }

    public void setPackageName(Node node) {
        packageName = node;
    }

    public void setAliasName(Token token) {
        aliasName = String.valueOf(token.getContent());
    }

    public Node getPackageName() {
        return packageName;
    }

    public String getAliasName() {
        return aliasName;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visitImportStatement(this);
    }

    @Override
    public Node accept(NodeModifier modifier) {
        return modifier.visitImportStatement(this);
    }

    @Override
    public String toString() {
        NodeStringifier string = new NodeStringifier("ImportStatement");
        string.addString("Package name:");
        string.addNode(packageName);
        string.addString("Alias name: " + aliasName);

        return string.toString();
    }
}
