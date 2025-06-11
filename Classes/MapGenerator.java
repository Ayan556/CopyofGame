import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

/**
 * The MapGenerator class is responsible for generating the level's grid layout,
 * placing the player in the center, and randomly placing obstacles while ensuring
 * enemy entrances and player paths remain accessible.
 */
public class MapGenerator {
    private final int rows, cols;                  // Number of tile rows and columns
    private final int tileSize;                    // Width/height of each tile
    private int level;                       // Current level (affects obstacle count)
    private final Rectangle playerSpawn;           // Player spawn location (center tile)
    private final ArrayList<Rectangle> obstacles;  // List of randomly generated obstacle tiles
    private final ArrayList<Rectangle> entrances;  // Fixed entrance tiles for enemy spawning
    private final ArrayList<Rectangle> walls;

    /**
     * Constructor initializes map properties and generates entrances + obstacles.
     * @param rows     Number of rows in the grid.
     * @param cols     Number of columns in the grid.
     * @param tileSize Size of each tile in pixels.
     * @param level    Current level number (affects difficulty).
     */
    public MapGenerator(int rows, int cols, int tileSize, int level) {
        this.rows = rows;
        this.cols = cols;
        this.tileSize = tileSize;
        this.level = level;
        this.obstacles = new ArrayList<>();
        this.entrances = new ArrayList<>();

        this.walls = new ArrayList<Rectangle>();
        walls.add(new Rectangle(0, 0, 375, 75));
        walls.add(new Rectangle(0, 0, 75, 375));
        walls.add(new Rectangle(525, 0, 375, 75));
        walls.add(new Rectangle(825, 0, 75, 375));
        walls.add(new Rectangle(0, 525, 75, 375));
        walls.add(new Rectangle(0, 825, 375, 75));
        walls.add(new Rectangle(525, 825, 375, 75));
        walls.add(new Rectangle(825, 525, 75, 375));

        // Calculate player spawn location (center of the grid)
        int centerX = cols / 2 * tileSize;
        int centerY = rows / 2 * tileSize;
        playerSpawn = new Rectangle(centerX, centerY, tileSize, tileSize);

        // Generate entrances and obstacles on map
        generateEntrances();
        generateObstacles();
    }

    /**
     * Adds 4 entrance rectangles to the center of each map edge (top, bottom, left, right).
     */
    private void generateEntrances() {
        // Top center entrance
        entrances.add(new Rectangle(((cols / 2)-1) * tileSize, 0, 2* tileSize, tileSize));
        // Bottom center entrance
        entrances.add(new Rectangle(((cols / 2)-1) * tileSize, (rows - 1) * tileSize, 2*tileSize, tileSize));
        // Left center entrance
        entrances.add(new Rectangle(0, ((rows / 2)-1) * tileSize, tileSize, 2*tileSize));
        // Right center entrance
        entrances.add(new Rectangle((cols - 1) * tileSize, ((rows / 2)-1) * tileSize, tileSize, 2*tileSize));
    }

    /**
     * Randomly generates obstacles on the map.
     * Ensures no overlap with player spawn or entrance tiles.
     * Obstacle count scales with level (difficulty).
     */
    public void generateObstacles() {
        Random rand = new Random();
        int maxObstacles = 5 * ((level % 5) + 1); // Increase obstacle count based on level

        // Determine the 2x2 center tile indices so we can block obstacle
        // placement in this area. The player spawns in the centre of these
        // tiles, so keeping them clear prevents overlap with obstacles.
        int centerRow1 = rows / 2 - 1;
        int centerRow2 = rows / 2;
        int centerCol1 = cols / 2 - 1;
        int centerCol2 = cols / 2;

        // Try placing obstacles until desired number is reached
        while (obstacles.size() < maxObstacles) {
            // Choose random tile coordinates
            int r = rand.nextInt(rows - 2) + 1;  // Range: 1 to rows-2
            int c = rand.nextInt(cols - 2) + 1;  // Range: 1 to cols-2

            // Create a Rectangle representing this tile
            Rectangle rect = new Rectangle(c * tileSize, r * tileSize, tileSize, tileSize);

            // Skip if this tile would overlap the player spawn or the other
            // central tiles surrounding the spawn location
            boolean isCenterTile = ((r == centerRow1 || r == centerRow2) && (c == centerCol1 || c == centerCol2));
            if (rect.intersects(playerSpawn) || isCenterTile) continue;

            // Skip if this tile would overlap or block an entrance
            boolean isEntranceOverlap = entrances.stream().anyMatch(e -> e.intersects(rect));
            if (isEntranceOverlap || isNearEntrance(rect)) continue;

            // Skip if this tile would overlap any existing obstacle
            boolean isObstacleOverlap = obstacles.stream().anyMatch(o -> o.intersects(rect));
            if (isObstacleOverlap) continue;

            // Safe to place obstacle
            obstacles.add(new Rectangle(rect.x, rect.y, tileSize, tileSize));
        }
    }

    private boolean isNearEntrance(Rectangle tile) {
        int buffer = tileSize; // 1-tile margin around entrances

        for (Rectangle entrance : entrances) {
            Rectangle extended = new Rectangle(
                    entrance.x - buffer,
                    entrance.y - buffer,
                    entrance.width + 2 * buffer,
                    entrance.height + 2 * buffer
            );
            if (extended.intersects(tile)) return true;
        }
        return false;
    }

    public ArrayList<Rectangle> getWalls() {
        return walls;
    }

    public Rectangle getClearSpawnPoint(int entranceIndex, int enemySize) {
        Rectangle entrance = entrances.get(entranceIndex);

        int spawnX = entrance.x;
        int spawnY = entrance.y;

        switch (entranceIndex) {
            case 0 -> { // Top entrance
                spawnY = entrance.y - enemySize;
                spawnX = entrance.x + (entrance.width - enemySize) / 2;
            }
            case 1 -> { // Bottom entrance
                spawnY = entrance.y + entrance.height;
                spawnX = entrance.x + (entrance.width - enemySize) / 2;
            }
            case 2 -> { // Left entrance
                spawnX = entrance.x - enemySize;
                spawnY = entrance.y + (entrance.height - enemySize) / 2;
            }
            case 3 -> { // Right entrance
                spawnX = entrance.x + entrance.width;
                spawnY = entrance.y + (entrance.height - enemySize) / 2;
            }
        }

        return new Rectangle(spawnX, spawnY, enemySize, enemySize);
    }

    /**
     * Blocks player movement when intersecting with obstacle tiles.
     * @param p         The player object
     * @param direction The direction the player is moving in
     */
    public void blockPlayer(Player p, int direction) {

        for (Rectangle wall : walls) {
            this.block(p, wall);
        }

        for (Rectangle tile : obstacles) {
            if (isEntrance(tile)) continue;  // Skip blocking if tile is an entrance
            this.block(p, tile);
        }
    }

    private void block(Player p, Rectangle obstacle) {
        if (!p.intersects(obstacle)) return;

        int overlapX = Math.min(p.x + p.width, obstacle.x + obstacle.width) - Math.max(p.x, obstacle.x);
        int overlapY = Math.min(p.y + p.height, obstacle.y + obstacle.height) - Math.max(p.y, obstacle.y);

        if (overlapX < overlapY) {
            // Horizontal collision
            if (p.x < obstacle.x) {
                p.x -= overlapX;
            } else {
                p.x += overlapX;
            }
        } else {
            // Vertical collision
            if (p.y < obstacle.y) {
                p.y -= overlapY;
            } else {
                p.y += overlapY;
            }
        }
    }

    /**
     * Checks if a given tile overlaps any of the entrances.
     * @param tile A rectangle tile to check.
     * @return True if it intersects with any entrance, false otherwise.
     */
    public boolean isEntrance(Rectangle tile) {
        return entrances.stream().anyMatch(e -> e.intersects(tile));
    }

    /** @return The player's spawn rectangle */
    public Rectangle getPlayerSpawn() {
        return playerSpawn;
    }

    /** @return List of all obstacle tiles */
    public ArrayList<Rectangle> getObstacles() {
        return obstacles;
    }

    /** @return List of all entrance rectangles */
    public ArrayList<Rectangle> getEntrances() {
        return entrances;
    }

    public void updateLevel(int lvl) {
        this.level = lvl;
    }

    /** Returns the tile size in pixels */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Returns a list of rectangles representing walkable tiles (no obstacles,
     * walls, or entrances).
     */
    public ArrayList<Rectangle> getWalkableTiles() {
        ArrayList<Rectangle> tiles = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle tile = new Rectangle(c * tileSize, r * tileSize, tileSize, tileSize);

                boolean blocked = false;

                if (tile.intersects(playerSpawn)) blocked = true;

                if (!blocked) {
                    for (Rectangle o : obstacles) {
                        if (o.intersects(tile)) {
                            blocked = true;
                            break;
                        }
                    }
                }

                if (!blocked) {
                    for (Rectangle e : entrances) {
                        if (e.intersects(tile)) {
                            blocked = true;
                            break;
                        }
                    }
                }

                if (!blocked) {
                    for (Rectangle w : walls) {
                        if (w.intersects(tile)) {
                            blocked = true;
                            break;
                        }
                    }
                }

                if (!blocked) tiles.add(tile);
            }
        }
        return tiles;
    }
}