package it.digigoose.model;

import java.util.ArrayList;
import java.util.List;

public class Tabellone {
    private List<Casella> caselle;
    
    public Tabellone() {
        caselle = new ArrayList<>();
        inizializzaTabellone();
    }
    
    public List<Casella> getCaselle() {
        return caselle;
    }
    
    public Casella getCasella(int posizione) {
        if (posizione >= 0 && posizione < caselle.size()) {
            return caselle.get(posizione);
        }
        return null;
    }
    
    public void inizializzaTabellone() {
        // Crea le 63 caselle del gioco dell'oca
        for (int i = 1; i <= 63; i++) {
            Casella casella = new Casella(i);
            
            // Per ora creiamo solo le caselle base, dopo aggiungeremo le speciali
            aggiungiCasella(casella);
        }
        
        // Qui in futuro implementeremo la configurazione delle caselle speciali
    }
    
    public void aggiungiCasella(Casella casella) {
        caselle.add(casella);
    }
    
    public int getPosizioneMassima() {
        return caselle.size();
    }
}