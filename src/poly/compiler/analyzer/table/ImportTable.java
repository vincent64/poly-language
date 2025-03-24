package poly.compiler.analyzer.table;

import poly.compiler.error.ResolvingError;
import poly.compiler.parser.tree.ContentNode;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.statement.ImportStatement;
import poly.compiler.resolver.Classes;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.util.ClassName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The ImportTable class. This class contains the list of import statements in a source file.
 * @author Vincent Philippe (@vincent64)
 */
public class ImportTable {
    private final Map<Import, ClassSymbol> importations;

    /**
     * Constructs the importations table with the given content node.
     * @param contentNode the content node
     */
    public ImportTable(ContentNode contentNode) {
        //Initialize imports list
        importations = new HashMap<>();

        //Add every import statement to the list
        for(Node node : contentNode.getImports())
            importations.put(new Import((ImportStatement) node), null);
    }

    /**
     * Resolves the importations table and make sure they are valid.
     */
    public void resolve() {
        Set<ClassName> classNames = new HashSet<>();
        Set<String> importNames = new HashSet<>();

        for(Import importation : importations.keySet()) {
            ClassSymbol classSymbol = Classes.findClass(importation.getClassName());

            //Make sure the importation is resolvable
            if(classSymbol == null)
                new ResolvingError.UnresolvableImportation(importation.getImportStatement(), importation.getLastName());

            //Make sure there is no ambiguous importations
            if(!importNames.add(importation.getLastName()))
                new ResolvingError.AmbiguousImportation(importation.getImportStatement(), importation.getLastName());

            //Make sure there is no duplicate importations
            if(!classNames.add(classSymbol.getClassName()))
                new ResolvingError.DuplicateImportation(importation.getImportStatement(), classSymbol.getClassName());

            //Update the importation entry
            importations.replace(importation, classSymbol);
        }
    }

    /**
     * Returns the class symbol from the given last class name.
     * @param lastName the last class name
     * @return the class symbol (null if not found)
     */
    public ClassSymbol findImportation(String lastName) {
        for(Map.Entry<Import, ClassSymbol> importation : importations.entrySet()) {
            if(importation.getKey().getLastName().equals(lastName))
                return importation.getValue();
        }

        return null;
    }
}
