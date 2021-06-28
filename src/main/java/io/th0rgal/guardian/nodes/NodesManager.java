package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.nodes.movements.flight.Flight;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.Configuration;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.combat.highcps.HighCPS;
import io.th0rgal.guardian.nodes.combat.reach.Reach;
import io.th0rgal.guardian.nodes.movements.speed.Speed;
import io.th0rgal.guardian.nodes.render.HealthBarNode;
import net.bytebuddy.description.field.FieldList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NodesManager {

    @FunctionalInterface
    interface NodeConstructor {
        Node create(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig config);
    }

    private final JavaPlugin plugin;
    private final Configuration nodesConfiguration;
    private final PlayersManager playersManager;
    private final PunishersManager punishersManager;
    private final List<Node> nodes = new ArrayList<>();

    public NodesManager(JavaPlugin plugin, Configuration nodesConfiguration, PlayersManager playersManager, PunishersManager punishersManager) {
        this.plugin = plugin;
        this.nodesConfiguration = nodesConfiguration;
        this.playersManager = playersManager;
        this.punishersManager = punishersManager;
        registerNode(HighCPS::new, "highcps");
        registerNode(Reach::new, "reach");

        registerNode(Flight::new, "flight");
        registerNode(Speed::new, "speed");

        registerNode(HealthBarNode::new, "healthbar");
    }

    public void registerNode(NodeConstructor nodeCreator, String name) {
        NodeConfig config = new NodeConfig(nodesConfiguration, name);
        if (config.getBoolean("enabled"))
            nodes.add(nodeCreator.create(plugin, playersManager, punishersManager, name, config));
    }

    public void enableAll() {
        for (Node node : nodes)
            node.enable();
    }

}
