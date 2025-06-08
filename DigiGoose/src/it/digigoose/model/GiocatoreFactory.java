package it.digigoose.model;

/**
 * Factory class per la creazione di oggetti Giocatore.
 */


public class GiocatoreFactory {
    
    public static Giocatore creaGiocatore(String nome, TipoGiocatore tipo, Colore colore) {
        return new Giocatore(nome, tipo, colore);
    }
    
    public static Giocatore creaGiocatoreUmano(String nome, Colore colore) {
        return creaGiocatore(nome, TipoGiocatore.UMANO, colore);
    }
    
    public static Giocatore creaGiocatoreCPU(String nome, Colore colore) {
        return creaGiocatore(nome, TipoGiocatore.COMPUTER, colore);
    }
}