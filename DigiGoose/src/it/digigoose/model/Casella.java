package it.digigoose.model;

import java.io.Serializable;

public class Casella implements Serializable {
    private int numero;
    private boolean speciale;
    private TipoEffettoCasella tipoEffetto;
    private int parametroEffetto; // Parametro aggiuntivo per effetti personalizzabili
    private static final long serialVersionUID = 1L;
    
    public Casella(int numero) {
        this.numero = numero;
        this.speciale = false;
        this.tipoEffetto = TipoEffettoCasella.NESSUNO;
        this.parametroEffetto = 0;
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
        this.speciale = tipoEffetto != TipoEffettoCasella.NESSUNO;
    }
    
    public int getParametroEffetto() {
        return parametroEffetto;
    }
    
    public void setParametroEffetto(int parametro) {
        this.parametroEffetto = parametro;
    }
    
    public void applicaEffetto(Giocatore giocatore) {
        if (!isSpeciale()) return;
        
        switch (tipoEffetto) {
            case RILANCIA: 
                giocatore.setRichiedeRelancio(true);
                break;
                
            case PONTE:
                giocatore.setPosizione(12);
                break;

            case POZZO: 
                giocatore.setTurniSaltati(2); 
                System.out.println("Sei caduto nel pozzo! Salterai 2 turni.");
                break;
            
            case RITORNO_INIZIO:
                giocatore.setPosizione(1);
                break;
                
            case RADDOPPIA_MOVIMENTO:
                break;
                
            case FERMA_UN_TURNO:
                giocatore.setTurniSaltati(1);
                break;
                
            case FERMA_DUE_TURNI:
                giocatore.setTurniSaltati(2);
                break;
                
            case VOLA_AVANTI:
                giocatore.setPosizione(28);
                break;
                
            case ATTENDI_DADO:
                giocatore.setTurniSaltati(-1); 
                break;
                
            case ATTENDI_DADO_SPECIFICO:
                if (giocatore.getPosizione() == 26) {
                    giocatore.setTurniSaltati(-3);
                    System.out.println("Devi ottenere 3 o 6 con almeno un dado per proseguire!");
                } else if (giocatore.getPosizione() == 53) {
                    giocatore.setTurniSaltati(-4);
                    System.out.println("Devi ottenere 4 o 5 con almeno un dado per proseguire!");
                }
                break;
                
            case RILANCIA_TORNA_INDIETRO: 
                // L'effetto è gestito interamente nell'InterfacciaUtente/Controller
                // che farà un tiro di dadi dedicato per tornare indietro.
                break;
                
            case RADDOPPIA_PUNTEGGIO:
                break;
                
            case VAI_INDIETRO:
                giocatore.setPosizione(Math.max(1, giocatore.getPosizione() - 5));
                break;
                
            case LABIRINTO:
                giocatore.setPosizione(35);
                break;
                
            case PRIGIONE: 
                giocatore.setTurniSaltati(2); 
                break;
                
            case VOLA_E_FERMA:
                giocatore.setPosizione(57);
                giocatore.setTurniSaltati(1);
                break;
                
            default:
                break;
        }
    }
}