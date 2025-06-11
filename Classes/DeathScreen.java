import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DeathScreen extends JFrame implements KeyListener{

    public static final int GAME_WIDTH = 900;
    public static final int GAME_HEIGHT = 900;
    private int score;
    private int retry;
    private BufferedImage youDied = ResourceLoader.loadImage("Death2.png");
    private BufferedImage yes = ResourceLoader.loadImage("PlayAgainYes.png");
    private BufferedImage no = ResourceLoader.loadImage("PlayAgainNo.png");
    private Font customFont = FontLoader.loadFont("Game-Font.ttf");

    public DeathScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        retry = 1;

        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        DrawingPanel drawingPanel = new DrawingPanel(screenSize.width, screenSize.height);
        drawingPanel.setFocusable(true);
        drawingPanel.requestFocusInWindow();
        drawingPanel.addKeyListener(this);
        this.add(drawingPanel);
        this.setVisible(true);
    }

    public void updateScore(int newScore) {
        this.score = newScore;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            retry = 1;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            retry = 2;
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_L) {
            switch (retry) {
                case 1:
                    this.dispose();
                    new Main();
                    break;
                case 2:
                    this.dispose();
                    new Homepage();
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

            g2.drawImage(youDied, xOffset, 300 + yOffset, null);

            g2.setColor(new Color(199, 193, 159));
            g2.setFont(customFont.deriveFont(Font.PLAIN, 80));
            g2.drawString("Final Score: " + score, 310 + xOffset, 520 + yOffset);

            switch (retry) {
                case 1:
                    g2.drawImage(yes, 280 + xOffset, 600 + yOffset, null);
                    break;
                case 2:
                    g2.drawImage(no, 280 + xOffset, 600 + yOffset, null);
                    break;
            }
        }
    }
}