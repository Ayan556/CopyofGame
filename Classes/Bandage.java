public class Bandage extends Heal {
    @Override
    public void apply(Player player) {
        player.addHealth(1);
    }
}
