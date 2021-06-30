package io.th0rgal.guardian.nodes;

import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.punishers.SerializedPunisherTrigger;
import io.th0rgal.guardian.storage.config.language.Message;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class Node {

    protected final JavaPlugin plugin;
    protected final GuardianJournal journal;
    protected final PlayersManager playersManager;
    protected final PunishersManager punishersManager;
    protected final String name;
    protected final NodeConfig configuration;

    public Node(JavaPlugin plugin, GuardianJournal journal, PlayersManager playersManager,
                PunishersManager punishersManager, String name, NodeConfig configuration) {
        this.plugin = plugin;
        this.journal = journal;
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

    protected void punish(GuardianPlayer player, String name, double addition, double multiply, String data) {
        journal.log(GuardianJournal.Type.NODE, Message.NODE_LOG, "player",
                player.asBukkitPlayer().getDisplayName(), "node", this.getClass().getSimpleName(), "data", data);
        punishersManager.punish(player, name, addition, multiply);
    }

    protected void applySerializedTrigger(GuardianPlayer player, List<SerializedPunisherTrigger> triggers, double value, String fieldName) {
        boolean triggered = false;
        for (SerializedPunisherTrigger trigger : triggers) {
            if ((triggered && !trigger.concurrent()) || value < trigger.trigger())
                continue;
            triggered = true;
            punish(player, trigger.name(), trigger.addition(), trigger.multiply(), String.format("%s: %.2f", fieldName, value));
        }
    }
}
