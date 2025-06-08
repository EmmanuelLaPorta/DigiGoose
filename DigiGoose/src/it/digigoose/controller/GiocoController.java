package it.digigoose.controller;

import java.util.List;
import java.util.Random;

import it.digigoose.model.*;


/**
 * Controller della configurazione iniziale di una nuova partita.
 * Gestisce l'inserimento del numero di giocatori, la selezione del tipo (umano/CPU),
 * l'assegnazione dei nomi e dei colori delle pedine.
 * Una volta confermate le impostazioni, inizializza una nuova partita.
 */


public class GiocoController {
    private Partita partitaCorrente;
    private ImpostazioniPartita impostazioniPartita;
    
    public GiocoController() {
        this.impostazioniPartita = new ImpostazioniPartita();
    }
    
    public void avviaNuovaPartita() {
        this.impostazioniPartita = new ImpostazioniPartita();
    }
    
    public void inserisciNumeroGiocatori(int numeroGiocatori) {
        if (numeroGiocatori < 2 || numeroGiocatori > 6) {
            throw new IllegalArgumentException("Il numero di giocatori deve essere compreso tra 2 e 6");
        }
        impostazioniPartita.setNumeroGiocatori(numeroGiocatori);
    }
    
    public void selezionaTipoGiocatore(int indice, String tipo) {
        TipoGiocatore tipoGiocatore = TipoGiocatore.valueOf(tipo.toUpperCase());
        impostazioniPartita.setTipoGiocatore(indice, tipoGiocatore);
    }
    
    public void inserisciNomeGiocatore(int indice, String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del giocatore non può essere vuoto");
        }
        impostazioniPartita.setNomeGiocatore(indice, nome);
    }
    
    public boolean selezionaColorePedina(int indice, Colore colore) {
        if (verificaColoreUnico(colore)) {
            impostazioniPartita.setColoreGiocatore(indice, colore);
            return true;
        }
        return false;
    }
    
    public boolean verificaColoreUnico(Colore colore) {
        List<Colore> coloriSelezionati = impostazioniPartita.getColoriSelezionati();
        if (coloriSelezionati.contains(colore)) {
            return false;
        }
        impostazioniPartita.aggiungiColoreSelezionato(colore);
        return true;
    }
    
    public String assegnaNomeCPU(int indice) {
        impostazioniPartita.increaseNumeroGiocatoriCPU();
        String nomeCPU = "CPU " + impostazioniPartita.getNumeroGiocatoriCPU();
        impostazioniPartita.setNomeGiocatore(indice, nomeCPU);
        return nomeCPU;
    }
    
    public Colore selezionaColorePedinaCPU(int indice) {
        List<Colore> coloriDisponibili = impostazioniPartita.getColoriDisponibili();
        if (coloriDisponibili.isEmpty()) {
            throw new IllegalStateException("Non ci sono più colori disponibili");
        }
        
        Random random = new Random();
        Colore coloreRandom = coloriDisponibili.get(random.nextInt(coloriDisponibili.size()));
        
        impostazioniPartita.setColoreGiocatore(indice, coloreRandom);
        impostazioniPartita.rimuoviColoreDisponibile(coloreRandom);
        
        return coloreRandom;
    }
    
    public void confermaImpostazioni() {
        inizializzaNuovaPartita();
    }
    
    public void inizializzaNuovaPartita() {
        partitaCorrente = new Partita();

        for (int i = 0; i < impostazioniPartita.getNumeroGiocatori(); i++) {
            String nome = impostazioniPartita.getNomeGiocatore(i);
            TipoGiocatore tipo = impostazioniPartita.getTipoGiocatore(i);
            Colore colore = impostazioniPartita.getColoreGiocatore(i);

            Giocatore giocatore = GiocatoreFactory.creaGiocatore(nome, tipo, colore);
            partitaCorrente.aggiungiGiocatore(giocatore);
        }

        partitaCorrente.setTabellone(new Tabellone());
        partitaCorrente.inizializzaPosizioni();

        partitaCorrente.determinaOrdineGiocatori();

        partitaCorrente.setStato(StatoPartita.IN_CORSO);
    }
    
    public List<Giocatore> determinaOrdineGiocatori() {
        return partitaCorrente.determinaOrdineGiocatori();
    }
    
    public void visualizzaTabellone() {
    }
    
    public Partita getPartitaCorrente() {
        return partitaCorrente;
    }
    
    public void setPartitaCorrente(Partita partita) {
        this.partitaCorrente = partita;
    }
    
    public ImpostazioniPartita getImpostazioniPartita() {
        return impostazioniPartita;
    }
}