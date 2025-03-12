package poly.compiler;

import poly.compiler.analyzer.Analyzer;
import poly.compiler.analyzer.Optimizer;
import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.error.LimitError;
import poly.compiler.file.SourceCode;
import poly.compiler.generator.Generator;
import poly.compiler.log.Debug;
import poly.compiler.output.ClassFile;
import poly.compiler.output.jvm.Limitations;
import poly.compiler.parser.Parser;
import poly.compiler.parser.tree.ContentNode;
import poly.compiler.resolver.*;
import poly.compiler.tokenizer.Token;
import poly.compiler.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * The CompilationUnit class. This class is used to compile a single source code file.
 * It contains the list of classes declared in the source code file.
 * @author Vincent Philippe (@vincent64)
 */
public class CompilationUnit {
    private final SourceCode sourceCode;
    private Token[] tokens;
    private ContentNode contentNode;
    private ImportTable importTable;
    private List<ClassDefinition> classDefinitions;

    /**
     * Constructs a compilation unit with the given source code.
     * @param sourceCode the source code
     */
    public CompilationUnit(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Tokenizes the source code.
     */
    public void tokenize() {
        tokens = Tokenizer.getInstance(sourceCode.getFullFileName(), sourceCode.getContent()).tokenize();

        //Print the tokens for debugging
        Debug.printTokens(tokens);
    }

    /**
     * Parses the code.
     */
    public void parse() {
        contentNode = Parser.getInstance(tokens).parse();

        //Print the AST for debugging
        Debug.printTree(contentNode);
    }

    /**
     * Resolves class definitions and builds the importations table.
     */
    public void resolveClass() {
        //Build the importations table
        importTable = new ImportTable(contentNode);

        //Resolve class definitions
        classDefinitions = Resolver.getInstance(contentNode, sourceCode.getPackageName()).resolve();
    }

    /**
     * Resolves class symbols.
     */
    public void resolveSymbols() {
        for(ClassDefinition definition : classDefinitions)
            SymbolResolver.getInstance(definition, importTable).resolve();

        //Print the project symbols for debugging
        Debug.printSymbols(ProjectClasses.getRootSymbol());
    }

    /**
     * Resolve dependencies and check inheritance.
     */
    public void resolveDependencies() {
        for(ClassDefinition definition : classDefinitions)
            DependencyResolver.getInstance(definition).resolve();

        //Print the project symbols for debugging
        Debug.printSymbols(ProjectClasses.getRootSymbol());
    }

    /**
     * Analyzes the code.
     */
    public void analyze() {
        for(ClassDefinition definition : classDefinitions)
            Analyzer.getInstance(importTable, definition).analyze();
    }

    /**
     * Optimizes the code. This step is optional.
     */
    public void optimize() {
        //Optimize the code if optimizations parameter is enabled
        if(Parameters.optimizations()) {
            for(ClassDefinition definition : classDefinitions)
                Optimizer.getInstance().optimize();
        }
    }

    /**
     * Generates the output bytecode and returns the class files.
     * @return the class files
     */
    public List<ClassFile> generate() {
        List<ClassFile> classFiles = new ArrayList<>();

        //Generate every class
        for(ClassDefinition definition : classDefinitions)
            classFiles.add(Generator.getInstance(definition, importTable, sourceCode.getFileName()).generate());

        //Make sure the constant pool does not overflow
        for(int i = 0; i < classFiles.size(); i++) {
            if(classFiles.get(i).getConstantPool().getEntryCount() > Limitations.MAX_CONSTANT_POOL_ENTRY_COUNT)
                new LimitError.ConstantPoolSize(classDefinitions.get(i));
        }

        return classFiles;
    }
}
