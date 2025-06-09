public class ShieldPotion extends Heal {
    @Override
    public void apply(Player player) {
        player.addShield(1);
    }
}
