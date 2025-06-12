import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a bullet projectile in the game.
 * Each bullet moves in a fixed direction and is removed if it leaves the game panel.
 */
public class Bullet extends Rectangle {

	private int vx, vy;       // Velocity components (direction and speed)
	private int panW, panH;   // Dimensions of the game panel (for bounds checking)
	// Image used when rendering this bullet
	// Protected so subclasses like BouncingBullet can swap graphics
	protected BufferedImage bulletImage;


	// Static resources - single sprite sheet containing all bullet directions
	private static final BufferedImage bulletSheet = ResourceLoader.loadImage("bulletSprites.png");
	private static final int sheetCols = 4;
	private static final int frameW = bulletSheet.getWidth() / sheetCols;
	private static final int frameH = bulletSheet.getHeight();



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

		// Determine column in sprite sheet based on direction
		int col = 0;

		switch (direction) {
			case 1 -> { vx = -8; vy = 0; col = 3; } // left
			case 2 -> { vx = 8; vy = 0; col = 2; }  // right
			case 3 -> { vx = 0; vy = -8; col = 1; } // up
			case 4 -> { vx = 0; vy = 8; col = 0; }  // down
		}

		bulletImage = bulletSheet.getSubimage(col * frameW, 0, frameW, frameH);

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