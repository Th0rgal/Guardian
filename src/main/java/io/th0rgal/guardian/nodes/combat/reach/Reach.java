package io.th0rgal.guardian.nodes.combat.reach;

import io.th0rgal.guardian.PlayersManager;
import io.th0rgal.guardian.PunishersManager;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.nodes.Node;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Reach extends Node implements Listener {
    public Reach(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, punishersManager, name, configuration);
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

        double distance = player.getLocation().distance(event.getEntity().getLocation());
        if (distance > 4.4D)
            plugin.getLogger().warning("reach: " + distance);
    }
}
