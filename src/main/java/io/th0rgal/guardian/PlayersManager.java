package io.th0rgal.guardian;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.th0rgal.guardian.storage.Database;
import io.th0rgal.guardian.storage.SQLite;
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
import java.util.Set;
import java.util.UUID;

public class PlayersManager implements Listener {

    private final Map<UUID, GuardianPlayer> players = new HashMap<>();
    private final Database database;
    private final Set<String> punishers;

    public PlayersManager(JavaPlugin plugin, Set<String> punishers) {
        this.database = new SQLite(plugin, punishers, "punishers");
        database.load();
        this.punishers = punishers;
        for (Player player : Bukkit.getOnlinePlayers())
            loadPlayer(player);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerPacketsListener(plugin);
    }

    public GuardianPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public GuardianPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        loadPlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GuardianPlayer guardianPlayer = getPlayer(player);
        for (String punisher : punishers)
            database.setScore(player.getUniqueId(), punisher, guardianPlayer.getScore(punisher));
        players.remove(player.getUniqueId());
    }

    private void loadPlayer(Player player) {
        GuardianPlayer guardianPlayer = new GuardianPlayer(player);
        for (String punisher : punishers)
            guardianPlayer.setScore(punisher, database.getScore(player.getUniqueId(), punisher));
        players.put(player.getUniqueId(), guardianPlayer);
    }

    private void registerPacketsListener(JavaPlugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        getPlayer(event.getPlayer()).updatePingTime();
                    }

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        getPlayer(event.getPlayer()).updatePongTime();
                    }
                });
    }
}
