package com.example.flowerplatform.security.oauth2.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
@Slf4j
//authorizationRequest를 encrypt하고 decrypt하는 클래스
public class StringEncAndDecryptor {

    @Value("${security.aes.key}")
    private String KEY;

    private final String ALGORITHM = "AES";


    public String encrypt(byte[] bytesToEncrypt) {
        try {
            Key aesKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(bytesToEncrypt);

            return Base64.getEncoder().encodeToString(encrypted);


        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return null;
    }

    public byte[] decrypt(String strToDecrypt)
    {
        try{
            Key aesKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);


            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey );
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));

            return decrypted;
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }

        return null;
    }
}
