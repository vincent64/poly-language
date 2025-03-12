package poly.compiler.util;

/**
 * The CharArray class. This class is used to create a dynamic array of characters,
 * where the size of the array expands according to the characters added to it.
 * @author Vincent Philippe (@vincent64)
 */
public class CharArray {
    private static final int CHUNK_SIZE = 64;
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
        if(size + array.length > this.array.length) {
            increaseArraySize();

            //Add the given array
            add(array);
        } else {
            //Add the given array to the main array
            System.arraycopy(array, 0, this.array, size, array.length);
            //Increase size count
            size += array.length;
        }
    }

    /**
     * Adds the given character to the char array.
     * @param value the character
     */
    public void add(char value) {
        if(size + 1 > array.length) increaseArraySize();

        //Add the given value to the main array
        array[size++] = value;
    }

    private void increaseArraySize() {
        //Initialize new array which is bigger
        char[] newArray = new char[array.length + CHUNK_SIZE];

        //Copy previous array into the new one
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
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
