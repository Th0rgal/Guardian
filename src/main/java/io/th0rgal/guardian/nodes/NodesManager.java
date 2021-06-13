package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.render.HealthBarNode;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NodesManager {

    @FunctionalInterface
    interface NodeConstructor {
        Node create(JavaPlugin plugin, PlayersManager playersManager, String name, NodeConfig config);
    }

    private final JavaPlugin plugin;
    private final Configuration nodesConfiguration;
    private final PlayersManager playersManager;
    private final List<Node> nodes = new ArrayList<>();

    public NodesManager(JavaPlugin plugin, Configuration nodesConfiguration, PlayersManager playersManager) {
        this.plugin = plugin;
        this.nodesConfiguration = nodesConfiguration;
        this.playersManager = playersManager;
        registerNode(HealthBarNode::new, "healthbar");
    }

    public void registerNode(NodeConstructor nodeCreator, String name) {
        NodeConfig config = new NodeConfig(nodesConfiguration, name);
        if (config.getBoolean("enabled"))
            nodes.add(nodeCreator.create(plugin, playersManager, name, config));
    }

    public void enableAll() {
        for (Node node : nodes)
            node.enable();
    }

}
