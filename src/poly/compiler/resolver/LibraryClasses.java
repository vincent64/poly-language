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
import poly.compiler.warning.GeneralWarning;

import java.util.*;

/**
 * The LibraryClasses class. This class contains every symbol from the libraries.
 * @author Vincent Philippe (@vincent64)
 */
public class LibraryClasses {
    private static final PackageSymbol rootSymbol = new PackageSymbol("");
    private static final Map<String, LibraryFile> libraryFiles = new HashMap<>();

    private LibraryClasses() { }

    /**
     * Loads the project libraries.
     */
    public static void loadLibraries() {
        //Load Java runtime library
        for(LibraryFile libraryFile : LibraryReader.loadJavaLibraryFiles())
            addLibraryFile(libraryFile);

        //Load Poly standard library
        if(Parameters.getPolylibPath() != null) {
            LibraryReader polyLibraryReader = LibraryReader.fromPolyLibrary(Parameters.getPolylibPath());
            for(LibraryFile libraryFile : polyLibraryReader.getLibraryFiles())
                addLibraryFile(libraryFile);
        }

        //Load third-party libraries
        if(Parameters.getLibraryPath() != null) {
            LibraryReader libraryReader = LibraryReader.fromLibrary(Parameters.getLibraryPath());
            for(LibraryFile libraryFile : libraryReader.getLibraryFiles())
                addLibraryFile(libraryFile);
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
     * Adds the given library file to the library files mapping.
     * This method throws a compilation warning if there are colliding class names.
     * @param libraryFile the library file
     */
    private static void addLibraryFile(LibraryFile libraryFile) {
        if(libraryFiles.put(libraryFile.getClassName().toQualifiedName(), libraryFile) != null)
            new GeneralWarning.ClassCollision(libraryFile.getClassName().toQualifiedName());
    }

    /**
     * Returns the library file corresponding to the given class name.
     * @param className the class name
     * @return the library file (null if not found)
     */
    private static LibraryFile findLibraryFile(ClassName className) {
        return libraryFiles.get(className.toQualifiedName());
    }

    /**
     * Returns the root package symbol.
     * @return the root symbol
     */
    public static PackageSymbol getRootSymbol() {
        return rootSymbol;
    }
}
