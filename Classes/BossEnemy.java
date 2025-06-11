import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class BossEnemy extends Enemy {

	BossEnemy(int x, int y, int width, int height, int num, int level) {
		super(x, y, width, height, 20 + (level * 5), 10 + (level * 2), num, 1.5 + (0.1 * level));
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

                        moveVector(0, moveY, 900, 900);
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

                        moveVector(moveX, 0, 900, 900);
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

                        moveVector(0, moveY, 900, 900);
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

                        moveVector(moveX, 0, 900, 900);
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
                        moveVector(moveX, moveY, 900, 900);
                        if (collides(map)) {
                                this.x = originalX;
                                this.y = originalY;
                                syncPosition();

                                moveVector(moveX, 0, 900, 900);
                                if (collides(map)) {
                                        this.x = originalX;
                                        syncPosition();
                                }

                                moveVector(0, moveY, 900, 900);
                                if (collides(map)) {
                                        this.y = originalY;
                                        syncPosition();
                                }
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

	/**
	 * Draws the BasicEnemy
	 */
	@Override
	public void drawCharacter(Graphics2D g, int xOffset, int yOffset) {
		g.setColor(Color.RED); // Make boss visually distinct
		g.fillRect(this.x + xOffset, this.y + yOffset, this.width, this.height);
		g.setColor(Color.BLACK);
		g.drawRect(this.x + xOffset, this.y + yOffset, this.width, this.height);
	}
}