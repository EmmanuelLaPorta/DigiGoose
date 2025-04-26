package it.digigoose.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImpostazioniPartita {
    private int numeroGiocatori;
    private List<String> nomiGiocatori;
    private List<TipoGiocatore> tipiGiocatori;
    private List<Colore> coloriSelezionati;
    private int numeroGiocatoriCPU;
    
    public ImpostazioniPartita() {
        this.numeroGiocatori = 0;
        this.nomiGiocatori = new ArrayList<>();
        this.tipiGiocatori = new ArrayList<>();
        this.coloriSelezionati = new ArrayList<>();
        this.numeroGiocatoriCPU = 0;
    }
    
    public int getNumeroGiocatori() {
        return numeroGiocatori;
    }
    
    public void setNumeroGiocatori(int n) {
        this.numeroGiocatori = n;
        // Inizializza le liste alla dimensione corretta
        for (int i = nomiGiocatori.size(); i < n; i++) {
            nomiGiocatori.add("");
            tipiGiocatori.add(null);
            coloriSelezionati.add(null);
        }
    }
    
    public String getNomeGiocatore(int indice) {
        if (indice >= 0 && indice < nomiGiocatori.size()) {
            return nomiGiocatori.get(indice);
        }
        return "";
    }
    
    public void setNomeGiocatore(int indice, String nome) {
        if (indice >= 0 && indice < nomiGiocatori.size()) {
            nomiGiocatori.set(indice, nome);
        } else if (indice == nomiGiocatori.size()) {
            nomiGiocatori.add(nome);
        }
    }
    
    public TipoGiocatore getTipoGiocatore(int indice) {
        if (indice >= 0 && indice < tipiGiocatori.size()) {
            return tipiGiocatori.get(indice);
        }
        return null;
    }
    
    public void setTipoGiocatore(int indice, TipoGiocatore tipo) {
        if (indice >= 0 && indice < tipiGiocatori.size()) {
            tipiGiocatori.set(indice, tipo);
        } else if (indice == tipiGiocatori.size()) {
            tipiGiocatori.add(tipo);
        }
    }
    
    public Colore getColoreGiocatore(int indice) {
        if (indice >= 0 && indice < coloriSelezionati.size()) {
            return coloriSelezionati.get(indice);
        }
        return null;
    }
    
    public void setColoreGiocatore(int indice, Colore colore) {
        if (indice >= 0 && indice < coloriSelezionati.size()) {
            coloriSelezionati.set(indice, colore);
        } else if (indice == coloriSelezionati.size()) {
            coloriSelezionati.add(colore);
        }
    }
    
    public int getNumeroGiocatoriCPU() {
        return numeroGiocatoriCPU;
    }
    
    public void increaseNumeroGiocatoriCPU() {
        this.numeroGiocatoriCPU++;
    }
    
    public List<Colore> getColoriSelezionati() {
        return coloriSelezionati;
    }
    
    public void aggiungiColoreSelezionato(Colore colore) {
        if (!coloriSelezionati.contains(colore)) {
            coloriSelezionati.add(colore);
        }
    }
    
    public List<Colore> getColoriDisponibili() {
        List<Colore> disponibili = new ArrayList<>(Arrays.asList(Colore.values()));
        disponibili.removeAll(coloriSelezionati);
        return disponibili;
    }
    
    public void rimuoviColoreDisponibile(Colore colore) {
        aggiungiColoreSelezionato(colore);
    }
}