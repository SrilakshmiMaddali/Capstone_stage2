package sm.com.camcollection;

import org.junit.Test;
import java.io.UnsupportedEncodingException;

import sm.com.camcollection.generator.UTF8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the UTF-8 converter.
 */
public class UTF8Test {

    @Test
    public void testEncode() {
        byte[] expected;
        try {
            expected = "testü".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            assertTrue(false);
            expected = "testü".getBytes();
        }
        byte[] converted = UTF8.encode("testü");
        assertEquals(expected.length, converted.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], converted[i]);
        }
    }

}