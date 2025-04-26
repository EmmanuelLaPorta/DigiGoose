package it.digigoose.view;

import java.util.List;
import java.util.Scanner;

import it.digigoose.controller.GiocoController;
import it.digigoose.model.*;

public class InterfacciaUtente {
    private GiocoController giocoController;
    private Scanner scanner;
    
    public InterfacciaUtente(GiocoController controller) {
        this.giocoController = controller;
        this.scanner = new Scanner(System.in);
    }
    
    public void mostraMenuPrincipale() {
        System.out.println("===== DigiGoose - Il Gioco dell'Oca Digitale =====");
        System.out.println("1. Nuova Partita");
        System.out.println("2. Esci");
        System.out.print("Scegli un'opzione: ");
        
        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline
        
        switch (scelta) {
            case 1:
                avviaNuovaPartita();
                break;
            case 2:
                System.out.println("Grazie per aver giocato a DigiGoose!");
                System.exit(0);
                break;
            default:
                System.out.println("Scelta non valida. Riprova.");
                mostraMenuPrincipale();
                break;
        }
    }
    
    public void avviaNuovaPartita() {
        giocoController.avviaNuovaPartita();
        mostraFormNumeroGiocatori();
    }
    
    public void mostraFormNumeroGiocatori() {
        System.out.println("\n===== Configurazione Partita =====");
        System.out.println("Inserisci il numero di giocatori (2-6): ");
        
        try {
            int numeroGiocatori = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline
            
            giocoController.inserisciNumeroGiocatori(numeroGiocatori);
            
            // Procedi alla configurazione di ogni giocatore
            for (int i = 0; i < numeroGiocatori; i++) {
                mostraOpzioniTipoGiocatore(i);
            }
            
            // Dopo aver configurato tutti i giocatori, conferma le impostazioni
            confermaImpostazioni();
            
        } catch (IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
            mostraFormNumeroGiocatori();
        }
    }
    
    public void mostraOpzioniTipoGiocatore(int indice) {
        System.out.println("\n===== Configurazione Giocatore " + (indice + 1) + " =====");
        System.out.println("Seleziona il tipo di giocatore:");
        System.out.println("1. Umano");
        System.out.println("2. Computer");
        System.out.print("Scelta: ");
        
        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline
        
        switch (scelta) {
            case 1:
                giocoController.selezionaTipoGiocatore(indice, "UMANO");
                mostraFormNomeGiocatore(indice);
                break;
            case 2:
                giocoController.selezionaTipoGiocatore(indice, "COMPUTER");
                String nomeCPU = giocoController.assegnaNomeCPU(indice);
                System.out.println("Nome assegnato: " + nomeCPU);
                Colore coloreCPU = giocoController.selezionaColorePedinaCPU(indice);
                System.out.println("Colore assegnato: " + coloreCPU.toString());
                break;
            default:
                System.out.println("Scelta non valida. Riprova.");
                mostraOpzioniTipoGiocatore(indice);
                break;
        }
    }
    
    public void mostraFormNomeGiocatore(int indice) {
        System.out.print("Inserisci il nome del giocatore: ");
        String nome = scanner.nextLine();
        
        try {
            giocoController.inserisciNomeGiocatore(indice, nome);
            mostraOpzioniColori(indice);
        } catch (IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
            mostraFormNomeGiocatore(indice);
        }
    }
    
    public void mostraOpzioniColori(int indice) {
        System.out.println("\nSeleziona un colore per la pedina:");
        
        List<Colore> coloriDisponibili = giocoController.getImpostazioniPartita().getColoriDisponibili();
        
        for (int i = 0; i < coloriDisponibili.size(); i++) {
            System.out.println((i + 1) + ". " + coloriDisponibili.get(i).toString());
        }
        
        System.out.print("Scelta: ");
        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline
        
        if (scelta >= 1 && scelta <= coloriDisponibili.size()) {
            Colore coloreSelezionato = coloriDisponibili.get(scelta - 1);
            boolean coloreValido = giocoController.selezionaColorePedina(indice, coloreSelezionato);
            
            if (!coloreValido) {
                System.out.println("Errore: Colore già selezionato. Scegli un altro colore.");
                mostraOpzioniColori(indice);
            }
        } else {
            System.out.println("Scelta non valida. Riprova.");
            mostraOpzioniColori(indice);
        }
    }
    
    public void confermaImpostazioni() {
        System.out.println("\n===== Conferma Impostazioni =====");
        System.out.println("Numero di giocatori: " + giocoController.getImpostazioniPartita().getNumeroGiocatori());
        
        for (int i = 0; i < giocoController.getImpostazioniPartita().getNumeroGiocatori(); i++) {
            System.out.println("Giocatore " + (i + 1) + ":");
            System.out.println("  Nome: " + giocoController.getImpostazioniPartita().getNomeGiocatore(i));
            System.out.println("  Tipo: " + giocoController.getImpostazioniPartita().getTipoGiocatore(i));
            System.out.println("  Colore: " + giocoController.getImpostazioniPartita().getColoreGiocatore(i));
        }
        
        System.out.println("\nVuoi confermare queste impostazioni? (S/N)");
        String risposta = scanner.nextLine().toUpperCase();
        
        if (risposta.equals("S")) {
            giocoController.confermaImpostazioni();
            List<Giocatore> ordineGiocatori = giocoController.determinaOrdineGiocatori();
            mostraTabelloneEGiocatori(giocoController.getPartitaCorrente());
        } else {
            System.out.println("Configurazione annullata. Torna al menu principale.");
            mostraMenuPrincipale();
        }
    }
    
    public void mostraTabelloneEGiocatori(Partita partita) {
        System.out.println("\n===== Tabellone di Gioco =====");
        
        // Rappresentazione semplice del tabellone (ASCII art)
        System.out.println("Tabellone del Gioco dell'Oca (63 caselle)");
        System.out.println("----------------------------------------");
        
        // Mostra i giocatori e le loro posizioni
        System.out.println("\n===== Giocatori =====");
        for (Giocatore g : partita.getGiocatori()) {
            System.out.println(g.getNome() + " (" + g.getPedina().getColore() + "): Casella " + g.getPosizione());
        }
        
        // Mostra il turno corrente
        System.out.println("\n===== Turno =====");
        System.out.println("È il turno di: " + partita.getGiocatoreCorrente().getNome());
        
        // Qui implementerai la logica per continuare il gioco
    }
    
    public void mostraMessaggioErrore(String messaggio) {
        System.out.println("ERRORE: " + messaggio);
    }
    
    public void mostraRisultatoDadi(int[] valori) {
        System.out.println("Hai lanciato i dadi: " + valori[0] + " e " + valori[1] + " (totale: " + (valori[0] + valori[1]) + ")");
    }
    
    public void mostraEffettoCasella(TipoEffettoCasella effetto) {
        System.out.println("Effetto della casella: " + effetto.toString());
    }
    
    public void mostraVincitore(Giocatore giocatore) {
        System.out.println("\n===== VINCITORE =====");
        System.out.println("Complimenti " + giocatore.getNome() + "! Hai vinto la partita!");
    }
}