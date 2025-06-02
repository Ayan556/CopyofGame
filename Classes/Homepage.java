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

public class Homepage extends JFrame implements KeyListener {
	public static final int GAME_WIDTH = 1600;
	public static final int GAME_HEIGHT = 900;
	int button;
	boolean instructions;
	private BufferedImage title = ResourceLoader.loadImage("Title.png");
	private BufferedImage play = ResourceLoader.loadImage("Play.png");
	private BufferedImage rules = ResourceLoader.loadImage("Rules.png");
	private BufferedImage exit = ResourceLoader.loadImage("Exit.png");
	private BufferedImage bg = ResourceLoader.loadImage("BgHomePage.png");


	Homepage() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		button = 1;

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
	
	@Override
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_W) {
    		if (button != 1) {
    			button--;
    		}
    		repaint();
    	} else if (e.getKeyCode() == KeyEvent.VK_S) {
    		if (button != 3) button++;
    		repaint();
    	}
    	
    	if (e.getKeyCode() == KeyEvent.VK_U) {
    		switch (button) {
    			case 1: 
    				this.dispose();
    				new Main();
    	            break;
    			case 2:
    				if (instructions) instructions = false;
    				else instructions = true;
    				repaint();
    				break;
    			case 3:
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
			g2.drawImage(bg, 0 + xOffset, 0, 1600 + xOffset, 940, null);

			if (instructions) {
            	g2.setColor(Color.BLUE);
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                g2.drawString("INSTRUCTIONS", 310 + xOffset, 520 + yOffset);
            } else {
				g2.drawImage(title, 416 +xOffset, 100 + yOffset, null);

				switch (button) {
                 case 1:
					 g2.drawImage(play, 568 + xOffset, 500 + yOffset, null);
					 break;
                 case 2:
					 g2.drawImage(rules, 568 + xOffset, 500 + yOffset, null);
					 break;
                 case 3:
					 g2.drawImage(exit, 568 + xOffset, 500 + yOffset, null);
					 break;
                 }
            }
        }
    }
}