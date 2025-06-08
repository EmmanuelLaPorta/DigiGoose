package it.digigoose.model;

import java.io.Serializable;


/**
* Rappresenta una pedina del gioco.
* Ogni pedina ha un colore e una posizione sul tabellone.
*/


public class Pedina implements Serializable {
    private Colore colore;
    private int posizione;
    
    private static final long serialVersionUID = 1L;
    
    public Pedina(Colore colore) {
        this.colore = colore;
        this.posizione = 0; 
    }
    
    public Colore getColore() {
        return colore;
    }
    
    public int getPosizione() {
        return posizione;
    }
    
    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }
    
    public int muovi(int passi) {
        this.posizione += passi;
        return this.posizione;
    }
}