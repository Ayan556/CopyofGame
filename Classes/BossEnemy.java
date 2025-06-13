import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BossEnemy extends Enemy {

	private int lastDirectionFacing = 4;
	private int frame;
	private int spriteCol = 0;
	private int spriteW = 75, spriteH = 75;
	BufferedImage walkingSpriteSheet = ResourceLoader.loadImage("BossWalking.png");
	BufferedImage idleSpriteSheet = ResourceLoader.loadImage("BossIdle.png");

	BossEnemy(int x, int y, int width, int height, int num, int level) {
		super(x, y, width, height, 10 + (level * 3), 10 + (level * 2), num, 1.5 + (0.1 * level));
	}

	/**
	 * Move towards the player
	 */
	@Override
	public void moveTowardPlayer(Player p, MapGenerator map, ArrayList<Enemy> others) {
		int moveY, moveX;

		if ((this.x < 0 || this.x > 900) && (this.y < 375 || this.y > 450)) {
			if (this.y < 375) moveY = (int) speed;
			else moveY = (int) -speed;

			int originalX = this.x;
			int originalY = this.y;

			this.y += moveY;

			//Direction
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		} else if ((this.y < 0 || this.y > 900) && (this.x < 375 || this.x > 450)) {
			if (this.x < 375) moveX = (int) speed;
			else moveX = (int) -speed;

			int originalX = this.x;
			int originalY = this.y;

			this.x += moveX;

			//Direction
			if (this.x > originalX) this.directionFacing = 2;
			else if (this.x < originalX) this.directionFacing = 1;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		} else if ((this.y < 0 || this.y > 900) && (this.x > 375 && this.x < 450)){
			if (this.y < 0) moveY = (int) speed;
			else moveY = (int) -speed;

			int originalX = this.x;
			int originalY = this.y;

			this.y += moveY;

			//Direction
			if (this.y > originalY) this.directionFacing = 4;
			else if (this.y < originalY) this.directionFacing = 3;
			else {
				this.directionFacing = 4;
				setMoving(false);
			}
		} else if ((this.x < 0 || this.x > 900) && (this.y > 375 && this.y < 450)) {
			if (this.x < 0) moveX = (int) speed;
			else moveX = (int) -speed;

			int originalX = this.x;
			int originalY = this.y;

			this.x += moveX;

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

			moveX = (int)((dx / dist) * speed);
			moveY = (int)((dy / dist) * speed);

			int originalX = this.x;
			int originalY = this.y;

			// Diagonal
			this.x += moveX;
			this.y += moveY;
			if (collides(map)) {
				this.x = originalX;
				this.y = originalY;

				//X movement only
				if (dx == 0) moveX = 0;
				else if (dx < 0) moveX = (int) -speed;
				else moveX = (int) speed;

				this.x += moveX;
				if (collides(map)) this.x = originalX;

				//Y movement only
				if (dy == 0) moveY = 0;
				else if (dy < 0) moveY = (int) -speed;
				else moveY = (int) speed;

				this.y += moveY;
				if (collides(map)) this.y = originalY;
			}

			int movedX = this.x - originalX;
			int movedY = this.y - originalY;

			if (movedX != 0 || movedY != 0) {
				changeDirection(originalX, originalY);
				setMoving(true);
			} else {
				setMoving(false);
			}
		}
	}

	/**
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
				spriteCol = 3;
				break;
			case 2:
				spriteCol = 2;
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