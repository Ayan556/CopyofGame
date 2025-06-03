import java.awt.*;
import java.util.ArrayList;

/**
 * Abstract superclass representing a general enemy in the game.
 * All enemy types (e.g., FastEnemy, TankEnemy, BossEnemy) should extend this class.
 * Encapsulates shared enemy attributes and basic logic.
 */
public abstract class Enemy extends Character {

    protected int health;         // Enemy's current health
    protected double speed;       // Movement speed
    private int num;

    /**
     * Constructs an enemy with given position, size, health, and speed.
     *
     * @param x       Starting x-coordinate
     * @param y       Starting y-coordinate
     * @param width   Width of the enemy (used for drawing/collision)
     * @param height  Height of the enemy
     * @param health  Starting health
     * @param speed   Movement speed
     * @param num		number that represents this enemy object
     */
    public Enemy(int x, int y, int width, int height, int health, int shield, int num, double speed) {
        super(x, y, width, height, health, shield, speed);  // Pass health=5, shield=5 to super
        this.health = health;
        this.speed = speed;
        this.num = num;

        // NOTE: 'shield' is included in constructor parameters, but unused.
        // If you want enemies to have shields (like the player), consider storing and using it.
    }

    /**
     * Move towards the Player
     */
    public abstract void moveTowardPlayer(Player player, MapGenerator map, ArrayList<Enemy> others);

    /**
     * Reduces the enemy's health by the damage value.
     * @param damage Amount of damage taken
     */
    public void updateHealth(int damage) {
        health -= damage;
    }

    /**
     * Checks if the enemy is still alive.
     *
     * @return true if health > 0, otherwise false
     */
    public boolean isAlive() {
        return health > 0;
    }

    // --- Getters for common enemy attributes ---

    public int getHealth() {
        return health;
    }

    public double getSpeed() {
        return speed;
    }

    public int getNum() {
        return num;
    }
}