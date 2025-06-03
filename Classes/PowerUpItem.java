public class PowerUpItem extends java.awt.Rectangle {
    private PowerUp powerUp;
    private java.awt.image.BufferedImage image;
    private java.awt.Color color;

    public PowerUpItem(int x, int y, int size, PowerUp powerUp,
                       java.awt.image.BufferedImage image, java.awt.Color color) {
        super(x, y, size, size);
        this.powerUp = powerUp;
        this.image = image;
        this.color = color;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void draw(java.awt.Graphics2D g2, int xOffset, int yOffset) {
        if (image != null) {
            g2.drawImage(image, this.x + xOffset, this.y + yOffset, this.width, this.height, null);
        } else {
            g2.setColor(color);
            g2.fillRect(this.x + xOffset, this.y + yOffset, this.width, this.height);
        }
    }
}
