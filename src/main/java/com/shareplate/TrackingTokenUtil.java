package com.shareplate;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class TrackingTokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());

            return java.util.HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException("Token hashing failed", e);
        }
    }

    public static boolean verifyToken(String token, String storedHash) {
        if (token == null || storedHash == null) {
            return false;
        }

        String generatedHash = hashToken(token);

        return MessageDigest.isEqual(
                generatedHash.getBytes(),
                storedHash.getBytes()
        );
    }
}