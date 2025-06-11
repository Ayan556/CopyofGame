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
	boolean instructions, credit;
	private BufferedImage instruction = ResourceLoader.loadImage("instructions.jpg");
	private BufferedImage credits = ResourceLoader.loadImage("creditsselected.png");
	private BufferedImage play = ResourceLoader.loadImage("playselected.png");
	private BufferedImage rules = ResourceLoader.loadImage("instselected.png");
	private BufferedImage quit = ResourceLoader.loadImage("quitselected.png");
	private BufferedImage bg = ResourceLoader.loadImage("TitleBackground4K.jpg");
	private Font customFont = FontLoader.loadFont("Game-Font.ttf");

	Homepage() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		button = 1;
		SoundPlayer.playBackground("CyberpunkMusic.wav");

		instructions = false;
		credit = false;

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
			if (button != 4) button++;
			repaint();
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
				g2.drawImage(instruction, -10, 0, 1580 + xOffset, 940, null);
			} else if (credit) {
				g2.setColor(Color.WHITE);
				g2.setFont(customFont.deriveFont(Font.PLAIN, 80));
				g2.drawString("Game Directed and Created By: Ayan, Candice, Minjin, Dominik", 310 + xOffset, 520 + yOffset);
			}	else {

				switch (button) {
					case 1:
						g2.drawImage(play, 0, 0, 1500, 850, null);
						break;
					case 2:
						g2.drawImage(rules, 0, 0, 1500, 850, null);
						break;
					case 3:
						g2.drawImage(credits, 0, 0, 1500, 850, null);
						break;
					case 4:
						g2.drawImage(quit, 0, 0, 1500, 850, null);
				}
			}
		}
	}
}