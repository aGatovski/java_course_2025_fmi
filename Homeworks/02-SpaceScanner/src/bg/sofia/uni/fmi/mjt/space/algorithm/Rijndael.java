package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {

    private static final String ENCRYPTION_ALGORITHM = "AES";

    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key cannot be null!");
        }

        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey);

            try (var encryptedOutputSteam = new CipherOutputStream(outputStream, cipher)) {
                inputStream.transferTo(encryptedOutputSteam);
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CipherException("Encrypt operation cannot be completed successfully", e);
        } catch (java.io.IOException e) {
            throw new CipherException("I/O error during encryption", e);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);

            try (OutputStream decryptedOutputStream = new CipherOutputStream(outputStream,
                cipher)) {
                inputStream.transferTo(decryptedOutputStream);
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CipherException("Decrypt operation cannot be completed successfully", e);
        } catch (java.io.IOException e) {
            throw new CipherException("I/O error during decryption", e);
        }
    }
}
