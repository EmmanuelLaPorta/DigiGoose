package it.digigoose.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Rappresenta il tabellone di gioco, composto da una sequenza di caselle.
 * Inizializza il tabellone standard con 64 caselle (da 0 a 63),
 * configurando le caselle speciali secondo le regole definite.
 */


public class Tabellone implements Serializable {
    private List<Casella> caselle;
    private static final long serialVersionUID = 1L;
    
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
    	//creazione base delle caselle
        for (int i = 0; i <= 63; i++) {
            Casella casella = new Casella(i);
            aggiungiCasella(casella);
        }
        
        // aggiunta caselle speciali
        
        getCasella(5).setTipoEffetto(TipoEffettoCasella.RILANCIA);
        getCasella(6).setTipoEffetto(TipoEffettoCasella.PONTE);
        getCasella(9).setTipoEffetto(TipoEffettoCasella.RITORNO_INIZIO);
        getCasella(14).setTipoEffetto(TipoEffettoCasella.RADDOPPIA_MOVIMENTO);
        getCasella(18).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);        
        getCasella(19).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);       
        getCasella(23).setTipoEffetto(TipoEffettoCasella.VOLA_AVANTI);        
        getCasella(26).setTipoEffetto(TipoEffettoCasella.ATTENDI_DADO_SPECIFICO);        
        getCasella(27).setTipoEffetto(TipoEffettoCasella.RILANCIA_TORNA_INDIETRO);       
        getCasella(31).setTipoEffetto(TipoEffettoCasella.POZZO);        
        getCasella(32).setTipoEffetto(TipoEffettoCasella.RADDOPPIA_PUNTEGGIO);       
        getCasella(36).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);       
        getCasella(41).setTipoEffetto(TipoEffettoCasella.RILANCIA);      
        getCasella(42).setTipoEffetto(TipoEffettoCasella.LABIRINTO);      
        getCasella(45).setTipoEffetto(TipoEffettoCasella.VAI_INDIETRO);      
        getCasella(50).setTipoEffetto(TipoEffettoCasella.RILANCIA);       
        getCasella(52).setTipoEffetto(TipoEffettoCasella.PRIGIONE);      
        getCasella(53).setTipoEffetto(TipoEffettoCasella.ATTENDI_DADO_SPECIFICO);     
        getCasella(54).setTipoEffetto(TipoEffettoCasella.VOLA_E_FERMA);      
        getCasella(58).setTipoEffetto(TipoEffettoCasella.RITORNO_INIZIO);      
        getCasella(59).setTipoEffetto(TipoEffettoCasella.RILANCIA_TORNA_INDIETRO);
            
    }
    
    public void aggiungiCasella(Casella casella) {
        caselle.add(casella);
    }
    
    public int getPosizioneMassima() {
        return caselle.size() - 1;
    }
}