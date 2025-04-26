package it.digigoose.model;

public class Casella {
    private int numero;
    private boolean speciale;
    private TipoEffettoCasella tipoEffetto;
    
    public Casella(int numero) {
        this.numero = numero;
        this.speciale = false;
        this.tipoEffetto = TipoEffettoCasella.NESSUNO;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public boolean isSpeciale() {
        return speciale;
    }
    
    public void setSpeciale(boolean speciale) {
        this.speciale = speciale;
    }
    
    public TipoEffettoCasella getTipoEffetto() {
        return tipoEffetto;
    }
    
    public void setTipoEffetto(TipoEffettoCasella tipoEffetto) {
        this.tipoEffetto = tipoEffetto;
    }
    
    public void applicaEffetto(Giocatore giocatore) {
        // Implementare i diversi effetti in base al tipo
        // Per adesso è vuoto
    }
}