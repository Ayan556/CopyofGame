import java.io.*;
import java.util.*;

/**
 * Utility class for persisting and sorting high scores.
 */
public class HighscoreManager {

    private static final String FILE_NAME = "highscores.txt";

    /** Append a score entry to the high score file and resort. */
    public static void addScore(String username, int score) {
        File file = new File(FILE_NAME);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(username + " - " + score);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortScores();
    }

    /**
     * Sorts the high score file from highest to lowest score.
     */
    public static void sortScores() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lines.sort((a, b) -> {
            int sa = parseScore(a);
            int sb = parseScore(b);
            return Integer.compare(sb, sa); // descending
        });
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int parseScore(String line) {
        int idx = line.lastIndexOf('-');
        if (idx == -1) return 0;
        try {
            return Integer.parseInt(line.substring(idx + 1).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
