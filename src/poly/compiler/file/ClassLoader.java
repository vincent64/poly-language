package poly.compiler.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The ClassLoader class. This class is used to load class files from libraries.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassLoader {
    /** The Java runtime file system URI. */
    public static final String JAVA_RUNTIME_URI = "jrt:/";
    /** The Java API base module. */
    public static final String JAVA_BASE_MODULE = "modules/java.base/";

    /**
     * Loads the given library file and returns a class reader.
     * @param libraryFile the library file
     * @return a class reader
     */
    public static ClassReader load(LibraryFile libraryFile) {
        return libraryFile instanceof LibraryFile.External
                ? load((LibraryFile.External) libraryFile)
                : load((LibraryFile.Java) libraryFile);
    }

    /**
     * Loads the given external library file and returns a class reader.
     * @param libraryFile the external library file
     * @return a class reader
     */
    private static ClassReader load(LibraryFile.External libraryFile) {
        File file = libraryFile.getFile();
        ZipEntry entry = libraryFile.getEntry();

        //Read class file content from ZIP
        try(ZipFile zipFile = new ZipFile(file);
                InputStream inputStream = zipFile.getInputStream(entry)) {
            return new ClassReader(inputStream.readAllBytes());
        } catch(IOException e) {
            throw new RuntimeException("Could not load library class " + libraryFile.getClassName() + ".");
        }
    }

    /**
     * Loads the given Java API library file and returns a class reader.
     * @param libraryFile the Java API library file
     * @return a class reader
     */
    private static ClassReader load(LibraryFile.Java libraryFile) {
        String fileName = libraryFile.getClassName().toInternalQualifiedName() + ClassWriter.CLASS_EXTENSION;

        //Get Java Runtime file system
        FileSystem fileSystem = FileSystems.getFileSystem(URI.create(JAVA_RUNTIME_URI));
        Path basePath = fileSystem.getPath(JAVA_BASE_MODULE, fileName);

        //Read class file content from path
        try {
            return new ClassReader(Files.readAllBytes(basePath));
        } catch(IOException e) {
            throw new RuntimeException("Could not load library class " + libraryFile.getClassName() + ".");
        }
    }
}
