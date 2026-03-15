package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;


class RijndaelTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private SecretKey secretKey;
    private Rijndael rijndaelAlgth;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(KEY_SIZE_IN_BITS); // 128-bit AES
        this.secretKey = keyGenerator.generateKey();
        this.rijndaelAlgth = new Rijndael(secretKey);
    }

    @Test
    void testRijndaelThrowsIllegalArgumentExceptionWhenKeyNull() {
        assertThrows(IllegalArgumentException.class, () -> new Rijndael(null),
            "Rijndael throws IllegalArgumentException when key is null");
    }

    @Test
    void testEncryptThrowsIllegalArgumentExceptionWhenInputStreamNull() {
        assertThrows(IllegalArgumentException.class,
            () -> rijndaelAlgth.encrypt(null, new ByteArrayOutputStream()),
            "Encrypt throws IllegalArgumentException when input stream is null");
    }

    @Test
    void testEncryptThrowsIllegalArgumentExceptionWhenOuputStreamNull() {
        assertThrows(IllegalArgumentException.class,
            () -> rijndaelAlgth.encrypt(new ByteArrayInputStream(new byte[0]), null),
            "Encrypt throws IllegalArgumentException when output stream is null");
    }

    @Test
    void testDecryptThrowsIllegalArgumentExceptionWhenInputStreamNull() {
        assertThrows(IllegalArgumentException.class,
            () -> rijndaelAlgth.decrypt(null, new ByteArrayOutputStream()),
            "Decrypt throws IllegalArgumentException when input stream is null");
    }

    @Test
    void testDecryptThrowsIllegalArgumentExceptionWhenOutputStreamNull() {
        assertThrows(IllegalArgumentException.class,
            () -> rijndaelAlgth.decrypt(new ByteArrayInputStream(new byte[0]), null),
            "Decrypt throws IllegalArgumentException when output stream is null");
    }

    @Test
    void testEncryptDecrypt() throws CipherException {
        String input =
            "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
        
        byte[] inputBytes = input.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        rijndaelAlgth.encrypt(inputStream, encryptedOutputStream);
        byte[] encryptedBytes = encryptedOutputStream.toByteArray();
        
        ByteArrayInputStream encryptedInputStream = new ByteArrayInputStream(encryptedBytes);
        ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream();

        rijndaelAlgth.decrypt(encryptedInputStream, decryptedOutputStream);
        String output = decryptedOutputStream.toString();

        assertEquals(input, output, "Decrypted message should match the original plaintext");
    }
}