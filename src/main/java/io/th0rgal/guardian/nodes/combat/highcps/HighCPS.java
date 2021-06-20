package io.th0rgal.guardian.nodes.combat.highcps;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.punisher.PunishersManager;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import io.th0rgal.guardian.punisher.SerializedPunisherTrigger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.TomlTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HighCPS extends Node implements Listener {

    private final List<SerializedPunisherTrigger> triggers;

    public HighCPS(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, punishersManager, name, configuration);
        triggers = new ArrayList<>();
        if (configuration.isArray("punishers"))
            for (Object punisherObject : configuration.getArray("punishers").toList()) {
                TomlTable punisherTable = (TomlTable) punisherObject;
                triggers.add(new SerializedPunisherTrigger(punisherTable.getString("punisher"),
                        punisherTable.getBoolean("concurrent"),
                        punisherTable.getDouble("min_cps"),
                        punisherTable.isDouble("add") ? punisherTable.getDouble("add") : 0,
                        punisherTable.isDouble("multiply") ? punisherTable.getDouble("multiply") : 1));
            }
        triggers.sort(Comparator.comparing(SerializedPunisherTrigger::trigger).reversed());
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR)
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
        CPSQueue cpsQueue = (CPSQueue) player.getData(this.getClass());
        if (cpsQueue == null) {
            cpsQueue = new CPSQueue((int) configuration.getLong("historic"));
            player.setData(this.getClass(), cpsQueue);
        }
        cpsQueue.update();
        applySerializedTrigger(player, triggers, cpsQueue.getCPS());
    }
}
