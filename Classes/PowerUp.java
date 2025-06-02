public abstract class PowerUp {
    protected int duration;

    public PowerUp(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    /** Activate the power-up on the given player */
    public abstract void activate(Player player);

    /** Revert any changes done by this power-up */
    public abstract void deactivate(Player player);
}