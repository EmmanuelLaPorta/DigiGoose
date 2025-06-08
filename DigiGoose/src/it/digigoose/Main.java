package it.digigoose;

import javax.swing.SwingUtilities;
import it.digigoose.controller.GiocoController;

public class Main {
    public static void main(String[] args) {
        System.out.println("DigiGoose - il gioco dell'oca digitale");
        
        GiocoController giocoController = new GiocoController();
        
        SwingUtilities.invokeLater(() -> {
            it.digigoose.view.InterfacciaUtente ui = new it.digigoose.view.InterfacciaUtente(giocoController);
            ui.creaEmostraGUI(); 
        });
    }
}