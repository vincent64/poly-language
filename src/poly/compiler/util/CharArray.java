package poly.compiler.util;

import java.lang.Character;

/**
 * The CharArray class. This class is used to create a dynamic array of characters,
 * where the size of the array expands according to the characters added to it.
 * @author Vincent Philippe (@vincent64)
 */
public class CharArray {
    private static final int CHUNK_SIZE = 32;
    private char[] array;
    private int size;

    /**
     * Constructs a char array with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public CharArray(int initialCapacity) {
        array = new char[initialCapacity];
    }

    /**
     * Constructs a char array with the default initial capacity.
     */
    public CharArray() {
        this(CHUNK_SIZE);
    }

    /**
     * Adds the given array of characters to the current char array.
     * @param array the array of characters
     */
    public void add(char[] array) {
        allocate(array.length);

        //Add the given array to the main array and increase size
        System.arraycopy(array, 0, this.array, size, array.length);
        size += array.length;
    }

    /**
     * Adds the given character to the char array.
     * @param value the character
     */
    public void add(char value) {
        allocate(1);

        //Add the given value to the main array
        array[size++] = value;
    }

    /**
     * Allocates more array size if adding the given characters count
     * would produce an array overflow.
     * @param count the characters count
     */
    private void allocate(int count) {
        while(size + count >= array.length) {
            char[] newArray = new char[array.length * 2];

            //Copy and replace the array
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
        }
    }

    /**
     * Returns the array of characters.
     * @return the array of characters
     */
    public char[] getChars() {
        //Initialize array with the correct size
        char[] array = new char[size];
        //Copy the array into the new one
        System.arraycopy(this.array, 0, array, 0, size);

        return array;
    }
}
