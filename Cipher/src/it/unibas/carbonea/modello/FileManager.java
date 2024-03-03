/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.modello;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author anton
 */
public class FileManager {
    
    public PDDocument loadPDF(String path) throws IOException{
        File file = new File(path);
        PDDocument pdfDoc = PDDocument.load(file);
        return pdfDoc;
    }
    
    public void savePDF(PDDocument pdfDoc, Path path) throws IOException{
        pdfDoc.save(Files.newOutputStream(path));
    }
        
    public JFileChooser creaFinestraCaricaFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF", "pdf"));
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }
    
    public JFileChooser creaFinestraSalvaFile() {
        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setFileFilter(new FileNameExtensionFilter("Documento PDF", "pdf"));
        return fc;
    }
    
    
    public void closePDF(PDDocument pdfDoc) throws IOException{
        pdfDoc.close();
    }
    
    public void saveKey(String aesKey){
        String currentDir = System.getProperty("user.dir") + "/Crypt/";
        Path saveDir = Paths.get(currentDir);
        try{
            Files.createDirectory(saveDir);
            //salvo chiave AES
            byte[] keyBytes = aesKey.getBytes();
            Files.write(Paths.get(saveDir + "/AesKey.txt"), keyBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE); 
        }catch(FileAlreadyExistsException ex){
            System.out.println("Percorso gia' esistente");
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void saveKeyCrypted(byte[] aesKeyCrypted) throws IOException{
        String currentDir = System.getProperty("user.dir") + "/Crypt/";
        Path saveDir = Paths.get(currentDir);
        try{
            Files.createDirectory(saveDir);
        }catch(FileAlreadyExistsException ex){
            System.out.println("Percorso gia' esistente");
        }
        Files.write(Paths.get(saveDir + "/AesKeyCrypt.txt"), aesKeyCrypted, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }
    
}
