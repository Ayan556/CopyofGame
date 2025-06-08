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
			int ts = map.getTileSize();
                        java.awt.Point start = new java.awt.Point((this.x + this.width / 2) / ts, (this.y + this.height / 2) / ts);
                        java.awt.Point goal = new java.awt.Point((p.x + p.width / 2) / ts, (p.y + p.height / 2) / ts);
                        java.util.ArrayList<java.awt.Point> path = map.findPath(start, goal);
                        if (path.size() > 1) {
                                java.awt.Point next = path.get(1);
                                int targetX = next.x * ts + ts / 2 - this.width / 2;
                                int targetY = next.y * ts + ts / 2 - this.height / 2;

                                double dx = targetX - this.x;
                                double dy = targetY - this.y;
                                double dist = Math.sqrt(dx * dx + dy * dy);
                                moveX = (int)Math.round((dx / dist) * speed);
                                moveY = (int)Math.round((dy / dist) * speed);

                                int originalX = this.x;
                                int originalY = this.y;

                                this.moving = true;
                                this.x += moveX;
                                this.y += moveY;
                                if (collides(map)) {
                                        this.x = originalX;
                                        this.y = originalY;
                                }

                                if (this.x > originalX) {
                                        this.directionFacing = 2;
                                } else if (this.x < originalX) {
                                        this.directionFacing = 1;
                                } else {
                                        if (this.y > originalY) this.directionFacing = 4;
                                        else if (this.y < originalY) this.directionFacing = 3;
                                        else {
                                                this.directionFacing = 4;
                                                setMoving(false);
                                        }
                                }
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
