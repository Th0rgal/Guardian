package io.th0rgal.guardian.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.storage.JournalDatabase;
import io.th0rgal.guardian.storage.PunishersDatabase;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.punishers.PunishersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
    private final PunishersDatabase punishersDatabase;
    private final JournalDatabase journalDatabase;
    private final PunishersManager punisher;
    private final GuardianJournal journal;
    private final BukkitAudiences adventure;
    private final LanguageConfiguration lang;

    public PlayersManager(JavaPlugin plugin, PunishersManager punisher,
                          GuardianJournal journal, BukkitAudiences adventure, LanguageConfiguration lang) {
        this.punishersDatabase = new PunishersDatabase(plugin, punisher.getPunishers(), "punishers");
        punishersDatabase.load();

        this.journalDatabase = new JournalDatabase(plugin, "journal", "node", "punisher");
        journalDatabase.load();

        this.punisher = punisher;
        this.journal = journal;
        this.adventure = adventure;
        this.lang = lang;
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
        if (guardianPlayer.isInspecting())
            guardianPlayer.leaveInspectMode();
        punishersDatabase.setScores(player.getUniqueId(), guardianPlayer.getScores());
        Map<String, Boolean> enabledJournals = new HashMap<>();
        enabledJournals.put("node", journal.isSubscribed(guardianPlayer.asBukkitPlayer(), GuardianJournal.Type.NODE));
        enabledJournals.put("punisher", journal.isSubscribed(guardianPlayer.asBukkitPlayer(), GuardianJournal.Type.PUNISHER));
        journalDatabase.subscribes(player.getUniqueId(), enabledJournals);
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
        GuardianPlayer guardianPlayer = new GuardianPlayer(player, this.adventure.player(player), lang);
        if (journalDatabase.isSubscribed(guardianPlayer.getId(), "node"))
            journal.subscribe(player, GuardianJournal.Type.NODE);
        if (journalDatabase.isSubscribed(guardianPlayer.getId(), "punisher"))
            journal.subscribe(player, GuardianJournal.Type.PUNISHER);
        for (String punisher : punisher.getPunishers())
            guardianPlayer.setScore(punisher, punishersDatabase.getScore(player.getUniqueId(), punisher));
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
