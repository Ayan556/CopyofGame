import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A basic enemy unit with minimal AI and a solid rectangle visual.
 * Inherits from the abstract Enemy class.
 */
public class BasicEnemy extends Enemy {

	private Color color;         // Fill color of the enemy
	private int panelWidth;      // Width of the game panel
	private int panelHeight;     // Height of the game panel

	//Sprite stuff
	private int frame;
	private int spriteRowNum = 0;
	private int spriteW = 75, spriteH = 75;
	private boolean moving;
	BufferedImage spriteSheet = ResourceLoader.loadImage("BasicEnemyMoving.png");
	BufferedImage idleSpriteSheet = ResourceLoader.loadImage("BasicEnemyIdle.png");

	/**
	 * Constructs a BasicEnemy with given parameters and sets default health and shield.
	 *
	 * @param x       X-coordinate of enemy spawn
	 * @param y       Y-coordinate of enemy spawn
	 * @param width   Width of the enemy
	 * @param height  Height of the enemy
	 * @param speed   Movement speed of the enemy
	 * @param panW    Width of the game panel (used for bounds or pathing)
	 * @param panH    Height of the game panel
	 */
	public BasicEnemy(int x, int y, int width, int height, double speed, int num,int panW, int panH) {
		super(x, y, width, height, 5, 5, num, speed); // Health = 5, Shield = 5
		this.color = Color.GREEN;
		this.panelWidth = panW;
		this.panelHeight = panH;
		this.moving = true;
	}

	/**
	 * Move towards the player
	 */
	@Override
	public void moveTowardPlayer(Player p, MapGenerator map, ArrayList<Enemy> others) {
		double dx = p.x - this.x;
		double dy = p.y - this.y;
		double dist = Math.sqrt(dx * dx + dy * dy);
		if (dist == 0) return;

		int moveX = (int)((dx / dist) * speed);
		int moveY = (int)((dy / dist) * speed);

		int originalX = this.x;
		int originalY = this.y;

		this.moving = true;

		// Diagonal
		this.x += moveX;
		this.y += moveY;
		if (collides(map) || collidesWithOthers(others)) {
			this.x = originalX;
			this.y = originalY;

			this.x += moveX;
			if (collides(map) || collidesWithOthers(others)) this.x = originalX;

			this.y += moveY;
			if (collides(map) || collidesWithOthers(others)) this.y = originalY;
		}

		//Direction
		if (this.x > originalX) {		//Enemy is moving right
			this.directionFacing = 2;
		} else if (this.x < originalX){	//Enemy is moving left
			this.directionFacing = 1;
		} else {						//Enemy doesn't move left or right
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		}
	}

	/**
	 * Check for collisions
	 * @param map		The game map
	 * @return			true or false depending on collision
	 */
	private boolean collides(MapGenerator map) {
		for (Rectangle r : map.getObstacles()) {
			if (this.intersects(r)) return true;
		}
		for (Rectangle wall : map.getWalls()) {
			if (this.intersects(wall)) return true;
		}
		return false;
	}

	private boolean collidesWithOthers(ArrayList<Enemy> enemies) {
		for (Enemy e : enemies) {
			if (e != this && this.intersects(e)) return true;
		}
		return false;
	}

	//---- Image stuff ----

	/**
	 * Set moving
	 * @param moving		boolean value for moving
	 */
	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	/**
	 * Change the frame
	 */
	public void changeFrame() {
		frame = (frame + 1) % 2;
	}

	/**
	 * Draws the BasicEnemy
	 */
	@Override
	public void drawCharacter(Graphics2D g, int xOffset, int yOffset) {
		switch (this.directionFacing) {
			case 1:
				spriteRowNum = 3;
				break;
			case 2:
				spriteRowNum = 2;
				break;
			case 3:
				spriteRowNum = 1;
				break;
			case 4:
				spriteRowNum = 0;
				break;
		}

		if (!moving) {
			g.drawImage(idleSpriteSheet, this.x + xOffset, this.y + yOffset, this.x + xOffset + spriteW, this.y + yOffset + spriteH, frame * spriteW, spriteRowNum * spriteH, (frame + 1)*spriteW, (spriteRowNum + 1)*spriteW, null);
		} else {
			g.drawImage(spriteSheet, this.x + xOffset, this.y + yOffset, this.x + xOffset + spriteW, this.y + yOffset + spriteH, frame * spriteW, spriteRowNum * spriteH, (frame + 1)*spriteW, (spriteRowNum + 1)*spriteW, null);
		}
	}
}