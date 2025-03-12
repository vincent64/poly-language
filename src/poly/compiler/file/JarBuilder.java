package poly.compiler.file;

import poly.compiler.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The JarBuilder class. This class is used to build a JAR file using
 * the output class files after compilation.
 * @author Vincent Philippe (@vincent64)
 */
public class JarBuilder {
    /** The Java Archive (JAR) file extension. */
    public static final String JAR_EXTENSION = ".jar";
    /** The generated JAR file name. */
    private static final String FILE_NAME = "project";

    private static ZipOutputStream outputStream;

    /**
     * Builds a JAR file if the JAR output parameter is enabled.
     */
    public static void build() {
        if(!Parameters.jarOutput()) return;

        //Get output folder path
        Path path = Paths.get(Parameters.getOutputPath());
        File file = new File(path.resolve(FILE_NAME + JAR_EXTENSION).toString());

        try {
            //Create archive
            outputStream = new JarOutputStream(new FileOutputStream(file));

            //Read output class files
            readDirectory(new File(path.toString()));
            outputStream.close();

        } catch(IOException e) {
            throw new RuntimeException("Could not build JAR file.");
        }
    }

    /**
     * Reads the content of the given directory and its subdirectories.
     * @param directory the directory
     * @throws IOException if an IO error occurred
     */
    private static void readDirectory(File directory) throws IOException {
        if(directory.listFiles() == null) return;

        //Iterate through every file and subdirectory recursively
        for(File file : directory.listFiles()) {
            if(file.isFile()) {
                //Read class files
                if(file.getName().endsWith(ClassWriter.CLASS_EXTENSION))
                    readFile(file);

            } else if(file.isDirectory()) {
                readDirectory(file);
            }
        }
    }

    /**
     * Reads the content of the given file and adds it to the JAR file.
     * @param file the file
     * @throws IOException if an IO error occurred
     */
    private static void readFile(File file) throws IOException {
        //Read class file content
        byte[] content = Files.readAllBytes(file.toPath());

        //Get relative path from output folder
        Path relativePath = Paths.get(Parameters.getOutputPath()).relativize(file.toPath());

        //Create entry inside the archive
        ZipEntry zipEntry = new JarEntry(relativePath.toString().replace("\\", "/"));

        //Write bytes content in entry
        outputStream.putNextEntry(zipEntry);
        outputStream.write(content);
        outputStream.closeEntry();
    }
}
