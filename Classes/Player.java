import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the main player controlled by the user.
 * Extends the abstract Character class and handles player-specific rendering and shooting.
 */

public class Player extends Character {

    private int panelWidth, panelHeight;  // Dimensions of the game panel

    // Character sprite images for different directions
    private int frame;
    private int spriteRowNum = 0;
    private int spriteW = 75, spriteH = 75;
    private boolean moving;
    private boolean shotgun;
    private Queue<PowerUp> powerUps = new LinkedList<>();
    private ArrayList<ActivePowerUp> activePowerUps = new ArrayList<>();
    BufferedImage spriteSheet = ResourceLoader.loadImage("PlayerSprite.png");
    BufferedImage idleSpriteSheet = ResourceLoader.loadImage("IdleSprite.png");

    /**
     * Constructs a Player object with defined attributes and starting position.
     *
     * @param x      Initial x position
     * @param y      Initial y position
     * @param width  Width of the player
     * @param height Height of the player
     * @param speed  Movement speed
     * @param panW   Panel width (for bounds or bullet limits)
     * @param panH   Panel height (for bounds or bullet limits)
     */
    public Player(int x, int y, int width, int height, double speed, int panW, int panH) {
        super(x, y, width, height, 5, 5, speed);  // Pass health=5, shield=5 to super
        this.panelWidth = panW;
        this.panelHeight = panH;
        this.moving = false;
    }

    public void changeFrame() {
        frame = (frame + 1) % 2;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    /**
     * Draws the player image corresponding to their current facing direction.
     *
     * @param g2       Graphics2D object for drawing
     * @param xOffset  X offset for camera or view translation
     * @param yOffset  Y offset for camera or view translation
     */
    @Override
    public void drawCharacter(Graphics2D g2, int xOffset, int yOffset) {
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
            g2.drawImage(idleSpriteSheet, this.x + xOffset, this.y + yOffset,
                    this.x + xOffset + spriteW, this.y + yOffset + spriteH,
                    frame * spriteW, spriteRowNum * spriteH,
                    (frame + 1) * spriteW, (spriteRowNum + 1) * spriteH, null);
        } else {
            g2.drawImage(spriteSheet, this.x + xOffset, this.y + yOffset,
                    this.x + xOffset + spriteW, this.y + yOffset + spriteH,
                    frame * spriteW, spriteRowNum * spriteH,
                    (frame + 1) * spriteW, (spriteRowNum + 1) * spriteH, null);
        }

    }

    /**
     * Creates bullets based on the current weapon mode. If shotgun mode is
     * active, multiple bouncing bullets are created with slight angle offsets.
     * Otherwise a single regular bullet is returned.
     */
    public ArrayList<Bullet> shoot() {
        ArrayList<Bullet> list = new ArrayList<>();

        int bulletW = 20;
        int bulletH = 20;

        // Center the bullet on the player's current position
        int bulletX = this.x + (this.width - bulletW) / 2;
        int bulletY = this.y + (this.height - bulletH) / 2;

        if (shotgun) {
            double baseAngle = 0;
            switch (this.directionFacing) {
                case 1 -> baseAngle = Math.PI;
                case 2 -> baseAngle = 0;
                case 3 -> baseAngle = -Math.PI / 2;
                case 4 -> baseAngle = Math.PI / 2;
            }

            int[] offsets = {-20, -10, 0, 10, 20};
            for (int off : offsets) {
                double angle = baseAngle + Math.toRadians(off);
                BouncingBullet b = new BouncingBullet(bulletX, bulletY, bulletW, bulletH,
                        panelWidth, panelHeight, this.directionFacing);
                int vx = (int)Math.round(Math.cos(angle) * 8);
                int vy = (int)Math.round(Math.sin(angle) * 8);
                b.setSpeedX(vx);
                b.setSpeedY(vy);
                list.add(b);
            }
        } else {
            list.add(new Bullet(bulletX, bulletY, bulletW, bulletH, panelWidth, panelHeight, directionFacing));
        }

        return list;
    }

    /** Adds a power-up to the player's inventory */
    public void addPowerUp(PowerUp p) {
        powerUps.offer(p);
    }

    /** Activates the next power-up in FIFO order */
    public void usePowerUp() {
        PowerUp p = powerUps.poll();
        if (p != null) {
            p.activate(this);
            activePowerUps.add(new ActivePowerUp(p));
        }
    }

    /** Updates active power-ups and handles expiration */
    public void updatePowerUps() {
        for (int i = activePowerUps.size() - 1; i >= 0; i--) {
            ActivePowerUp ap = activePowerUps.get(i);
            ap.duration--;
            if (ap.duration <= 0) {
                ap.powerUp.deactivate(this);
                activePowerUps.remove(i);
            }
        }
    }

    public void setShotgun(boolean active) {
        this.shotgun = active;
    }

    public boolean isShotgun() {
        return shotgun;
    }

    private static class ActivePowerUp {
        PowerUp powerUp;
        int duration;
        ActivePowerUp(PowerUp p) {
            powerUp = p;
            duration = p.getDuration();
        }
    }
}