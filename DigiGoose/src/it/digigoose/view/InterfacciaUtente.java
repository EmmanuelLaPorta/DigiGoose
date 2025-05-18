package it.digigoose.view;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import it.digigoose.controller.GiocoController;
import it.digigoose.controller.PartitaController;
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

        String inputLine = scanner.nextLine();

        try {
            int scelta = Integer.parseInt(inputLine);

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
        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero intero.");
            mostraMenuPrincipale();
        }
    }

    public void avviaNuovaPartita() {
        giocoController.avviaNuovaPartita();
        try {
             mostraFormNumeroGiocatori();
        } catch (RuntimeException configEx) {
             System.out.println("\nConfigurazione partita fallita: " + configEx.getMessage());
             System.out.println("Torno al menu principale.");
             mostraMenuPrincipale();
        }
    }

    public void mostraFormNumeroGiocatori() {
        System.out.println("\n===== Configurazione Partita =====");
        System.out.println("Inserisci il numero di giocatori (2-6): ");
        String inputLine = scanner.nextLine();

        try {
            int numeroGiocatori = Integer.parseInt(inputLine);

            try {
                giocoController.inserisciNumeroGiocatori(numeroGiocatori);

                for (int i = 0; i < numeroGiocatori; i++) {
                     mostraOpzioniTipoGiocatore(i);
                }

                confermaImpostazioni();

            } catch (IllegalArgumentException e) {
                System.out.println("Errore: " + e.getMessage());
                mostraFormNumeroGiocatori();
            }

        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero intero.");
            mostraFormNumeroGiocatori();
        }
    }

    public void mostraOpzioniTipoGiocatore(int indice) {
        System.out.println("\n===== Configurazione Giocatore " + (indice + 1) + " =====");
        System.out.println("Seleziona il tipo di giocatore:");
        System.out.println("1. Umano");
        System.out.println("2. Computer");
        System.out.print("Scelta: ");

        String inputLine = scanner.nextLine();

        try {
            int scelta = Integer.parseInt(inputLine);

            switch (scelta) {
                case 1:
                    try {
                        giocoController.selezionaTipoGiocatore(indice, "UMANO");
                        mostraFormNomeGiocatore(indice);
                    } catch (RuntimeException ex) {
                         System.out.println("Errore durante configurazione giocatore umano: " + ex.getMessage());
                         throw ex;
                    }
                    break;
                case 2:
                     try {
                        giocoController.selezionaTipoGiocatore(indice, "COMPUTER");
                        String nomeCPU = giocoController.assegnaNomeCPU(indice);
                        System.out.println("Nome assegnato: " + nomeCPU);
                        Colore coloreCPU = giocoController.selezionaColorePedinaCPU(indice);
                        System.out.println("Colore assegnato: " + coloreCPU.toString());
                     } catch (RuntimeException ex) {
                         System.out.println("Errore durante configurazione giocatore computer: " + ex.getMessage());
                         throw ex;
                     }
                    break;
                default:
                    System.out.println("Scelta non valida. Riprova.");
                    mostraOpzioniTipoGiocatore(indice);
                    break;
            }

        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero intero.");
            mostraOpzioniTipoGiocatore(indice);
        }
    }

    public void mostraFormNomeGiocatore(int indice) {
        System.out.print("Inserisci il nome del giocatore: ");
        String nome = scanner.nextLine();

        try {
            giocoController.inserisciNomeGiocatore(indice, nome);
             try {
                mostraOpzioniColori(indice);
             } catch (RuntimeException ex) {
                 System.out.println("Errore durante selezione colore: " + ex.getMessage());
                 throw ex;
             }
        } catch (IllegalArgumentException e) {
            System.out.println("Errore: " + e.getMessage());
            mostraFormNomeGiocatore(indice);
        }
    }

    public void mostraOpzioniColori(int indice) {
        System.out.println("\nSeleziona un colore per la pedina:");

        List<Colore> coloriDisponibili = giocoController.getImpostazioniPartita().getColoriDisponibili();

        if (coloriDisponibili.isEmpty()) {
             throw new IllegalStateException("Nessun colore disponibile per il giocatore " + (indice + 1));
        }

        for (int i = 0; i < coloriDisponibili.size(); i++) {
            System.out.println((i + 1) + ". " + coloriDisponibili.get(i).toString());
        }

        System.out.print("Scelta: ");
        String inputLine = scanner.nextLine();

        try {
            int scelta = Integer.parseInt(inputLine);

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

        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero intero.");
            mostraOpzioniColori(indice);
        }
    }


    public void confermaImpostazioni() {
        System.out.println("\n===== Conferma Impostazioni =====");
        System.out.println("Numero di giocatori: " + giocoController.getImpostazioniPartita().getNumeroGiocatori());

        int numConfigured = giocoController.getImpostazioniPartita().getNumeroGiocatori();
         if (numConfigured == 0) {
             System.out.println("Nessun giocatore configurato.");
         } else {
            for (int i = 0; i < numConfigured; i++) {
                String nome = giocoController.getImpostazioniPartita().getNomeGiocatore(i);
                TipoGiocatore tipo = giocoController.getImpostazioniPartita().getTipoGiocatore(i);
                Colore colore = giocoController.getImpostazioniPartita().getColoreGiocatore(i);

                 System.out.println("Giocatore " + (i + 1) + ":");
                 System.out.println("  Nome: " + (nome != null ? nome : "N/D"));
                 System.out.println("  Tipo: " + (tipo != null ? tipo : "N/D"));
                 System.out.println("  Colore: " + (colore != null ? colore : "N/D"));
            }
         }


        System.out.println("\nVuoi confermare queste impostazioni? (S/N)");
        String risposta = scanner.nextLine().toUpperCase();

        if (risposta.equals("S")) {
            try {
                giocoController.confermaImpostazioni();
                System.out.println("Impostazioni confermate. Avvio partita...");
                mostraTabelloneEGiocatori(giocoController.getPartitaCorrente());

            } catch (Exception gameStartEx) {
                 System.out.println("Errore durante l'avvio della partita: " + gameStartEx.getMessage());
                 System.out.println("Torno al menu principale.");
                 mostraMenuPrincipale();
            }

        } else {
            System.out.println("Configurazione annullata. Torna al menu principale.");
            mostraMenuPrincipale();
        }
    }

    public void mostraTabelloneEGiocatori(Partita partita) {
        while (partita.getStato() == StatoPartita.IN_CORSO) {
            System.out.println("\n===== Tabellone di Gioco =====");

            System.out.println("Tabellone del Gioco dell'Oca (63 caselle)");
            System.out.println("----------------------------------------");

            System.out.println("\n===== Giocatori =====");
            for (Giocatore g : partita.getOrdineGiocatori()) {
                 String status = "";
                 if (g.getTurniSaltati() > 0) {
                     status = " (fermo per " + g.getTurniSaltati() + " turni)";
                 } else if (g.getTurniSaltati() == -1) {
                      status = " (in attesa di un 6)";
                 } else if (g.getTurniSaltati() == -2) {
                      status = " (in prigione, attende un 6)";
                 } else if (g.getTurniSaltati() == -3) {
                      status = " (attende un 3 o un 6)";
                 } else if (g.getTurniSaltati() == -4) {
                      status = " (attende un 4 o un 5)";
                 }
                System.out.println(g.getNome() + " (" + g.getPedina().getColore() + "): Casella " + g.getPosizione() + status);
            }

            System.out.println("\n===== Giro " + partita.getGiroCorrente() + ", Turno " + partita.getTurnoCorrente() + " =====");
            Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
            System.out.println("È il turno di: " + giocatoreCorrente.getNome());

            // --- Check if the current player is blocked for a fixed number of turns (>0) ---
            if (giocatoreCorrente.getTurniSaltati() > 0) {
                 System.out.println(giocatoreCorrente.getNome() + " deve saltare questo turno. Rimangono " + giocatoreCorrente.getTurniSaltati() + " turni da saltare.");
                 giocatoreCorrente.decrementaTurniSaltati(); // Decrement the count as the turn is skipped
                 // Pass the turn immediately if blocked for > 0 turns
                 partita.passaAlProssimoGiocatore();
                 continue; // Skip the rest of the loop and start the next turn
            }

            // --- Player is either free (==0) or blocked by condition (<0) ---
            // In both cases, they attempt to take their turn (roll dice)
            if (giocatoreCorrente.getTurniSaltati() < 0) {
                 // Print specific blocking message before the turn attempt
                 if (giocatoreCorrente.getTurniSaltati() == -1) {
                     System.out.println(giocatoreCorrente.getNome() + " è bloccato in attesa di un 6 lanciato da qualsiasi giocatore.");
                 } else if (giocatoreCorrente.getTurniSaltati() == -2) {
                     System.out.println(giocatoreCorrente.getNome() + " è bloccato in prigione. Deve lanciare un 6.");
                 } else if (giocatoreCorrente.getTurniSaltati() == -3) {
                      System.out.println(giocatoreCorrente.getNome() + " è bloccato. Deve lanciare un 3 o un 6.");
                 } else if (giocatoreCorrente.getTurniSaltati() == -4) {
                      System.out.println(giocatoreCorrente.getNome() + " è bloccato. Deve lanciare un 4 o un 5.");
                 }
            }

            // --- Handle the turn (roll dice, move, apply effects) ---
            if (giocatoreCorrente.getTipo() == TipoGiocatore.UMANO) {
                gestisciTurnoUmano(partita, giocatoreCorrente);
            } else {
                gestisciTurnoComputer(partita, giocatoreCorrente);
            }

            // --- After the turn handler finishes ---

            // Check for winner after the player's move and effect application
            PartitaController partitaController = new PartitaController(partita);
            if (partita.getStato() == StatoPartita.TERMINATA || partitaController.verificaVincitore(giocatoreCorrente)) {
                 // Partita is over (either set by casella effect or reaching 63)
                mostraVincitore(giocatoreCorrente);
                partita.setStato(StatoPartita.TERMINATA); // Ensure state is TERMINATA
                // The loop condition will now be false, and the loop will exit.
            } else {
                // If game is not over, pass to the next player *ONLY IF* the current player is NOT still blocked (<0)
                if (giocatoreCorrente.getTurniSaltati() >= 0) {
                    partita.passaAlProssimoGiocatore();
                } else {
                    // If the player is still blocked (<0), they remain the current player.
                    System.out.println(giocatoreCorrente.getNome() + " rimane bloccato e ritenterà nel prossimo turno.");
                }
            }
        }

        // Partita terminata, torna al menu principale
        System.out.println("\nPremi INVIO per tornare al menu principale...");
        scanner.nextLine(); // Consume newline potentially left by previous input
        scanner.nextLine(); // Wait for the user to press Enter
        mostraMenuPrincipale(); // Return to main menu
    }


    private void gestisciTurnoUmano(Partita partita, Giocatore giocatore) {
        PartitaController partitaController = new PartitaController(partita);

        System.out.println("\nOpzioni per " + giocatore.getNome() + ":");
        System.out.println("1. Lancia i dadi");
        System.out.println("2. Esci al menu principale (salvataggio non disponibile)");
        System.out.print("Scegli un'opzione: ");

        String inputLine = scanner.nextLine();

        try {
            int scelta = Integer.parseInt(inputLine);

            switch (scelta) {
                case 1:
                    System.out.println("Lancio i dadi...");
                    int[] risultatoDadi = partitaController.tiraDadi();
                    mostraRisultatoDadi(risultatoDadi);

                    // Check immediately if this roll unblocks the player (or any other player)
                    partitaController.verificaELiberaGiocatoriBloccati(risultatoDadi);

                    // If the player is *still* blocked after this roll, their turn ends here.
                    if (giocatore.getTurniSaltati() < 0) {
                         // Message about still being blocked is handled after the turn handler returns in mostraTabelloneEGiocatori
                         // Exit this method, turn is over for this player (no move, no effects unless unblocked)
                         return;
                    }
                    // If getTurniSaltati() >= 0, they were either never blocked or are now freed. Proceed with move.


                    // Proceed with movement and effects (only if not blocked <0)
                    Dadi dadi = new Dadi(6, 2);
                    dadi.setValori(risultatoDadi);
                    int passi = dadi.getSomma();

                    Casella casellaDestinazione = partitaController.muoviPedina(giocatore, passi);
                    System.out.println(giocatore.getNome() + " si sposta alla casella " + giocatore.getPosizione());

                    if (casellaDestinazione != null && casellaDestinazione.isSpeciale()) {
                         partitaController.applicaEffettoCasellaEsteso(casellaDestinazione, giocatore, risultatoDadi);
                         mostraEffettoCasella(casellaDestinazione.getTipoEffetto());
                         System.out.println("Nuova posizione: " + giocatore.getPosizione());
                    }
                    // Turn is now finished, mostraTabelloneEGiocatori will decide if it's the next player's turn.
                    break;

                case 2:
                    System.out.println("Sei sicuro di voler uscire? La partita in corso andrà persa (S/N)");
                    String risposta = scanner.nextLine().toUpperCase();
                    if (risposta.equals("S")) {
                        partita.setStato(StatoPartita.TERMINATA); // Exit game loop
                        // Returning from this method allows mostraTabelloneEGiocatori to check state and exit.
                    } else {
                         System.out.println("Uscita annullata.");
                         gestisciTurnoUmano(partita, giocatore); // Re-prompt the turn menu
                    }
                    break;

                default:
                    System.out.println("Scelta non valida. Riprova.");
                    gestisciTurnoUmano(partita, giocatore); // Recursive call
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido. Inserisci un numero intero.");
            gestisciTurnoUmano(partita, giocatore); // Recursive call
        } catch (Exception gameTurnEx) {
            System.out.println("Si è verificato un errore durante il tuo turno: " + gameTurnEx.getMessage());
             // For simplicity, print error and let game loop continue.
        }
    }

    private void gestisciTurnoComputer(Partita partita, Giocatore giocatore) {
        PartitaController partitaController = new PartitaController(partita);

        System.out.println("\nÈ il turno del computer: " + giocatore.getNome());
        System.out.println("Premi INVIO per far lanciare i dadi al computer...");
        scanner.nextLine();

        int[] risultatoDadi = partitaController.tiraDadi();
        mostraRisultatoDadi(risultatoDadi);

        // Check immediately if this roll unblocks the player (or any other player)
        partitaController.verificaELiberaGiocatoriBloccati(risultatoDadi);

         // If the CPU player is still blocked after this roll, their turn ends here.
        if (giocatore.getTurniSaltati() < 0) {
            // Message about still being blocked is handled after the turn handler returns in mostraTabelloneEGiocatori
            // Exit this method, turn is over for this player (no move, no effects unless unblocked)
            return;
        }
        // If getTurniSaltati() >= 0, they were either never blocked or are now freed. Proceed with move.


        // Proceed with movement and effects (only if not blocked <0)
        Dadi dadi = new Dadi(6, 2);
        dadi.setValori(risultatoDadi);
        int passi = dadi.getSomma();

        Casella casellaDestinazione = partitaController.muoviPedina(giocatore, passi);
        System.out.println(giocatore.getNome() + " si sposta alla casella " + giocatore.getPosizione());

        if (casellaDestinazione != null && casellaDestinazione.isSpeciale()) {
            partitaController.applicaEffettoCasellaEsteso(casellaDestinazione, giocatore, risultatoDadi);
            mostraEffettoCasella(casellaDestinazione.getTipoEffetto());
            System.out.println("Nuova posizione: " + giocatore.getPosizione());
        }

    }

    public void mostraMessaggioErrore(String messaggio) {
        System.out.println("ERRORE: " + messaggio);
    }

    public void mostraRisultatoDadi(int[] valori) {
        if (valori != null && valori.length == 2) {
            System.out.println("Hai lanciato i dadi: " + valori[0] + " e " + valori[1] + " (totale: " + (valori[0] + valori[1]) + ")");
        } else {
            System.out.println("Risultato dadi non disponibile.");
        }
    }

    public void mostraEffettoCasella(TipoEffettoCasella effetto) {
        if (effetto != null && effetto != TipoEffettoCasella.NESSUNO) {
             System.out.println("Effetto della casella: " + effetto.toString().replace("_", " "));
        }
    }

    public void mostraVincitore(Giocatore giocatore) {
        System.out.println("\n===== VINCITORE =====");
        System.out.println("Complimenti " + giocatore.getNome() + "! Hai vinto la partita!");
        System.out.println("=====================");
    }

     public void closeScanner() {
         if (scanner != null) {
             scanner.close();
         }
     }
}