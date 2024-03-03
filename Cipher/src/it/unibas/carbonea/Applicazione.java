/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea;

import it.unibas.carbonea.controllo.ControlloPannelloPrincipale;
import it.unibas.carbonea.modello.Modello;
import it.unibas.carbonea.vista.PannelloPrincipale;
import it.unibas.carbonea.vista.VistaFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author anton
 */
public class Applicazione {
    
    private ControlloPannelloPrincipale controlloPannelloPrincipale;
    private Modello modello;
    private VistaFrame vistaFrame;
    private PannelloPrincipale pannelloPrincipale;
    private static Applicazione instance = new Applicazione();
    
    public ControlloPannelloPrincipale getControlloPannelloPrincipale() {
        return controlloPannelloPrincipale;
    }

    public Modello getModello() {
        return modello;
    }

    public VistaFrame getVistaFrame() {
        return vistaFrame;
    }

    public PannelloPrincipale getPannelloPrincipale() {
        return pannelloPrincipale;
    }

    public static Applicazione getInstance() {
        return instance;
    }
    
    public void inizializza(){
        this.controlloPannelloPrincipale = new ControlloPannelloPrincipale();
        this.modello = new Modello();
        this.vistaFrame = new VistaFrame();
        this.vistaFrame.setTitle("Cipher");
        this.pannelloPrincipale = new PannelloPrincipale();
        this.pannelloPrincipale.inizializza();
        this.vistaFrame.inizializza();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               getInstance().inizializza();
            }
        });
    }
    
    
    
    
}
