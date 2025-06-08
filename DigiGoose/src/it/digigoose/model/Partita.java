package it.digigoose.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Rappresenta lo stato completo di una partita del gioco.
 * Include informazioni sui giocatori, l'ordine di gioco, il giocatore corrente,
 * il tabellone, il turno e lo stato generale della partita.
 */



public class Partita implements Serializable {
    private String id;
    private Date dataCreazione;
    private List<Giocatore> giocatori;
    private List<Giocatore> ordineGiocatori;
    private Giocatore giocatoreCorrente;
    private Tabellone tabellone;
    private int turnoCorrente;
    private StatoPartita stato;
    private int giroCorrente = 1;
    private int indicePrimoGiocatoreGiroAttuale = 0; 

    private static final long serialVersionUID = 1L;
    
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

    public void setId(String id) {
        this.id = id;
    }
    
    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
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
        this.ordineGiocatori = new ArrayList<>(ordine); 
    }
    
    public int getGiroCorrente() {
        return giroCorrente;
    }

    public void setGiroCorrente(int giroCorrente) { 
        this.giroCorrente = giroCorrente;
    }

    public void incrementaGiro() {
        giroCorrente++;
        turnoCorrente = 1;
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

    public void setTurnoCorrente(int turnoCorrente) { 
        this.turnoCorrente = turnoCorrente;
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
        List<Giocatore> giocatoriMischiati = new ArrayList<>(giocatori);
        Collections.shuffle(giocatoriMischiati);
        setOrdineGiocatori(giocatoriMischiati);
        if (!giocatoriMischiati.isEmpty()) {
            setGiocatoreCorrente(giocatoriMischiati.get(0));
        }
        return giocatoriMischiati;
    }
    
    public void inizializzaPosizioni() {
        for (Giocatore giocatore : giocatori) {
            giocatore.setPosizione(0); 
        }
    }
    
    public void passaAlProssimoGiocatore() {
        if (ordineGiocatori.isEmpty()) return;
        
        int indiceCorrente = ordineGiocatori.indexOf(giocatoreCorrente);
        int indiceProssimo = (indiceCorrente + 1) % ordineGiocatori.size();
        
        if (indiceProssimo == 0) { 
             if (indiceCorrente == ordineGiocatori.size() -1 ) { 
                incrementaGiro();
             } else { 
                incrementaTurno();
             }
        } else {
            incrementaTurno();
        }
        
        giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        
        while (giocatoreCorrente.devePassareTurno()) {
            if (giocatoreCorrente.getTurniSaltati() < 0) {
                break; 
            } else {
                giocatoreCorrente.decrementaTurniSaltati();
            }
            
            indiceProssimo = (ordineGiocatori.indexOf(giocatoreCorrente) + 1) % ordineGiocatori.size();
            if (indiceProssimo == 0) {
                 if (ordineGiocatori.indexOf(giocatoreCorrente) == ordineGiocatori.size() -1 ) {
                    incrementaGiro();
                 } else {
                 }
            }

            giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        }
    }
}