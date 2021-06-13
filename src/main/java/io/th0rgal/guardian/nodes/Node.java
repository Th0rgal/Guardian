package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.NodeConfig;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Node {

    protected final JavaPlugin plugin;
    protected final String name;
    protected final NodeConfig configuration;

    public Node(JavaPlugin plugin, String name, NodeConfig configuration) {
        this.plugin = plugin;
        this.name = name;
        this.configuration = configuration;
    }

    public abstract void enable();

    public abstract void disable();

    public boolean enable(GuardianPlayer player) {
        return player.enableNode(this.getClass());
    }

    public void disable(GuardianPlayer player) {
        player.disableNode(this.getClass());
    }

    public boolean isDisabled(GuardianPlayer player) {
        return player.isDisabled(this.getClass());
    }
}
