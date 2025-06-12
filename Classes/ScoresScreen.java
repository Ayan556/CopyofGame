import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * Full-screen frame that shows the leaderboard using {@link LeaderboardPanel}.
 * Press 'L' to return to the {@link Homepage}.
 */
public class ScoresScreen extends JFrame implements KeyListener {
    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;

    private final BufferedImage background = ResourceLoader.loadImage("scoresBackground.jpg");
    private final LeaderboardPanel leaderboard;

    public ScoresScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);

        leaderboard = new LeaderboardPanel();
        leaderboard.setOpaque(false); // allow background to be visible

        DrawingPanel panel = new DrawingPanel(screenSize.width, screenSize.height);
        panel.setLayout(new BorderLayout());
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(this);
        panel.add(leaderboard, BorderLayout.CENTER);

        this.add(panel);
        this.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_L) {
            this.dispose();
            new Homepage();
        } else {
            // Delegate scrolling keys to the leaderboard panel
            leaderboard.keyPressed(e);
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    private class DrawingPanel extends JPanel {
        private final int screenWidth, screenHeight;
        public DrawingPanel(int w, int h) {
            this.screenWidth = w;
            this.screenHeight = h;
            setPreferredSize(new Dimension(w, h));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill background and draw the image centered
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(background, 0, 0, screenWidth, screenHeight, null);
        }
    }
}
