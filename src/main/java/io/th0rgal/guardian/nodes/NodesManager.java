package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.exceptions.ExceptionHandler;
import io.th0rgal.guardian.nodes.render.HealthBarNode;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class NodesManager {

    private final JavaPlugin plugin;
    private final Configuration nodesConfiguration;
    private final PlayersManager playersManager;
    private final List<Node> nodes = new ArrayList<>();

    public NodesManager(JavaPlugin plugin, Configuration nodesConfiguration, PlayersManager playersManager) {
        this.plugin = plugin;
        this.nodesConfiguration = nodesConfiguration;
        this.playersManager = playersManager;
        registerNode(HealthBarNode.class, "healthbar");
    }

    public void registerNode(Class<? extends Node> nodeClass, String name) {
        try {
            NodeConfig nodeConfig = new NodeConfig(nodesConfiguration, name);
            if (nodeConfig.getBoolean("enabled"))
                nodes.add(nodeClass
                        .getConstructor(JavaPlugin.class, PlayersManager.class, String.class, NodeConfig.class)
                        .newInstance(plugin, playersManager, name, nodeConfig));
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
