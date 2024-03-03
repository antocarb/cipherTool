/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.modello;

import java.text.DateFormat;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author anton
 */
public class PDFFile {
    
    private PDDocument document;
    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private String creationDate;
    private String modificationDate;
    private String text;

    public PDFFile() {
    }

    public PDFFile(PDDocument document, String title, String author, String subject, String keywords, String creator, String producer, String creationDate, String modificationDate) {
        this.document = document;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.keywords = keywords;
        this.creator = creator;
        this.producer = producer;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }
    
    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    public String getTitle() {
        return document.getDocumentInformation().getTitle() != null ? document.getDocumentInformation().getTitle() : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return document.getDocumentInformation().getAuthor() != null ? document.getDocumentInformation().getAuthor() : "";
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return document.getDocumentInformation().getSubject() != null ? document.getDocumentInformation().getSubject() : "";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getKeywords() {
        return document.getDocumentInformation().getKeywords() != null ? document.getDocumentInformation().getKeywords() : "";
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCreator() {
        return document.getDocumentInformation().getCreator() != null ? document.getDocumentInformation().getCreator() : "";
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }
    
    public String getCreationDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        return df.format(document.getDocumentInformation().getCreationDate().getTime()) != null ? df.format(document.getDocumentInformation().getCreationDate().getTime()) : "";
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        return df.format(document.getDocumentInformation().getModificationDate().getTime()) != null ? df.format(document.getDocumentInformation().getModificationDate().getTime()) : "";
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    

    
}
