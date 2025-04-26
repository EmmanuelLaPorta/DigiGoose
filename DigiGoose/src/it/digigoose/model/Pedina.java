package it.digigoose.model;

public class Pedina {
    private Colore colore;
    private int posizione;
    
    public Pedina(Colore colore) {
        this.colore = colore;
        this.posizione = 0; // Posizione iniziale
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