package poly.compiler.file;

import poly.compiler.log.Debug;
import poly.compiler.util.PackageName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The ProjectReader class. This class is used to read the content of
 * every Poly code file in the source folder of the project directory.
 * @author Vincent Philippe (@vincent64)
 */
public class ProjectReader {
    private static List<SourceCode> sourceCodes;

    /**
     * Reads and returns the source code files at the given source folder path.
     * @param sourcePath the source folder path
     * @return the source codes
     */
    public static List<SourceCode> read(String sourcePath) {
        //Initialize source codes list
        sourceCodes = new ArrayList<>();

        //Get source folder path
        Path path = Paths.get(sourcePath);
        File sourceDirectory = new File(path.toString());

        //Read project files
        if(sourceDirectory.exists() && sourceDirectory.isDirectory())
            readDirectory(sourceDirectory, new PackageName());

        //Read single file
        if(sourceDirectory.exists() && sourceDirectory.isFile()
                && CodeReader.isPolyFile(sourceDirectory.getName()))
            readFile(sourceDirectory, new PackageName());

        //Print the source code files for debugging
        Debug.printSourceFiles(sourceCodes);

        return sourceCodes;
    }

    /**
     * Reads the given directory in the source folder with the given package path.
     * This method is called recursively if there are subdirectories.
     * @param directory the directory
     * @param packageName the package path
     */
    private static void readDirectory(File directory, PackageName packageName) {
        if(directory.listFiles() == null) return;

        //Iterate through every file and subdirectory recursively
        for(File file : directory.listFiles()) {
            if(file.isFile()) {
                //Read Poly code file and add to source codes
                if(CodeReader.isPolyFile(file.getName()))
                    readFile(file, packageName);

            } else if(file.isDirectory()) {
                readDirectory(file, packageName.addName(file.getName().toCharArray()));
            }
        }
    }

    /**
     * Reads the given file in the source folder with the given package path.
     * @param file the file
     * @param packageName the package name
     */
    private static void readFile(File file, PackageName packageName) {
        //Get file name without the extension
        String fileName = file.getName().substring(0, file.getName().length() - 5);

        try {
            //Read code from file
            char[] content = CodeReader.read(file);

            //Create source code from file
            SourceCode sourceCode = new SourceCode(fileName, packageName, content);
            sourceCodes.add(sourceCode);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
