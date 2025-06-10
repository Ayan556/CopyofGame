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

    public java.awt.image.BufferedImage getImage() {
        return image;
    }

    public void draw(java.awt.Graphics2D g2, int xOffset, int yOffset) {
        if (image != null) {
            int drawW = this.width;
            int drawH = (int) (drawW * image.getHeight() / (double) image.getWidth());
            if (drawH > this.height) {
                drawH = this.height;
                drawW = (int) (drawH * image.getWidth() / (double) image.getHeight());
            }
            int drawX = this.x + xOffset + (this.width - drawW) / 2;
            int drawY = this.y + yOffset + (this.height - drawH) / 2;
            g2.drawImage(image, drawX, drawY, drawW, drawH, null);
        } else {
            g2.setColor(color);
            g2.fillRect(this.x + xOffset, this.y + yOffset, this.width, this.height);
        }
    }
}
