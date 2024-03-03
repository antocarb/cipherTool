/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.controllo;

import it.unibas.carbonea.Applicazione;
import it.unibas.carbonea.Costanti;
import it.unibas.carbonea.modello.AESAlgorithm;
import it.unibas.carbonea.modello.FileManager;
import it.unibas.carbonea.modello.PDFFile;
import it.unibas.carbonea.modello.RSAAlgorithm;
import it.unibas.carbonea.vista.PannelloPrincipale;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author anton
 */
public class ControlloPannelloPrincipale {
    
    private Action azioneEsci = new AzioneEsci();
    private Action azioneCaricaFile = new AzioneCaricaFile();
    private Action azioneCifraMetadati = new AzioneCifraMetadati();
    private Action azioneCifraNomi = new AzioneCifraNomi();
    private Action azioneCifraNumeri = new AzioneCifraNumeri();
    private Action azioneCifraTestoSelezionato = new AzioneCifraTestoSelezionato();

    private AESAlgorithm aes;
    private RSAAlgorithm rsa;
    
    
    public Action getAzioneEsci() {
        return azioneEsci;
    }

    public Action getAzioneCaricaFile() {
        return azioneCaricaFile;
    }
    
    public Action getAzioneCifraMetadati() {
        return azioneCifraMetadati;
    }
    
    public Action getAzioneCifraNomi() {
        return azioneCifraNomi;
    }

    public void setAzioneCifraNomi(Action azioneCifraNomi) {
        this.azioneCifraNomi = azioneCifraNomi;
    }
    
    public Action getAzioneCifraNumeri() {
        return azioneCifraNumeri;
    }

    public void setAzioneCifraNumeri(Action azioneCifraNumeri) {
        this.azioneCifraNumeri = azioneCifraNumeri;
    }
    
    public Action getAzioneCifraTestoSelezionato() {
        return azioneCifraTestoSelezionato;
    }

    public void setAzioneCifraTestoSelezionato(Action azioneCifraTestoSelezionato) {
        this.azioneCifraTestoSelezionato = azioneCifraTestoSelezionato;
    }
    
    private class AzioneEsci extends AbstractAction{

        public AzioneEsci() {
            this.putValue(NAME, "Esci");
            this.putValue(SHORT_DESCRIPTION, "Esci dall'applicazione");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    
    private class AzioneCaricaFile extends AbstractAction{

        public AzioneCaricaFile() {
            this.putValue(NAME, "Carica");
            this.putValue(SHORT_DESCRIPTION, "Carica un file");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setFileFilter(new FileNameExtensionFilter("Documento PDF", "pdf"));
            fc.setAcceptAllFileFilterUsed(false);
            int result = fc.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileSelezionato = fc.getSelectedFile();
                Path percorso = fileSelezionato.toPath();
                System.out.println("Path del file: " + percorso.toString());
                Applicazione.getInstance().getModello().putBean(Costanti.PATH_FILE, percorso);
                try {
                    PDDocument pdfDoc = PDDocument.load(fileSelezionato);
                    Applicazione.getInstance().getPannelloPrincipale().setLabelFile(fileSelezionato.getName());
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("File caricato");
                    
                    PDFFile pdf = caricaPDFFile(pdfDoc);
                    Applicazione.getInstance().getModello().putBean(Costanti.PDF_FILE, pdf);
                    
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("Estraggo i metadati e il testo...");
                    Applicazione.getInstance().getPannelloPrincipale().abilitaCampiMetadati(true);
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("Metadati e testo estratti");
                    Applicazione.getInstance().getPannelloPrincipale().abilitaCampoNomi(false);
                    Applicazione.getInstance().getPannelloPrincipale().abilitaCampoTesto(true);
                    estraiMetadati(pdf);
                    estraiText(pdf);
                } catch (IOException ex) {
                    Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        private PDFFile caricaPDFFile(PDDocument documento) throws IOException {
            PDFFile pdf = new PDFFile();
            pdf.setDocument(documento);
            PDDocumentInformation pdi = documento.getDocumentInformation();
            pdf.setTitle(pdi.getTitle());
            pdf.setAuthor(pdi.getAuthor());
            pdf.setSubject(pdi.getSubject());
            pdf.setKeywords(pdi.getKeywords());
            pdf.setCreator(pdi.getCreator());
            pdf.setProducer(pdi.getProducer());
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            pdf.setCreationDate(df.format(pdi.getCreationDate().getTime()));
            pdf.setModificationDate(df.format(pdi.getModificationDate().getTime()));
            pdf.setText(estraiTesto(documento));
            return pdf;
        }
        
        private void estraiMetadati(PDFFile pdf){
            PannelloPrincipale pp = Applicazione.getInstance().getPannelloPrincipale();
            pp.setTitolo(pdf.getTitle());
            pp.setAutore(pdf.getAuthor());
            pp.setOggetto(pdf.getSubject());
            pp.setParoleChiave(pdf.getKeywords());
            pp.setCreatore(pdf.getCreator());
            pp.setProduttore(pdf.getProducer());
            pp.setDataCreazione(pdf.getCreationDate());
            pp.setDataUltimaModifica(pdf.getModificationDate());
        }
        
        private String estraiTesto(PDDocument documento) throws IOException {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(documento);
        }
        
        private void estraiText(PDFFile pdf) throws IOException{
            Applicazione.getInstance().getPannelloPrincipale().setCampoTesto(estraiTesto(pdf.getDocument()));
        }
    }
    
    private class AzioneCifraMetadati extends AbstractAction{

        public AzioneCifraMetadati() {
            this.putValue(NAME, "Cifra metadati");
            this.putValue(SHORT_DESCRIPTION, "Cifra metadati del file");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(Applicazione.getInstance().getPannelloPrincipale().isAesSelected() && Applicazione.getInstance().getModello().getBean(Costanti.PDF_FILE) != null){
                try{
                    FileManager fileManager = new FileManager();
                    aes = new AESAlgorithm();
                    String aesKey = aes.generatesKey();
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("Chiave AES generata");
                    //salvo chiave AES
                    fileManager.saveKey(aesKey);
                    
                    //prendo il file pdf ed estraggo i metadati che ho cifrato con AES
                    PDFFile pdf = (PDFFile)Applicazione.getInstance().getModello().getBean(Costanti.PDF_FILE);
                    estraiMetadatiAES(pdf, aes, aesKey);
                    
                    //salvo il documento PDF modificato
                    Path percorsoFile = (Path)Applicazione.getInstance().getModello().getBean(Costanti.PATH_FILE);
                    fileManager.savePDF(pdf.getDocument(), percorsoFile);
                    
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("Metadati cifrati salvati");
                    Applicazione.getInstance().getVistaFrame().mostraMessaggioInformativo("Metadati cifrati nel documento");
                    Applicazione.getInstance().getPannelloPrincipale().abilitaCampoNomi(true);
                    //chiudo il documento PDF modificato
                    fileManager.closePDF(pdf.getDocument());     
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else if(Applicazione.getInstance().getPannelloPrincipale().isRsaSelected() && Applicazione.getInstance().getModello().getBean(Costanti.PDF_FILE) != null){
                rsa = new RSAAlgorithm();
                FileManager fileManager = new FileManager();
                try {
                    rsa.generatesKeys();
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                }
                Applicazione.getInstance().getPannelloPrincipale().statusMessage("Chiavi RSA generate");
                PublicKey pKey = rsa.getPublicKey();
                
                rsa = new RSAAlgorithm(pKey);
                aes = new AESAlgorithm();
                try {
                    //genero la chiave con AES
                    String aesKey = aes.generatesKey();
                    //prendo il file pdf ed estraggo i metadati che ho cifrato con la chiave AES
                    PDFFile pdf = (PDFFile)Applicazione.getInstance().getModello().getBean(Costanti.PDF_FILE);
                    estraiMetadatiAES(pdf, aes, aesKey);
                    //cripto la chiave AES con RSA
                    byte[] aesKeyCrypted = rsa.encode(aesKey);
                    //salvo su disco la chiave AES criptata e il documento PDF modificato
                    fileManager.saveKeyCrypted(aesKeyCrypted);
                    Path percorsoFile = (Path)Applicazione.getInstance().getModello().getBean(Costanti.PATH_FILE);
                    fileManager.savePDF(pdf.getDocument(), percorsoFile);
                    
                    Applicazione.getInstance().getPannelloPrincipale().statusMessage("Metadati cifrati salvati");
                    Applicazione.getInstance().getVistaFrame().mostraMessaggioInformativo("Metadati cifrati");
                    Applicazione.getInstance().getPannelloPrincipale().abilitaCampoNomi(true);
                    //chiudo documento PDF modificato
                    fileManager.closePDF(pdf.getDocument());
                } catch (Exception ex) {
                    Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        }
        
        private void estraiMetadatiAES(PDFFile pdf, AESAlgorithm aes, String aesKey) throws Exception{
            PDDocumentInformation pdi = pdf.getDocument().getDocumentInformation();
            PannelloPrincipale pp = Applicazione.getInstance().getPannelloPrincipale();
            if(!pdf.getTitle().isEmpty()){
                pp.setTitolo(aes.encrypt(pdf.getTitle(), aesKey));
                pdi.setTitle(aes.encrypt(pdf.getTitle(), aesKey));
            } else {
                pp.setTitolo("");
                pdi.setTitle("");
            }
            if(!pdf.getAuthor().isEmpty()){
                pp.setAutore(aes.encrypt(pdf.getAuthor(), aesKey));
                pdi.setAuthor(aes.encrypt(pdf.getAuthor(), aesKey));
            } else {
                pp.setAutore("");
                pdi.setAuthor("");
            }
            if(!pdf.getSubject().isEmpty()){
                pp.setOggetto(aes.encrypt(pdf.getSubject(), aesKey));
                pdi.setSubject(aes.encrypt(pdf.getSubject(), aesKey));
            } else {
                pp.setOggetto("");
                pdi.setSubject("");
            }
            if(!pdf.getKeywords().isEmpty()){
                pp.setParoleChiave(aes.encrypt(pdf.getKeywords(), aesKey));
                pdi.setKeywords(aes.encrypt(pdf.getKeywords(), aesKey));
            } else {
                pp.setParoleChiave("");
                pdi.setKeywords("");
            }
            if(!pdf.getCreator().isEmpty()){
                pp.setCreatore(aes.encrypt(pdf.getCreator(), aesKey));
                pdi.setCreator(aes.encrypt(pdf.getCreator(), aesKey));
            } else {
                pp.setCreatore("");
                pdi.setCreator("");
            }
            if(!pdf.getProducer().isEmpty()){
                pp.setProduttore(aes.encrypt(pdf.getProducer(), aesKey));
                pdi.setProducer(aes.encrypt(pdf.getProducer(), aesKey));
            } else {
                pp.setProduttore("");
                pdi.setProducer("");
            }
            Applicazione.getInstance().getModello().putBean(Costanti.DATA_CREAZIONE, pdi.getCreationDate().getTime());
            Applicazione.getInstance().getModello().putBean(Costanti.DATA_MODIFICA, pdi.getModificationDate().getTime());
            pp.setDataCreazione(aes.encrypt(pdf.getCreationDate(), aesKey));
            Calendar dataCreazioneCasuale = generaDataCasuale(); 
            pdi.setCreationDate(dataCreazioneCasuale);
            pp.setDataUltimaModifica(aes.encrypt(pdf.getModificationDate(), aesKey));
            Calendar dataModificaCasuale = generaDataCasuale(); 
            pdi.setModificationDate(dataModificaCasuale);
            System.out.println("Data creazione originale: " + Applicazione.getInstance().getModello().getBean(Costanti.DATA_CREAZIONE));
            System.out.println("Data modifica originale: " + Applicazione.getInstance().getModello().getBean(Costanti.DATA_MODIFICA));
        }
        
        /*Creo un oggetto Random per generare valori casuali
        Genero un anno casuale compreso tra un valore minimo e massimo
        Genero un mese casuale (da 0 a 11, quindi aggiungi 1)
        Genero un giorno casuale (da 1 a 28/30/31, a seconda del mese)
        Genero ore, minuti, secondi e millisecondi casuali
        Creo un oggetto Calendar e imposto i valori casuali*/
        
        private Calendar generaDataCasuale() {
            Random random = new Random();
            
            int minYear = 2000;
            int maxYear = 2022;
            int year = random.nextInt(maxYear - minYear + 1) + minYear;
            
            int month = random.nextInt(12) + 1;

            int day = random.nextInt(31) + 1; 

            int hour = random.nextInt(24);
            int minute = random.nextInt(60);
            int second = random.nextInt(60);
            int millisecond = random.nextInt(1000);

            Calendar dataCasuale = new GregorianCalendar(year, month - 1, day, hour, minute, second);
            dataCasuale.set(Calendar.MILLISECOND, millisecond);

            return dataCasuale;
        }         
    }
    
    public String cifraParolaConAES(String parolaDaCercare) throws Exception{
        String nuovaParola = "";
        AESAlgorithm aes = new AESAlgorithm();
        String aesKey = aes.generatesKey();
        String parolaCifrata = aes.encrypt(parolaDaCercare, aesKey);
        for(int i = 0; i < parolaDaCercare.length(); ++i) {
            nuovaParola += parolaDaCercare.charAt(i) == ' ' ? " " : parolaCifrata.charAt(i);
        }
        return nuovaParola;
    }
    
    public String cifraParolaConRSA(String parolaDaCercare) throws Exception{
        String nuovaParola = "";
        RSAAlgorithm rsa = new RSAAlgorithm();
        AESAlgorithm aes = new AESAlgorithm();
        FileManager fileManager = new FileManager();
        String aesKey = aes.generatesKey();
        try {
            rsa.generatesKeys();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
        }
        Applicazione.getInstance().getPannelloPrincipale().statusMessage("Chiavi RSA generate");
        PublicKey pKey = rsa.getPublicKey();
        rsa = new RSAAlgorithm(pKey);
        byte[] aesKeyCrypted = rsa.encode(aesKey);
        fileManager.saveKeyCrypted(aesKeyCrypted);
        String parolaCifrata = aes.encrypt(parolaDaCercare, aesKey);
        for(int i = 0; i < parolaDaCercare.length(); ++i) {
            nuovaParola += parolaDaCercare.charAt(i) == ' ' ? " " : parolaCifrata.charAt(i);
        }
        return nuovaParola;
    }
        
    public void copiaTesto(PDDocument documentoSorgente, Consumer<List<TextPosition>> aggiornamento) throws IOException{
        for(int i = 0; i < documentoSorgente.getNumberOfPages(); i++){
            PDPage paginaSorgente = documentoSorgente.getPage(i);
            copiaTestoCifrato(documentoSorgente, i, paginaSorgente, aggiornamento);
        }
    }
        
    public void copiaTestoCifrato(PDDocument documentoSorgente, int numeroPaginaSorgente, PDPage paginaSorgente, Consumer<List<TextPosition>> aggiornamento) throws IOException{
        List<TextPosition> listaCaratteri = new ArrayList<>();
        PDFTextStripper pdfTextStripper = new PDFTextStripper(){
            @Override
            protected void writeString(String testo, List<TextPosition> caratteri) throws IOException{
                listaCaratteri.addAll(caratteri);
                super.writeString(testo, caratteri);
            }
        };
        pdfTextStripper.setStartPage(numeroPaginaSorgente + 1);
        pdfTextStripper.setEndPage(numeroPaginaSorgente + 1);
        pdfTextStripper.getText(documentoSorgente);
            
        if(aggiornamento != null){
            aggiornamento.accept(listaCaratteri);
        }
            
        PDRectangle paginaSorgenteCropBox = paginaSorgente.getCropBox();
        float yOffset = paginaSorgenteCropBox.getUpperRightY() + paginaSorgenteCropBox.getLowerLeftY();
        try(PDPageContentStream contentStream = new PDPageContentStream(documentoSorgente, paginaSorgente, AppendMode.APPEND, true, true)){
            contentStream.beginText();
            float x = 0;
            float y = yOffset;
            for(TextPosition carattere : listaCaratteri){
                contentStream.setFont(carattere.getFont(), carattere.getFontSizeInPt());
                contentStream.newLineAtOffset(carattere.getX() - x, -(carattere.getY() - y));
                contentStream.showText(carattere.getUnicode());
                x = carattere.getX();
                y = carattere.getY();
            }
            contentStream.endText();
            contentStream.close();
        }catch(IOException ex){
            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception e){
            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private class AzioneCifraNomi extends AbstractAction{

        public AzioneCifraNomi() {
            this.putValue(NAME, "Cifra nomi");
            this.putValue(SHORT_DESCRIPTION, "Cifra dati sensibili nel documento");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Path pathFile = (Path)Applicazione.getInstance().getModello().getBean(Costanti.PATH_FILE);
            File fileSorgente = new File(pathFile.toUri());
            try {
                PDDocument documentoSorgente = PDDocument.load(fileSorgente);
                String parolaDaCercare = Applicazione.getInstance().getPannelloPrincipale().getNomePersona();
                if(parolaDaCercare.trim().isEmpty()){
                   Applicazione.getInstance().getVistaFrame().mostraMessaggioErrore("Attenzione! Inserisci nome/i da cifrare");
                   return;
                }
                boolean isRSA = Applicazione.getInstance().getPannelloPrincipale().isRsaSelected();
                String parolaCifrataAES = cifraParolaConAES(parolaDaCercare);
                String parolaCifrataRSA = cifraParolaConRSA(parolaDaCercare);
                
                for(int i = 0; i < documentoSorgente.getNumberOfPages(); i++){
                    PDPage paginaSorgente = documentoSorgente.getPage(i);
                    List<TextPosition> caratteri = new ArrayList<>();
                    PDFTextStripper pdfTextStripper = new PDFTextStripper(){
                        @Override
                        protected void writeString(String testo, List<TextPosition> listaCaratteri) throws IOException{
                            caratteri.addAll(listaCaratteri);
                            super.writeString(testo, listaCaratteri);
                        }
                    };
                    pdfTextStripper.setStartPage(i + 1);
                    pdfTextStripper.setEndPage(i + 1);
                    pdfTextStripper.getText(documentoSorgente);
                    
                    PDRectangle paginaSorgenteCropBox = paginaSorgente.getCropBox();
                    float yOffset = paginaSorgenteCropBox.getUpperRightY() + paginaSorgenteCropBox.getLowerLeftY();
                    
                    PDRectangle mediaBox = paginaSorgente.getMediaBox();
                    
                    float pageWidth = mediaBox.getWidth();
                    float pageHeight = mediaBox.getHeight();
                    try(PDPageContentStream contentStream = new PDPageContentStream(documentoSorgente, paginaSorgente, AppendMode.APPEND, true, true)){
                        float x = 0;
                        float y = yOffset;
                        for(TextPosition carattere : caratteri){
                            contentStream.setNonStrokingColor(255, 255, 255);
                            contentStream.addRect(x, y, pageWidth, pageHeight);
                            contentStream.fill();
                            x = carattere.getX();
                            y = carattere.getY();
                        }
                        contentStream.close();
                    }
                    if(isRSA){
                       copiaTesto(documentoSorgente, list->{
                        try {
                            cercaSostituisciParola(list, parolaDaCercare, parolaCifrataRSA);
                        } catch (Exception ex) {
                            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    } else {
                        copiaTesto(documentoSorgente, list->{
                        try {
                            cercaSostituisciParola(list, parolaDaCercare, parolaCifrataAES);
                        } catch (Exception ex) {
                            Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        });
                    }
                    
                }
                documentoSorgente.save(fileSorgente);
                documentoSorgente.close();
                Applicazione.getInstance().getPannelloPrincipale().statusMessage("Documento cifrato salvato");
                Applicazione.getInstance().getVistaFrame().mostraMessaggioInformativo("Nomi cifrati");
            } catch (IOException ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }
        /*uso StringTokenizer per suddividere il testo in parole
        utilizzo un set per contare le parole distinte
        itero attraverso le parole e le aggiungo al set
        */
        private void cercaSostituisciParola(List<TextPosition> caratteri, String parolaDaCercare, String parolaCifrata){
            if(parolaDaCercare == null || parolaDaCercare.length() == 0){
               return;
            }
            StringTokenizer tokenizer = new StringTokenizer(parolaDaCercare);
            Set<String> paroleDistinte = new HashSet<>();
            while (tokenizer.hasMoreTokens()) {
                String parola = tokenizer.nextToken();
                paroleDistinte.add(parola);
            }
            int posizioneCandidata = 0;
            String candidata = "";
            for(String parola : paroleDistinte){
                for(int i = 0; i < caratteri.size(); i++){
                    candidata += caratteri.get(i).getUnicode();
                    if(!parola.startsWith(candidata)){
                        candidata = "";
                        posizioneCandidata = i + 1;
                    } else if(parola.length() == candidata.length()){
                        for(int j = 0; j < parola.length(); j++){
                            TextPosition carattere = caratteri.get(posizioneCandidata);
                            int lunghezza = carattere.getUnicode().length();
                            String cifraQui = "";
                            if(lunghezza > 0 && j < parolaCifrata.length()){
                                int fine = j + lunghezza;
                                if(fine > parolaCifrata.length())
                                    fine = parolaCifrata.length();
                                cifraQui = parolaCifrata.substring(j, fine);
                            }
                            TextPosition nuovoCarattere = new TextPosition(carattere.getRotation(), carattere.getPageWidth(), carattere.getPageHeight(), 
                            carattere.getTextMatrix(), carattere.getEndX(), carattere.getEndY(), carattere.getHeight(), carattere.getIndividualWidths()[0],
                            carattere.getWidthOfSpace(), cifraQui, carattere.getCharacterCodes(), carattere.getFont(), carattere.getFontSize(), 
                            (int)carattere.getFontSizeInPt());
                            caratteri.set(posizioneCandidata, nuovoCarattere);
                            posizioneCandidata++;
                            j += lunghezza;
                        }
                    }
                }
            }     
        }
    }
    
    private class AzioneCifraNumeri extends AbstractAction{

        public AzioneCifraNumeri() {
            this.putValue(NAME, "Cifra numeri");
            this.putValue(SHORT_DESCRIPTION, "Cifra info numeriche");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Path pathFile = (Path)Applicazione.getInstance().getModello().getBean(Costanti.PATH_FILE);
            File fileSorgente = new File(pathFile.toUri());
            try {
                PDDocument documentoSorgente = PDDocument.load(fileSorgente);
                boolean isRSA = Applicazione.getInstance().getPannelloPrincipale().isRsaSelected();
                for(int i = 0; i < documentoSorgente.getNumberOfPages(); i++){
                    PDPage paginaSorgente = documentoSorgente.getPage(i);
                    List<TextPosition> caratteri = new ArrayList<>();
                    PDFTextStripper pdfTextStripper = new PDFTextStripper(){
                        @Override
                        protected void writeString(String testo, List<TextPosition> listaCaratteri) throws IOException{
                            caratteri.addAll(listaCaratteri);
                            super.writeString(testo, listaCaratteri);
                        }
                    };
                    pdfTextStripper.setStartPage(i + 1);
                    pdfTextStripper.setEndPage(i + 1);
                    pdfTextStripper.getText(documentoSorgente);
                    
                    PDRectangle paginaSorgenteCropBox = paginaSorgente.getCropBox();
                    float yOffset = paginaSorgenteCropBox.getUpperRightY() + paginaSorgenteCropBox.getLowerLeftY();
                    
                    PDRectangle mediaBox = paginaSorgente.getMediaBox();
                    
                    float pageWidth = mediaBox.getWidth();
                    float pageHeight = mediaBox.getHeight();
                    try(PDPageContentStream contentStream = new PDPageContentStream(documentoSorgente, paginaSorgente, AppendMode.APPEND, true, true)){
                        float x = 0;
                        float y = yOffset;
                        for(TextPosition carattere : caratteri){
                            contentStream.setNonStrokingColor(255, 255, 255);
                            contentStream.addRect(x, y, pageWidth, pageHeight);
                            contentStream.fill();
                            x = carattere.getX();
                            y = carattere.getY();
                        }
                        contentStream.close();
                    }
                    
                    String testo = pdfTextStripper.getText(documentoSorgente);
                    String patternString = "\\b\\d+(\\.\\d+)?\\b";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(testo);
                    while(matcher.find()) {
                        String infoEconomica = matcher.group();
                        String infoEconomicaCifrataAES = cifraParolaConAES(infoEconomica);
                        String infoEconomicaCifrataRSA = cifraParolaConRSA(infoEconomica);
                        if(isRSA){
                            copiaTesto(documentoSorgente, list->{
                            try {
                                cercaSostituisciParola(list, infoEconomica, infoEconomicaCifrataRSA);
                            } catch (Exception ex) {
                                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            });
                        } else {
                            copiaTesto(documentoSorgente, list->{
                            try {
                                cercaSostituisciParola(list, infoEconomica, infoEconomicaCifrataAES);
                            } catch (Exception ex) {
                                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            });
                        }
                    } 
                }
                documentoSorgente.save(fileSorgente);
                documentoSorgente.close();
                Applicazione.getInstance().getPannelloPrincipale().statusMessage("Documento cifrato salvato");
                Applicazione.getInstance().getVistaFrame().mostraMessaggioInformativo("Info numeriche cifrate");
            }catch (IOException ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void cercaSostituisciParola(List<TextPosition> caratteri, String infoEconomica, String parolaCifrata){
            if(infoEconomica == null || infoEconomica.length() == 0){
               return;
            }
            int posizioneCandidata = 0;
            String candidata = "";
            for(int i = 0; i < caratteri.size(); i++){
                candidata += caratteri.get(i).getUnicode();
                if(!infoEconomica.startsWith(candidata)){
                    candidata = "";
                    posizioneCandidata = i + 1;
                } else if(infoEconomica.length() == candidata.length()){
                    for(int j = 0; j < infoEconomica.length(); j++){
                        TextPosition carattere = caratteri.get(posizioneCandidata);
                        int lunghezza = carattere.getUnicode().length();
                        String cifraQui = "";
                        if(lunghezza > 0 && j < parolaCifrata.length()){
                            int fine = j + lunghezza;
                            if(fine > parolaCifrata.length())
                                fine = parolaCifrata.length();
                            cifraQui = parolaCifrata.substring(j, fine);
                        }
                        TextPosition nuovoCarattere = new TextPosition(carattere.getRotation(), carattere.getPageWidth(), carattere.getPageHeight(), 
                        carattere.getTextMatrix(), carattere.getEndX(), carattere.getEndY(), carattere.getHeight(), carattere.getIndividualWidths()[0],
                        carattere.getWidthOfSpace(), cifraQui, carattere.getCharacterCodes(), carattere.getFont(), carattere.getFontSize(), 
                        (int)carattere.getFontSizeInPt());
                        caratteri.set(posizioneCandidata, nuovoCarattere);
                        posizioneCandidata++;
                        j += lunghezza;
                    }
                }
            }
        }
    
    }    
    
    private class AzioneCifraTestoSelezionato extends AbstractAction{

        public AzioneCifraTestoSelezionato() {
            this.putValue(NAME, "Cifra testo");
            this.putValue(SHORT_DESCRIPTION, "Cifra testo selezionato");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Path pathFile = (Path)Applicazione.getInstance().getModello().getBean(Costanti.PATH_FILE);
            File fileSorgente = new File(pathFile.toUri());
            try {
                PDDocument documentoSorgente = PDDocument.load(fileSorgente);
                boolean isRSA = Applicazione.getInstance().getPannelloPrincipale().isRsaSelected();
                for(int i = 0; i < documentoSorgente.getNumberOfPages(); i++){
                    PDPage paginaSorgente = documentoSorgente.getPage(i);
                    List<TextPosition> caratteri = new ArrayList<>();
                    PDFTextStripper pdfTextStripper = new PDFTextStripper(){
                        @Override
                        protected void writeString(String testo, List<TextPosition> listaCaratteri) throws IOException{
                            caratteri.addAll(listaCaratteri);
                            super.writeString(testo, listaCaratteri);
                        }
                    };
                    pdfTextStripper.setStartPage(i + 1);
                    pdfTextStripper.setEndPage(i + 1);
                    pdfTextStripper.getText(documentoSorgente);
                    
                    PDRectangle paginaSorgenteCropBox = paginaSorgente.getCropBox();
                    float yOffset = paginaSorgenteCropBox.getUpperRightY() + paginaSorgenteCropBox.getLowerLeftY();
                    
                    PDRectangle mediaBox = paginaSorgente.getMediaBox();
                    
                    float pageWidth = mediaBox.getWidth();
                    float pageHeight = mediaBox.getHeight();
                    try(PDPageContentStream contentStream = new PDPageContentStream(documentoSorgente, paginaSorgente, AppendMode.APPEND, true, true)){
                        float x = 0;
                        float y = yOffset;
                        for(TextPosition carattere : caratteri){
                            contentStream.setNonStrokingColor(255, 255, 255);
                            contentStream.addRect(x, y, pageWidth, pageHeight);
                            contentStream.fill();
                            x = carattere.getX();
                            y = carattere.getY();
                        }
                        contentStream.close();
                    }
                    
                    String testoSelezionato = Applicazione.getInstance().getPannelloPrincipale().getTestoSelezionato();
                    String[] parole = testoSelezionato.split("\\s+");
                    for(String parola : parole){
                        String parolaCifrataAES = cifraParolaConAES(parola);
                        String parolaCifrataRSA = cifraParolaConRSA(parola);
                        if(isRSA){
                            copiaTesto(documentoSorgente, list->{
                            try {
                                cercaSostituisciParola(list, parola, parolaCifrataRSA);
                            } catch (Exception ex) {
                                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            });
                        } else {
                            copiaTesto(documentoSorgente, list->{
                            try {
                                cercaSostituisciParola(list, parola, parolaCifrataAES);
                            } catch (Exception ex) {
                                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            });
                        } 
                    } 
                }
                documentoSorgente.save(fileSorgente);
                documentoSorgente.close();
                Applicazione.getInstance().getPannelloPrincipale().statusMessage("Documento cifrato salvato");
                Applicazione.getInstance().getVistaFrame().mostraMessaggioInformativo("Testo cifrato");
            } catch (IOException ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ControlloPannelloPrincipale.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }
         
        private void cercaSostituisciParola(List<TextPosition> caratteri, String parola, String parolaCifrata){
            if(parola == null || parola.length() == 0){
               return;
            }
            int posizioneCandidata = 0;
            String candidata = "";
            
            for(int i = 0; i < caratteri.size(); i++){
                candidata += caratteri.get(i).getUnicode();
                if(!parola.startsWith(candidata)){
                    candidata = "";
                    posizioneCandidata = i + 1;
                } else if(parola.length() == candidata.length()){
                    for(int j = 0; j < parola.length(); j++){
                        TextPosition carattere = caratteri.get(posizioneCandidata);
                        int lunghezza = carattere.getUnicode().length();
                        String cifraQui = "";
                        if(lunghezza > 0 && j < parolaCifrata.length()){
                            int fine = j + lunghezza;
                            if(fine > parolaCifrata.length())
                                fine = parolaCifrata.length();
                            cifraQui = parolaCifrata.substring(j, fine);
                        }
                        TextPosition nuovoCarattere = new TextPosition(carattere.getRotation(), carattere.getPageWidth(), carattere.getPageHeight(), 
                        carattere.getTextMatrix(), carattere.getEndX(), carattere.getEndY(), carattere.getHeight(), carattere.getIndividualWidths()[0],
                        carattere.getWidthOfSpace(), cifraQui, carattere.getCharacterCodes(), carattere.getFont(), carattere.getFontSize(), 
                        (int)carattere.getFontSizeInPt());
                        caratteri.set(posizioneCandidata, nuovoCarattere);
                        posizioneCandidata++;
                        j += lunghezza;
                    }
                }
            }      
        }
    }
    
}
    
    
    
 