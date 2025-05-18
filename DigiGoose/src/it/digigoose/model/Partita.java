package it.digigoose.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private int indicePrimoGiocatoreGiroAttuale = 0; // Indice del primo giocatore nel giro corrente

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
    
    public int getGiroCorrente() {
        return giroCorrente;
    }

    public void incrementaGiro() {
        giroCorrente++;
        // Resetta il turno quando inizia un nuovo giro
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
        
        // Se stiamo tornando al primo giocatore, incrementa il giro
        if (indiceProssimo == 0) {
            incrementaGiro();
        } else {
            // Altrimenti, incrementa il turno all'interno del giro corrente
            incrementaTurno();
        }
        
        giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        
        // Se il prossimo giocatore deve saltare il turno, lo aggiorniamo e passiamo al prossimo
        while (giocatoreCorrente.devePassareTurno()) {
            // Gestione speciale per ATTENDI_DADO e PRIGIONE
            if (giocatoreCorrente.getTurniSaltati() < 0) {
                // Non facciamo nulla, questo giocatore dovrà rimanere fermo
                // finché la condizione non verrà sbloccata tramite un altro metodo
            } else {
                // Comportamento normale: decrementa i turni saltati
                giocatoreCorrente.decrementaTurniSaltati();
            }
            
            indiceProssimo = (indiceProssimo + 1) % ordineGiocatori.size();
            
            // Se stiamo tornando al primo giocatore dopo aver gestito un salto di turno
            if (indiceProssimo == 0) {
                incrementaGiro();
            }
            
            giocatoreCorrente = ordineGiocatori.get(indiceProssimo);
        }
    }
}