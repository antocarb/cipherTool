/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.modello;

import java.security.Key;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author anton
 */
public class AESAlgorithm {
    
    private String aesKey;
    private String bo;

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
    
    //passo il testo da cifrare e restituisco il testo in byte cifrato
    public String encrypt(String text, String aesKey)throws Exception{
        byte[] keyBytes = aesKey.getBytes(); 
        Key secretKey = new SecretKeySpec(keyBytes, "AES"); 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(text.getBytes());
        String cipherTextBase64 = Base64.getEncoder().encodeToString(cipherText);
        return cipherTextBase64;
    }
    
    //passo il testo cifrato e restituisco il testo in byte in chiaro
    public byte[] decrypt(String cipherText)throws Exception{
        byte[] keyBytes = getAesKey().getBytes();
        Key secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedText = cipher.doFinal(cipherText.getBytes());
        return decryptedText;
    }
    
    //genero chiave AES a 16 caratteri
    public String generatesKey(){
        String key = "";
        Random rand = new Random();
        for(int i = 0; i < 16; i++){
            int c = rand.nextInt(122-48)+48;
            if((c >= 58 && c <=64) | (c >= 91 && c <= 96) ){
                i--;
                continue;
            }
            key += ((char)c);
        }
        return key;
    }
    
    
    
}
