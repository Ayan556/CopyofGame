import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayer {
    private static Clip backgroundClip;

    /**
     * Plays the specified audio file on loop from the /res/Audio directory.
     * @param filename The audio file name (e.g., "BackgroundMusic.mp3")
     */
    public static void playBackground(String filename) {
        stopBackground();
        try (InputStream is = SoundPlayer.class.getResourceAsStream("/Audio/" + filename)) {
            if (is == null) {
                throw new IOException("Audio not found: /Audio/" + filename);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error playing background audio: " + filename);
            e.printStackTrace();
        }
    }

    /** Stops the currently playing background audio if any. */
    public static void stopBackground() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }

    /**
     * Plays the specified audio file once from the /res/Audio directory.
     * @param filename The audio file name
     */
    public static void playSound(String filename) {
        try (InputStream is = SoundPlayer.class.getResourceAsStream("/Audio/" + filename)) {
            if (is == null) {
                throw new IOException("Audio not found: /Audio/" + filename);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing audio: " + filename);
            e.printStackTrace();
        }
    }
}