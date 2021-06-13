package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.exceptions.ExceptionHandler;
import io.th0rgal.guardian.nodes.provided.HealthBarNode;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NodesManager {

    private final JavaPlugin plugin;
    private final Configuration nodesConfiguration;
    private final List<Node> nodes = new ArrayList<>();

    public NodesManager(JavaPlugin plugin, Configuration nodesConfiguration) {
        this.plugin = plugin;
        this.nodesConfiguration = nodesConfiguration;
        registerNode("healthbar", HealthBarNode.class);
    }

    public void registerNode(String name, Class<? extends Node> nodeClass) {
        try {
            nodes.add(nodeClass
                    .getConstructor(JavaPlugin.class, String.class)
                    .newInstance(plugin, name));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException exception) {
            new ExceptionHandler(exception).fire(this.plugin.getLogger());
        }
    }

    public void enableAll() {
        for (Node node : nodes)
            node.enable();
    }

}
