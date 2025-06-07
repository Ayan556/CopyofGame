import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import javax.sound.sampled.*;

/**
 * The main game engine class that handles the frame, game loop, input, and rendering.
 */
public class Main extends JFrame implements ActionListener, KeyListener {

	// Constants
	public static final int GAME_WIDTH = 900;
	public static final int GAME_HEIGHT = 900;
	private final int TIMESPEED = 10; // Timer delay in ms
	private static int COUNTER = 0;
	private static int DAMAGE_RATE = 30;
	private static int FRAME_REFRESH_RATE = 8;

	// Enemies waves
	private HashMap<Integer, Integer> entranceSpawnCounts = new HashMap<>();
	private int wave;
	private int enemiesToSpawn;
	private int enemiesSpawnedThisWave;
	private boolean waveInProgress;
	private boolean paused, resume;
	private int xOffset, yOffset;

	// State fields
	private Set<Integer> keysPressed = new HashSet<>(); // Tracks currently held keys
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private HashMap<Integer, Integer> enemyDamageCooldown = new HashMap<>();
	private ArrayList<Bullet> bullets = new ArrayList<>();
	private ArrayList<PowerUpItem> powerUpItems = new ArrayList<>();


	// Game objects
	private Player player;
	private MapGenerator map;
	private Score score;
	private DrawingPanel draw;
	private Graphics2D g2;

	//enemy identifier
	private int enemyNums;

	// Assets
	private BufferedImage background = ResourceLoader.loadImage("BackgroundMap.png");
	private BufferedImage obstacle = ResourceLoader.loadImage("Obstacle.png");
	private BufferedImage shotgunIcon = ResourceLoader.loadImage("ShotgunIcon2.png");
	private BufferedImage speedIcon = ResourceLoader.loadImage("SpeedBoostIcon.png");
	private BufferedImage pauseBackground = ResourceLoader.loadImage("PauseBG.png");
        private BufferedImage heartsSheet = ResourceLoader.loadImage("HealthBar.png");
        private BufferedImage shieldFull = ResourceLoader.loadImage("FullShield.png");
        private BufferedImage shieldEmpty = ResourceLoader.loadImage("EmptyShield.png");
	private Font customFont = FontLoader.loadFont("Game-Font.ttf");


	// Dimensions
	private int panW = GAME_WIDTH, panH = GAME_HEIGHT;

	// Timer
	private Timer timer;

	/**
	 * Entry point for the program.
	 */
	public static void main(String[] args) {
		new Homepage();
	}

	/**
	 * Constructor initializes game setup.
	 */
	public Main() {
		setup();
	}

	/**
	 * Initializes the JFrame, player, map, and timer.
	 */
	private void setup() {
		// Get full screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		enemyNums = 0;

		// Initialize core game objects
		player = new Player((GAME_WIDTH - 70) / 2, (GAME_HEIGHT - 70) / 2, 70, 70, 5, GAME_WIDTH, GAME_HEIGHT);
		map = new MapGenerator(10, 10, 75, 1); // Creates 10x10 grid of 75px tiles
		score = new Score();

		//Enemies
		wave = 1;
		enemiesToSpawn = 2;
		enemiesSpawnedThisWave = 0;
		waveInProgress = false;
		paused = false;
		resume = true;

		// Drawing panel handles rendering
		draw = new DrawingPanel(screenSize.width, screenSize.height);

		this.setSize(screenSize.width, screenSize.height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.add(draw);
		this.setVisible(true);
		SoundPlayer.playBackground("BackgroundMusic.wav");

		// Input and timer
		this.addKeyListener(this);
		enemies = new ArrayList<>();
		timer = new Timer(TIMESPEED, this);
		//timer.start();
	}

	/**
	 * Handles all movement logic for the player and bullets.
	 */
	private void move() {
		// Update bullets
		for (int i = 0; i < bullets.size(); ) {
			Bullet b = bullets.get(i);
			boolean remove = false;

			// Collision with obstacles
			for (Rectangle r : map.getObstacles()) {
				if (b.intersects(r)) {
					if (b instanceof BouncingBullet) {
						((BouncingBullet) b).bounce(r);
					} else {
						remove = true;
					}
					break;
				}
			}

			// Collision with walls
			if (!remove) {
				for (Rectangle wall : map.getWalls()) {
					if (b.intersects(wall)) {
						if (b instanceof BouncingBullet) {
							((BouncingBullet) b).bounce(wall);
						} else {
							remove = true;
						}
						break;
					}
				}
			}

			// Collision with enemies
			if (!remove) {
				for (Enemy e : enemies) {
					if (b.intersects(e)) {
						e.updateHealth(1);
						remove = true;
						break;
					}
				}
			}

			if (!remove && b.disappear()) {
				remove = true;
			}
			if (remove) {
				bullets.remove(i);
			} else {
				b.moveBullet();
				i++; // Only increment if not removed
			}
		}
		// TODO: Add enemy movement
		for (Enemy e : enemies) {
			e.moveTowardPlayer(player, map, enemies);
		}
	}

	/**
	 * Handles player movement using smooth multi-key input.
	 */
	private void handleSmoothMovement() {
		boolean up = keysPressed.contains(KeyEvent.VK_W);
		boolean down = keysPressed.contains(KeyEvent.VK_S);
		boolean left = keysPressed.contains(KeyEvent.VK_A);
		boolean right = keysPressed.contains(KeyEvent.VK_D);

		// Allow diagonal movement by checking combinations
		if (up && left) {
			player.move(3, panW, panH);
			player.move(1, panW, panH);
		} else if (up && right) {
			player.move(3, panW, panH);
			player.move(2, panW, panH);
		} else if (down && left) {
			player.move(4, panW, panH);
			player.move(1, panW, panH);
		} else if (down && right) {
			player.move(4, panW, panH);
			player.move(2, panW, panH);
		} else {
			if (up) {
				player.move(3, panW, panH);
			}
			if (down) {
				player.move(4, panW, panH);
			}
			if (left) {
				player.move(1, panW, panH);
			}
			if (right) {
				player.move(2, panW, panH);
			}
		}

		// Prevent player from moving through obstacles
		map.blockPlayer(player, player.getDirectionFacing());
	}

	/**
	 * Handles cleanup of offscreen/dead objects.
	 */
	private void aliveDead() {
		// Remove off-screen bullets
		for (int i = bullets.size() - 1; i >= 0; i--) {
			if (bullets.get(i).disappear()) {
				bullets.remove(i);
			}
		}

		// Remove dead enemies and update score
		for (int i = enemies.size() - 1; i >= 0; i--) {
			if (!enemies.get(i).isAlive()) {
				enemies.remove(i);
				score.updateScore(10);
			}
		}
	}

	/**
	 * Check if player collects any power-up items
	 */
	private void checkPowerUpPickup() {
		for (int i = powerUpItems.size() - 1; i >= 0; i--) {
			PowerUpItem item = powerUpItems.get(i);
			if (player.intersects(item)) {
				player.addPowerUp(item.getPowerUp(), item.getImage());
				powerUpItems.remove(i);
			}
		}
	}

	/**
	 * Spawn power-ups on random walkable tiles
	 */
	private void spawnPowerUps() {
		powerUpItems.clear();
		java.util.List<Rectangle> tiles = map.getWalkableTiles();

		if (tiles.isEmpty()) {
			return;
		}

		Random rand = new Random();
		int size = map.getTileSize() / 2;

		Rectangle tile1 = tiles.get(rand.nextInt(tiles.size()));
		Rectangle tile2 = tiles.get(rand.nextInt(tiles.size()));

		int x1 = tile1.x + (tile1.width - size) / 2;
		int y1 = tile1.y + (tile1.height - size) / 2;
		int x2 = tile2.x + (tile2.width - size) / 2;
		int y2 = tile2.y + (tile2.height - size) / 2;

		int duration = 1000; // 10 seconds at 10ms per tick
		powerUpItems.add(new PowerUpItem(x1, y1, size, new Shotgun(duration), shotgunIcon, java.awt.Color.BLUE));
		powerUpItems.add(new PowerUpItem(x2, y2, size, new SpeedBoost(duration, 3), speedIcon, java.awt.Color.YELLOW));
	}

	private void dealDamage() {
		int num, cooldown;

		for (Enemy enemy : enemies) {
			/*
			 * If enemy is intersecting player, check for enemy's damage cooldown time.
			 * If the cooldown is at 0, player takes damage and the cooldown is reset. Otherwise, the enemy's cooldown counts down.
			 * Reset the enemy if it stops touching the player
			 */
			num = enemy.getNum();

			if (enemyDamageCooldown.containsKey(enemy.getNum())) {
				cooldown = enemyDamageCooldown.get(enemy.getNum());
			}
			else {
				cooldown = 0;
			}

			if (enemy.intersects(player)) {
				if (cooldown == 0) {
					player.updateHealth(1);
					enemyDamageCooldown.put(num, DAMAGE_RATE);
				} else {
					enemyDamageCooldown.put(num, cooldown - 1);
				}
			} else {
				enemyDamageCooldown.put(num, 0);
			}
		}
	}

	/**
	 * Handles key press events.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_J && !waveInProgress && !paused) {
			waveInProgress = true;
			// Ensure no stray bullets from the previous wave carry
			// over when the new wave begins
			bullets.clear();
			timer.start();
			player.x = (GAME_WIDTH - 70) / 2;
			player.y = (GAME_WIDTH - 70) / 2;
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_I && !paused) {
			timer.stop();
			SoundPlayer.pauseBackground();
			paused = true;
			repaint();
			return;
                } else if (e.getKeyCode() == KeyEvent.VK_U && paused && resume) {
                        paused = false;
                        if (waveInProgress) {
                                        timer.start();
                        }
                        SoundPlayer.resumeBackground();
                        repaint();
                        return;
		} else if (e.getKeyCode() == KeyEvent.VK_U && paused) {
			SoundPlayer.stopBackground();
			this.dispose();
			new Homepage();
			return;
		}

		if (paused) {
			if (e.getKeyCode() == KeyEvent.VK_W && !resume) {
				resume = true;
				repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_S && resume) {
				resume = false;
				repaint();
			}
			return;
		}


		// Fire bullet only once per press
		if (e.getKeyCode() == KeyEvent.VK_U && !keysPressed.contains(KeyEvent.VK_U)) {
				bullets.addAll(player.shoot());
		}
		// Track the U key so holding it down doesn't repeatedly fire
		if (e.getKeyCode() == KeyEvent.VK_U) {
				keysPressed.add(KeyEvent.VK_U);
		}

		if (e.getKeyCode() == KeyEvent.VK_O) {
			player.usePowerUp();
		}

		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_D) {
			keysPressed.add(e.getKeyCode());
			player.setMoving(true);
		}
	}

	/**
	 * Handles key release events.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.remove(e.getKeyCode());
	}

	/**
	 * Not used, but required by KeyListener.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * Timer tick
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (!player.isAlive()) {
			timer.stop();
			SoundPlayer.stopBackground();
			DeathScreen deathScreen = new DeathScreen();
			deathScreen.updateScore(score.score);
			this.dispose();
			return;
		}

		//Set moving for animation
		if (keysPressed.isEmpty()) {
			player.setMoving(false);
		}


		//Set frame for animation
		COUNTER++;
		if (COUNTER == FRAME_REFRESH_RATE) {
			player.changeFrame();

			for (Enemy en : enemies) {
				en.changeFrame();
			}

			COUNTER = 0;
		}

		dealDamage();
		aliveDead();
		checkPowerUpPickup();
		player.updatePowerUps();


		// Spawn enemies for current wave
		if (waveInProgress && enemiesSpawnedThisWave < enemiesToSpawn) {
			int entranceIndex = enemyNums % 4;
			int enemySize = 75;

			// Track how many enemies have spawned at this entrance so far
			int count = entranceSpawnCounts.getOrDefault(entranceIndex, 0);

			// Get the base spawn location for the entrance
			Rectangle baseSpawn = map.getClearSpawnPoint(entranceIndex, enemySize);

			// Offset spacing per enemy so they don't stack
			int spacing = 80;
			int spawnX = baseSpawn.x;
			int spawnY = baseSpawn.y;

			// Adjust position depending on entrance orientation
			switch (entranceIndex) {
				case 0, 1 -> spawnX += count * spacing; // top or bottom = offset X
				case 2, 3 -> spawnY += count * spacing; // left or right = offset Y
			}

			// Update the count in the map
			entranceSpawnCounts.put(entranceIndex, count + 1);

			// Create the enemy
			if (wave % 5 == 0) {
				enemies.add(new BossEnemy(spawnX, spawnY, enemySize, enemySize, enemyNums, wave));
			} else {
				enemies.add(new BasicEnemy(spawnX, spawnY, enemySize, enemySize, 2.5, enemyNums, GAME_WIDTH, GAME_HEIGHT));
			}
			enemyNums++;
			enemiesSpawnedThisWave++;
		}


		// End the wave setup when done
		if (enemiesSpawnedThisWave == enemiesToSpawn && enemies.size() == 0) {
			if (wave % 5 == 0) {
				timer.stop();
				waveInProgress = false;
			}

			wave++;
			// Remove any bullets still on screen so they do not
			// persist into the next wave
			bullets.clear();
			map.updateLevel(wave);

			if (wave % 5 == 0) {
				enemiesToSpawn = (wave / 5);
			} else {
				enemiesToSpawn = wave + 1;
			}
			enemiesSpawnedThisWave = 0;
			entranceSpawnCounts.clear();

			if (wave % 5 == 1) {
				map = new MapGenerator(10, 10, 75, (wave / 5) + 1);
			}

			if (wave >= 3 && wave % 3 == 0) {
				spawnPowerUps();
			}
		}

		handleSmoothMovement();
		move();
		repaint();
	}

	/**
	 * Inner class for drawing the game.
	 */
	private class DrawingPanel extends JPanel {
		private int screenWidth, screenHeight;

		public DrawingPanel(int screenWidth, int screenHeight) {
			this.screenWidth = screenWidth;
			this.screenHeight = screenHeight;
			this.setPreferredSize(new Dimension(screenWidth, screenHeight));
			this.setBackground(Color.BLACK);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Scale the game world to always fit inside the panel
                        double scale = Math.min(getWidth() / (double) GAME_WIDTH,
                                        getHeight() / (double) GAME_HEIGHT);
                        int worldW = (int) (GAME_WIDTH * scale);
                        int worldH = (int) (GAME_HEIGHT * scale);
                        int transX = (getWidth() - worldW) / 2;
                        int transY = (getHeight() - worldH) / 2;
                        xOffset = (int) (transX / scale);
                        yOffset = (int) (transY / scale);

                        java.awt.geom.AffineTransform oldTransform = g2.getTransform();
                        g2.translate(transX, transY);
                        g2.scale(scale, scale);


			// Draw background
			g2.drawImage(background, xOffset, yOffset, null);

			// Draw obstacles
			for (Rectangle tile : map.getObstacles()) {
				g2.drawImage(obstacle, tile.x + xOffset, tile.y + yOffset, null);
			}

			// Draw power-ups
			for (PowerUpItem item : powerUpItems) {
				item.draw(g2, xOffset, yOffset);
			}

			// Draw bullets
			for (Bullet b : bullets) {
				b.draw(g2, xOffset, yOffset);
			}

			// Draw player
			player.drawCharacter(g2, xOffset, yOffset);

                        //Draw Enemies
                        for (Enemy e : enemies) {
                                e.drawCharacter(g2, xOffset, yOffset);
                        }

                        // Reset transform so HUD elements remain constant size
                        g2.setTransform(oldTransform);

                        int barLength = 150;
                        int spacing = 20; // space between bars

                        int leftHUDWidth = transX; // Width of black margin on the left
                        int barY = getHeight() / 10; // Fixed top margin for HUD
                        int bar1X = Math.max(10, (leftHUDWidth - barLength) / 2); // center or pad
                        int bar2X = bar1X;

                        int destH = 0;
                        if (heartsSheet != null) {
                                int rowHeight = heartsSheet.getHeight() / 5;
                                int rowWidth = heartsSheet.getWidth();
                                int destW = barLength;
                                destH = (int) ((rowHeight / (double) rowWidth) * destW);

                                // Draw heart-based health indicator
                                int heartsX = bar1X;
                                int heartsY = barY;
                                int rowIndex = Math.max(0, Math.min(4, 5 - player.getHealth()));
                                g2.drawImage(heartsSheet, heartsX, heartsY, heartsX + destW, heartsY + destH,
                                                0, rowIndex * rowHeight, rowWidth, (rowIndex + 1) * rowHeight, null);

                                // Draw shield icons
                                int shieldSize = destH; // match heart height
                                int shieldX = bar2X;
                                int shieldY = barY + destH + spacing;
                                for (int i = 0; i < 5; i++) {
                                        BufferedImage img = i < player.getShield() ? shieldFull : shieldEmpty;
                                        g2.drawImage(img, shieldX + i * shieldSize, shieldY, shieldSize, shieldSize, null);
                                }
                        }

                        // Draw collected power-up icons grouped by type
                        int iconSize = 60;
                        int invY = barY + destH * 2 + spacing + 40;

			java.util.Map<Class<? extends PowerUp>, DisplayEntry> invMap = new java.util.LinkedHashMap<>();

			for (Player.InventoryPowerUp ip : player.getPowerUps()) {
					Class<? extends PowerUp> type = ip.powerUp.getClass();
					DisplayEntry entry = invMap.get(type);
					if (entry == null) {
							entry = new DisplayEntry(ip.icon);
							invMap.put(type, entry);
					}
					entry.count++;
					if (ip.active) {
							entry.active = true;
							entry.remaining = ip.remaining;
					}
			}

			int idx = 0;
			for (DisplayEntry entry : invMap.values()) {
					int drawY = invY + idx * (iconSize + 30);
					boolean show = !entry.active || COUNTER % 20 < 10;
					if (show && entry.icon != null) {
							g2.drawImage(entry.icon, bar1X, drawY, iconSize, iconSize, null);
							if (entry.count > 1) {
									g2.setColor(Color.WHITE);
									g2.drawString("x" + entry.count, bar1X + iconSize - 15, drawY + iconSize - 5);
							}
					}

					if (entry.active) {
							g2.setColor(Color.WHITE);
							g2.drawString(String.valueOf(entry.remaining / 100), bar1X, drawY + iconSize + 15);
					}
					idx++;
			}

                        score.trackScore();
                        score.drawScore(g2, screenWidth, screenHeight, transX);

                        g2.setFont(customFont.deriveFont(Font.PLAIN, 80));
                        g2.setColor(Color.WHITE);
                        int waveX = transX + worldW + 20;
                        int waveY = transY + (int)(200 * scale);
                        g2.drawString("Wave " + wave, waveX, waveY);

			if (!waveInProgress) {
                                g2.drawImage(pauseBackground, transX, transY, worldW, worldH, null);
                                if (wave == 1) g2.drawString("Press X to Begin", transX + (int)(250 * scale), transY + (int)(450 * scale));
                                else g2.drawString("Wave " + (wave-1) + " Completed, Press X to Continue", transX + (int)(50 * scale), transY + (int)(450 * scale));
			}

			if (paused) {
                                g2.drawImage(pauseBackground, transX, transY, worldW, worldH, null);
                                g2.setColor(Color.WHITE);
                                g2.drawString("Paused", transX + (int)(380 * scale), transY + (int)(400 * scale));

				if (resume) {
                                        g2.setColor(Color.WHITE);
                                        g2.drawString("Resume", transX + (int)(380 * scale), transY + (int)(500 * scale));
                                        g2.setColor(Color.GRAY);
                                        g2.drawString("Exit", transX + (int)(380 * scale), transY + (int)(550 * scale));
				} else {
                                        g2.setColor(Color.GRAY);
                                        g2.drawString("Resume", transX + (int)(380 * scale), transY + (int)(500 * scale));
                                        g2.setColor(Color.WHITE);
                                        g2.drawString("Exit", transX + (int)(380 * scale), transY + (int)(550 * scale));
				}
			}
		}
	}

        /** Helper class for displaying power-up icons with counts and timers */
        private static class DisplayEntry {
                BufferedImage icon;
                int count = 0;
                boolean active = false;
                int remaining = 0;

                DisplayEntry(BufferedImage icon) {
                        this.icon = icon;
                }
        }

        private class Score {
		private BufferedImage scoreSheet = ResourceLoader.loadImage("ScoreNums.png");

		//Score keeper
		private int score, ones, tens, hundreds;

		//coordinates
		private int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;

		Score() {
			this.score = 0;
			this.ones = 0;
			this.tens = 0;
			this.hundreds = 0;
		}

		public void updateScore(int increase) {
			score += increase;
		}

		public void trackScore() {
			hundreds = score / 100;
			tens = (score % 100) / 10;
			ones = (score % 10);
		}

                public void drawScore(Graphics2D g, int screenWidth, int screenHeight, int hudMargin) {
                        // Calculate the right-side margin for score (mirror of HP/SH bars)
                        int rightHUDWidth = hudMargin;

                        int scoreY = screenHeight / 10;  // Align with health/shield bar height
                        int scoreX = screenWidth - rightHUDWidth + 20;  // Padding into right black margin

			drawDigit(g, hundreds, scoreX, scoreY, scoreX + 46, scoreY + 56);
			drawDigit(g, tens, scoreX + 60, scoreY, scoreX + 106, scoreY + 56);
			drawDigit(g, ones, scoreX + 120, scoreY, scoreX + 166, scoreY + 56);
		}


		private void drawDigit(Graphics2D g, int digit, int dx1, int dy1, int dx2, int dy2) {
			switch (digit) {
				case 0:
					sx1 = 196;
					sy1 = 56;
					sx2 = 242;
					sy2 = 112;
					break;
				case 1:
					sx1 = 0;
					sy1 = 0;
					sx2 = 46;
					sy2 = 56;
					break;
				case 2:
					sx1 = 49;
					sy1 = 0;
					sx2 = 95;
					sy2 = 56;
					break;
				case 3:
					sx1 = 98;
					sy1 = 0;
					sx2 = 144;
					sy2 = 56;
					break;
				case 4:
					sx1 = 147;
					sy1 = 0;
					sx2 = 193;
					sy2 = 56;
					break;
				case 5:
					sx1 = 196;
					sy1 = 0;
					sx2 = 242;
					sy2 = 56;
					break;
				case 6:
					sx1 = 0;
					sy1 = 56;
					sx2 = 46;
					sy2 = 112;
					break;
				case 7:
					sx1 = 49;
					sy1 = 56;
					sx2 = 95;
					sy2 = 112;
					break;
				case 8:
					sx1 = 98;
					sy1 = 56;
					sx2 = 144;
					sy2 = 112;
					break;
				case 9:
					sx1 = 147;
					sy1 = 56;
					sx2 = 193;
					sy2 = 112;
					break;
			}

			g.drawImage(scoreSheet, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
		}
	}
}