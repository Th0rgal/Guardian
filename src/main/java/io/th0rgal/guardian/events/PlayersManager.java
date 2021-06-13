package io.th0rgal.guardian.events;

import io.th0rgal.guardian.GuardianPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersManager implements Listener {

    private final Map<UUID, GuardianPlayer> players = new HashMap<>();

    public PlayersManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player player : Bukkit.getOnlinePlayers())
            players.put(player.getUniqueId(), new GuardianPlayer(player));
    }

    public GuardianPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public GuardianPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        players.put(player.getUniqueId(), new GuardianPlayer(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        players.remove(player.getUniqueId());
    }
}
