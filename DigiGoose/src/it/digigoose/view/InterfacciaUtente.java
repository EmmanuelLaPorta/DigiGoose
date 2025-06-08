package it.digigoose.view;

import it.digigoose.controller.GiocoController;
import it.digigoose.controller.PartitaController;
import it.digigoose.controller.GestoreSalvataggio; 
import it.digigoose.model.Casella;
import it.digigoose.model.Colore;
import it.digigoose.model.Dadi;
import it.digigoose.model.Giocatore;
import it.digigoose.model.Partita;
import it.digigoose.model.StatoPartita;
import it.digigoose.model.TipoEffettoCasella;
import it.digigoose.model.TipoGiocatore;
import it.digigoose.model.ImpostazioniPartita;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InterfacciaUtente {
    private GiocoController giocoController;
    private PartitaController partitaController;
    private GestoreSalvataggio gestoreSalvataggio; 

    private JFrame mainFrame;
    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    private JPanel menuPanel;
    private JButton loadGameButton; 
    private JPanel settingsPanel;
    private JPanel gamePlayPanel;

    private JSpinner numPlayersSpinner;
    private List<PlayerConfigPanel> playerConfigPanels;

    private GameBoardPanel gameBoardPanel;
    private JTextArea messageArea;
    private JButton rollDiceButton;
    private JLabel currentPlayerLabel;
    private JLabel currentTurnLabel;

    private static final String MENU_PANEL = "MenuPanel";
    private static final String SETTINGS_PANEL = "SettingsPanel";
    private static final String GAME_PANEL = "GamePanel";

    private int[] currentDiceRoll;
    private Casella landingSquare;
    private int stepsToMove;


    public InterfacciaUtente(GiocoController controller) {
        this.giocoController = controller;
        this.playerConfigPanels = new ArrayList<>();
        this.gestoreSalvataggio = new GestoreSalvataggio(); 
    }

    public void creaEmostraGUI() {
        mainFrame = new JFrame("DigiGoose - Il Gioco dell'Oca Digitale");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        creaMenuPanel(); 
        settingsPanel = new JPanel(); 
        gamePlayPanel = new JPanel(); 

        mainCardPanel.add(menuPanel, MENU_PANEL);
        mainCardPanel.add(new JPanel(), SETTINGS_PANEL); 
        mainCardPanel.add(new JPanel(), GAME_PANEL);     

        mainFrame.add(mainCardPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        mostraMenuPrincipale();
    }

    private void creaMenuPanel() {
        menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("DigiGoose - Il Gioco dell'Oca Digitale", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        menuPanel.add(titleLabel, gbc);

        JButton nuovaPartitaButton = new JButton("Nuova Partita");
        nuovaPartitaButton.setPreferredSize(new Dimension(150, 40));
        nuovaPartitaButton.addActionListener(e -> confermaEAvviaNuovaPartita()); 
        menuPanel.add(nuovaPartitaButton, gbc);

        loadGameButton = new JButton("Carica Partita");
        loadGameButton.setPreferredSize(new Dimension(150, 40));
        loadGameButton.addActionListener(e -> caricaPartitaSalvata());
        menuPanel.add(loadGameButton, gbc);

        JButton esciButton = new JButton("Esci");
        esciButton.setPreferredSize(new Dimension(150, 40));
        esciButton.addActionListener(e -> System.exit(0));
        menuPanel.add(esciButton, gbc);

        aggiornaStatoBottoneCarica();
    }

    private void aggiornaStatoBottoneCarica() {
        if (loadGameButton != null) {
            loadGameButton.setVisible(gestoreSalvataggio.esisteSalvataggio());
        }
    }
    
    private void caricaPartitaSalvata() {
        try {
            Partita partitaCaricata = gestoreSalvataggio.caricaPartita();
            giocoController.setPartitaCorrente(partitaCaricata); 
            partitaController = new PartitaController(partitaCaricata); 
            
            creaAndShowGamePlayPanel(); 
            messageArea.setText("Partita caricata con successo!\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
            iniziaLogicaDiGioco(); 
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Errore durante il caricamento della partita: " + e.getMessage(), "Errore Caricamento", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public void mostraMenuPrincipale() {
        if (menuPanel == null) creaMenuPanel();
        aggiornaStatoBottoneCarica(); 
        mainCardPanel.add(menuPanel, MENU_PANEL); 
        cardLayout.show(mainCardPanel, MENU_PANEL);
    }

    private void confermaEAvviaNuovaPartita() {
        if (gestoreSalvataggio.esisteSalvataggio()) {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Iniziare una nuova partita cancellerà il salvataggio esistente. Continuare?",
                    "Nuova Partita",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                gestoreSalvataggio.eliminaSalvataggio();
                aggiornaStatoBottoneCarica(); 
                avviaNuovaPartitaVera();
            }
        } else {
            avviaNuovaPartitaVera();
        }
    }
    
    private void avviaNuovaPartitaVera() {
        giocoController.avviaNuovaPartita();
        creaAndShowSettingsPanel();
    }

    private void creaAndShowSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout(10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("Numero di giocatori (2-6):"));
        SpinnerModel numPlayersModel = new SpinnerNumberModel(2, 2, 6, 1);
        numPlayersSpinner = new JSpinner(numPlayersModel);
        numPlayersSpinner.setPreferredSize(new Dimension(50, 25));
        numPlayersSpinner.addChangeListener(e -> updatePlayerConfigPanelsContainer(((Integer) numPlayersSpinner.getValue())));
        topPanel.add(numPlayersSpinner);
        settingsPanel.add(topPanel, BorderLayout.NORTH);
    
        JPanel configsOuterContainer = new JPanel(new GridBagLayout()); 
        JPanel configsContainer = new JPanel();
        configsContainer.setLayout(new BoxLayout(configsContainer, BoxLayout.Y_AXIS));
        configsOuterContainer.add(configsContainer); 

        JScrollPane scrollPane = new JScrollPane(configsOuterContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        settingsPanel.add(scrollPane, BorderLayout.CENTER);
    
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton confirmButton = new JButton("Conferma Impostazioni");
        confirmButton.setPreferredSize(new Dimension(200, 40));
        confirmButton.addActionListener(e -> raccogliEConfermaImpostazioni());
        buttonsPanel.add(confirmButton);
    
        JButton cancelButton = new JButton("Annulla");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> mostraMenuPrincipale());
        buttonsPanel.add(cancelButton);
        settingsPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
        updatePlayerConfigPanelsContainer((Integer) numPlayersSpinner.getValue()); 
    
        mainCardPanel.add(settingsPanel, SETTINGS_PANEL); 
        cardLayout.show(mainCardPanel, SETTINGS_PANEL);
    }
    
    private void updatePlayerConfigPanelsContainer(int numPlayers) {
        if (settingsPanel == null || settingsPanel.getComponentCount() < 2) {
            return;
        }
        Component centerComponent = settingsPanel.getComponent(1); 
        if (!(centerComponent instanceof JScrollPane)) {
            return; 
        }
        JScrollPane scrollPane = (JScrollPane) centerComponent;
        JPanel configsOuterContainer = (JPanel) scrollPane.getViewport().getView();
        JPanel configsContainer = (JPanel) configsOuterContainer.getComponent(0);

        configsContainer.removeAll();
        playerConfigPanels.clear();
    
        for (int i = 0; i < numPlayers; i++) {
            PlayerConfigPanel pcp = new PlayerConfigPanel(i, giocoController.getImpostazioniPartita());
            playerConfigPanels.add(pcp);
            configsContainer.add(pcp);
            configsContainer.add(Box.createRigidArea(new Dimension(0, 5))); 
        }
        configsContainer.revalidate();
        configsContainer.repaint();
    }


    private void raccogliEConfermaImpostazioni() {
        try {
            int numPlayers = (Integer) numPlayersSpinner.getValue();
            giocoController.inserisciNumeroGiocatori(numPlayers);

            List<Colore> coloriSceltiGlobalmente = new ArrayList<>();

            for (int i = 0; i < numPlayers; i++) {
                PlayerConfigPanel pcp = playerConfigPanels.get(i);
                TipoGiocatore tipo = pcp.getTipoGiocatore();
                String nome = pcp.getNomeGiocatore();
                Colore colore = pcp.getColoreScelto();

                if (colore == null) {
                    throw new IllegalArgumentException("Giocatore " + (i + 1) + " deve selezionare un colore.");
                }
                if (coloriSceltiGlobalmente.contains(colore)) {
                    throw new IllegalArgumentException("Colore " + colore + " selezionato più volte. Ogni giocatore deve avere un colore unico.");
                }
                coloriSceltiGlobalmente.add(colore);

                giocoController.selezionaTipoGiocatore(i, tipo.toString());
                if (tipo == TipoGiocatore.UMANO) {
                    if (nome.isEmpty()) {
                        throw new IllegalArgumentException("Il nome per il Giocatore " + (i + 1) + " (Umano) non può essere vuoto.");
                    }
                    giocoController.inserisciNomeGiocatore(i, nome);
                } else { 
                    giocoController.assegnaNomeCPU(i); 
                }
                giocoController.getImpostazioniPartita().setColoreGiocatore(i, colore);
            }
            
            giocoController.confermaImpostazioni();
            partitaController = new PartitaController(giocoController.getPartitaCorrente());

            creaAndShowGamePlayPanel();
            iniziaLogicaDiGioco();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Errore nelle impostazioni: " + ex.getMessage(), "Errore Configurazione", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void creaAndShowGamePlayPanel() {
        gamePlayPanel = new JPanel(new BorderLayout(10, 10));
        gamePlayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gameBoardPanel = new GameBoardPanel();
        JScrollPane boardScrollPane = new JScrollPane(gameBoardPanel);
        gamePlayPanel.add(boardScrollPane, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        currentTurnLabel = new JLabel("Giro: - Turno: -");
        currentTurnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        eastPanel.add(currentTurnLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0,5)));

        currentPlayerLabel = new JLabel("Turno di: -");
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        eastPanel.add(currentPlayerLabel);
        eastPanel.add(Box.createRigidArea(new Dimension(0,10)));

        rollDiceButton = new JButton("Lancia i Dadi");
        rollDiceButton.setPreferredSize(new Dimension(150, 40));
        rollDiceButton.addActionListener(e -> gestisciTurnoGiocatore());
        eastPanel.add(rollDiceButton);
        eastPanel.add(Box.createRigidArea(new Dimension(0,20)));
        
        JButton menuButton = new JButton("Torna al Menu");
        menuButton.setPreferredSize(new Dimension(150, 30));
        menuButton.addActionListener(e -> gestisciRitornoAlMenu()); 
        eastPanel.add(menuButton);
        eastPanel.add(Box.createVerticalGlue()); 

        gamePlayPanel.add(eastPanel, BorderLayout.EAST);

        messageArea = new JTextArea(8, 50); 
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        gamePlayPanel.add(messageScrollPane, BorderLayout.SOUTH);
        
        mainCardPanel.add(gamePlayPanel, GAME_PANEL);
        cardLayout.show(mainCardPanel, GAME_PANEL);
    }

    private void gestisciRitornoAlMenu() {
        if (partitaController != null && partitaController.getPartita() != null &&
            partitaController.getPartita().getStato() == StatoPartita.IN_CORSO) {
            
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Vuoi salvare la partita prima di tornare al menu?",
                    "Salva Partita",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    gestoreSalvataggio.salvaPartita(partitaController.getPartita());
                    messageArea.append("Partita salvata.\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                    JOptionPane.showMessageDialog(mainFrame, "Partita salvata con successo!", "Salvataggio", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Errore durante il salvataggio: " + ex.getMessage(), "Errore Salvataggio", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                partitaController.getPartita().setStato(StatoPartita.TERMINATA); 
                mostraMenuPrincipale();
            } else if (choice == JOptionPane.NO_OPTION) {
                partitaController.getPartita().setStato(StatoPartita.TERMINATA);
                mostraMenuPrincipale();
            }
        } else {
            mostraMenuPrincipale();
        }
    }


    private void iniziaLogicaDiGioco() {
        Partita partita = partitaController.getPartita();
        gameBoardPanel.setPartita(partita);
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
        updateGameInfo();
        prepareNextTurn();
    }
    
    private void updateGameInfo() {
        Partita partita = partitaController.getPartita();
        if (partita == null) return;

        currentTurnLabel.setText("Giro: " + partita.getGiroCorrente() + ", Turno: " + partita.getTurnoCorrente());
        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
        if (giocatoreCorrente != null) {
            currentPlayerLabel.setText("Turno di: " + giocatoreCorrente.getNome() + " (" + giocatoreCorrente.getPedina().getColore() + ")");
        }
        
        gameBoardPanel.repaint();
    }

    private void prepareNextTurn() {
        Partita partita = partitaController.getPartita();
        if (partita.getStato() != StatoPartita.IN_CORSO) {
            rollDiceButton.setEnabled(false);
            if (partita.getStato() == StatoPartita.TERMINATA) {
                 Giocatore vincitore = null;
                 for(Giocatore g : partita.getGiocatori()){
                     if(g.getPosizione() == 63){ 
                         vincitore = g;
                         break;
                     }
                 }
                 if (vincitore == null && partita.getGiocatoreCorrente() != null && partitaController.verificaVincitore(partita.getGiocatoreCorrente())) {
                     vincitore = partita.getGiocatoreCorrente();
                 }

                 if (vincitore != null) {
                    mostraVincitore(vincitore);
                 } else {
                    messageArea.append("\nPartita terminata o interrotta.\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                 }
            }
            aggiornaStatoBottoneCarica(); 
            return;
        }

        updateGameInfo(); 
        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();

        if (giocatoreCorrente.getTurniSaltati() > 0) {
            messageArea.append(giocatoreCorrente.getNome() + " deve saltare questo turno. Rimangono " + giocatoreCorrente.getTurniSaltati() + " turni da saltare.\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
            giocatoreCorrente.decrementaTurniSaltati();
            partitaController.passaTurno(); 
            Timer skipTimer = new Timer(700, e -> prepareNextTurn()); 
            skipTimer.setRepeats(false);
            skipTimer.start();
            return;
        }
        
        if (giocatoreCorrente.getTurniSaltati() < 0) { // Giocatore bloccato da condizione speciale
            String blockMessage = getBlockMessage(giocatoreCorrente);
            messageArea.append(giocatoreCorrente.getNome() + blockMessage + " Tenterà di lanciare i dadi.\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        }

        if (giocatoreCorrente.getTipo() == TipoGiocatore.UMANO) {
            rollDiceButton.setEnabled(true);
            messageArea.append("\n" + "Tocca a " + giocatoreCorrente.getNome() + ". Lancia i dadi.\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } else { 
            rollDiceButton.setEnabled(false); 
            messageArea.append("\n" + "Turno del Computer: " + giocatoreCorrente.getNome() + ".\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
            Timer computerTurnTimer = new Timer(1000, ae -> gestisciTurnoGiocatore()); 
            computerTurnTimer.setRepeats(false);
            computerTurnTimer.start();
        }
    }
    
    private String getBlockMessage(Giocatore g) {
        if (g.getTurniSaltati() == -1) return " è bloccato in attesa di un 6.";
        // -2 (Prigione) ora è gestito come turniSaltati > 0, quindi non dovrebbe apparire qui.
        // Se rimane per altri effetti, ok, altrimenti si può rimuovere.
        if (g.getTurniSaltati() == -2) return " è in prigione (condizione speciale non più attiva per casella 52). Deve lanciare un 6."; 
        if (g.getTurniSaltati() == -3) return " è bloccato (Casella 26). Deve lanciare un 3 o un 6.";
        if (g.getTurniSaltati() == -4) return " è bloccato (Casella 53). Deve lanciare un 4 o un 5.";
        return " è bloccato.";
    }
    
    private void gestisciTurnoGiocatore() {
        rollDiceButton.setEnabled(false);
        Partita partita = partitaController.getPartita();
        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente();
    
        if (giocatoreCorrente == null || partita.getStato() != StatoPartita.IN_CORSO) {
            prepareNextTurn();
            return;
        }
    
        messageArea.append(giocatoreCorrente.getNome() + " sta per lanciare i dadi...\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    
        Timer rollTimer = new Timer(700, e -> { 
            currentDiceRoll = partitaController.tiraDadi();
            mostraRisultatoDadi(currentDiceRoll); 
    
            partitaController.verificaELiberaGiocatoriBloccati(currentDiceRoll);
    
            if (giocatoreCorrente.getTurniSaltati() < 0) { // Se ancora bloccato dopo il tentativo
                messageArea.append(giocatoreCorrente.getNome() + " non ha ottenuto il risultato necessario e rimane" + getBlockMessage(giocatoreCorrente) + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
                partitaController.passaTurno(); 
                prepareNextTurn();
                return; 
            }
    
            Dadi dadi = new Dadi(6, 2);
            dadi.setValori(currentDiceRoll);
            stepsToMove = dadi.getSomma();
    
            messageArea.append(giocatoreCorrente.getNome() + " si muove di " + stepsToMove + " passi.\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
    
            Timer moveTimer = new Timer(1000, ev -> { 
                landingSquare = partitaController.muoviPedina(giocatoreCorrente, stepsToMove);
                messageArea.append(giocatoreCorrente.getNome() + " arriva alla casella " + giocatoreCorrente.getPosizione() + ".\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
                gameBoardPanel.repaint();
    
                if (landingSquare != null && landingSquare.getNumero() == 63) { 
                    partita.setStato(StatoPartita.TERMINATA);
                    concludiTurnoEAvanza();
                } else if (landingSquare != null && landingSquare.isSpeciale()) {
                    messageArea.append("Casella speciale ("+landingSquare.getNumero()+"): " + landingSquare.getTipoEffetto().toString().replace("_", " ") + ". Applico effetto...\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                    
                    Timer effectTimer = new Timer(1200, effEv -> { 
                        if (landingSquare.getTipoEffetto() == TipoEffettoCasella.RILANCIA_TORNA_INDIETRO) {
                            messageArea.append(giocatoreCorrente.getNome() + " deve rilanciare per tornare indietro...\n");
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());

                            int[] diceRollForBackward = partitaController.tiraDadi();
                            mostraRisultatoDadi(diceRollForBackward); 

                            int stepsBackward = diceRollForBackward[0] + diceRollForBackward[1];

                            messageArea.append(giocatoreCorrente.getNome() + " torna indietro di " + stepsBackward + " caselle.\n");
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());

                            int oldPos = giocatoreCorrente.getPosizione();
                            int newPos = Math.max(1, oldPos - stepsBackward); 
                            giocatoreCorrente.setPosizione(newPos);
                            gameBoardPanel.repaint();

                            Casella finalLandingSquareAfterBackward = partita.getTabellone().getCasella(newPos);
                            messageArea.append(giocatoreCorrente.getNome() + " atterra sulla casella " + newPos + ".\n");
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());

                            if (finalLandingSquareAfterBackward != null && finalLandingSquareAfterBackward.isSpeciale()) {
                                TipoEffettoCasella newEffectType = finalLandingSquareAfterBackward.getTipoEffetto();
                                if (newEffectType != TipoEffettoCasella.RILANCIA_TORNA_INDIETRO) { 
                                     messageArea.append("La nuova casella (" + newPos + ") è speciale: " + newEffectType.toString().replace("_", " ") + ". Applico effetto...\n");
                                     messageArea.setCaretPosition(messageArea.getDocument().getLength());
                                     
                                     finalLandingSquareAfterBackward.applicaEffetto(giocatoreCorrente);
                                     // Se l'effetto della nuova casella è RADDOPPIA_MOVIMENTO o RADDOPPIA_PUNTEGGIO,
                                     // currentDiceRoll NON è il tiro che ha portato lì. Questo è un limite.
                                     // Per ora, questi effetti da RILANCIA_TORNA_INDIETRO non useranno i dadi.
                                     // Gli effetti che settano richiedeRelancio (BALZO_E_RILANCIA) o turniSaltati funzioneranno.
                                     
                                     messageArea.append(giocatoreCorrente.getNome() + " è ora alla casella " + giocatoreCorrente.getPosizione() + ".\n");
                                     if (giocatoreCorrente.getTurniSaltati() != 0) {
                                         messageArea.append(giocatoreCorrente.getNome() + (giocatoreCorrente.getTurniSaltati() > 0 ? " salterà " + giocatoreCorrente.getTurniSaltati() + " turni." : getBlockMessage(giocatoreCorrente)) + "\n");
                                     }
                                     messageArea.setCaretPosition(messageArea.getDocument().getLength());
                                     gameBoardPanel.repaint();
                                }
                            }
                            concludiTurnoEAvanza();
                        } else { // Per tutti gli altri effetti speciali
                            partitaController.applicaEffettoCasellaEsteso(landingSquare, giocatoreCorrente, currentDiceRoll);
                            messageArea.append(giocatoreCorrente.getNome() + " è ora alla casella " + giocatoreCorrente.getPosizione() + ".\n");
                            if (giocatoreCorrente.getTurniSaltati() != 0) {
                                 messageArea.append(giocatoreCorrente.getNome() + (giocatoreCorrente.getTurniSaltati() > 0 ? " salterà " + giocatoreCorrente.getTurniSaltati() + " turni." : getBlockMessage(giocatoreCorrente)) + "\n");
                            }
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());
                            gameBoardPanel.repaint();
                            concludiTurnoEAvanza();
                        }
                    });
                    effectTimer.setRepeats(false);
                    effectTimer.start();
                } else {
                    concludiTurnoEAvanza();
                }
            });
            moveTimer.setRepeats(false);
            moveTimer.start();
        });
        rollTimer.setRepeats(false);
        rollTimer.start();
    }
    
    private void concludiTurnoEAvanza() {
        Partita partita = partitaController.getPartita();
        Giocatore giocatoreCorrente = partita.getGiocatoreCorrente(); 
    
        if (partita.getStato() != StatoPartita.TERMINATA && giocatoreCorrente != null && partitaController.verificaVincitore(giocatoreCorrente)) {
            partita.setStato(StatoPartita.TERMINATA);
        }
    
        if (partita.getStato() != StatoPartita.TERMINATA) {
            if (giocatoreCorrente != null && giocatoreCorrente.getTurniSaltati() >= 0) { // Non bloccato da condizione speciale
                if (giocatoreCorrente.getRichiedeRelancio()) {
                    messageArea.append(giocatoreCorrente.getNome() + " ha diritto a un altro tiro!\n");
                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
                    giocatoreCorrente.setRichiedeRelancio(false); 
                    if (giocatoreCorrente.getTipo() == TipoGiocatore.UMANO) {
                        rollDiceButton.setEnabled(true);
                    } else {
                        Timer computerReRollTimer = new Timer(1000, ae -> gestisciTurnoGiocatore());
                        computerReRollTimer.setRepeats(false);
                        computerReRollTimer.start();
                    }
                    return; 
                } else {
                    partitaController.passaTurno();
                }
            }
            else if (giocatoreCorrente != null && giocatoreCorrente.getTurniSaltati() == 0) { 
                 partitaController.passaTurno();
            } else if (giocatoreCorrente != null && giocatoreCorrente.getTurniSaltati() < 0) {
                // Giocatore è ancora bloccato da condizione speciale, il turno passa comunque
                partitaController.passaTurno();
            }
        }
        prepareNextTurn();
    }

    public void mostraRisultatoDadi(int[] valori) {
        if (valori != null && valori.length == 2) {
            messageArea.append("Dadi: " + valori[0] + " e " + valori[1] + " (Totale: " + (valori[0] + valori[1]) + ")\n");
        } else {
            messageArea.append("Risultato dadi non disponibile.\n");
        }
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void mostraVincitore(Giocatore giocatore) {
        String winMessage = "\n===== VINCITORE =====\nComplimenti " + giocatore.getNome() + "! Hai vinto la partita!\n=====================\n";
        messageArea.append(winMessage);
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
        JOptionPane.showMessageDialog(mainFrame, "Complimenti " + giocatore.getNome() + "! Hai vinto la partita!", "Vittoria!", JOptionPane.INFORMATION_MESSAGE);
        rollDiceButton.setEnabled(false);
        if (gestoreSalvataggio.esisteSalvataggio()) {
           gestoreSalvataggio.eliminaSalvataggio();
           aggiornaStatoBottoneCarica();
        }
    }

    private class PlayerConfigPanel extends JPanel {
        private int playerIndex;
        private JComboBox<TipoGiocatore> typeComboBox;
        private JTextField nameField;
        private JComboBox<Colore> colorComboBox;
        private JLabel nameLabelForField;


        public PlayerConfigPanel(int index, ImpostazioniPartita impostazioniGlobali) {
            this.playerIndex = index;

            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            setBorder(BorderFactory.createTitledBorder("Giocatore " + (playerIndex + 1)));
            setPreferredSize(new Dimension(550, 80)); 

            typeComboBox = new JComboBox<>(TipoGiocatore.values());
            typeComboBox.setPreferredSize(new Dimension(100, 25));
            
            nameLabelForField = new JLabel("Nome:");
            nameField = new JTextField(12);
            nameField.setPreferredSize(new Dimension(120, 25));
            
            colorComboBox = new JComboBox<>(Colore.values());
            colorComboBox.setPreferredSize(new Dimension(100, 25));
            
            if (Colore.values().length > playerIndex) {
                colorComboBox.setSelectedIndex(playerIndex % Colore.values().length);
            }


            typeComboBox.addActionListener(e -> {
                TipoGiocatore selectedType = (TipoGiocatore) typeComboBox.getSelectedItem();
                nameField.setEnabled(selectedType == TipoGiocatore.UMANO);
                if (selectedType == TipoGiocatore.COMPUTER) {
                    nameField.setText("CPU " + (playerIndex + 1)); 
                } else {
                    nameField.setText("");
                }
            });

            add(new JLabel("Tipo:"));
            add(typeComboBox);
            add(nameLabelForField);
            add(nameField);
            add(new JLabel("Colore:"));
            add(colorComboBox);

            typeComboBox.setSelectedItem(TipoGiocatore.UMANO); 
            nameField.setEnabled(true);
        }

        public TipoGiocatore getTipoGiocatore() {
            return (TipoGiocatore) typeComboBox.getSelectedItem();
        }

        public String getNomeGiocatore() {
            return nameField.getText().trim();
        }

        public Colore getColoreScelto() {
            return (Colore) colorComboBox.getSelectedItem();
        }
    }
}