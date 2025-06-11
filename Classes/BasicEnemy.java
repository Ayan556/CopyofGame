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
	private int spriteCol = 0;
	private int spriteW = 75, spriteH = 75;
	private boolean moving;
	BufferedImage walkingSpriteSheet = ResourceLoader.loadImage("enemyWalk.png");
	BufferedImage idleSpriteSheet = ResourceLoader.loadImage("enemyIdle.png");

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
                double moveY, moveX;

                if ((this.x < 0 || this.x > 900) && (this.y < 375 || this.y > 450)) {
                        if (this.y < 375) moveY = speed;
                        else moveY = -speed;

			int originalX = this.x;
			int originalY = this.y;

                        moveVector(0, moveY, panelWidth, panelHeight);
                        syncPosition();

			//Direction
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
                } else if ((this.y < 0 || this.y > 900) && (this.x < 375 || this.x > 450)) {
                        if (this.x < 375) moveX = speed;
                        else moveX = -speed;

			int originalX = this.x;
			int originalY = this.y;

                        moveVector(moveX, 0, panelWidth, panelHeight);
                        syncPosition();

			//Direction
			if (this.x > originalX) this.directionFacing = 2;
			else if (this.x < originalX) this.directionFacing = 1;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
                } else if ((this.y < 0 || this.y > 900) && (this.x > 375 && this.x < 450)){
                        if (this.y < 0) moveY = speed;
                        else moveY = -speed;

			int originalX = this.x;
			int originalY = this.y;

                        moveVector(0, moveY, panelWidth, panelHeight);
                        syncPosition();

			//Direction
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
                } else if ((this.x < 0 || this.x > 900) && (this.y > 375 && this.y < 450)) {
                        if (this.x < 0) moveX = speed;
                        else moveX = -speed;

			int originalX = this.x;
			int originalY = this.y;

                        moveVector(moveX, 0, panelWidth, panelHeight);
                        syncPosition();

			//Direction
			if (this.x > originalX) this.directionFacing = 2;
			else if (this.x < originalX) this.directionFacing = 1;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		} else {
			double dx = p.x - this.x;
			double dy = p.y - this.y;
			double dist = Math.sqrt(dx * dx + dy * dy);
			if (dist == 0) return;

                        moveX = (dx / dist) * speed;
                        moveY = (dy / dist) * speed;

			int originalX = this.x;
			int originalY = this.y;

			this.moving = true;

			// Diagonal
                        moveVector(moveX, moveY, panelWidth, panelHeight);
                        if (collides(map)) {
                                this.x = originalX;
                                this.y = originalY;
                                syncPosition();

				//X movement only
                                if (dx == 0) moveX = 0;
                                else if (dx < 0) moveX = -speed;
                                else moveX = speed;

                                moveVector(moveX, 0, panelWidth, panelHeight);
                                if (collides(map)) {
                                        this.x = originalX;
                                        syncPosition();
                                }

				//Y movement onlyAdd commentMore actions
                                if (dy == 0) moveY = 0;
                                else if (dy < 0) moveY = -speed;
                                else moveY = speed;

                                moveVector(0, moveY, panelWidth, panelHeight);
                                if (collides(map)) {
                                        this.y = originalY;
                                        syncPosition();
                                }
                        }

                        changeDirection(originalX, originalY);
                        syncPosition();
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

	/**Add commentMore actions
	 * Change the direction of the enemy
	 */
	private void changeDirection(int originalX, int originalY) {
		//Direction
		if (this.x > originalX) {        //Enemy is moving right
			this.directionFacing = 2;
		} else if (this.x < originalX) {    //Enemy is moving left
			this.directionFacing = 1;
		} else {                        //Enemy doesn't move left or right
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		}
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
				spriteCol  = 3;
				break;
			case 2:
				spriteCol  = 2;
				break;
			case 3:
				spriteCol = 1;
				break;
			case 4:
				spriteCol = 0;
				break;
		}

		if (!moving) {
			g.drawImage(
					idleSpriteSheet,
					this.x + xOffset, this.y + yOffset,
					this.x + xOffset + spriteW, this.y + yOffset + spriteH,
					spriteCol*spriteW, frame*spriteH,
					spriteCol*spriteW+spriteW, (frame+1)*spriteH,
					null);
		} else {
			g.drawImage(
					walkingSpriteSheet,
					this.x + xOffset, this.y + yOffset,
					this.x + xOffset + spriteW, this.y + yOffset + spriteH,
					spriteCol*spriteW, frame*spriteH,
					spriteCol*spriteW+spriteW, (frame+1)*spriteH,
					null);
		}
	}
}