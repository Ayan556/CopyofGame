import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class ResourceLoader {
    /**
     * Loads an image from /res/images/ if res is marked as a Resources Root.
     * @param filename The image file name (e.g., "Character.png")
     * @return The loaded BufferedImage, or null if not found
     */
    public static BufferedImage loadImage(String filename) {
        try (InputStream is = ResourceLoader.class.getResourceAsStream("C:\\Users\\Ayan Talukdar\\OneDrive\\Documents\\CopyofGame\\res\\images\\" + filename)) {
            if (is == null) {
                throw new IOException("Image not found: /images/" + filename);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Error loading image: " + filename);
            e.printStackTrace();
            return null;
        }
    }
}