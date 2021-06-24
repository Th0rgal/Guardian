package io.th0rgal.guardian.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.punisher.PunishersManager;
import io.th0rgal.guardian.storage.Database;
import io.th0rgal.guardian.storage.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayersManager implements Listener {

    private final Map<UUID, GuardianPlayer> players = new HashMap<>();
    private final Database database;
    private final PunishersManager punisher;

    public PlayersManager(JavaPlugin plugin, PunishersManager punisher) {
        this.database = new SQLite(plugin, punisher.getPunishers(), "punishers");
        database.load();
        this.punisher = punisher;
        for (Player player : Bukkit.getOnlinePlayers())
            loadPlayer(player);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerPacketsListener(plugin);
        registerPunisherScoreDecrease(plugin);
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
        if (guardianPlayer.isFrozen())
            guardianPlayer.switchFreeze();
        for (String punisher : punisher.getPunishers())
            database.setScore(player.getUniqueId(), punisher, guardianPlayer.getScore(punisher));
        players.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamaged(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player)
            getPlayer((Player) event.getEntity()).setLastHit();
    }

    @EventHandler(ignoreCancelled = true)
    public void onFrozenPlayerMove(final PlayerMoveEvent event) {
        GuardianPlayer player = getPlayer(event.getPlayer());
        if (player.isFrozen() && !event.getTo().toVector().equals(event.getFrom().toVector()))
            event.setCancelled(true);
    }

    private void loadPlayer(Player player) {
        GuardianPlayer guardianPlayer = new GuardianPlayer(player);
        for (String punisher : punisher.getPunishers())
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

    private void registerPunisherScoreDecrease(JavaPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (GuardianPlayer player : players.values())
                for (String punisherName : punisher.getPunishersConfig().keySet())
                    player.addScore(punisherName, -punisher.getPunishersConfig().get(punisherName).getDecrease());
        }, 20 * 60, 20 * 60);
    }
}
