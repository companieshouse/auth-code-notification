package uk.gov.companieshouse.authcodenotification.utils;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.authcodenotification.exception.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class Encrypter {

    private static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";

    public String encrypt(String plainTextToEncrypt, String key) throws EncryptionException, InvalidKeyException {
        Validate.notNull(plainTextToEncrypt, "plainTextToEncrypt must not be null");
        Validate.notNull(key, "key must not be null");

        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] plainTextBytes = plainTextToEncrypt.getBytes(StandardCharsets.UTF_8);

            var cipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);

            // AES in CBC mode requires any initialisation vector (IV) which should be random for each encryption.
            // use a cryptographically strong random generator to generate the data for this IV
            var random = SecureRandom.getInstanceStrong();
            var initialisationVector = new byte[Cipher.getInstance(AES_CBC_PKCS_5_PADDING).getBlockSize()];
            random.nextBytes(initialisationVector);

            // Encrypt the plaintext using the given key and generated initialisation vector
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, AES), new IvParameterSpec(initialisationVector));
            byte[] encodedBytes = cipher.doFinal(plainTextBytes);

            // As the initialisation vector is needed for decryption, concatenate it to the encrypted auth code. It is not an issue that the
            // initialisation vector is in plaintext as it is random every time
            byte[] encodedSaltedBytes = concatByte(initialisationVector, encodedBytes);

            return Base64.getEncoder().encodeToString(encodedSaltedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new EncryptionException(e.getMessage(), e);
        }
    }

    private byte[] concatByte(byte[] byteArray1, byte[] byteArray2) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        outputStream.write(byteArray1);
        outputStream.write(byteArray2);
        return outputStream.toByteArray();
    }
}
