import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel that displays the high score leaderboard. Text is centred
 * in a resizable rectangle outline and supports scrolling with W/S.
 */
public class LeaderboardPanel extends JPanel implements KeyListener {

    /** Number of leaderboard entries visible at once. */
    private static final int VISIBLE_LINES = 7;

    /** Padding around text inside the outline rectangle. */
    private static final int PADDING = 40;

    /** Parsed list of username/score pairs. */
    private final List<ScoreEntry> scores = new ArrayList<>();

    /** Font loaded from /res/Fonts */
    private final Font font = FontLoader.loadFont("Game-Font.ttf");

    /** Current scroll offset. */
    private int scrollOffset = 0;

    /** Optional player name used to highlight the player's own score. */
    private final String playerName;

    public LeaderboardPanel(String playerName) {
        this.playerName = playerName;
        setFocusable(true);
        addKeyListener(this);
        loadScores();
    }

    /** Convenience constructor when no player name highlighting is needed. */
    public LeaderboardPanel() {
        this(null);
    }

    /**
     * Loads scores from highscores.txt. Ensures the file is sorted
     */
    public final void loadScores() {
        HighscoreManager.sortScores();
        scores.clear();
        File file = new File("highscores.txt");
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    int idx = line.lastIndexOf('-');
                    if (idx != -1) {
                        String name = line.substring(0, idx).trim();
                        try {
                            int sc = Integer.parseInt(line.substring(idx + 1).trim());
                            scores.add(new ScoreEntry(name, sc));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Ensure we do not scroll past the end after reloading
        scrollOffset = Math.min(scrollOffset, Math.max(0, scores.size() - VISIBLE_LINES));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Bold, large font for arcade readability
        g2.setFont(font.deriveFont(Font.BOLD, 60f));
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight() + 10; // extra spacing between entries

        int visible = Math.min(VISIBLE_LINES, scores.size());

        // Calculate the maximum string width of the visible entries to size the rectangle
        int maxWidth = 0;
        for (int i = 0; i < visible; i++) {
            ScoreEntry e = scores.get(i + scrollOffset);
            String text = e.username + " - " + e.score;
            maxWidth = Math.max(maxWidth, fm.stringWidth(text));
        }

        // Rectangle dimensions with padding on all sides
        int rectWidth = maxWidth + PADDING * 2;
        int rectHeight = lineHeight * visible + PADDING * 2 - 10; // minus extra spacing

        // Center the rectangle in the panel
        int rectX = (getWidth() - rectWidth) / 2;
        int rectY = (getHeight() - rectHeight) / 2;

        // Draw hollow rectangle
        g2.setColor(Color.WHITE);
        g2.drawRect(rectX, rectY, rectWidth, rectHeight);

        // Draw the visible leaderboard entries centred within the rectangle
        int y = rectY + PADDING + fm.getAscent();
        for (int i = 0; i < visible; i++) {
            int index = scrollOffset + i;
            ScoreEntry entry = scores.get(index);
            String text = entry.username + " - " + entry.score;
            int textW = fm.stringWidth(text);
            int x = rectX + (rectWidth - textW) / 2;

            // Highlight the top score in gold, player's own score in cyan
            if (index == 0) {
                g2.setColor(new Color(255, 215, 0));
            } else if (playerName != null && playerName.equalsIgnoreCase(entry.username)) {
                g2.setColor(Color.CYAN);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(text, x, y);
            y += lineHeight;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W && scrollOffset > 0) {
            scrollOffset--; // scroll up
            repaint();
        } else if (code == KeyEvent.VK_S && scrollOffset < Math.max(0, scores.size() - VISIBLE_LINES)) {
            scrollOffset++; // scroll down
            repaint();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    /** Simple struct-like holder for parsed score lines. */
    private static class ScoreEntry {
        final String username;
        final int score;
        ScoreEntry(String u, int s) { this.username = u; this.score = s; }
    }
}
