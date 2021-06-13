package io.th0rgal.guardian;

import io.th0rgal.guardian.nodes.Node;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GuardianPlayer {

    private final Player player;
    private final HashSet<Class<? extends Node>> disabledNodes;
    private final Map<Class<? extends Node>, Record> data;

    public GuardianPlayer(Player player) {
        this.player = player;
        this.disabledNodes = new HashSet<>();
        this.data = new HashMap<>();
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

    public Record getData(Class<? extends Node> nodeClass) {
        return data.get(nodeClass);
    }

    public Record setData(Class<? extends Node> nodeClass) {
        return data.get(nodeClass);
    }

}
