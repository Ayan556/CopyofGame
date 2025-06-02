import java.awt.Rectangle;

public class BouncingBullet extends Bullet {
    public BouncingBullet(int x, int y, int w, int h, int panW, int panH, int direction) {
        super(x, y, w, h, panW, panH, direction);
    }

    public void bounceX() {
        setSpeedX(-getSpeedX());
    }

    public void bounceY() {
        setSpeedY(-getSpeedY());
    }

    public void bounce(Rectangle r) {
        int overlapX = Math.min(this.x + this.width, r.x + r.width) - Math.max(this.x, r.x);
        int overlapY = Math.min(this.y + this.height, r.y + r.height) - Math.max(this.y, r.y);
        if (overlapX <= overlapY) {
            bounceX();
        }
        if (overlapY <= overlapX) {
            bounceY();
        }
    }
}