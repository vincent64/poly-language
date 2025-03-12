package poly.compiler.file;

import poly.compiler.output.ClassFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * The ClassWriter class. This class is used to write class files into files.
 * @author Vincent Philippe (@vincent64)
 */
public class ClassWriter {
    /** The class file extension. */
    public static final String CLASS_EXTENSION = ".class";

    /**
     * Writes the given class file in the given directory path.
     * @param classFile the class file
     * @param directoryPath the directory path
     * @throws IOException if an IO error occurred
     */
    public static void write(ClassFile classFile, Path directoryPath) throws IOException {
        Path filePath = directoryPath.resolve(classFile.getClassQualifiedName() + CLASS_EXTENSION);

        File file = new File(filePath.toString());
        file.getParentFile().mkdirs();

        //Create new file
        if(!file.exists())
            file.createNewFile();

        try(OutputStream stream = new FileOutputStream(filePath.toString())) {
            stream.write(classFile.getBytes());
        }
    }
}
