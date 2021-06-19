package io.th0rgal.guardian.nodes.movements.speed;

public class SpeedData {

    private long lastJump;
    private long lastIceWalk;

    public SpeedData() {
        lastJump = System.currentTimeMillis();
        lastIceWalk = 0;
    }

    public boolean isOnGround() {
        return System.currentTimeMillis() - lastJump > 750;
    }

    public void setLastJump() {
        lastJump = System.currentTimeMillis();
    }

    public boolean wasOnIce() {
        return System.currentTimeMillis() - lastIceWalk < 1000;
    }

    public void setOnIce() {
        lastIceWalk = System.currentTimeMillis();
    }

}
