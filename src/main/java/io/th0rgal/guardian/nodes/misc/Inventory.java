package io.th0rgal.guardian.nodes.misc;

import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.punishers.SerializedPunisher;
import io.th0rgal.guardian.storage.config.NodeConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.TomlTable;

public class Inventory extends Node implements Listener {

    private final SerializedPunisher serializedPunisher;
    private final boolean forbidWalking;
    private final boolean forbidSneaking;
    private final long walkDelay;

    public Inventory(JavaPlugin plugin, GuardianJournal journal, PlayersManager playersManager,
                     PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, journal, playersManager, punishersManager, name, configuration);
        TomlTable punisherTable = configuration.getTable("punisher");
        forbidWalking = configuration.getBoolean("forbid_walking");
        forbidSneaking = configuration.getBoolean("forbid_sneaking");
        walkDelay = configuration.getLong("walk_delay");
        serializedPunisher = new SerializedPunisher(punisherTable.getString("punisher"),
                punisherTable.getBoolean("concurrent"),
                punisherTable.isDouble("add") ? punisherTable.getDouble("add") : 0,
                punisherTable.isDouble("multiply") ? punisherTable.getDouble("multiply") : 1);
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        Long lastMove = (Long) guardianPlayer.getData(this.getClass());
        boolean walked = (forbidWalking && lastMove != null && System.currentTimeMillis() - lastMove < walkDelay);
        boolean sneaked = forbidSneaking && player.isSneaking();

        if (walked || sneaked)
            punish(guardianPlayer,
                    serializedPunisher.name(),
                    serializedPunisher.addition(),
                    serializedPunisher.multiply(),
                    String.format("{ walked: %b, sneaked: %b }", walked, sneaked));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        long lastHitDiff = Math.abs(System.currentTimeMillis() - guardianPlayer.getLastHit());
        if ((event.getTo().equals(event.getFrom()))
                || lastHitDiff < 1500L
                || player.getNoDamageTicks() != 0
                || player.getVehicle() != null
                || event.getFrom().getY() < event.getTo().getY())
            return;

        guardianPlayer.setData(this.getClass(), System.currentTimeMillis());
    }
}
