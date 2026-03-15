package bg.sofia.uni.fmi.mjt.music.validation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int MASK = 0xff;

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(password.getBytes((StandardCharsets.UTF_8)));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(HASH_ALGORITHM + " not available", e);
        }
    }

    public static boolean verify(String password, String storedHash) {
        return hash(password).equals(storedHash);
    }

    // source: https://www.baeldung.com/sha-256-hashing-java
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);

        for (byte b : bytes) {
            String hex = Integer.toHexString(MASK & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
