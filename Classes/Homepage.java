import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

public class Homepage extends JFrame implements KeyListener {
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    int button;
    boolean instructions, credit, score;

    // True when the leaderboard panel is visible
    private boolean showingLeaderboard;

    // Reusable leaderboard panel
    private LeaderboardPanel leaderboardPanel;

    // Main drawing panel reference so we can request focus back
    private DrawingPanel drawingPanel;

    // Legacy fields kept for reference but no longer used
    ArrayList<String> scoreList;
    private int scrollIndex = 0;
    private static final int VISIBLE_LINES = 10;

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

    Homepage() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        button = 1;
        SoundPlayer.playBackground("CyberpunkMusic.wav");

        instructions = false;
        credit = false;
        showingLeaderboard = false;

        creditLines = new ArrayList<String>();
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

        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);

        drawingPanel = new DrawingPanel(screenSize.width, screenSize.height);
        drawingPanel.setLayout(new BorderLayout());
        drawingPanel.setFocusable(true);
        drawingPanel.requestFocusInWindow();
        drawingPanel.addKeyListener(this);

        // Setup leaderboard panel within the homepage so we can
        // swap to it without creating a new window or stopping music
        leaderboardPanel = new LeaderboardPanel();
        leaderboardPanel.setOpaque(false);
        leaderboardPanel.setVisible(false);
        drawingPanel.add(leaderboardPanel, BorderLayout.CENTER);

        this.add(drawingPanel);
        this.setVisible(true);
    }

    // Legacy score loading method kept for reference
    private void loadScores() {
        scoreList = new ArrayList<>();
        File file = new File("highscores.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) scoreList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // When the leaderboard is showing, delegate key events
        if (showingLeaderboard) {
            if (e.getKeyCode() == KeyEvent.VK_L) {
                // Hide leaderboard and return to menu
                showingLeaderboard = false;
                leaderboardPanel.setVisible(false);
                drawingPanel.requestFocusInWindow();
                repaint();
            } else {
                // Allow scrolling within the leaderboard
                leaderboardPanel.keyPressed(e);
            }
            return;
        }

        if (!credit && !score && !instructions) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                if (button != 1) {
                    button--;
                }
                repaint();
            } else if (e.getKeyCode() == KeyEvent.VK_S) {
                if (button != 5) button++;
                repaint();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_L) {
            switch (button) {
                case 1:
                    SoundPlayer.stopBackground();
                    this.dispose();
                    new Main();
                    break;

                case 2:
                    if (instructions) instructions = false;
                    else instructions = true;
                    repaint();
                    break;

                case 3:
                    if (credit) credit = false;
                    else credit = true;
                    repaint();
                    break;

                case 4:
                    // Show the leaderboard without stopping the music
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

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    private class DrawingPanel extends JPanel {
        private int screenWidth, screenHeight;

        public DrawingPanel(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int xOffset = (getWidth() - GAME_WIDTH) / 2;
            int yOffset = (getHeight() - GAME_HEIGHT) / 2;

            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(bg, 0, 0, screenWidth, screenHeight, null);

            if (instructions) {
                g2.drawImage(instruction, 0, 0, screenWidth, screenHeight, null);
            } else if (credit) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, screenWidth, screenHeight);
                g2.setColor(Color.WHITE);
                g2.setFont(customFont.deriveFont(Font.PLAIN, 100));
                FontMetrics fm = g.getFontMetrics();
                int textHeight = fm.getHeight();
                int counter = 1;

                for (String line : creditLines) {
                    int textWidth = fm.stringWidth(line);
                    int x = (getWidth() - textWidth) / 2;
                    g2.drawString(line, x, 275 + yOffset + counter*textHeight);

                    counter++;
                }
            } else if (showingLeaderboard) {
                // Darken the background and draw the leaderboard backdrop
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, screenWidth, screenHeight);
                g2.drawImage(scoresBG, 0, 0, screenWidth, screenHeight, null);
            } else {
                switch (button) {
                    case 1:
                        g2.drawImage(play, 0, 0, screenWidth, screenHeight, null);
                        break;
                    case 2:
                        g2.drawImage(rules, 0, 0, screenWidth, screenHeight, null);
                        break;
                    case 3:
                        g2.drawImage(credits, 0, 0, screenWidth, screenHeight, null);
                        break;
                    case 4:
                        g2.drawImage(scores, 0, 0, screenWidth, screenHeight, null);
                        break;
                    case 5:
                        g2.drawImage(quit, 0, 0, screenWidth, screenHeight, null);
                }
            }
        }
    }
}