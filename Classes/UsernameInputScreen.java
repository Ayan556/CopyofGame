import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Overlay panel allowing the player to enter a 3 character username
 * using a virtual keyboard controlled with W,A,S,D and L to select.
 */
public class UsernameInputScreen extends JPanel implements KeyListener {

    /** Key mappings for the virtual joystick. */
    public static final int INPUT_UP = KeyEvent.VK_W;
    public static final int INPUT_DOWN = KeyEvent.VK_S;
    public static final int INPUT_LEFT = KeyEvent.VK_A;
    public static final int INPUT_RIGHT = KeyEvent.VK_D;
    public static final int INPUT_SELECT = KeyEvent.VK_L;

    private static final int MAX_LENGTH = 3;
    private final VirtualKeyboard keyboard = new VirtualKeyboard();
    private final StringBuilder input = new StringBuilder(MAX_LENGTH);
    private final UsernameListener listener;
    private final Font font;

    /** Callback interface for when the username has been chosen. */
    public interface UsernameListener {
        void usernameSelected(String username);
    }

    public UsernameInputScreen(UsernameListener listener) {
        this.listener = listener;
        this.setOpaque(false); // allow darkened background
        this.setFocusTraversalKeysEnabled(false);
        this.font = FontLoader.loadFont("Game-Font.ttf");
        addKeyListener(this);
        setFocusable(true);
    }

     /**
     * Deactivates the input screen so it no longer consumes key events.
     */
    public void close() {
        setVisible(false);
        setFocusable(false);
        removeKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Darken the background
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw username placeholders
        g2.setFont(font.deriveFont(Font.PLAIN, 80));
        g2.setColor(Color.WHITE);
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (i < input.length()) display.append(input.charAt(i)).append(' ');
            else display.append("_ ");
        }
        String disp = display.toString().trim();
        int strW = g2.getFontMetrics().stringWidth(disp);
        g2.drawString(disp, (getWidth() - strW) / 2, 120);

        // Draw keyboard
        keyboard.draw(g2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == INPUT_UP) keyboard.moveUp();
        else if (code == INPUT_DOWN) keyboard.moveDown();
        else if (code == INPUT_LEFT) keyboard.moveLeft();
        else if (code == INPUT_RIGHT) keyboard.moveRight();
        else if (code == INPUT_SELECT) handleSelection();
        repaint();
    }
    private void handleSelection() {
        String label = keyboard.getSelectedKey();
        if ("BACK".equals(label)) {
            if (input.length() > 0) input.setLength(input.length() - 1);
        } else if ("ENTER".equals(label)) {
            if (input.length() == MAX_LENGTH) {
                listener.usernameSelected(input.toString());
            }
        } else { // letter
            if (input.length() < MAX_LENGTH) {
                input.append(label);
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    /**
     * Simple virtual keyboard with arrow-style navigation.
     */
    private class VirtualKeyboard {
        private final String[][] layout = {
                {"Q","W","E","R","T","Y","U","I","O","P"},
                {"A","S","D","F","G","H","J","K","L"},
                {"Z","X","C","V","B","N","M"},
                {"BACK","ENTER"}
        };
        private int row = 0;
        private int col = 0;
        private final int KEY_W = 60;
        private final int KEY_H = 60;
        private final int SPACING = 10;

        String getSelectedKey() { return layout[row][col]; }
        void moveUp() { if (row > 0) { row--; col = Math.min(col, layout[row].length-1); } }
        void moveDown() { if (row < layout.length-1) { row++; col = Math.min(col, layout[row].length-1); } }
        void moveLeft() { if (col > 0) col--; }
        void moveRight() { if (col < layout[row].length-1) col++; }

        void draw(Graphics2D g2) {
            int totalHeight = layout.length * KEY_H + (layout.length - 1) * SPACING;
            int startY = (getHeight() - totalHeight) / 2 + 80;
            for (int r = 0; r < layout.length; r++) {
                int rowLen = layout[r].length;
                int totalWidth;
                if (r == 3) {
                    totalWidth = rowLen * KEY_W * 2 + (rowLen - 1) * SPACING;
                } else {
                    totalWidth = rowLen * KEY_W + (rowLen - 1) * SPACING;
                }
                int x = (getWidth() - totalWidth) / 2;
                for (int c = 0; c < rowLen; c++) {
                    boolean sel = (r == row && c == col);
                    int w = KEY_W;
                    if (r == 3) w = KEY_W * 2; // bigger back/enter keys
                    g2.setColor(sel ? new Color(255, 200, 50) : new Color(80,80,80));
                    g2.fillRoundRect(x, startY, w, KEY_H, 10,10);
                    g2.setColor(Color.WHITE);
                    g2.setFont(font.deriveFont(Font.PLAIN, 30));
                    String label = layout[r][c];
                    int strW = g2.getFontMetrics().stringWidth(label);
                    int strH = g2.getFontMetrics().getAscent();
                    g2.drawString(label, x + (w - strW)/2, startY + (KEY_H + strH)/2 - 5);
                    x += w + SPACING;
                }
                startY += KEY_H + SPACING;
            }
        }
    }
}
