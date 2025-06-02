import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a bullet projectile in the game.
 * Each bullet moves in a fixed direction and is removed if it leaves the game panel.
 */
public class Bullet extends Rectangle {

	private int vx, vy;       // Velocity components (direction and speed)
	private int panW, panH;   // Dimensions of the game panel (for bounds checking)
	private BufferedImage bulletImage;  // Individual bullet image for direction

	// Static resources to load only once
	private static final BufferedImage bulletLeft = ResourceLoader.loadImage("BulletLeft.png");
	private static final BufferedImage bulletRight = ResourceLoader.loadImage("BulletRight.png");
	private static final BufferedImage bulletUp = ResourceLoader.loadImage("BulletUp.png");
	private static final BufferedImage bulletDown = ResourceLoader.loadImage("BulletDown.png");



	/**
	 * Constructs a bullet with specified starting position, size, and direction.
	 *
	 * @param x         X-coordinate of the bullet's origin
	 * @param y         Y-coordinate of the bullet's origin
	 * @param w         Width of the bullet
	 * @param h         Height of the bullet
	 * @param panW      Width of the game panel
	 * @param panH      Height of the game panel
	 * @param direction Integer direction: 1=left, 2=right, 3=up, 4=down
	 */
	public Bullet(int x, int y, int w, int h, int panW, int panH, int direction) {
		super(x, y, w, h);  // Initialize position and size via Rectangle superclass

		// Set bullet speed based on direction
		switch (direction) {
			case 1 -> { vx = -8; vy = 0; bulletImage = bulletLeft; }
			case 2 -> { vx = 8; vy = 0; bulletImage = bulletRight; }
			case 3 -> { vx = 0; vy = -8; bulletImage = bulletUp; }
			case 4 -> { vx = 0; vy = 8; bulletImage = bulletDown; }
		}

		this.panW = panW;
		this.panH = panH;
	}

	/**
	 * Updates the bullet's position based on its velocity.
	 */
	public void moveBullet() {
		this.x += vx;
		this.y += vy;
	}

	/**
	 * Sets the horizontal speed (velocity in x-axis).
	 * Useful for advanced behaviors (e.g. bouncing or curved shots).
	 *
	 * @param vx New horizontal speed
	 */
	public void setSpeedX(int vx) {
		this.vx = vx;
	}

	/**
	 * Sets the vertical speed (velocity in y-axis).
	 *
	 * @param vy New vertical speed
	 */
	/**
	 * Sets the vertical speed (velocity in y-axis).
	 *
	 * @param vy New vertical speed
	 */
	public void setSpeedY(int vy) {
		this.vy = vy;
	}

	/** Returns current horizontal speed */
	public int getSpeedX() {
		return vx;
	}

	/** Returns current vertical speed */
	public int getSpeedY() {
		return vy;
	}

	/**
	 * Checks if the bullet has gone offscreen (out of bounds).
	 * If true, the bullet should be removed from the game.
	 *
	 * @return true if bullet is outside the screen bounds, false otherwise
	 */
	public boolean disappear() {
		return (this.x < 0 || this.x > panW || this.y < 0 || this.y > panH);
	}

	public void draw(Graphics2D g2, int xOffset, int yOffset) {
		g2.drawImage(bulletImage, this.x + xOffset, this.y + yOffset, 20, 20, null);
	}
}