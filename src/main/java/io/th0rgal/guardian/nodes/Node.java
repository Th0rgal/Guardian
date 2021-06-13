package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Node {

    protected final JavaPlugin plugin;
    protected final String name;

    public Node(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract void enable();

    public abstract void disable();

    public abstract void enable(GuardianPlayer player);

    public abstract void disable(GuardianPlayer player);

}
