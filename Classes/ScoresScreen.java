import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoresScreen extends JFrame implements KeyListener {
    public static final int GAME_WIDTH = 1600;
    public static final int GAME_HEIGHT = 900;

    private BufferedImage background = ResourceLoader.loadImage("scoresBackground.jpg");
    private Font customFont = FontLoader.loadFont("Game-Font.ttf");
    private List<String> scores;
    private int scrollIndex = 0;
    private static final int VISIBLE_LINES = 10;

    public ScoresScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        }

        loadScores();

        DrawingPanel panel = new DrawingPanel(screenSize.width, screenSize.height);
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(this);
        this.add(panel);
        this.setVisible(true);
    }

    private void loadScores() {
        scores = new ArrayList<>();
        File file = new File("highscores.txt");
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) scores.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W && scrollIndex > 0) {
            scrollIndex--;
            repaint();
        } else if (code == KeyEvent.VK_S && scrollIndex < Math.max(0, scores.size() - VISIBLE_LINES)) {
            scrollIndex++;
            repaint();
        } else if (code == KeyEvent.VK_L) {
            this.dispose();
            new Homepage();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    private class DrawingPanel extends JPanel {
        private int screenWidth, screenHeight;
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

            int xOffset = (getWidth() - GAME_WIDTH) / 2;
            int yOffset = (getHeight() - GAME_HEIGHT) / 2;

            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(background, xOffset, yOffset, GAME_WIDTH, GAME_HEIGHT, null);

            int rectW = 800;
            int rectH = 600;
            int rectX = (getWidth() - rectW) / 2;
            int rectY = (getHeight() - rectH) / 2;
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(rectX, rectY, rectW, rectH);
            g2.setColor(Color.WHITE);
            g2.setFont(customFont.deriveFont(Font.PLAIN, 50));
            int lineHeight = g2.getFontMetrics().getHeight();
            for (int i = 0; i < VISIBLE_LINES; i++) {
                int idx = scrollIndex + i;
                if (idx >= scores.size()) break;
                String line = scores.get(idx);
                g2.drawString(line, rectX + 40, rectY + 80 + i * lineHeight);
            }
        }
    }
}
