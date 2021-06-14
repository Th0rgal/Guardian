package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.PlayersManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Node {

    protected final JavaPlugin plugin;
    protected final PlayersManager playersManager;
    protected final String name;
    protected final NodeConfig configuration;

    public Node(JavaPlugin plugin, PlayersManager playersManager, String name, NodeConfig configuration) {
        this.plugin = plugin;
        this.playersManager = playersManager;
        this.name = name;
        this.configuration = configuration;
    }

    public abstract void enable();

    public abstract void disable();

    public boolean enableFor(GuardianPlayer player) {
        return player.enableNode(this.getClass());
    }

    public void disableFor(GuardianPlayer player) {
        player.disableNode(this.getClass());
    }

    public boolean isDisabledFor(GuardianPlayer player) {
        return player.isDisabled(this.getClass());
    }
}
