package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.punishers.SerializedPunisherTrigger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class Node {

    protected final JavaPlugin plugin;
    protected final PlayersManager playersManager;
    protected final PunishersManager punishersManager;
    protected final String name;
    protected final NodeConfig configuration;

    public Node(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig configuration) {
        this.plugin = plugin;
        this.playersManager = playersManager;
        this.punishersManager = punishersManager;
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

    protected void applySerializedTrigger(GuardianPlayer player, List<SerializedPunisherTrigger> triggers, double value) {
        boolean triggered = false;
        for (SerializedPunisherTrigger trigger : triggers) {
            if ((triggered && !trigger.concurrent()) || value < trigger.trigger())
                continue;
            triggered = true;
            punishersManager.add(player, trigger.name(), trigger.addition());
            punishersManager.multiply(player, trigger.name(), trigger.multiply());
        }
    }
}
