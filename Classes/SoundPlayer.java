import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayer {
    private static Clip backgroundClip;
    // Volume scale between 0.0 (mute) and 1.0 (full volume)
    private static final float DEFAULT_VOLUME = 0.5f;

    /**
     * Adjusts the volume of the provided clip. If the clip does not support
     * MASTER_GAIN control this method does nothing.
     */
    private static void setVolume(Clip clip, float volume) {
        if (clip == null) {
            return;
        }
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (20.0 * Math.log10(Math.max(0.0001f, volume)));
            dB = Math.max(dB, control.getMinimum());
            dB = Math.min(dB, control.getMaximum());
            control.setValue(dB);
        } catch (IllegalArgumentException ignored) {
            // Clip doesn't support volume control
        }
    }

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
            setVolume(backgroundClip, DEFAULT_VOLUME);
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

    /** Pauses the background audio without closing the clip. */
    public static void pauseBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    /** Resumes a previously paused background audio clip. */
    public static void resumeBackground() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
        }
    }

    /**
     * Plays the specified audio file once from the /res/Audio directory and
     * returns the Clip so callers may stop it early if desired.
     *
     * @param filename The audio file name
     * @return the Clip that is playing, or null if an error occurred
     */
    public static Clip playSound(String filename) {
        try (InputStream is = SoundPlayer.class.getResourceAsStream("/Audio/" + filename)) {
            if (is == null) {
                throw new IOException("Audio not found: /Audio/" + filename);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(is);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            setVolume(clip, DEFAULT_VOLUME);
            clip.start();
            return clip;
        } catch (Exception e) {
            System.err.println("Error playing audio: " + filename);
            e.printStackTrace();
        }
        return null;
    }

    /** Stops and closes the provided Clip if it is not null. */
    public static void stopClip(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}