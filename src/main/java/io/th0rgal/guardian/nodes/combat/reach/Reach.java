package io.th0rgal.guardian.nodes.combat.reach;

import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.nodes.Node;
import io.th0rgal.guardian.punishers.SerializedPunisherTrigger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.TomlTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Reach extends Node implements Listener {

    private final List<SerializedPunisherTrigger> triggers;

    public Reach(JavaPlugin plugin, GuardianJournal journal, PlayersManager playersManager,
                 PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, journal, playersManager, punishersManager, name, configuration);
        triggers = new ArrayList<>();
        if (configuration.isArray("punishers"))
            for (Object punisherObject : configuration.getArray("punishers").toList()) {
                TomlTable punisherTable = (TomlTable) punisherObject;
                triggers.add(new SerializedPunisherTrigger(punisherTable.getString("punisher"),
                        punisherTable.getBoolean("concurrent"),
                        punisherTable.getDouble("min_reach"),
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


    @EventHandler
    public void onPlayerDamages(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player) || player.getGameMode() != GameMode.SURVIVAL)
            return;
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        if (!isDisabledFor(guardianPlayer))
            applySerializedTrigger(guardianPlayer, triggers, player.getLocation().distance(event.getEntity().getLocation()), "reach");
    }
}
