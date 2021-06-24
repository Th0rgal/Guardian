package io.th0rgal.guardian;

import io.th0rgal.guardian.commands.InspectData;
import io.th0rgal.guardian.nodes.Node;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    private long lastHit = -1;
    private float walkSpeed;
    private boolean frozen = false;
    private InspectData inspectData;

    public GuardianPlayer(Player player) {
        this.player = player;
        this.scores = new HashMap<>();
        this.disabledNodes = new HashSet<>();
        this.data = new HashMap<>();
    }

    public void setScore(String punisher, double score) {
        scores.put(punisher, Math.max(0, score));
    }

    public void addScore(String punisher, double scoreAddition) {
        setScore(punisher, getScore(punisher) + scoreAddition);
    }

    public void multiplyScore(String punisher, double scoreMultiplicator) {
        setScore(punisher, getScore(punisher) * scoreMultiplicator);
    }

    public double getScore(String punisher) {
        return scores.getOrDefault(punisher, 0D);
    }

    public Map<String, Double> getScores() {
        return scores;
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

    public Player asBukkitPlayer() {
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

    public long getLastHit() {
        return lastHit;
    }

    public void setLastHit() {
        lastHit = System.currentTimeMillis();
    }

    public void switchFreeze() {
        if (frozen) {
            frozen = false;
            player.setWalkSpeed(walkSpeed);
        } else {
            frozen = true;
            walkSpeed = player.getWalkSpeed();
            player.setWalkSpeed(0);
        }
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void kill() {
        player.setHealth(0);
    }

    public void ban() {
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), null, null, null);
    }

    public void setInspectData(Player target, Location location, ItemStack[] inventory, GameMode gameMode, boolean invisible) {
        setInspectData(new InspectData(target, location, inventory, gameMode, invisible));
    }

    public void setInspectData(InspectData inspectData) {
        this.inspectData = inspectData;
    }

    public InspectData getInspectData() {
        return inspectData;
    }

    public void setInspectMode(Player target) {
        setInspectData(target,
                player.getLocation(),
                player.getInventory().getContents(),
                player.getGameMode(),
                player.isInvisible());
        player.setGameMode(GameMode.CREATIVE);
        player.setInvisible(true);
    }

    public void leaveInspectMode() {
        player.teleport(inspectData.location());
        player.getInventory().setContents(inspectData.inventory());
        player.setGameMode(inspectData.gameMode());
        player.setInvisible(inspectData.invisible());
        this.setInspectData(null);
    }

    public boolean isInspecting() {
        return this.inspectData != null;
    }
}
