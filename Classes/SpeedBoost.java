import javax.sound.sampled.Clip;

public class SpeedBoost extends PowerUp {
    private double boostAmount;
    private Clip soundClip;

    public SpeedBoost(int duration, double boostAmount) {
        super(duration);
        this.boostAmount = boostAmount;
    }

    @Override
    public void activate(Player player) {
        player.addSpeed(boostAmount);
        soundClip = SoundPlayer.playSound("SpeedBoostSound.wav");
    }

    @Override
    public void deactivate(Player player) {
        player.addSpeed(-boostAmount);
        SoundPlayer.stopClip(soundClip);
        soundClip = null;
    }
}