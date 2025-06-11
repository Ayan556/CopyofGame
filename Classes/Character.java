import java.awt.*;

/**
 * Abstract superclass representing a general character in the game.
 * All playable and non-playable characters (e.g., Player and Enemies) will inherit from this.
 * Encapsulates shared attributes like position, movement, health, and rendering responsibility.
 */
public abstract class Character extends Rectangle {

    // Core character attributes
    protected int health;             // Total health of the character
    protected int shield;             // Shield value that absorbs damage before health
    protected double speed;           // Movement speed (use double for precision)
    protected int directionFacing;    // 1=Left, 2=Right, 3=Up, 4=Down
    protected boolean moving;
    protected int frame;

    // Track precise position to allow smooth fractional movementAdd commentMore actions
    protected double preciseX;
    protected double preciseY;

    /**
     * Constructor to initialize all character fields.
     *
     * @param x       Starting x-coordinate
     * @param y       Starting y-coordinate
     * @param width   Width of the character (used for drawing and collision)
     * @param height  Height of the character
     * @param health  Starting health value
     * @param shield  Starting shield value
     * @param speed   Movement speed (pixels per frame or unit)
     */
    public Character(int x, int y, int width, int height, int health, int shield, double speed) {
        super(x, y, width, height);
        this.health = health;
        this.shield = shield;
        this.speed = speed;
        this.directionFacing = 4; // Default facing downwards

        this.preciseX = x;
        this.preciseY = y;
    }

    //----Image stuff----

    /**
     * Abstract method for drawing the character.
     * Must be implemented in subclasses (e.g., Player, Enemy) using appropriate sprites or shapes.
     *
     * @param g         Graphics2D context used for rendering
     * @param xOffset   Viewport or camera X offset
     * @param yOffset   Viewport or camera Y offset
     */
    public abstract void drawCharacter(Graphics2D g, int xOffset, int yOffset);

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void changeFrame() {
        frame = (frame + 1) % 2;
    }

    /**
     * Moves the character in the specified direction if not blocked by screen bounds.
     * Direction values:
     *   1 = Left, 2 = Right, 3 = Up, 4 = Down
     *
     * The hardcoded `75` margin provides a buffer from the screen edges. Adjust as needed.
     *
     * @param direction Direction to move
     * @param panW      Width of the game panel (screen)
     * @param panH      Height of the game panel
     */
    public void move(int direction, int panW, int panH) {
        this.directionFacing = direction;

        switch (direction) {
            case 1 -> moveVector(-speed, 0, panW, panH);
            case 2 -> moveVector(speed, 0, panW, panH);
            case 3 -> moveVector(0, -speed, panW, panH);
            case 4 -> moveVector(0, speed, panW, panH);
        }
    }

    /**
     * Moves the character by the provided x/y deltas applying bounds checking.
     */
    public void moveVector(double dx, double dy, int panW, int panH) {
        preciseX += dx;
        preciseY += dy;

        if (preciseX < 0) preciseX = 0;
        if (preciseY < 0) preciseY = 0;
        if (preciseX + width > panW) preciseX = panW - width;
        if (preciseY + height > panH) preciseY = panH - height;

        this.x = (int)Math.round(preciseX);
        this.y = (int)Math.round(preciseY);
    }

    /** Synchronize precise coordinates with integer position */
    public void syncPosition() {
        this.preciseX = this.x;
        this.preciseY = this.y;
    }

    /**
     * Reduces the character’s shield first, then health if shield is depleted.
     * Any excess damage beyond the remaining shield is passed to health.
     *
     * @param damage Amount of damage to apply
     */
    public void updateHealth(int damage) {
        if (shield > 0) {
            shield -= damage;
            if (shield < 0) {
                health += shield; // Apply overflow damage to health (shield is negative)
                shield = 0;
            }
        } else {
            health -= damage;
        }
    }

    /**
     * Checks if the character is alive.
     *
     * @return true if health > 0, otherwise false (dead)
     */
    public boolean isAlive() {
        return health > 0;
    }

    // ----- Getters for core attributes -----

    public int getHealth() {
        return health;
    }

    public int getShield() {
        return shield;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDirectionFacing() {
        return directionFacing;
    }
    /**
     * Sets the character's movement speed.
     * @param speed New speed value
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Adds the specified amount to the current speed (can be negative).
     * @param delta Amount to add to speed
     */
    public void addSpeed(double delta) {
        this.speed += delta;
    }

    /**
     * Adds the specified amount to the character's health without
     * exceeding the maximum of 5.
     * @param amount amount of hearts to restore
     */
    public void addHealth(int amount) {
        this.health += amount;
        if (this.health > 5) this.health = 5;
        if (this.health < 0) this.health = 0;
    }

    /**
     * Adds the specified amount to the character's shield without
     * exceeding the maximum of 5.
     * @param amount amount of shields to restore
     */
    public void addShield(int amount) {
        this.shield += amount;
        if (this.shield > 5) this.shield = 5;
        if (this.shield < 0) this.shield = 0;
    }
}