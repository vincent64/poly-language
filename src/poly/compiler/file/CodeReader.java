package poly.compiler.file;

import poly.compiler.tokenizer.Alphabet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The CodeReader class. This class is used to read the content of a code file.
 * The file must have a valid .poly extension to be read.
 * @author Vincent Philippe (@vincent64)
 */
public class CodeReader {
    /** The Poly programming language file extension. */
    public static final String EXTENSION = ".poly";

    /**
     * Reads and returns the content of a code file as an array of characters.
     * @param file the code file
     * @return the array of characters
     * @throws IOException if the code file could not be read
     */
    public static char[] read(File file) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }

            return stringBuilder.toString().toCharArray();
        }
    }

    /**
     * Returns whether the given file name has the extension of a Poly source file.
     * @param fileName the name of the code file
     * @return true if the file extension is .poly
     */
    public static boolean isPolyFile(String fileName) {
        return fileName.endsWith(EXTENSION);
    }

    /**
     * Returns whether the given file name is a valid Poly code file name.
     * @param fileName the name of the code file
     * @return true if the file name if a valid name
     */
    public static boolean isValidFileName(String fileName) {
        if(!isPolyFile(fileName))
            return false;

        //Check file name without extension
        char[] name = fileName.substring(0, fileName.length() - 5).toCharArray();

        for(int i = 0; i < name.length - 5; i++) {
            char character = name[i];

            if(!Alphabet.Type.STRING.isInPrimaryAlphabet(character)
                && !Alphabet.Type.STRING.isInSecondaryAlphabet(character)) {
                return false;
            }
        }

        return true;
    }
}
