import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
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

	//enemy identifier
	private int enemyNums;

	// Assets
	private BufferedImage background = ResourceLoader.loadImage("BackgroundMap.png");
        private BufferedImage obstacle = ResourceLoader.loadImage("Obstacle.png");
        private BufferedImage shotgunIcon = ResourceLoader.loadImage("ShotgunIcon.png");
        private BufferedImage speedIcon = ResourceLoader.loadImage("SpeedBoostIcon.png");

	// Dimensions
	private int panW = GAME_WIDTH, panH = GAME_HEIGHT;

	// Timer
	private Timer timer;
	private Graphics2D g2;

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
		DrawingPanel draw = new DrawingPanel(screenSize.width, screenSize.height);

		this.setSize(screenSize.width, screenSize.height);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.add(draw);
		this.setVisible(true);

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

			boolean collided = false;

			// Check collision with obstacles
			for (Rectangle r : map.getObstacles()) {
				if (b.intersects(r)) {
					if (bullets.size() > 0) {
						bullets.remove(i);
						collided = true;
						break;
					}
				}
			}

			// Check collision with walls if not already collided
			if (!collided) {
				for (Rectangle wall : map.getWalls()) {
					if (b.intersects(wall)) {
						if (bullets.size() > 0) {
							bullets.remove(i);
							collided = true;
							break;
						}
					}
				}
			}

			//collision with enemies
			for (Enemy e : enemies) {
				if (b.intersects(e)) {
					if (bullets.size() > 0) {
						bullets.remove(i);
						e.updateHealth(1);
					}
				}
			}

			// Out-of-bounds removal
			if (b.disappear()) {
				if (bullets.size() > 0)	bullets.remove(i);
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
			if (up) player.move(3, panW, panH);
			if (down) player.move(4, panW, panH);
			if (left) player.move(1, panW, panH);
			if (right) player.move(2, panW, panH);
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

        /** Check if player collects any power-up items */
        private void checkPowerUpPickup() {
                for (int i = powerUpItems.size() - 1; i >= 0; i--) {
                        PowerUpItem item = powerUpItems.get(i);
                        if (player.intersects(item)) {
                                player.addPowerUp(item.getPowerUp(), item.getImage());
                                powerUpItems.remove(i);
                        }
                }
        }

        /** Spawn a single power-up on a random walkable tile */
        private void spawnPowerUps() {
                powerUpItems.clear();
                java.util.List<Rectangle> tiles = map.getWalkableTiles();
                if (tiles.isEmpty()) return;

                Random rand = new Random();
                int size = map.getTileSize();

                Rectangle tile = tiles.get(rand.nextInt(tiles.size()));

                int x = tile.x + (tile.width - size) / 2;
                int y = tile.y + (tile.height - size) / 2;

                int duration = 3000; // 30 seconds at 10ms per tick

                if (rand.nextBoolean()) {
                        powerUpItems.add(new PowerUpItem(x, y, size,
                                        new Shotgun(duration), shotgunIcon, java.awt.Color.BLUE));
                } else {
                        powerUpItems.add(new PowerUpItem(x, y, size,
                                        new SpeedBoost(duration, 3), speedIcon, java.awt.Color.YELLOW));
                }
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

			if (enemyDamageCooldown.containsKey(enemy.getNum())) cooldown = enemyDamageCooldown.get(enemy.getNum());
			else cooldown = 0;

			if (enemy.intersects(player)) {
				if (cooldown == 0) {
					player.updateHealth(1);
					enemyDamageCooldown.put(num, DAMAGE_RATE);
				} else enemyDamageCooldown.put(num, cooldown - 1);
			} else enemyDamageCooldown.put(num, 0);
		}
	}

	/**
	 * Handles key press events.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_J && !waveInProgress) {
			waveInProgress = true;
			if (wave % 5 == 0) enemiesToSpawn = (wave / 5);
			else enemiesToSpawn = wave + 1;
			enemiesSpawnedThisWave = 0;
			entranceSpawnCounts.clear();
			timer.start();
			player.x = (GAME_WIDTH - 70) / 2;
			player.y = (GAME_WIDTH - 70) / 2;
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_I && !paused) {
			timer.stop();
			paused = true;
			repaint();
			return;
		} else if (e.getKeyCode() == KeyEvent.VK_U && paused && resume) {
			paused = false;
			timer.start();
			return;
		} else if (e.getKeyCode() == KeyEvent.VK_U && paused) {
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
	public void keyTyped(KeyEvent e) {
		// Unused
	}

	/**
	 * Timer tick
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (!player.isAlive()) {
			timer.stop();
			DeathScreen deathScreen = new DeathScreen();
			deathScreen.updateScore(score.score);
			this.dispose();
			return;
		}

		//Set moving for animation
		if (keysPressed.isEmpty()) player.setMoving(false);


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
		if (enemiesSpawnedThisWave == enemiesToSpawn && enemies.size()==0) {
			waveInProgress = false;
			wave++;
			timer.stop();
                        map.updateLevel(wave);

                        if (wave %5 == 1) {
                                map = new MapGenerator(10, 10, 75, (wave/5)+1);
                        }
                        if (wave >= 2 && wave % 2 == 0) {
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

			// Calculate center offset for the game window
			xOffset = (getWidth() - GAME_WIDTH) / 2;
			yOffset = (getHeight() - GAME_HEIGHT) / 2;


			if (paused) {
				g.setColor(Color.BLACK);
				g.drawRect(0 + xOffset, 0 + yOffset, 900 + xOffset, 900 + yOffset);
				g.setColor(Color.WHITE);
				g.drawString("Paused", 450 + xOffset, 450 + yOffset);

				if (resume) {
					g.setColor(Color.WHITE);
					g.drawString("Resume", 450 + xOffset, 500 + yOffset);
					g.setColor(Color.GRAY);
					g.drawString("Exit", 450 + xOffset, 550 + yOffset);
				} else {
					g.setColor(Color.GRAY);
					g.drawString("Resume", 450 + xOffset, 500 + yOffset);
					g.setColor(Color.WHITE);
					g.drawString("Exit", 450 + xOffset, 550 + yOffset);
				}
				return;
			}

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

			int barHeight = 20;
			int barLength = 150;
			int spacing = 20; // space between bars

			int leftHUDWidth = (getWidth() - Main.GAME_WIDTH) / 2; // Width of black margin
			int barY = getHeight() / 10; // Fixed top margin for HUD
			int bar1X = (leftHUDWidth - (2 * barLength + spacing)) / 2; // Left padding
			int bar2X = bar1X + barLength + spacing;

			// Health bar (left)
			g2.setColor(Color.RED);
			int healthFill = (int) ((player.getHealth() / 5.0) * barLength); // Assuming max = 5
			g2.fillRect(bar1X, barY, healthFill, barHeight);
			g2.setColor(Color.WHITE);
			g2.drawRect(bar1X, barY, barLength, barHeight);
			g2.drawString("HP", bar1X + 5, barY - 5);

                        // Shield bar (right)
                        g2.setColor(Color.CYAN);
                        int shieldFill = (int) ((player.getShield() / 5.0) * barLength);
                        g2.fillRect(bar2X, barY, shieldFill, barHeight);
                        g2.setColor(Color.WHITE);
                        g2.drawRect(bar2X, barY, barLength, barHeight);
                        g2.drawString("SH", bar2X + 5, barY - 5);

                        // Draw collected power-up icons
                        int iconSize = 40;
                        int invY = barY + barHeight + 40;
                        int idx = 0;
                        for (Player.InventoryPowerUp ip : player.getPowerUps()) {
                                int drawY = invY + idx * (iconSize + 30);
                                boolean show = !ip.active || COUNTER % 20 < 10;
                                if (show) {
                                        if (ip.icon != null)
                                                g2.drawImage(ip.icon, bar1X, drawY, iconSize, iconSize, null);
                                }
                                if (ip.active) {
                                        g2.setColor(Color.WHITE);
                                        g2.drawString(String.valueOf(ip.remaining / 100), bar1X, drawY + iconSize + 15);
                                }
                                idx++;
                        }

			score.trackScore();
			score.drawScore(xOffset, yOffset, g2, screenWidth, screenHeight);

			if (!waveInProgress && !timer.isRunning()) {
				g2.setColor(Color.WHITE);
				g2.drawString("Wave " + wave, 500, 400);
				g2.drawString("Press D to continue", 600, 400);
			}
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

		public void drawScore(int xOffset, int yOffset, Graphics2D g, int screenWidth, int screenHeight) {
			// Calculate the right-side margin for score (mirror of HP/SH bars)
			int rightHUDWidth = (screenWidth - GAME_WIDTH) / 2;

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