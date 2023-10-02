/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.KDFCalculator;
import org.bouncycastle.crypto.fips.Scrypt;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.Strings;

/**
 *
 * @author gabri
 */
public class Crypto {
    private static byte[] useScryptKDF(char[] password,
        byte [] salt, int costParameter, int blocksize, int parallelizationParam ) {
                
        KDFCalculator<Scrypt.Parameters> calculator
                = new Scrypt.KDFFactory()
                        .createKDFCalculator(
                                Scrypt.ALGORITHM.using(salt, costParameter, blocksize, parallelizationParam,
                                        Strings.toUTF8ByteArray(password)));
        byte[] output = new byte[32];
        calculator.generateBytes(output);
        return output;
    }
     
    public static byte[] computeSCRYPT(String password, byte[] salt){
        int addProvider;
        addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        
        int costParameter = 2048;
        int blocksize = 8; // exemplo: 8
        int parallelizationParam = 1; // exemplo: 1
        
         byte[] derivedKeyFromScrypt;
        derivedKeyFromScrypt = useScryptKDF(password.toCharArray(), salt, 
                costParameter,
                blocksize, parallelizationParam);
        
        return derivedKeyFromScrypt;
    }
    
    public static String generateNonce(){
        int addProvider;
        addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        byte iv[] = new byte[16];
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("DEFAULT", "BCFIPS");
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.out.println(ex);
            return null;
        }
        random.nextBytes(iv);
        return Utils.toHex(iv);
    }
    
    public static SecretKey generateKey(String secret, byte[] salt){
        int iterations = 1000;
        PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), 
                salt, iterations, 128);
        SecretKeyFactory pbkdf2 = null;
        String derivedPass = null;
        try {
            pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS");
            SecretKey sk = pbkdf2.generateSecret(spec);
            derivedPass = Hex.encodeHexString(sk.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(org.bouncycastle.util.encoders.Hex.decode(derivedPass), "AES");
    }
    
    public static byte[] generateSalt(){
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
    
    public static byte[] encryptMessage(SecretKey key, String text, String iv){
        int tagLen = 128;
        
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            return null;
        }

        GCMParameterSpec spec = new GCMParameterSpec(tagLen, Utils.toByteArray(iv));

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
            System.out.println(ex);
        }
        
        byte[] encryptedText;

        try {
            encryptedText = cipher.doFinal(Utils.toByteArray(text));
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            return null;
        }
        
        return encryptedText;
    }

    
    public static String decryptMessage(SecretKey key, Message message){
        int tagLen = 128;
        
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            System.out.println(ex);
            return null;
        }
        
        String iv = message.getNonce();

        GCMParameterSpec spec = new GCMParameterSpec(tagLen, Utils.toByteArray(iv));

        try {
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
            System.out.println(ex);
            return null;
        }
        
        byte[] decryptedText;

        try {
            decryptedText = cipher.doFinal(message.getMessage());
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex);
            return null;
        }
        
        return Utils.toString(decryptedText);
    }
}
