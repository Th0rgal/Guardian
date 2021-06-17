package io.th0rgal.guardian;

import io.th0rgal.guardian.nodes.Node;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class GuardianPlayer {

    private final Player player;
    private final Map<String, Double> scores;
    private final HashSet<Class<? extends Node>> disabledNodes;
    private final Map<Class<? extends Node>, Object> data;
    private long lastPingTime;
    private long ping = -1;

    public GuardianPlayer(Player player) {
        this.player = player;
        this.scores = new HashMap<>();
        this.disabledNodes = new HashSet<>();
        this.data = new HashMap<>();
    }

    public void setScore(String punisher, double score) {
        scores.put(punisher, score);
    }

    public void addScore(String punisher, double scoreAddition) {
        scores.put(punisher, getScore(punisher) + scoreAddition);
    }

    public void multiplyScore(String punisher, double scoreMultiplicator) {
        scores.put(punisher, getScore(punisher) * scoreMultiplicator);
    }

    public double getScore(String punisher) {
        return scores.getOrDefault(punisher, 0D);
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

    public UUID getId() {
        return player.getUniqueId();
    }

    public Object getData(Class<? extends Node> nodeClass) {
        return data.get(nodeClass);
    }

    public void setData(Class<? extends Node> nodeClass, Object nodeData) {
        data.put(nodeClass, nodeData);
    }

    public long getPing() {
        return ping;
    }

    public void updatePingTime() {
        this.lastPingTime = System.currentTimeMillis();
    }

    public void updatePongTime() {
        ping = System.currentTimeMillis() - lastPingTime;
    }

}
