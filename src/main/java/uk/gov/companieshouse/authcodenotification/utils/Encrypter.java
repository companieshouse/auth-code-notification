package uk.gov.companieshouse.authcodenotification.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Encrypter {

    public static String encrypt(String plaintext, byte[] key) throws Exception{
        byte[] ptext = plaintext.getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[Cipher.getInstance("AES/CBC/PKCS5Padding").getBlockSize()];
        random.nextBytes(iv);

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        byte[] enc = cipher.doFinal(ptext);

        byte[] encSalted = concatByte(iv, enc);
        return Base64.getEncoder().encodeToString(encSalted);
    }

    private static byte[] concatByte(byte[] a, byte[] b){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            outputStream.write(a);
            outputStream.write(b);
            return outputStream.toByteArray();
        }
        catch(IOException e){
            //todo: deal with this
            return null;
        }
        
    }

}
