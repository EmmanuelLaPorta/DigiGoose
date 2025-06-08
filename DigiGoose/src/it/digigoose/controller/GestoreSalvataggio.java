package it.digigoose.controller;

import it.digigoose.model.Colore;
import it.digigoose.model.Giocatore;
import it.digigoose.model.Partita;
import it.digigoose.model.StatoPartita;
import it.digigoose.model.TipoGiocatore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * gestione del salvataggio e il caricamento dello stato di una partita su file JSON.
 * sono presenti i metodi per salvare, caricare ed eliminare un salvataggio
 */


public class GestoreSalvataggio {
    private static final String SAVE_FILE_NAME = "digigoose_save.json";
    private static final String DIRECTORY_SALVATAGGI = "save/"; 

    public GestoreSalvataggio() {
        File dir = new File(DIRECTORY_SALVATAGGI);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getSaveFilePath() {
        return DIRECTORY_SALVATAGGI + SAVE_FILE_NAME;
    }

    public boolean esisteSalvataggio() {
        return new File(getSaveFilePath()).exists();
    }

    
    //salvataggio
    public void salvaPartita(Partita partita) throws IOException {
        JSONObject partitaJson = new JSONObject();
        partitaJson.put("id", partita.getId());
        partitaJson.put("dataCreazione", partita.getDataCreazione().getTime());
        partitaJson.put("turnoCorrente", partita.getTurnoCorrente());
        partitaJson.put("stato", partita.getStato().toString());
        partitaJson.put("giroCorrente", partita.getGiroCorrente());

        JSONArray giocatoriJson = new JSONArray();
        Map<String, Giocatore> giocatoriMap = new HashMap<>(); 
        for (Giocatore g : partita.getGiocatori()) {
            giocatoriMap.put(g.getId(), g);
            JSONObject giocatoreJson = new JSONObject();
            giocatoreJson.put("id", g.getId());
            giocatoreJson.put("nome", g.getNome());
            giocatoreJson.put("tipo", g.getTipo().toString());
            giocatoreJson.put("turniSaltati", g.getTurniSaltati());
            giocatoreJson.put("richiedeRelancio", g.getRichiedeRelancio());

            JSONObject pedinaJson = new JSONObject();
            pedinaJson.put("colore", g.getPedina().getColore().toString());
            pedinaJson.put("posizione", g.getPedina().getPosizione());
            giocatoreJson.put("pedina", pedinaJson);

            giocatoriJson.put(giocatoreJson);
        }
        partitaJson.put("giocatori", giocatoriJson);

        JSONArray ordineGiocatoriIds = new JSONArray();
        for (Giocatore g : partita.getOrdineGiocatori()) {
            ordineGiocatoriIds.put(g.getId());
        }
        partitaJson.put("ordineGiocatoriIds", ordineGiocatoriIds);

        if (partita.getGiocatoreCorrente() != null) {
            partitaJson.put("giocatoreCorrenteId", partita.getGiocatoreCorrente().getId());
        }

        try (FileWriter file = new FileWriter(getSaveFilePath())) {
            file.write(partitaJson.toString(4)); 
            file.flush();
        }
    }

    //carica partita
    public Partita caricaPartita() throws IOException {
        try (FileReader reader = new FileReader(getSaveFilePath())) {
            JSONObject partitaJson = new JSONObject(new org.json.JSONTokener(reader));

            Partita partita = new Partita(); 

            partita.setId(partitaJson.getString("id")); 
            partita.setDataCreazione(new Date(partitaJson.getLong("dataCreazione")));
            partita.setTurnoCorrente(partitaJson.getInt("turnoCorrente"));
            partita.setStato(StatoPartita.valueOf(partitaJson.getString("stato")));
            partita.setGiroCorrente(partitaJson.getInt("giroCorrente"));

            JSONArray giocatoriJson = partitaJson.getJSONArray("giocatori");
            List<Giocatore> giocatoriCaricati = new ArrayList<>();
            Map<String, Giocatore> giocatoriMapById = new HashMap<>();

            for (int i = 0; i < giocatoriJson.length(); i++) {
                JSONObject giocatoreJson = giocatoriJson.getJSONObject(i);
                JSONObject pedinaJson = giocatoreJson.getJSONObject("pedina");

                Giocatore giocatore = new Giocatore(
                        giocatoreJson.getString("nome"),
                        TipoGiocatore.valueOf(giocatoreJson.getString("tipo")),
                        Colore.valueOf(pedinaJson.getString("colore"))
                );
       
                String jsonPlayerId = giocatoreJson.getString("id");


                giocatore.getPedina().setPosizione(pedinaJson.getInt("posizione"));
                giocatore.setTurniSaltati(giocatoreJson.getInt("turniSaltati"));
                giocatore.setRichiedeRelancio(giocatoreJson.getBoolean("richiedeRelancio"));
                
                giocatoriCaricati.add(giocatore);
                giocatoriMapById.put(jsonPlayerId, giocatore); 
            }
            partita.getGiocatori().clear(); 
            partita.getGiocatori().addAll(giocatoriCaricati);


            JSONArray ordineGiocatoriIdsJson = partitaJson.getJSONArray("ordineGiocatoriIds");
            List<Giocatore> ordineGiocatoriCaricato = new ArrayList<>();
            for (int i = 0; i < ordineGiocatoriIdsJson.length(); i++) {
                String giocatoreId = ordineGiocatoriIdsJson.getString(i);
                if (giocatoriMapById.containsKey(giocatoreId)) {
                    ordineGiocatoriCaricato.add(giocatoriMapById.get(giocatoreId));
                }
            }
            partita.setOrdineGiocatori(ordineGiocatoriCaricato);

            if (partitaJson.has("giocatoreCorrenteId")) {
                String giocatoreCorrenteId = partitaJson.getString("giocatoreCorrenteId");
                if (giocatoriMapById.containsKey(giocatoreCorrenteId)) {
                    partita.setGiocatoreCorrente(giocatoriMapById.get(giocatoreCorrenteId));
                } else if (!ordineGiocatoriCaricato.isEmpty()){
                    partita.setGiocatoreCorrente(ordineGiocatoriCaricato.get(0)); 
                }
            } else if (!ordineGiocatoriCaricato.isEmpty()){
                 partita.setGiocatoreCorrente(ordineGiocatoriCaricato.get(0)); 
            }
            
            return partita;
        }
    }

    public void eliminaSalvataggio() {
        File saveFile = new File(getSaveFilePath());
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
}