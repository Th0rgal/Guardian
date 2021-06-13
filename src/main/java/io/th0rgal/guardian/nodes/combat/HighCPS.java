package io.th0rgal.guardian.nodes.combat;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HighCPS extends Node implements Listener {

    public HighCPS(JavaPlugin plugin, PlayersManager playersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, name, configuration);
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
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
    }
}
