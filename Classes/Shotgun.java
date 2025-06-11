public class Shotgun extends PowerUp {
    public Shotgun(int duration) {
        super(duration);
    }

    @Override
    public void activate(Player player) {
        player.setShotgun(true);
    }

    @Override
    public void deactivate(Player player) {
        player.setShotgun(false);
    }
}