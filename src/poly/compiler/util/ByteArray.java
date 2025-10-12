package poly.compiler.util;

/**
 * The ByteArray class. This class is used to create a dynamic array of bytes,
 * where the size of the array expands according to the bytes added to it.
 * The class contains several methods to add byte arrays, single byte,
 * integer and short values to the byte array.
 * @author Vincent Philippe (@vincent64)
 */
public final class ByteArray {
    private static final int CHUNK_SIZE = 32;
    private byte[] array;
    private int size;

    /**
     * Constructs a byte array with the given initial capacity.
     * @param initialCapacity the initial capacity
     */
    public ByteArray(int initialCapacity) {
        array = new byte[initialCapacity];
    }

    /**
     * Constructs a byte array with the default initial capacity.
     */
    public ByteArray() {
        this(CHUNK_SIZE);
    }

    /**
     * Adds the given array of bytes to the current byte array.
     * @param array the array of bytes
     */
    public void add(byte[] array) {
        allocate(array.length);

        //Add the given array to the main array and increase size
        System.arraycopy(array, 0, this.array, size, array.length);
        size += array.length;
    }

    /**
     * Adds the given byte to the byte array.
     * @param value the byte
     */
    public void add(byte value) {
        allocate(1);

        //Add the given value to the main array
        array[size++] = value;
    }

    /**
     * Adds the given integer to the byte array.
     * @param value the integer
     */
    public void add(int value) {
        add((byte) ((value >> 24) & 0xFF));
        add((byte) ((value >> 16) & 0xFF));
        add((byte) ((value >> 8) & 0xFF));
        add((byte) (value & 0xFF));
    }

    /**
     * Adds the given short to the byte array.
     * @param value the short
     */
    public void add(short value) {
        add((byte) ((value >> 8) & 0xFF));
        add((byte) (value & 0xFF));
    }

    /**
     * Allocates more array size if adding the given bytes count
     * would produce an array overflow.
     * @param count the bytes count
     */
    private void allocate(int count) {
        while(size + count >= array.length) {
            byte[] newArray = new byte[array.length * 2];

            //Copy and replace the array
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
    }

    /**
     * Returns the array of bytes.
     * @return the array of bytes
     */
    public byte[] getBytes() {
        //Initialize array with the correct size
        byte[] array = new byte[size];
        //Copy the array into the new one
        System.arraycopy(this.array, 0, array, 0, size);

        return array;
    }

    /**
     * Returns the given 4-byte array as an integer value.
     * @param bytes the bytes array
     * @return the integer value
     */
    public static int getIntegerFromByteArray(byte[] bytes) {
        int value = bytes[0];
        value = (value << 8) | bytes[1];
        value = (value << 8) | bytes[2];
        value = (value << 8) | bytes[3];

        return value;
    }

    /**
     * Returns the given 2-byte array as a short value.
     * @param bytes the bytes array
     * @return the short value
     */
    public static short getShortFromByteArray(byte[] bytes) {
        return (short) ((bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF));
    }

    /**
     * Returns the given short value as an array of 2 bytes.
     * @param value the short value
     * @return the bytes array
     */
    public static byte[] getShortAsByteArray(short value) {
        byte[] bytes = new byte[Short.BYTES];

        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Returns the given integer value as an array of 4 bytes.
     * @param value the integer value
     * @return the bytes array
     */
    public static byte[] getIntegerAsByteArray(int value) {
        byte[] bytes = new byte[Integer.BYTES];

        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Returns the given long value as an array of 8 bytes.
     * @param value the long value
     * @return the bytes array
     */
    public static byte[] getLongAsByteArray(long value) {
        byte[] bytes = new byte[Long.BYTES];

        bytes[0] = (byte) ((value >> 56L) & 0xFF);
        bytes[1] = (byte) ((value >> 48L) & 0xFF);
        bytes[2] = (byte) ((value >> 40L) & 0xFF);
        bytes[3] = (byte) ((value >> 32L) & 0xFF);
        bytes[4] = (byte) ((value >> 24L) & 0xFF);
        bytes[5] = (byte) ((value >> 16L) & 0xFF);
        bytes[6] = (byte) ((value >> 8L) & 0xFF);
        bytes[7] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Returns the given float value as an array of 4 bytes. The returned bytes
     * are the IEEE-754 single-precision representation of the float value.
     * @param value the float value
     * @return the bytes array
     */
    public static byte[] getFloatAsByteArray(float value) {
        //Transform float to IEEE-754 representation
        int bits = Float.floatToRawIntBits(value);

        return getIntegerAsByteArray(bits);
    }

    /**
     * Returns the given double value as an array of 8 bytes. The returned bytes
     * are the IEEE-754 double-precision representation of the double value.
     * @param value the double value
     * @return the bytes array
     */
    public static byte[] getDoubleAsByteArray(double value) {
        //Transform float to IEEE-754 double representation
        long bits = Double.doubleToRawLongBits(value);

        return getLongAsByteArray(bits);
    }
}
