package it.digigoose.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Partita {
    private String id;
    private Date dataCreazione;
    private List<Giocatore> giocatori;
    private List<Giocatore> ordineGiocatori;
    private Giocatore giocatoreCorrente;
    private Tabellone tabellone;
    private int turnoCorrente;
    private StatoPartita stato;
    
    public Partita() {
        this.id = java.util.UUID.randomUUID().toString();
        this.dataCreazione = new Date();
        this.giocatori = new ArrayList<>();
        this.ordineGiocatori = new ArrayList<>();
        this.tabellone = new Tabellone();
        this.turnoCorrente = 1;
        this.stato = StatoPartita.CONFIGURAZIONE;
    }
    
    public Partita(List<Giocatore> giocatori) {
        this();
        this.giocatori.addAll(giocatori);
    }
    
    public String getId() {
        return id;
    }
    
    public Date getDataCreazione() {
        return dataCreazione;
    }
    
    public List<Giocatore> getGiocatori() {
        return giocatori;
    }
    
    public void aggiungiGiocatore(Giocatore giocatore) {
        giocatori.add(giocatore);
    }
    
    public List<Giocatore> getOrdineGiocatori() {
        return ordineGiocatori;
    }
    
    public void setOrdineGiocatori(List<Giocatore> ordine) {
        this.ordineGiocatori = ordine;
    }
    
    public Giocatore getGiocatoreCorrente() {
        return giocatoreCorrente;
    }
    
    public void setGiocatoreCorrente(Giocatore giocatore) {
        this.giocatoreCorrente = giocatore;
    }
    
    public Tabellone getTabellone() {
        return tabellone;
    }
    
    public void setTabellone(Tabellone tabellone) {
        this.tabellone = tabellone;
    }
    
    public int getTurnoCorrente() {
        return turnoCorrente;
    }
    
    public void incrementaTurno() {
        turnoCorrente++;
    }
    
    public StatoPartita getStato() {
        return stato;
    }
    
    public void setStato(StatoPartita stato) {
        this.stato = stato;
    }
    
    public List<Giocatore> determinaOrdineGiocatori() {
        // Crea una copia della lista dei giocatori
        List<Giocatore> giocatoriMischiati = new ArrayList<>(giocatori);
        // Mischia l'ordine in modo casuale
        Collections.shuffle(giocatoriMischiati);
        // Imposta l'ordine e il primo giocatore
        setOrdineGiocatori(giocatoriMischiati);
        if (!giocatoriMischiati.isEmpty()) {
            setGiocatoreCorrente(giocatoriMischiati.get(0));
        }
        return giocatoriMischiati;
    }
    
    public void inizializzaPosizioni() {
        for (Giocatore giocatore : giocatori) {
            giocatore.setPosizione(0); // Posizione iniziale
        }
    }
    
    public void passaAlProssimoGiocatore() {
        if (ordineGiocatori.isEmpty()) return;
        
        int indiceCorrente = ordineGiocatori.indexOf(giocatoreCorrente);
        int indiceProssimo = (indiceCorrente + 1) % ordineGiocatori.size();
        
        giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        
        // Se il prossimo giocatore deve saltare il turno, lo aggiorniamo e passiamo al prossimo
        while (giocatoreCorrente.devePassareTurno()) {
            giocatoreCorrente.decrementaTurniSaltati();
            indiceProssimo = (indiceProssimo + 1) % ordineGiocatori.size();
            giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        }
    }
}