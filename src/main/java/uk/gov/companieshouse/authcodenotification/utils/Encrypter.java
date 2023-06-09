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

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] plainTextBytes = plainTextToEncrypt.getBytes(StandardCharsets.UTF_8);

        Cipher cipher;
        byte[] initialisationVector;
        try {
            cipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
            var random = SecureRandom.getInstanceStrong();
            initialisationVector = new byte[Cipher.getInstance(AES_CBC_PKCS_5_PADDING).getBlockSize()];
            random.nextBytes(initialisationVector);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e){
            throw new EncryptionException(e.getMessage(), e);
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, AES), new IvParameterSpec(initialisationVector));
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionException(e.getMessage(), e);
        }

        byte[] encodedBytes;
        byte[] encodedSaltedBytes;
        try {
            encodedBytes = cipher.doFinal(plainTextBytes);
            encodedSaltedBytes = concatByte(initialisationVector, encodedBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new EncryptionException(e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(encodedSaltedBytes);
    }

    private byte[] concatByte(byte[] byteArray1, byte[] byteArray2) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        outputStream.write(byteArray1);
        outputStream.write(byteArray2);
        return outputStream.toByteArray();
    }
}
