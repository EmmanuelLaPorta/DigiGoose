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
            case BALZO_E_RILANCIA:
                giocatore.setRichiedeRelancio(true);
                // Avanza di 3 caselle
                giocatore.setPosizione(giocatore.getPosizione() + 3);
                break;
                
            case PONTE:
                // Vai alla casella 12
                giocatore.setPosizione(12);
                break;

            case POZZO:
                giocatore.saltaTurni(3);
                System.out.println("Sei caduto nel pozzo! Salterai 3 turni.");
                break;
            
            case RITORNO_INIZIO:
                // Torna alla casella di partenza
                giocatore.setPosizione(1);
                break;
                
            case RADDOPPIA_MOVIMENTO:
                // L'implementazione dipende dal controller
                // Verrà gestito nel PartitaController
                break;
                
            case FERMA_UN_TURNO:
                giocatore.setTurniSaltati(1);
                break;
                
            case FERMA_DUE_TURNI:
                giocatore.setTurniSaltati(2);
                break;
                
            case VOLA_AVANTI:
                // Vola alla casella 28 (per la casella 23)
                giocatore.setPosizione(28);
                break;
                
            case ATTENDI_DADO:
                // Deve attendere che un altro giocatore lanci un 6 per muoversi
                giocatore.setTurniSaltati(-1); // Valore speciale -1 indica attesa del dado
                break;
                
            case ATTENDI_DADO_SPECIFICO:
                // Deve attendere un valore specifico (gestito nel controller)
                giocatore.setTurniSaltati(-3); // -3 per casella 26 (attende 3,6)
                                              // -4 per casella 53 (attende 4,5)
                if (giocatore.getPosizione() == 26) {
                    giocatore.setTurniSaltati(-3);
                    System.out.println("Devi ottenere 3 o 6 con almeno un dado per proseguire!");
                } else if (giocatore.getPosizione() == 53) {
                    giocatore.setTurniSaltati(-4);
                    System.out.println("Devi ottenere 4 o 5 con almeno un dado per proseguire!");
                }
                break;
                
            case RILANCIA_TORNA_INDIETRO:
                giocatore.setRichiedeRelancio(true);
                // Il valore di quanto tornare indietro dipende dal rilancio
                // Verrà gestito nel controller
                break;
                
            case RADDOPPIA_PUNTEGGIO:
                // Implementazione nel controller
                break;
                
            case VAI_INDIETRO:
                // Torna indietro di 5 caselle
                giocatore.setPosizione(Math.max(1, giocatore.getPosizione() - 5));
                break;
                
            case LABIRINTO:
                // Torna alla casella 35 (per la casella 42)
                giocatore.setPosizione(35);
                break;
                
            case PRIGIONE:
                // Resta fermo finché non fai 6
                giocatore.setTurniSaltati(-2); // Gestito diversamente nel controller
                break;
                
            case VOLA_E_FERMA:
                // Vola alla casella 57 e fermati un turno
                giocatore.setPosizione(57);
                giocatore.setTurniSaltati(1);
                break;
                
            default:
                break;
        }
    }
}