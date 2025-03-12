package poly.compiler.content;

import poly.compiler.util.ByteArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ByteArrayTest {
    @Test
    void generalUsageWorks() {
        ByteArray byteArray = new ByteArray(64);
        byteArray.add(new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
        });

        byte[] expected = new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F
        };

        assertArrayEquals(expected, byteArray.getBytes());
    }

    @Test
    void arrayOverflowWorks() {
        ByteArray byteArray = new ByteArray(16);

        for(int i = 0; i < 24; i++)
            byteArray.add(new byte[] {(byte) i});

        byte[] expected = new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
        };

        assertArrayEquals(expected, byteArray.getBytes());
    }

    @Test
    void arrayHugeOverflowWorks() {
        ByteArray byteArray = new ByteArray(64);
        byteArray.add(new byte[2064]);

        assertArrayEquals(new byte[2064], byteArray.getBytes());
    }
}
