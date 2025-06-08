package it.digigoose.view;

import it.digigoose.model.Casella;
import it.digigoose.model.Giocatore;
import it.digigoose.model.Partita;
import it.digigoose.model.TipoEffettoCasella;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pannello Swing che disegna il tabellone di gioco, incluse le caselle,
 * i numeri delle caselle, gli effetti speciali e le pedine dei giocatori.
 * fornisce anche tooltip informativi al passaggio del mouse sulle caselle speciali
 */


public class GameBoardPanel extends JPanel {
    private Partita partita;
    private static final int SQUARE_SIZE = 70;
    private static final int PAWN_SIZE = 18;
    private static final int PAWN_OFFSET = 2; // Offset per più pedine

    private Map<Integer, Point> squareCoordinates;
    private Map<it.digigoose.model.Colore, java.awt.Color> awtColorMap;

    private Casella hoveredCasella = null;
    private Timer tooltipTimer;
    private JWindow tooltipWindow;
    private JTextArea tooltipTextArea;

    
    
    //mappa per associare le descrizioni degli effetti al tipo di casella
    private static final Map<TipoEffettoCasella, String> effettoDescrizioniMap = new HashMap<>();

    static {
        effettoDescrizioniMap.put(TipoEffettoCasella.RILANCIA, "Rilancia i dadi."); 
        effettoDescrizioniMap.put(TipoEffettoCasella.PONTE, "Vai direttamente alla casella 12.");
        effettoDescrizioniMap.put(TipoEffettoCasella.RITORNO_INIZIO, "Torna alla casella 1.");
        effettoDescrizioniMap.put(TipoEffettoCasella.RADDOPPIA_MOVIMENTO, "Avanza nuovamente del punteggio ottenuto con i dadi.");
        effettoDescrizioniMap.put(TipoEffettoCasella.FERMA_UN_TURNO, "Salti il prossimo turno.");
        effettoDescrizioniMap.put(TipoEffettoCasella.FERMA_DUE_TURNI, "Salti i prossimi 2 turni."); 
        effettoDescrizioniMap.put(TipoEffettoCasella.VOLA_AVANTI, "Vola alla casella 28.");
        effettoDescrizioniMap.put(TipoEffettoCasella.ATTENDI_DADO, "Sei bloccato qui finché un altro giocatore non tirerà un 6 dal suo lancio di dadi.");
        effettoDescrizioniMap.put(TipoEffettoCasella.RILANCIA_TORNA_INDIETRO, "Rilancia i dadi e torna indietro del numero di caselle pari al nuovo punteggio.");
        effettoDescrizioniMap.put(TipoEffettoCasella.RADDOPPIA_PUNTEGGIO, "Avanza nuovamente del punteggio ottenuto con i dadi.");
        effettoDescrizioniMap.put(TipoEffettoCasella.VAI_INDIETRO, "Torna indietro di 5 caselle.");
        effettoDescrizioniMap.put(TipoEffettoCasella.LABIRINTO, "Torna indietro alla casella 35.");
        effettoDescrizioniMap.put(TipoEffettoCasella.PRIGIONE, "Sei in prigione, resti fermo 2 turni.");
        effettoDescrizioniMap.put(TipoEffettoCasella.POZZO, "Sei caduto nel pozzo, resti fermo 2 turni."); 
        effettoDescrizioniMap.put(TipoEffettoCasella.ATTENDI_DADO_SPECIFICO, "Casella 26: Devi ottenere un 3 o un 6 per proseguire.\nCasella 53: Devi ottenere un 4 o un 5 per proseguire.");
        effettoDescrizioniMap.put(TipoEffettoCasella.VOLA_E_FERMA, "Vola alla casella 57, ma poi perdi un turno.");
        effettoDescrizioniMap.put(TipoEffettoCasella.NESSUNO, "Casella normale.");
    }

    //costruttore
    public GameBoardPanel() {
        setBackground(java.awt.Color.decode("#C8E6C9")); 
        initAwtColorMap();
        squareCoordinates = new HashMap<>();

        tooltipTimer = new Timer(1000, e -> showTooltip());
        tooltipTimer.setRepeats(false);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getPoint());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseExit();
            }
        });
    }

    //tooltip
    @Override
    public void addNotify() {
        super.addNotify();
        if (tooltipWindow == null && SwingUtilities.getWindowAncestor(this) != null) {
            tooltipWindow = new JWindow(SwingUtilities.getWindowAncestor(this));
            tooltipTextArea = new JTextArea();
            tooltipTextArea.setEditable(false);
            tooltipTextArea.setLineWrap(true);
            tooltipTextArea.setWrapStyleWord(true);
            tooltipTextArea.setOpaque(true);
            tooltipTextArea.setBackground(new Color(255, 255, 224)); 
            tooltipTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            tooltipWindow.add(tooltipTextArea);
            tooltipWindow.setFocusableWindowState(false); 
        }
    }

    //associazione colori pedine a Swing
    private void initAwtColorMap() {
        awtColorMap = new HashMap<>();
        awtColorMap.put(it.digigoose.model.Colore.ROSSO, java.awt.Color.RED);
        awtColorMap.put(it.digigoose.model.Colore.BLU, java.awt.Color.BLUE);
        awtColorMap.put(it.digigoose.model.Colore.VERDE, java.awt.Color.GREEN.darker());
        awtColorMap.put(it.digigoose.model.Colore.GIALLO, java.awt.Color.YELLOW);
        awtColorMap.put(it.digigoose.model.Colore.VIOLA, new java.awt.Color(128, 0, 128)); 
        awtColorMap.put(it.digigoose.model.Colore.ARANCIONE, java.awt.Color.ORANGE);
    }

    
    
    public void setPartita(Partita partita) {
        this.partita = partita;
        if (partita != null && partita.getTabellone() != null) {
            calculateSquarePositions(getWidth(), getHeight());
        }
        repaint();
    }

    
    //calcolo caselle e disposizione in griglia
    private void calculateSquarePositions(int panelWidth, int panelHeight) {
        squareCoordinates.clear();
        if (partita == null || partita.getTabellone() == null) return;

        List<Casella> caselle = partita.getTabellone().getCaselle();
        if (caselle.isEmpty()) return;

        int numSquares = 63;
        int currentX = 10;
        int currentY = 10;
        int squaresInRow = Math.max(1, (panelWidth - 20) / (SQUARE_SIZE + 2));
        if (squaresInRow == 0) squaresInRow = 1; 


        for (int i = 0; i <= numSquares; i++) {
            if (partita.getTabellone().getCasella(i) == null) continue;

            squareCoordinates.put(i, new Point(currentX, currentY));

            currentX += (SQUARE_SIZE + 2);
            if ((i + 1) % squaresInRow == 0 && i < numSquares) {
                currentX = 10;
                currentY += (SQUARE_SIZE + 2);
            }
        }
        int requiredWidth = squaresInRow * (SQUARE_SIZE + 2) + 20;
        int numRows = (numSquares / squaresInRow) + ((numSquares % squaresInRow == 0) ? 0 : 1);
        int requiredHeight = numRows * (SQUARE_SIZE + 2) + 20;
        
        if (getPreferredSize() == null || getPreferredSize().width != requiredWidth || getPreferredSize().height != requiredHeight) {
            setPreferredSize(new Dimension(requiredWidth, requiredHeight));
            revalidate(); 
        }
    }
    
    
    //gestione movimento mouse per tooltip:
    
    //se è su una casella
    private void handleMouseMove(Point mousePos) {
        Casella c = getCasellaAtPoint(mousePos);
        if (c != null) {
            if (c != hoveredCasella) {
                hideTooltip();
                tooltipTimer.stop();
                hoveredCasella = c;
                if (hoveredCasella.isSpeciale() || hoveredCasella.getNumero() == 0 || hoveredCasella.getNumero() == 63) {
                    tooltipTimer.restart();
                }
            }
        } else { 
            if (hoveredCasella != null) {
                hideTooltip();
                tooltipTimer.stop();
                hoveredCasella = null;
            }
        }
    }
    //se non è su una casella
    private void handleMouseExit() {
        hideTooltip();
        tooltipTimer.stop();
        hoveredCasella = null;
    }

    
    
    private Casella getCasellaAtPoint(Point p) {
        for (Map.Entry<Integer, Point> entry : squareCoordinates.entrySet()) {
            Rectangle rect = new Rectangle(entry.getValue().x, entry.getValue().y, SQUARE_SIZE, SQUARE_SIZE);
            if (rect.contains(p)) {
                return partita.getTabellone().getCasella(entry.getKey());
            }
        }
        return null;
    }

    
    //funzione che gestisce il tooltip
    private void showTooltip() {
        if (hoveredCasella == null || tooltipWindow == null) return;

        String description = effettoDescrizioniMap.get(hoveredCasella.getTipoEffetto());
        if (hoveredCasella.getNumero() == 0) description = "Casella di partenza";
        if (hoveredCasella.getNumero() == 63) description = "Fai un tiro di dadi esatto qui e vinci la partita!";
        
        if (description == null || description.isEmpty()) {
            description = "Effetto speciale non specificato.";
        }

        if (hoveredCasella.getTipoEffetto() == TipoEffettoCasella.ATTENDI_DADO_SPECIFICO) {
            if (hoveredCasella.getNumero() == 26) {
                description = "Devi ottenere un 3 o un 6 con i dadi per proseguire.";
            } else if (hoveredCasella.getNumero() == 53) {
                description = "Devi ottenere un 4 o un 5 con i dadi per proseguire.";
            }
        }


        tooltipTextArea.setText(hoveredCasella.getTipoEffetto().toString().replace("_", " ") + "\n\n" + description);
        tooltipWindow.pack();
        
        Point mouseScreenPos = MouseInfo.getPointerInfo().getLocation();
        tooltipWindow.setLocation(mouseScreenPos.x + 15, mouseScreenPos.y + 10);
        
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (tooltipWindow.getX() + tooltipWindow.getWidth() > screenBounds.x + screenBounds.width) {
            tooltipWindow.setLocation(mouseScreenPos.x - tooltipWindow.getWidth() - 5, tooltipWindow.getY());
        }
        if (tooltipWindow.getY() + tooltipWindow.getHeight() > screenBounds.y + screenBounds.height) {
            tooltipWindow.setLocation(tooltipWindow.getX(), mouseScreenPos.y - tooltipWindow.getHeight() - 5);
        }

        tooltipWindow.setVisible(true);
    }

    private void hideTooltip() {
        if (tooltipWindow != null) {
            tooltipWindow.setVisible(false);
        }
    }
    
    
    //gestione del testo delle caselle
    private List<String> wrapText(String text, FontMetrics fm, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int linesCount = 0;

        for (String word : words) {
            if (linesCount >= maxLines) break;

            String testLine = currentLine.length() > 0 ? currentLine.toString() + " " + word : word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    linesCount++;
                }
                if (linesCount >= maxLines) break;
                currentLine = new StringBuilder(word);
                if (fm.stringWidth(currentLine.toString()) > maxWidth) { 
                    String truncatedWord = "";
                    for (char c : currentLine.toString().toCharArray()) {
                        if (fm.stringWidth(truncatedWord + c) <= maxWidth - fm.stringWidth("..")) {
                            truncatedWord += c;
                        } else break;
                    }
                    lines.add(truncatedWord + "..");
                    linesCount++;
                    currentLine.setLength(0); 
                    if (linesCount >= maxLines) break;
                }
            }
        }

        if (currentLine.length() > 0 && linesCount < maxLines) {
            if (fm.stringWidth(currentLine.toString()) > maxWidth) {
                 String truncatedWord = "";
                 for (char c : currentLine.toString().toCharArray()) {
                    if (fm.stringWidth(truncatedWord + c) <= maxWidth - fm.stringWidth("..")) {
                        truncatedWord += c;
                    } else break;
                }
                lines.add(truncatedWord + "..");
            } else {
                lines.add(currentLine.toString());
            }
        }
        return lines;
    }


    
    //metodo che effettuvamente disegna il contenuto del pannello
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (partita == null || partita.getTabellone() == null) {
            g.drawString("Nessuna partita caricata.", 20, 20);
            return;
        }

        if (squareCoordinates.isEmpty() ||
            (squareCoordinates.size() != partita.getTabellone().getCaselle().size()) ||
            (getWidth() > 0 && getHeight() > 0 && (getPreferredSize().width != getWidth() || getPreferredSize().height != getHeight()))) {
            calculateSquarePositions(getWidth(), getHeight());
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<Casella> caselle = partita.getTabellone().getCaselle();

        for (Casella casella : caselle) {
            Point p = squareCoordinates.get(casella.getNumero());
            if (p == null) continue;

            g2d.setColor(java.awt.Color.WHITE);
            if (casella.getNumero() == 0) g2d.setColor(java.awt.Color.LIGHT_GRAY);
            if (casella.getNumero() == 63) g2d.setColor(java.awt.Color.ORANGE);
            if (casella.isSpeciale()) g2d.setColor(Color.decode("#FFF9C4")); 
            
            g2d.fillRect(p.x, p.y, SQUARE_SIZE, SQUARE_SIZE);

            g2d.setColor(java.awt.Color.DARK_GRAY);
            g2d.drawRect(p.x, p.y, SQUARE_SIZE, SQUARE_SIZE);

            g2d.setColor(java.awt.Color.BLACK);
            Font numberFont = new Font("Arial", Font.BOLD, 16);
            g2d.setFont(numberFont);
            String numStr = String.valueOf(casella.getNumero());
            FontMetrics fmNum = g2d.getFontMetrics();
            g2d.drawString(numStr, p.x + (SQUARE_SIZE - fmNum.stringWidth(numStr)) / 2, p.y + fmNum.getAscent() + 5);

            if (casella.isSpeciale() && casella.getTipoEffetto() != it.digigoose.model.TipoEffettoCasella.NESSUNO) {
                Font effectFont = new Font("Arial", Font.PLAIN, 9);
                g2d.setFont(effectFont);
                FontMetrics fmEffect = g2d.getFontMetrics();
                String effectName = casella.getTipoEffetto().toString().replace("_", " ");
                
                List<String> lines = wrapText(effectName, fmEffect, SQUARE_SIZE - 6, 2); 

                int lineHeight = fmEffect.getHeight();
                int topOfNumberSpace = p.y + fmNum.getAscent() + 5 + fmNum.getDescent();
                int bottomOfSquare = p.y + SQUARE_SIZE - 3; 
                int spaceForEffectText = bottomOfSquare - topOfNumberSpace;
                int totalEffectTextHeight = lines.size() * lineHeight;
                
                int startY = topOfNumberSpace + (spaceForEffectText - totalEffectTextHeight) / 2 + fmEffect.getAscent();
                if (startY < topOfNumberSpace + fmEffect.getAscent()) startY = topOfNumberSpace + fmEffect.getAscent(); 

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    g2d.drawString(line, p.x + (SQUARE_SIZE - fmEffect.stringWidth(line))/2, startY + i * lineHeight);
                }
            }
        }

        List<Giocatore> giocatori = partita.getOrdineGiocatori();
        Map<Integer, Integer> pawnsOnSquareCount = new HashMap<>();

        for (Giocatore giocatore : giocatori) {
            int pos = giocatore.getPosizione();
            Point squareP = squareCoordinates.get(pos);
            if (squareP == null) continue;

            int pawnCount = pawnsOnSquareCount.getOrDefault(pos, 0);
            int pawnX = squareP.x + 5 + (pawnCount % 3) * (PAWN_SIZE + PAWN_OFFSET);
            int pawnY = squareP.y + SQUARE_SIZE / 2 + (pawnCount / 3) * (PAWN_SIZE + PAWN_OFFSET) - PAWN_SIZE / 2;

            g2d.setColor(awtColorMap.getOrDefault(giocatore.getPedina().getColore(), java.awt.Color.BLACK));
            g2d.fillOval(pawnX, pawnY, PAWN_SIZE, PAWN_SIZE);
            g2d.setColor(java.awt.Color.BLACK);
            g2d.drawOval(pawnX, pawnY, PAWN_SIZE, PAWN_SIZE);

            g2d.setColor(getContrastColor(awtColorMap.getOrDefault(giocatore.getPedina().getColore(), java.awt.Color.BLACK)));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String initial = giocatore.getNome().length() > 0 ? giocatore.getNome().substring(0, 1).toUpperCase() : "?";
            FontMetrics fmPawn = g2d.getFontMetrics();
            g2d.drawString(initial, pawnX + (PAWN_SIZE - fmPawn.stringWidth(initial)) / 2, pawnY + fmPawn.getAscent() + (PAWN_SIZE - fmPawn.getHeight()) / 2);

            pawnsOnSquareCount.put(pos, pawnCount + 1);
        }
    }

    private java.awt.Color getContrastColor(java.awt.Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? java.awt.Color.BLACK : java.awt.Color.WHITE;
    }
}