package io.th0rgal.guardian;

import org.bukkit.entity.Player;

public class GuardianPlayer {

    private final Player player;

    public GuardianPlayer(Player player) {
        this.player = player;
    }

    public Player toBukkitPlayer() {
        return player;
    }

}
