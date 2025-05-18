package it.digigoose.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        // Creiamo prima tutte le caselle normali (63 caselle del gioco dell'oca)
        for (int i = 0; i <= 63; i++) {
            Casella casella = new Casella(i);
            aggiungiCasella(casella);
        }
        
        // Configura le caselle speciali seguendo le regole esatte
        
        // Casella 5: Balzo e rilancia
        getCasella(5).setTipoEffetto(TipoEffettoCasella.BALZO_E_RILANCIA);
        
        // Casella 6: Il ponte (vai alla casella 12)
        getCasella(6).setTipoEffetto(TipoEffettoCasella.PONTE);
        
        // Casella 9: Ritorna alla casella 1
        getCasella(9).setTipoEffetto(TipoEffettoCasella.RITORNO_INIZIO);
        
        // Casella 14: Raddoppia il punteggio dei dadi
        getCasella(14).setTipoEffetto(TipoEffettoCasella.RADDOPPIA_MOVIMENTO);
        
        // Casella 18: Fermati un turno
        getCasella(18).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);
        
        // Casella 19: Locanda, fermati un turno
        getCasella(19).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);
        
        // Casella 23: Vai alla casella 28
        Casella casella23 = getCasella(23);
        casella23.setSpeciale(true);
        casella23.setTipoEffetto(TipoEffettoCasella.VOLA_AVANTI);
        
        // Casella 26: Attendi dado specifico (3 o 6)
        getCasella(26).setTipoEffetto(TipoEffettoCasella.ATTENDI_DADO_SPECIFICO);
        
        // Casella 27: Rilancia e torna indietro
        getCasella(27).setTipoEffetto(TipoEffettoCasella.RILANCIA_TORNA_INDIETRO);
        
        // Casella 31: Pozzo, fermo per 2 turni
        getCasella(31).setTipoEffetto(TipoEffettoCasella.FERMA_DUE_TURNI);
        
        // Casella 32: Raddoppia punteggio dadi
        getCasella(32).setTipoEffetto(TipoEffettoCasella.RADDOPPIA_PUNTEGGIO);
        
        // Casella 36: Fermati un turno
        getCasella(36).setTipoEffetto(TipoEffettoCasella.FERMA_UN_TURNO);
        
        // Casella 41: Rilancia e vai avanti
        getCasella(41).setTipoEffetto(TipoEffettoCasella.BALZO_E_RILANCIA);
        
        // Casella 42: Labirinto, torna alla casella 35
        Casella casella42 = getCasella(42);
        casella42.setSpeciale(true);
        casella42.setTipoEffetto(TipoEffettoCasella.LABIRINTO);
        
        // Casella 45: Vai indietro di 5 caselle
        getCasella(45).setTipoEffetto(TipoEffettoCasella.VAI_INDIETRO);
        
        // Casella 50: Rilancia e vai avanti
        getCasella(50).setTipoEffetto(TipoEffettoCasella.BALZO_E_RILANCIA);
        
        // Casella 52: Prigione, fermo per 2 turni
        getCasella(52).setTipoEffetto(TipoEffettoCasella.FERMA_DUE_TURNI);
        
        // Casella 53: Attendi dado specifico (4 o 5)
        getCasella(53).setTipoEffetto(TipoEffettoCasella.ATTENDI_DADO_SPECIFICO);
        
        // Casella 54: Vola alla casella 57 e ferma un turno
        Casella casella54 = getCasella(54);
        casella54.setSpeciale(true);
        casella54.setTipoEffetto(TipoEffettoCasella.VOLA_E_FERMA);
        
        // Casella 58: Ritorna alla casella 1
        getCasella(58).setTipoEffetto(TipoEffettoCasella.RITORNO_INIZIO);
        
        // Casella 59: Rilancia e torna indietro
        getCasella(59).setTipoEffetto(TipoEffettoCasella.RILANCIA_TORNA_INDIETRO);
        
        // Le caselle dell'oca tradizionali (multiple di 9, tranne la 9 che ha regola speciale)
        for (int i = 18; i < 63; i += 9) {
            if (i != 18 && i != 27 && i != 36 && i != 45 && i != 54) { // Escludi quelle con regole specifiche
                Casella casellaOca = getCasella(i);
                casellaOca.setSpeciale(true);
                casellaOca.setTipoEffetto(TipoEffettoCasella.BALZO_E_RILANCIA);
            }
        }
    }
    
    public void aggiungiCasella(Casella casella) {
        caselle.add(casella);
    }
    
    public int getPosizioneMassima() {
        return caselle.size() - 1;
    }
}