package io.th0rgal.guardian;

import io.th0rgal.guardian.nodes.Node;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class GuardianPlayer {

    private final Player player;
    private final HashSet<Class<? extends Node>> disabledNodes;

    public GuardianPlayer(Player player) {
        this.player = player;
        this.disabledNodes = new HashSet<>();
    }

    public void disableNode(Class<? extends Node> nodeClass) {
        disabledNodes.add(nodeClass);
    }

    public boolean enableNode(Class<? extends Node> nodeClass) {
        return disabledNodes.remove(nodeClass);
    }

    public boolean isDisabled(Class<? extends Node> nodeClass) {
        return disabledNodes.remove(nodeClass);
    }

    public Player toBukkitPlayer() {
        return player;
    }

}
