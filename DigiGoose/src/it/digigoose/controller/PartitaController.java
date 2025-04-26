package it.digigoose.controller;

import java.util.List;

import it.digigoose.model.*;

public class PartitaController {
    private Partita partita;
    
    public PartitaController() {
    }
    
    public PartitaController(Partita partita) {
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
        partita.passaAlProssimoGiocatore();
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
        if (casella != null && casella.isSpeciale()) {
            casella.applicaEffetto(giocatore);
        }
    }
    
    public boolean verificaVincitore(Giocatore giocatore) {
        // Nel gioco dell'oca, si vince arrivando esattamente alla casella 63
        return giocatore.getPosizione() == partita.getTabellone().getPosizioneMassima();
    }
    
    public void salvaPartita() {
        // Implementazione per salvare la partita (per futuro sviluppo)
    }
    
    public Partita caricaPartita() {
        // Implementazione per caricare una partita salvata (per futuro sviluppo)
        return null;
    }
    
    public Partita getPartita() {
        return partita;
    }
    
    public void setPartita(Partita partita) {
        this.partita = partita;
    }
}