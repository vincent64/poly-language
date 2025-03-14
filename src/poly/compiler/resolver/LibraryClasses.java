package poly.compiler.resolver;

import poly.compiler.Parameters;
import poly.compiler.file.ClassLoader;
import poly.compiler.file.LibraryFile;
import poly.compiler.file.LibraryReader;
import poly.compiler.log.Debug;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.PackageSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * The LibraryClasses class. This class contains every symbol from the libraries.
 * @author Vincent Philippe (@vincent64)
 */
public class LibraryClasses {
    private static final PackageSymbol rootSymbol = new PackageSymbol("");
    private static final List<LibraryFile> libraryFiles = new ArrayList<>();

    private LibraryClasses() { }

    /**
     * Loads the project libraries.
     */
    public static void loadLibraries() {
        //Load Java runtime library
        libraryFiles.addAll(LibraryReader.loadJavaLibraryFiles());

        //Load Poly standard library
        if(Parameters.getPolylibPath() != null) {
            LibraryReader polyLibraryReader = LibraryReader.fromPolyLibrary(Parameters.getPolylibPath());
            libraryFiles.addAll(polyLibraryReader.getLibraryFiles());
        }

        //Load third-party libraries
        if(Parameters.getLibraryPath() != null) {
            LibraryReader libraryReader = LibraryReader.fromLibrary(Parameters.getLibraryPath());
            libraryFiles.addAll(libraryReader.getLibraryFiles());
        }

        //Print the libraries for debugging
        Debug.printLibraryFiles(libraryFiles);
    }

    /**
     * Returns the symbol from the given symbol name.
     * @param symbolName the symbol name
     * @return the symbol (null if none found)
     */
    public static Symbol findSymbol(String symbolName) {
        return rootSymbol.findSymbol(symbolName);
    }

    /**
     * Returns the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol (null if not found)
     */
    public static ClassSymbol findClass(ClassName className) {
        ClassSymbol classSymbol = rootSymbol.findClass(className);

        //Return loaded class symbol
        if(classSymbol != null)
            return classSymbol;

        return loadClass(className);
    }

    /**
     * Loads the class symbol from the given class name.
     * @param className the class name
     * @return the class symbol
     */
    private static ClassSymbol loadClass(ClassName className) {
        LibraryFile libraryFile = findLibraryFile(className);

        //Make sure the library file exists
        if(libraryFile == null)
            return null;

        return ClassLoader.load(libraryFile).read();
    }

    /**
     * Returns the library file corresponding to the given class name.
     * @param className the class name
     * @return the library file (null if not found)
     */
    private static LibraryFile findLibraryFile(ClassName className) {
        for(LibraryFile libraryFile : libraryFiles) {
            if(libraryFile.getClassName().isSimilar(className))
                return libraryFile;
        }

        return null;
    }

    /**
     * Returns the root package symbol.
     * @return the root symbol
     */
    public static PackageSymbol getRootSymbol() {
        return rootSymbol;
    }
}
