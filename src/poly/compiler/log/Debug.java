package poly.compiler.log;

import poly.compiler.file.LibraryFile;
import poly.compiler.file.SourceCode;
import poly.compiler.parser.tree.ContentNode;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.tokenizer.Token;

import java.util.List;
import java.util.Map;

/**
 * The Debug class. This class is a tool that provides the ability to print the code
 * during each step of the compilation process. This class cannot be enabled or used
 * by a user from the command interface.
 * @author Vincent Philippe (@vincent64)
 */
public class Debug {
    /** Whether to print debug messages in the console. Can only be modified here, directly in the code. */
    private static final boolean DEBUG = false;

    private Debug() { }

    /**
     * Prints the given source codes as a list of full file names.
     * @param sourceCodes the source codes
     */
    public static void printSourceFiles(List<SourceCode> sourceCodes) {
        if(DEBUG) {
            System.out.println("\nSOURCE FILES ------------------------------------------------------------------------");
            for(SourceCode sourceCode : sourceCodes)
                System.out.println(sourceCode.getFullFileName());
        }
    }

    /**
     * Prints the given library files as a list of class qualified names.
     * @param libraryFiles the library files
     */
    public static void printLibraryFiles(Map<?, LibraryFile> libraryFiles) {
        if(DEBUG) {
            System.out.println("\nLIBRARY FILES -----------------------------------------------------------------------");
            System.out.println("Total library files: " + libraryFiles.size());
            for(LibraryFile libraryFile : libraryFiles.values())
                System.out.println(libraryFile.getClassName().toInternalQualifiedName());
        }
    }

    /**
     * Prints the given tokens array.
     * @param tokens the tokens
     */
    public static void printTokens(Token[] tokens) {
        if(DEBUG) {
            System.out.println("\nTOKENS ------------------------------------------------------------------------------");
            for(Token token : tokens)
                System.out.println(token);
        }
    }

    /**
     * Prints the given AST as a tree-shaped nodes structure.
     * @param contentNode the content node
     */
    public static void printTree(ContentNode contentNode) {
        if(DEBUG) {
            System.out.println("\nABSTRACT SYNTAX TREE ----------------------------------------------------------------");
            System.out.println(contentNode);
        }
    }

    /**
     * Prints the given symbol and its subsymbols.
     * @param symbol the symbol
     */
    public static void printSymbols(Symbol symbol) {
        if(DEBUG) {
            System.out.println("\nPROJECT SYMBOLS ---------------------------------------------------------------------");
            System.out.println(symbol);
        }
    }
}
