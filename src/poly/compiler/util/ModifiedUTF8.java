package poly.compiler.util;

import poly.compiler.error.GeneralError;

/**
 * The ModifiedUTF8 class. This class contains methods to encode to and decode from
 * the motified UTF-8 encoding. The modified UTF-8 encoding is a special UTF-8 encoding
 * used by the JVM to encode class files, strings and characters.
 * @author Vincent Philippe (@vincent64)
 */
public class ModifiedUTF8 {
    private ModifiedUTF8() { }

    /**
     * Returns the modified UTF-8 encoding of the given string value.
     * @param value the string
     * @return the encoded bytes
     */
    public static byte[] encodeBytes(String value) {
        ByteArray array = new ByteArray();

        for(char c : value.toCharArray()) {
            if(c == 0x0000) {
                array.add((byte) 0xC0);
                array.add((byte) 0x80);
            } else if(c <= 0x007F) {
                array.add((byte) c);
            } else if(c <= 0x07FF) {
                array.add((byte) (0b1100_0000 | (c >> 6)));
                array.add((byte) (0b1000_0000 | (c & 0b1111_11)));
            } else {
                array.add((byte) (0b1110_0000 | (c >> 12)));
                array.add((byte) (0b1000_0000 | ((c >> 6) & 0b1111_11)));
                array.add((byte) (0b1000_0000 | (c & 0b1111_11)));
            }
        }

        return array.getBytes();
    }

    /**
     * Returns the decoded string from the given modified UTF-8 encoded bytes.
     * @param bytes the modified UTF-8 bytes
     * @return the decoded string
     */
    public static String decodeString(byte[] bytes) {
        CharArray array = new CharArray();

        for(int i = 0; i < bytes.length; i++) {
            int x = bytes[i] & 0xFF;

            if((x >> 7) == 0) {
                array.add((char) x);
            } else if((x >> 5) == 0b110) {
                if(i < bytes.length - 1) {
                    int y = bytes[++i] & 0xFF;

                    //Make sure the following bits are valid
                    if((y & 0b1100_0000) != 0b1000_0000)
                        new GeneralError.InvalidUTF8Encoding();

                    array.add((char) (((x & 0x1F) << 6) | (y & 0x3F)));
                } else new GeneralError.InvalidUTF8Encoding();
            } else if((x >> 4) == 0b1110) {
                if(i < bytes.length - 2) {
                    int y = bytes[++i] & 0xFF, z = bytes[++i] & 0xFF;

                    //Make sure the following bits are valid
                    if((y & 0b1100_0000) != 0b1000_0000 || (z & 0b1100_0000) != 0b1000_0000)
                        new GeneralError.InvalidUTF8Encoding();

                    array.add((char) (((x & 0xF) << 12) | ((y & 0x3F) << 6) | (z & 0x3F)));
                } else new GeneralError.InvalidUTF8Encoding();
            } else new GeneralError.InvalidUTF8Encoding();
        }

        return new String(array.getChars());
    }
}
