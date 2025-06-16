import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class DeathScreen extends JPanel implements KeyListener{

    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;

    private Dimension screenSize;
    private SwitchScreens switchScreens;
    private int score;
    private String username;
    private int retry;
    private BufferedImage youDied = ResourceLoader.loadImage("DeathScreen4K.png");
    private BufferedImage yes = ResourceLoader.loadImage("PlayAgainYes.png");
    private BufferedImage no = ResourceLoader.loadImage("PlayAgainNo.png");
    private Font customFont = FontLoader.loadFont("Game-Font.ttf");

    public DeathScreen(SwitchScreens switchScreens) {
        this.switchScreens = switchScreens;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        retry = 1;

        this.setFocusable(true);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
    }

    public void setResult(String user, int newScore) {
        this.username = user;
        this.score = newScore;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A) {
            retry = 1;
            repaint();
        } else if (code == KeyEvent.VK_D) {
            retry = 2;
            repaint();
        } else if (code == KeyEvent.VK_L || e.getKeyCode() == KeyEvent.VK_U || e.getKeyCode() == KeyEvent.VK_J) {
            if (retry == 1) {
                switchScreens.startGame(); // Restart the game
            } else if (retry == 2) {
                switchScreens.showHomepage(); // Go back to homepage
            }
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xOffset = (getWidth() - GAME_WIDTH) / 2;
        int yOffset = (getHeight() - GAME_HEIGHT) / 2;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Draw the death screen background centered in the window
        g2.drawImage(youDied, 0, 0, screenWidth, screenHeight, null);

        g2.setColor(Color.WHITE);
        g2.setFont(customFont.deriveFont(Font.PLAIN, 150));
        g2.drawString(username + " - " + score, screenWidth/3 + xOffset, 450 + yOffset);

        switch (retry) {
            case 1:
                g2.drawImage(yes, 0, 100, screenWidth, screenHeight, null);
                break;
            case 2:
                g2.drawImage(no, 0, 100, screenWidth, screenHeight, null);
                break;
        }
    }
}