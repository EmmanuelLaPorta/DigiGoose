// package it.digigoose.controller;

// import java.io.*;
// import it.digigoose.model.Partita;

// public class GestoreSalvataggio {
//     private static final String DIRECTORY_SALVATAGGI = "salvataggi/";
    
//     public GestoreSalvataggio() {
//         // Assicurati che la directory dei salvataggi esista
//         File dir = new File(DIRECTORY_SALVATAGGI);
//         if (!dir.exists()) {
//             dir.mkdirs();
//         }
//     }
    
//     public void salvaPartita(Partita partita) throws IOException {
//         String filePath = DIRECTORY_SALVATAGGI + "partita_" + partita.getId() + ".ser";
//         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
//             oos.writeObject(partita);
//         }
//     }
    
//     public Partita caricaPartita(String id) throws IOException, ClassNotFoundException {
//         String filePath = DIRECTORY_SALVATAGGI + "partita_" + id + ".ser";
//         try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
//             return (Partita) ois.readObject();
//         }
//     }
    
//     public String[] getListaSalvataggi() {
//         File dir = new File(DIRECTORY_SALVATAGGI);
//         return dir.list((d, name) -> name.endsWith(".ser"));
//     }
// }