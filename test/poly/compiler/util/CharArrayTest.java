package poly.compiler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharArrayTest {
    @Test
    void testGeneralUsage() {
        CharArray charArray = new CharArray(64);
        charArray.add(new char[] {
                0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
                0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x000E, 0x000F
        });

        char[] expected = new char[] {
                0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
                0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x000E, 0x000F
        };

        assertArrayEquals(expected, charArray.getChars());
    }

    @Test
    void testArrayOverflow() {
        CharArray charArray = new CharArray(16);

        for(int i = 0; i < 24; i++)
            charArray.add(new char[] {(char) i});

        char[] expected = new char[] {
                0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,
                0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0x000E, 0x000F,
                0x0010, 0x0011, 0x0012, 0x0013, 0x0014, 0x0015, 0x0016, 0x0017
        };

        assertArrayEquals(expected, charArray.getChars());
    }

    @Test
    void testArrayHugeOverflow() {
        CharArray charArray = new CharArray(64);
        charArray.add(new char[2064]);

        assertArrayEquals(new char[2064], charArray.getChars());
    }
}
