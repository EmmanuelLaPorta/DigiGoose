package it.digigoose.model;

public class Giocatore {
    private String id;
    private String nome;
    private TipoGiocatore tipo;
    private Pedina pedina;
    private int turniSaltati;
    private boolean richiedeRelancio;
    
    public Giocatore(String nome, TipoGiocatore tipo, Colore colore) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.tipo = tipo;
        this.pedina = new Pedina(colore);
        this.turniSaltati = 0;
        this.richiedeRelancio = false;
    }
    
    public String getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public TipoGiocatore getTipo() {
        return tipo;
    }
    
    public Pedina getPedina() {
        return pedina;
    }
    
    public void setPedina(Pedina pedina) {
        this.pedina = pedina;
    }
    
    public int getPosizione() {
        return pedina.getPosizione();
    }
    
    public void setPosizione(int posizione) {
        pedina.setPosizione(posizione);
    }
    
    public int getTurniSaltati() {
        return turniSaltati;
    }
    
    public void setTurniSaltati(int turni) {
        this.turniSaltati = turni;
    }
    
    public boolean getRichiedeRelancio() {
        return richiedeRelancio;
    }
    
    public void setRichiedeRelancio(boolean relancio) {
        this.richiedeRelancio = relancio;
    }
    
    public boolean devePassareTurno() {
        return turniSaltati > 0;
    }
    
    public void decrementaTurniSaltati() {
        if (turniSaltati > 0) {
            turniSaltati--;
        }
    }
}