import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class Homepage extends JPanel implements KeyListener {
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;

    private int button;
    private boolean instructions, credit, score;
    private boolean showingLeaderboard;

    private LeaderboardPanel leaderboardPanel;
    private SwitchScreens switchScreens;

    ArrayList<String> creditLines;

    private BufferedImage instruction = ResourceLoader.loadImage("Instructions4K.png");
    private BufferedImage credits = ResourceLoader.loadImage("SelectedCredit.png");
    private BufferedImage play = ResourceLoader.loadImage("SelectedPlay.png");
    private BufferedImage rules = ResourceLoader.loadImage("SelectedInstructions.png");
    private BufferedImage scores = ResourceLoader.loadImage("SelectedScores.png");
    private BufferedImage quit = ResourceLoader.loadImage("SelectedQuit.png");
    private BufferedImage bg = ResourceLoader.loadImage("TitleBackground4K.png");
    private BufferedImage scoresBG = ResourceLoader.loadImage("LeaderboardBackground4K.png");
    private Font customFont = FontLoader.loadFont("Game-Font.ttf");

    public Homepage(SwitchScreens switchScreens) {
        this.switchScreens = switchScreens;
        this.button = 1;

        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);

        instructions = false;
        credit = false;
        showingLeaderboard = false;

        creditLines = new ArrayList<>();
        creditLines.add("Game Directed and Created By:");
        creditLines.add("Ayan Talukdar");
        creditLines.add("Minjin Choi");
        creditLines.add("Candice Lee");
        creditLines.add("Dominik Fear-Firman");
        creditLines.add(" ");
        creditLines.add("Artists:");
        creditLines.add("Catherine Zhang");
        creditLines.add("Minjin Choi");
        creditLines.add("Candice Lee");

        leaderboardPanel = new LeaderboardPanel();
        leaderboardPanel.setOpaque(false);
        leaderboardPanel.setVisible(false);
        this.setLayout(new BorderLayout());
        this.add(leaderboardPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xOffset = (getWidth() - GAME_WIDTH) / 2;
        int yOffset = (getHeight() - GAME_HEIGHT) / 2;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

        if (instructions) {
            g2.drawImage(instruction, 0, 0, getWidth(), getHeight(), null);
        } else if (credit) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(customFont.deriveFont(Font.PLAIN, 100));
            FontMetrics fm = g.getFontMetrics();
            int textHeight = fm.getHeight();
            int counter = 1;

            for (String line : creditLines) {
                int textWidth = fm.stringWidth(line);
                int x = (getWidth() - textWidth) / 2;
                g2.drawString(line, x, 275 + yOffset + counter * textHeight);
                counter++;
            }
        } else if (showingLeaderboard) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.drawImage(scoresBG, 0, 0, getWidth(), getHeight(), null);

            // Draw selection hint in the bottom-right corner
            String selectText = "Joystick to scroll up/down the leaderboard";
            float baseSize = 80f * getWidth() / GAME_WIDTH;
            g2.setFont(customFont.deriveFont(Font.PLAIN, baseSize));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(selectText);
            int x = getWidth() - textWidth - 20;
            int y = getHeight() - 20;
            g2.drawString(selectText, x, y);
        } else {
            switch (button) {
                case 1:
                    g2.drawImage(play, 0, 0, getWidth(), getHeight(), null);
                    break;
                case 2:
                    g2.drawImage(rules, 0, 0, getWidth(), getHeight(), null);
                    break;
                case 3:
                    g2.drawImage(credits, 0, 0, getWidth(), getHeight(), null);
                    break;
                case 4:
                    g2.drawImage(scores, 0, 0, getWidth(), getHeight(), null);
                    break;
                case 5:
                    g2.drawImage(quit, 0, 0, getWidth(), getHeight(), null);
                    break;
            }

            // Draw selection hint in the bottom-right corner
            String selectText = "Press Z to select";
            float baseSize = 80f * getWidth() / GAME_WIDTH;
            g2.setFont(customFont.deriveFont(Font.PLAIN, baseSize));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(selectText);
            int x = getWidth() - textWidth - 20;
            int y = getHeight() - 20;
            g2.drawString(selectText, x, y);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (showingLeaderboard) {
            if (e.getKeyCode() == KeyEvent.VK_L) {
                showingLeaderboard = false;
                leaderboardPanel.setVisible(false);
                this.requestFocusInWindow();
                repaint();
            } else {
                leaderboardPanel.keyPressed(e);
            }
            return;
        }

        if (!credit && !score && !instructions) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                if (button > 1) {
                    button--;
                }
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                if (button < 5) {
                    button++;
                }
                repaint();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_L) {
            switch (button) {
                case 1:
                    switchScreens.startGame();
                    break;
                case 2:
                    instructions = !instructions;
                    repaint();
                    break;
                case 3:
                    credit = !credit;
                    repaint();
                    break;
                case 4:
                    showingLeaderboard = true;
                    leaderboardPanel.loadScores();
                    leaderboardPanel.setVisible(true);
                    repaint();
                    break;
                case 5:
                    System.exit(0);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}