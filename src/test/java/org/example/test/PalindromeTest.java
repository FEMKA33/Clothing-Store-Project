package org.example.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PalindromeTest {

    private boolean isPalindrome(String text) {
        String clean = text.replaceAll("\\s+", "").toLowerCase();
        return new StringBuilder(clean).reverse().toString().equals(clean);
    }

    @Test
    void testPalindromePositive() {
        assertTrue(isPalindrome("Level"));
    }

    @Test
    void testPalindromeNegative() {
        assertFalse(isPalindrome("Hello"));
    }
}