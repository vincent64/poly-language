package poly.compiler.file;

import poly.compiler.output.ClassFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The ProjectWriter class. This class is used to write the compiled class files
 * in the project output folder.
 * @author Vincent Philippe (@vincent64)
 */
public class ProjectWriter {

    /**
     * Writes the given class files in the given output path.
     * @param classFiles the class files
     * @param outputPath the output path
     */
    public static void write(List<ClassFile> classFiles, String outputPath) {
        //Get output folder path
        Path path = Paths.get(outputPath);

        File outputDirectoy = new File(path.toString());
        outputDirectoy.mkdirs();

        for(ClassFile classFile : classFiles) {
            try {
                //Write the class file
                ClassWriter.write(classFile, path);
            } catch(IOException e) {
                throw new RuntimeException("Could not write class file " + classFile + ".");
            }
        }
    }
}
