/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toywhats;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import de.taimos.totp.TOTP;
import java.io.FileOutputStream;

/**
 *
 * @author gabri
 */
public class Authenticator {
    
    private static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);

    }
    
    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
    
    public static void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }

    }
        
    private static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static boolean authenticate(String phone){

        try {

            // Cria chave secreta simétrica
            String secret = generateSecretKey();
            String TOTPcode = getTOTPCode(secret);

            String barCodeUrl = getGoogleAuthenticatorBarCode(secret, phone, "ToyWhats");

            int width = 246;
            int height = 246;

            // Fica no diretório do projeto.
            createQRCode(barCodeUrl, "auth.png", height, width);
            System.out.println(TOTPcode);
            System.out.println("Procure o arquivo auth.png no diretorio do projeto e leia o QR code para digitar o código");
            createQRCode(TOTPcode, "auth2.png", height, width);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Entre o código de autenticação: ");

            String code = scanner.nextLine();

            if(code.equals(getTOTPCode(secret))) {
                return true;
            } else {
                return false;
            }
        } catch (WriterException | IOException ex) {
            System.out.println(ex);
            return false;
        }
    }
    
}
