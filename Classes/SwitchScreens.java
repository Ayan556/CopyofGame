import java.awt.*;
import javax.swing.*;

public class SwitchScreens extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Main main;
    private Homepage homepage;
    private DeathScreen deathScreen;
    private UsernameInputScreen userInput;

    public static final String HOMEPAGE = "Homepage";
    public static final String MAIN = "Main";
    public static final String DEATH = "Death";
    public static final String USER_INPUT = "UserInput";

    public SwitchScreens() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Neon Rebellion");
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        homepage = new Homepage(this);
        deathScreen = new DeathScreen(this);

        // Initialize but don't create Main yet
        main = null;

        cardPanel.add(homepage, HOMEPAGE);
        cardPanel.add(deathScreen, DEATH);

        // Set names for components
        homepage.setName(HOMEPAGE);
        deathScreen.setName(DEATH);

        this.add(cardPanel);
        this.pack();
        this.setVisible(true);

        showHomepage();
    }

    public void showUsernameInput() {
        SoundPlayer.playBackground("BackgroundMusic.wav");
        if (userInput == null) {
            userInput = new UsernameInputScreen(name -> {
                if (name == null || name.trim().isEmpty()) {
                    name = "P1"; // Default username
                }

                // Initialize main game only after username is entered
                this.main = new Main();
                this.main.setUsername(name);
                cardPanel.add(main, MAIN);
                cardLayout.show(cardPanel, MAIN);

                // Ensure focus
                SwingUtilities.invokeLater(() -> {
                    this.main.requestFocusInWindow();
                    main.setFocusable(true);
                });
            });

            userInput.setName(USER_INPUT);
            cardPanel.add(userInput, USER_INPUT);
        }

        cardLayout.show(cardPanel, USER_INPUT);
        SwingUtilities.invokeLater(() -> {
            userInput.requestFocusInWindow();
        });
    }

    public void startGame() {
        showUsernameInput(); // Start with username input first
    }

    public void showHomepage() {
        if (main != null) {
            cardPanel.remove(main);
            main = null;
        }

        if (userInput != null) {
            cardPanel.remove(userInput);
            userInput = null;
        }

        cardLayout.show(cardPanel, HOMEPAGE);
        SwingUtilities.invokeLater(() -> {
            SoundPlayer.playBackground("CyberpunkMusic.wav");
            homepage.requestFocusInWindow();
        });
    }

    public void showDeathScreen(String username, int score) {
        if (main != null) {
            cardPanel.remove(main);
            main = null;
        }

        if (userInput != null) {
            cardPanel.remove(userInput);
            userInput = null;
        }

        deathScreen.setResult(username, score);
        cardLayout.show(cardPanel, DEATH);
        SwingUtilities.invokeLater(() -> {
            SoundPlayer.stopBackground();
            SoundPlayer.playSound("GameOver.wav");
            deathScreen.requestFocusInWindow();
        });
    }

    // Modified getComponentByName to work with CardLayout
    private Component getComponentByName(String name) {
        for (Component comp : cardPanel.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }
}