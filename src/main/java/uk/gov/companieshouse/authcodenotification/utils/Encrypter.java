package uk.gov.companieshouse.authcodenotification.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${AUTH_CODE_ENCRYPTION_KEY}")
    private String aesKeyString;

    public String encrypt(String plainText) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            IOException {
        byte[] key = aesKeyString.getBytes(StandardCharsets.UTF_8);
        byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);

        var cipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);

        var random = SecureRandom.getInstanceStrong();
        var initialisationVector = new byte[Cipher.getInstance(AES_CBC_PKCS_5_PADDING).getBlockSize()];
        random.nextBytes(initialisationVector);

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES), new IvParameterSpec(initialisationVector));
        byte[] encodedBytes = cipher.doFinal(plainTextBytes);

        byte[] encodedSaltedBytes = concatByte(initialisationVector, encodedBytes);
        return Base64.getEncoder().encodeToString(encodedSaltedBytes);
    }

    private byte[] concatByte(byte[] byteArray1, byte[] byteArray2) throws IOException {
        var outputStream = new ByteArrayOutputStream();
        outputStream.write(byteArray1);
        outputStream.write(byteArray2);
        return outputStream.toByteArray();
    }
}
