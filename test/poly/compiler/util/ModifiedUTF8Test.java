package poly.compiler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModifiedUTF8Test {
    @Test
    void testEncodingSimple() {
        String value = "ABCabc123_+-*/%";
        byte[] expected = {
                0x41, 0x42, 0x43, 0x61, 0x62, 0x63, 0x31, 0x32,
                0x33, 0x5F, 0x2B, 0x2D, 0x2A, 0x2F, 0x25
        };

        assertArrayEquals(expected, ModifiedUTF8.encodeBytes(value));
    }

    @Test
    void testEncodingNull() {
        String value = "\0";
        byte[] expected = { (byte) 0xC0, (byte) 0x80 };

        assertArrayEquals(expected, ModifiedUTF8.encodeBytes(value));
    }

    @Test
    void testEncodingRaw() {
        String value = new String(new char[] { 0x0001, 0x007F, 0x0080, 0x07FF, 0x0800, 0xFFFF });
        byte[] expected = {
                0x01, 0x7F,
                (byte) 0xC2, (byte) 0x80, (byte) 0xDF, (byte) 0xBF,
                (byte) 0xE0, (byte) 0xA0, (byte) 0x80,
                (byte) 0xEF, (byte) 0xBF, (byte) 0xBF
        };

        assertArrayEquals(expected, ModifiedUTF8.encodeBytes(value));
    }

    @Test
    void testDecodingSimple() {
        String expected = "ABCabc123_+-*/%";
        byte[] bytes = {
                0x41, 0x42, 0x43, 0x61, 0x62, 0x63, 0x31, 0x32,
                0x33, 0x5F, 0x2B, 0x2D, 0x2A, 0x2F, 0x25
        };

        assertEquals(expected, ModifiedUTF8.decodeString(bytes));
    }

    @Test
    void testDecodingNull() {
        String expected = "\0";
        byte[] bytes = { (byte) 0xC0, (byte) 0x80 };

        assertEquals(expected, ModifiedUTF8.decodeString(bytes));
    }

    @Test
    void testDecodingRaw() {
        String expected = new String(new char[] { 0x0001, 0x007F, 0x0080, 0x07FF, 0x0800, 0xFFFF });
        byte[] bytes = {
                0x01, 0x7F,
                (byte) 0xC2, (byte) 0x80, (byte) 0xDF, (byte) 0xBF,
                (byte) 0xE0, (byte) 0xA0, (byte) 0x80,
                (byte) 0xEF, (byte) 0xBF, (byte) 0xBF
        };

        assertEquals(expected, ModifiedUTF8.decodeString(bytes));
    }
}
