/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.modello;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

/**
 *
 * @author anton
 */
public class RSAAlgorithm {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public RSAAlgorithm() {
    }
    
    public RSAAlgorithm(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
    
    public RSAAlgorithm(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public RSAAlgorithm(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public void generatesKeys() throws NoSuchAlgorithmException, IOException{
        KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
        keyPair.initialize(2048);
        
        KeyPair kp = keyPair.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();
        
        String currentPath = System.getProperty("user.dir") + "/keys/";
        Path sPath = Paths.get(currentPath);
        try{
            Files.createDirectory(sPath);
        }catch(FileAlreadyExistsException ex){
            System.out.println("Percorso gia' esistente");
        }
        Path privateDirectory = Paths.get(sPath + "/key.priv");
        Path publicDirectory = Paths.get(sPath + "/key.pub");
        Files.write(privateDirectory, privateKey.getEncoded(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        Files.write(publicDirectory, publicKey.getEncoded(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    
    //codifico l'array di byte con l'algoritmo RSA
    public byte[] encode(String text)throws Exception{
        byte[] cipherText = null;
        if(publicKey == null){
           return null;
        }    
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);     
        cipherText = cipher.doFinal(text.getBytes());
        return cipherText;
    }
    
    //decodifico l'array di byte con l'algoritmo RSA
    public byte[] decode(String cipherText)throws Exception{  
        byte[] decryptedText = null;
        if(privateKey == null){
           return null;
        }
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedText = cipher.doFinal(cipherText.getBytes());
        return decryptedText;
    }
    
    
}