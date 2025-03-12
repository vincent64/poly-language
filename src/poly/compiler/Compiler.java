package poly.compiler;

import poly.compiler.file.JarBuilder;
import poly.compiler.file.ProjectReader;
import poly.compiler.file.ProjectWriter;
import poly.compiler.file.SourceCode;
import poly.compiler.log.Verbose;
import poly.compiler.output.ClassFile;
import poly.compiler.resolver.LibraryClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * The Compiler class. This class is the entry point of the compiling process.
 * @author Vincent Philippe (@vincent64)
 */
public class Compiler {
    /** The compiler current version. */
    public static final String VERSION = "0.1";

    /** The compiler starting message. */
    private static final String HEADER = """
             ⬡ The Poly programming language compiler (v%s)
             Invented and coded by Vincent Philippe (@vincent64)
            """;

    /** The compiler information message. */
    private static final String HEADER_INFORMATION = """
             ┌──────────────────────────────────────────────────────────────────┐
             │ INFORMATION: Poly is a very recent programming language,         │
             │ and its compiler is currently in its early phase.                │
             │ If you notice any bug or you suspect an issue with the compiler, │
             │ raise an issue report on the GitHub page immediatly. Thanks!     │
             └──────────────────────────────────────────────────────────────────┘
            """;

    /** The compiler help message. */
    private static final String HEADER_HELP = """
              General command syntax:
                poly [project] [arguments] [options]
             
              Project:          The path to the project folder.
             
              Arguments:
                --src [path]    The path to the source folder.
                --out [path]    The path to the output folder.
                --libs [path]   The path to the library folder.
             
              Options:
                -warnings       Prints warning messages.
                -verbose        Prints every compilation-related message.
                -optimize       Optimizes the output code.
                -jar            Produces a JAR file with the output code.
             
              View the Poly documentation for more information.
             """;

    public static void main(String[] arguments) {
        //Print header message (in red because it looks cooler)
        System.err.println(HEADER.formatted(VERSION));
        System.err.flush();
        System.out.println(HEADER_INFORMATION);
        System.out.flush();

        //Print help message if no arguments
        if(arguments.length == 0) {
            System.out.println(HEADER_HELP);
            return;
        }

        //Initialize parameters from arguments
        Parameters.initialize(arguments);

        //Load project library files
        LibraryClasses.loadLibraries();

        //Load project source code files
        List<SourceCode> sourceCodes = ProjectReader.read(Parameters.getSourcePath());

        //Measure starting compilation time
        long startCompilationTime = System.currentTimeMillis();

        //Start compilation
        List<ClassFile> classFiles = compile(sourceCodes);

        //Compute total compilation time in milliseconds
        long compilationTime = System.currentTimeMillis() - startCompilationTime;

        //Write project output class files
        ProjectWriter.write(classFiles, Parameters.getOutputPath());
        JarBuilder.build();

        //Print success message
        System.out.println("Project successfully compiled in " + compilationTime + " ms.");
    }

    /**
     * Compiles the given list of source codes and returns the list of compiled class files.
     * @param sourceCodes the list of source codes
     * @return the list of compiled class file
     */
    private static List<ClassFile> compile(List<SourceCode> sourceCodes) {
        //Initialize compilation units list
        List<CompilationUnit> compilationUnits = new ArrayList<>();

        //Instanciate a compilation unit for each source code
        for(SourceCode sourceCode : sourceCodes)
            compilationUnits.add(new CompilationUnit(sourceCode));

        long startTime = System.currentTimeMillis();

        //Tokenize the code
        for(CompilationUnit unit : compilationUnits)
            unit.tokenize();

        //Parse the code
        for(CompilationUnit unit : compilationUnits)
            unit.parse();

        long parsingTime = System.currentTimeMillis();
        Verbose.println("Parsing completed in " + (parsingTime - startTime) + " ms.");

        //Resolve classes
        for(CompilationUnit unit : compilationUnits)
            unit.resolveClass();

        //Resolve symbols
        for(CompilationUnit unit : compilationUnits)
            unit.resolveSymbols();

        //Resolve project
        for(CompilationUnit unit : compilationUnits)
            unit.resolveDependencies();

        long resolvingTime = System.currentTimeMillis();
        Verbose.println("Resolving completed in " + (resolvingTime - parsingTime) + " ms.");

        //Analyze the code
        for(CompilationUnit unit : compilationUnits)
            unit.analyze();

        //Optimize output code
        for(CompilationUnit unit : compilationUnits)
            unit.optimize();

        long analyzingTime = System.currentTimeMillis();
        Verbose.println("Analyzing completed in " + (analyzingTime - resolvingTime) + " ms.");

        //Initialize class files list
        List<ClassFile> classFiles = new ArrayList<>();

        //Generate the output code
        for(CompilationUnit unit : compilationUnits)
            classFiles.addAll(unit.generate());

        return classFiles;
    }
}