package it.digigoose.model;
import java.util.Random;

/**
 * rappresenta un set di dadi utilizzati nel gioco.
 * permette di lanciare i dadi e ottenere i valori risultanti
 */


public class Dadi {
    private int numeroFacce;
    private int[] valori;
    
    //costruttore
    public Dadi(int numeroFacce, int numeroDadi) {
        this.numeroFacce = numeroFacce;
        this.valori = new int[numeroDadi];
    }
    
    //lancia un set di dadi, ritorna un array con i risultati di ogni dado
    public int[] lancia() {
        Random random = new Random();
        for (int i = 0; i < valori.length; i++) {
            valori[i] = random.nextInt(numeroFacce) + 1;
        }
        return valori;
    }
    
    //somma dei dadi per calcolo spostamento pedina
    public int getSomma() {
        int somma = 0;
        for (int valore : valori) {
            somma += valore;
        }
        return somma;
    }
    
    public int[] getValori() {
        return valori;
    }
    
    public void setValori(int[] valori) {
        if (valori.length == this.valori.length) {
            this.valori = valori;
        } else {
            throw new IllegalArgumentException("Il numero di valori deve essere uguale a " + this.valori.length);
        }
    }
}