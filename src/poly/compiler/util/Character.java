package poly.compiler.util;

/**
 * The Character class. This class is a utility used to manipulate character arrays
 * and characters in general.
 * @author Vincent Philippe (@vincent64)
 */
public class Character {
    /**
     * Returns whether the two given character arrays represent the same string.
     * @param string1 the first character string
     * @param string2 the second character string
     * @return true if both character arrays are equal
     */
    public static boolean isSameString(char[] string1, char[] string2) {
        //Make sure they have the same length first
        if(string1.length == string2.length) {
            //Compare character-by-character
            for(int i = 0; i < string1.length; i++) {
                if(string1[i] != string2[i]) return false;
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Returns a substring of the given characters array at the given start and end indices.
     * @param content the characters content
     * @param startIndex the start index
     * @param endIndex the end index
     * @return a substring of the characters content
     */
    public static char[] getSubstring(char[] content, int startIndex, int endIndex) {
        char[] substring = new char[endIndex - startIndex];

        //Extract substring from the content
        for(int i = startIndex, j = 0; i < endIndex; i++, j++) {
            substring[j] = content[i];
        }

        return substring;
    }
}
