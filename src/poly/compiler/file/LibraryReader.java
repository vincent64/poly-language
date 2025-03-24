package poly.compiler.file;

import poly.compiler.util.ClassName;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The LibraryReader class. This class is used to read the
 */
public class LibraryReader {
    private final List<LibraryFile> libraryFiles;

    private LibraryReader() {
        //Initialize library files list
        libraryFiles = new ArrayList<>();
    }

    /**
     * Reads the library from the given file.
     * @param file the file
     */
    private void readLibrary(File file) {
        try(ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();

                if(!entry.isDirectory() && name.endsWith(ClassWriter.CLASS_EXTENSION)) {
                    //Get file qualified name without extension
                    name = name.substring(0, name.length() - ClassWriter.CLASS_EXTENSION.length());

                    //Add file to library files
                    libraryFiles.add(new LibraryFile.External(ClassName.fromStringQualifiedName(name), file, entry));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read library " + file.getName() + ".");
        }
    }

    /**
     * Returns a library reader from the given library folder path.
     * @param libraryPath the library path
     * @return a library reader
     */
    public static LibraryReader fromLibrary(String libraryPath) {
        LibraryReader libraryReader = new LibraryReader();

        //Get library folder path
        Path path = Paths.get(libraryPath);
        File libraryDirectory = new File(path.toString());

        //Read library files
        if(libraryDirectory.exists() && libraryDirectory.isDirectory()) {
            File[] files = libraryDirectory.listFiles();

            //Make sure files are not null
            if(files == null)
                return libraryReader;

            for(File file : files) {
                if(file.isFile() && file.getName().endsWith(JarBuilder.JAR_EXTENSION))
                    libraryReader.readLibrary(file);
            }
        }

        return libraryReader;
    }

    /**
     * Returns a library reader from the given Poly standard library path.
     * @param polyLibraryPath the standard library path
     * @return a library reader
     */
    public static LibraryReader fromPolyLibrary(String polyLibraryPath) {
        LibraryReader libraryReader = new LibraryReader();

        //Read library files
        libraryReader.readLibrary(new File(Paths.get(polyLibraryPath).toString()));

        return libraryReader;
    }

    /**
     * Loads and return the Java API library files.
     * @return the Java library files
     */
    public static List<LibraryFile> loadJavaLibraryFiles() {
        try {
            //Get Java file system
            FileSystem fileSystem = FileSystems.getFileSystem(URI.create(ClassLoader.JAVA_RUNTIME_URI));
            Path basePath = fileSystem.getPath(ClassLoader.JAVA_BASE_MODULE);

            //Initialize library files list
            List<LibraryFile> libraryFiles = new ArrayList<>();

            try(Stream<Path> paths = Files.walk(basePath)) {
                paths.filter(path -> path.toString().endsWith(ClassWriter.CLASS_EXTENSION))
                        .forEach(path -> {
                            //Parse library class name from path name
                            path = fileSystem.getPath("modules", "java.base").relativize(path);
                            String pathName = path.toString();
                            String fileName = pathName.substring(0, pathName.length() - ClassWriter.CLASS_EXTENSION.length());
                            ClassName className = ClassName.fromStringQualifiedName(fileName);

                            //Add library to the library files list
                            libraryFiles.add(new LibraryFile.Java(className));
                        });

                return libraryFiles;
            }
        } catch(IOException e) {
            throw new RuntimeException("Could not load library internal library classes.");
        }
    }

    /**
     * Returns the library files.
     * @return the library files
     */
    public List<LibraryFile> getLibraryFiles() {
        return libraryFiles;
    }
}
