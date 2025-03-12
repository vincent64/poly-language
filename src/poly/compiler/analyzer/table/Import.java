package poly.compiler.analyzer.table;

import poly.compiler.parser.tree.statement.ImportStatement;
import poly.compiler.util.ClassName;

/**
 * The Import class. This class represents an importation in a source code file,
 * and contains the import statement and the class name of the imported class.
 * @author Vincent Philippe (@vincent64)
 */
public class Import {
    private final ImportStatement importStatement;
    private final ClassName className;

    /**
     * Constructs an importation from the given import statement.
     * @param importStatement the import statement
     */
    public Import(ImportStatement importStatement) {
        this.importStatement = importStatement;

        //Get class name from package name
        className = ClassName.fromNodeQualifiedName(importStatement.getPackageName());
    }

    /**
     * Returns the import statement node.
     * @return the import statement
     */
    public ImportStatement getImportStatement() {
        return importStatement;
    }

    /**
     * Returns the class name.
     * @return the class name
     */
    public ClassName getClassName() {
        return className;
    }

    /**
     * Returns the first name of the class name.
     * @return the first name
     */
    public String getFirstName() {
        return className.getFirst();
    }

    /**
     * Returns the last name of the class name.
     * @return the last name
     */
    public String getLastName() {
        return className.getLast();
    }
}
