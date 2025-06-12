import java.awt.Rectangle;

import java.awt.image.BufferedImage;

public class BouncingBullet extends Bullet {

    // Legacy bullet images used for shotgun power-up
    private static final BufferedImage bulletLeft  = ResourceLoader.loadImage("BulletLeft.png");
    private static final BufferedImage bulletRight = ResourceLoader.loadImage("BulletRight.png");
    private static final BufferedImage bulletUp    = ResourceLoader.loadImage("BulletUp.png");
    private static final BufferedImage bulletDown  = ResourceLoader.loadImage("BulletDown.png");

    public BouncingBullet(int x, int y, int w, int h, int panW, int panH, int direction) {
        super(x, y, w, h, panW, panH, direction);

        // Replace the sprite sheet frame with the original images for shotgun bullets
        switch (direction) {
            case 1 -> bulletImage = bulletLeft;
            case 2 -> bulletImage = bulletRight;
            case 3 -> bulletImage = bulletUp;
            case 4 -> bulletImage = bulletDown;
        }
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