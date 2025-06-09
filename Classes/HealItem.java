public class HealItem extends java.awt.Rectangle {
    private Heal heal;
    private java.awt.image.BufferedImage image;
    private java.awt.Color color;

    public HealItem(int x, int y, int size, Heal heal,
                    java.awt.image.BufferedImage image, java.awt.Color color) {
        super(x, y, size, size);
        this.heal = heal;
        this.image = image;
        this.color = color;
    }

    public Heal getHeal() {
        return heal;
    }

    public java.awt.image.BufferedImage getImage() {
        return image;
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
