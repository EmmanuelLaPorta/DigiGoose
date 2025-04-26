package it.digigoose;

import it.digigoose.controller.GiocoController;
import it.digigoose.view.InterfacciaUtente;

public class Main {
    public static void main(String[] args) {
        System.out.println("DigiGoose - Gioco dell'Oca Digitale");
        
        // Inizializza il controller
        GiocoController giocoController = new GiocoController();
        
        // Inizializza l'interfaccia utente
        InterfacciaUtente ui = new InterfacciaUtente(giocoController);
        
        // Avvia l'applicazione
        ui.mostraMenuPrincipale();
    }
}