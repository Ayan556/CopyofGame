import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

    private static Font customFont;

    public static Font loadFont(String fontPath)  {
        InputStream fontStream = FontLoader.class.getResourceAsStream("/Fonts/" + fontPath);

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        return customFont;
    }
}