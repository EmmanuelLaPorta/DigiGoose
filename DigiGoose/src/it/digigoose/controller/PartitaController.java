package it.digigoose.controller;

// import java.io.IOException;
import java.util.List;

import it.digigoose.model.*;


public class PartitaController {
    private Partita partita;
    
    public PartitaController() {
    }
    
    public PartitaController(Partita partita) {
    	this();
        this.partita = partita;
    }
    
    public void iniziaPartita(List<Giocatore> giocatori) {
        partita = new Partita(giocatori);
        partita.determinaOrdineGiocatori();
        partita.inizializzaPosizioni();
        partita.setStato(StatoPartita.IN_CORSO);
    }
    
    public Giocatore getGiocatoreCorrente() {
        return partita.getGiocatoreCorrente();
    }
    
    public void passaTurno() {
        // Non è più necessario gestire il giroCorrente qui
        // poiché sarà gestito nella classe Partita
        partita.passaAlProssimoGiocatore();
    }
    
    public int getGiroCorrente() {
        return partita.getGiroCorrente();
    }
    
    public int[] tiraDadi() {
        // Per il gioco dell'oca classico, si usano due dadi a sei facce
        Dadi dadi = new Dadi(6, 2);
        return dadi.lancia();
    }
    
    public Casella muoviPedina(Giocatore giocatore, int passi) {
        int posizionePrecedente = giocatore.getPosizione();
        int nuovaPosizione = posizionePrecedente + passi;
        
        // Gestione limiti del tabellone
        if (nuovaPosizione > partita.getTabellone().getPosizioneMassima()) {
            int eccesso = nuovaPosizione - partita.getTabellone().getPosizioneMassima();
            nuovaPosizione = partita.getTabellone().getPosizioneMassima() - eccesso;
        }
        
        giocatore.setPosizione(nuovaPosizione);
        return partita.getTabellone().getCasella(nuovaPosizione);
    }
    
    public void applicaEffettoCasella(Casella casella, Giocatore giocatore) {
        if (casella.getNumero() == 63) {           
            partita.setStato(StatoPartita.TERMINATA);
        } else if (casella != null && casella.isSpeciale()) {
            casella.applicaEffetto(giocatore);
        }
    }
    
    public boolean verificaVincitore(Giocatore giocatore) {
        if (giocatore.getPosizione() == 63) {
            return true;
        }
        return false;
    }
    
    
    public Partita getPartita() {
        return partita;
    }
    
    public void setPartita(Partita partita) {
        this.partita = partita;
    }
    
    public void verificaELiberaGiocatoriBloccati(int[] valoreDadi) {
        // Controlla se qualcuno ha lanciato un 6 (per ATTENDI_DADO)
        boolean dado6Lanciato = false;
        boolean dado3Lanciato = false;
        boolean dado4Lanciato = false;
        boolean dado5Lanciato = false;
        
        for (int valore : valoreDadi) {
            if (valore == 6) dado6Lanciato = true;
            if (valore == 3) dado3Lanciato = true;
            if (valore == 4) dado4Lanciato = true;
            if (valore == 5) dado5Lanciato = true;
        }
        
        // Se è stato lanciato un 6, libera tutti i giocatori in attesa di un dado 6
        if (dado6Lanciato) {
            for (Giocatore g : partita.getGiocatori()) {
                // -1 indica ATTENDI_DADO
                if (g.getTurniSaltati() == -1) {
                    g.setTurniSaltati(0); // Resetta, il giocatore è ora libero
                    System.out.println(g.getNome() + " è stato liberato e può giocare al prossimo turno!");
                }
            }
        }
        
        // Verifica se il giocatore corrente è in prigione (-2) e ha fatto 6
        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
        if (giocatoreCorrente.getTurniSaltati() == -2 && dado6Lanciato) {
            giocatoreCorrente.setTurniSaltati(0); // Libera dalla prigione
            System.out.println(giocatoreCorrente.getNome() + " ha fatto 6 ed è uscito di prigione!");
        }
        
        // Verifica se il giocatore è sulla casella 26 e deve fare 3 o 6
        if (giocatoreCorrente.getTurniSaltati() == -3 && (dado3Lanciato || dado6Lanciato)) {
            giocatoreCorrente.setTurniSaltati(0); // Libera il giocatore
            System.out.println(giocatoreCorrente.getNome() + " ha fatto " + 
                              (dado3Lanciato ? "3" : "6") + " e può proseguire!");
        }
        
        // Verifica se il giocatore è sulla casella 53 e deve fare 4 o 5
        if (giocatoreCorrente.getTurniSaltati() == -4 && (dado4Lanciato || dado5Lanciato)) {
            giocatoreCorrente.setTurniSaltati(0); // Libera il giocatore
            System.out.println(giocatoreCorrente.getNome() + " ha fatto " + 
                              (dado4Lanciato ? "4" : "5") + " e può proseguire!");
        }
    }

    // Metodi da aggiungere alla classe PartitaController

    /**
     * Gestisce gli effetti speciali che richiedono interazione con i dadi
     */
    public void gestisciEffettiSpeciali(Casella casella, Giocatore giocatore, int[] valoreDadi) {
        if (casella == null || !casella.isSpeciale()) return;
        
        int sommaDadi = valoreDadi[0] + valoreDadi[1];
        
        switch (casella.getTipoEffetto()) {
            case RADDOPPIA_MOVIMENTO:
                // Per la casella 14: raddoppia il punteggio dei dadi
                int nuovaPosizione = giocatore.getPosizione() + sommaDadi;
                giocatore.setPosizione(nuovaPosizione);
                System.out.println("Effetto: movimento raddoppiato! Ti muovi di altri " + sommaDadi + " passi.");
                break;
                
            case RADDOPPIA_PUNTEGGIO:
                // Per la casella 32: raddoppia il punteggio dei dadi
                int puntiExtra = sommaDadi;
                giocatore.setPosizione(giocatore.getPosizione() + puntiExtra);
                System.out.println("Effetto: punteggio raddoppiato! Ti muovi di altri " + puntiExtra + " passi.");
                break;
                
            case RILANCIA_TORNA_INDIETRO:
                // Per le caselle 27 e 59: rilancia i dadi e torna indietro
                int[] nuovoLancio = tiraDadi();
                int passiIndietro = nuovoLancio[0] + nuovoLancio[1];
                int posizioneFinale = Math.max(1, giocatore.getPosizione() - passiIndietro);
                giocatore.setPosizione(posizioneFinale);
                System.out.println("Effetto: hai rilanciato i dadi (" + nuovoLancio[0] + ", " + nuovoLancio[1] + 
                                ") e torni indietro di " + passiIndietro + " caselle.");
                break;
                
            default:
                // Gli altri effetti sono già gestiti nella classe Casella
                break;
        }
    }

    /**
     * Metodo esteso per applicare effetti casella, inclusi quelli speciali
     */
    public void applicaEffettoCasellaEsteso(Casella casella, Giocatore giocatore, int[] valoreDadi) {
        if (casella.getNumero() == 63) {           
            partita.setStato(StatoPartita.TERMINATA);
        } else if (casella != null && casella.isSpeciale()) {
            // Prima applica l'effetto base della casella
            casella.applicaEffetto(giocatore);
            
            // Poi gestisci gli effetti speciali che richiedono interazione con i dadi
            gestisciEffettiSpeciali(casella, giocatore, valoreDadi);
        }
    }
}