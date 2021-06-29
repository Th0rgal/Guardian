package io.th0rgal.guardian.nodes.movements.flight;

import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.punishers.SerializedPunisher;
import io.th0rgal.guardian.storage.config.NodeConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.tomlj.TomlTable;

import java.util.*;

public class Flight extends Node implements Listener {

    public final Map<UUID, Long> lastJump = new HashMap<>();
    private final SerializedPunisher serializedPunisher;
    private final boolean rollback;
    private final double tolerance;

    public Flight(JavaPlugin plugin, GuardianJournal journal, PlayersManager playersManager,
                  PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, journal, playersManager, punishersManager, name, configuration);
        TomlTable punisherTable = configuration.getTable("punisher");
        rollback = configuration.getBoolean("rollback");
        tolerance = configuration.getDouble("tolerance");
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

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        Location from = event.getFrom().clone();
        Player player = event.getPlayer();
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        Location location = player.getLocation();
        Location blockLocation = new Location(player.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        Location supportLocation = blockLocation.clone().add(0, -1, 0);
        long lastHitDiff = Math.abs(System.currentTimeMillis() - guardianPlayer.getLastHit());

        if ((event.getTo().equals(event.getFrom()))
                || lastHitDiff < 1500L
                || player.getNoDamageTicks() != 0
                || player.getVehicle() != null
                || player.getGameMode().equals(GameMode.CREATIVE)
                || player.getAllowFlight()
                || player.isSwimming()
                || player.isGliding()
                || player.isFlying()
                || player.hasPotionEffect(PotionEffectType.SPEED)
                || player.hasPotionEffect(PotionEffectType.JUMP)
                || player.hasPotionEffect(PotionEffectType.LEVITATION)
                || isDisabledFor(guardianPlayer)) {
            return;
        }

        if (isAir(blockLocation.getBlock().getType()) && isAir(supportLocation.getBlock().getType())
                && isAir(from.clone().add(0, -1, 0).getBlock().getType())) {
            Vector move = event.getTo().toVector().subtract(from.toVector());
            double ySpeed = move.clone().setX(0).setZ(0).length();
            double speed = move.length();
            double currentTolerance = tolerance * (1D + ((double) player.getPing()) / 4000D);
            if (((move.getY() > 0 && !nonAirNear(from.subtract(move.clone().multiply(8)))
                    && (ySpeed > 0.17 || (ySpeed > 0.0835 * currentTolerance))))
                    || (move.getY() == 0 && speed > 0.41 * currentTolerance)) {
                if (rollback)
                    event.setCancelled(true);
                punish(guardianPlayer,
                        serializedPunisher.name(),
                        serializedPunisher.addition(),
                        serializedPunisher.multiply(),
                        String.format("{ ySpeed: %.2f, speed: %.2f }", speed, ySpeed));
            }
        }
    }

    private boolean isAir(Material material) {
        List<Material> airs = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
        return airs.contains(material);
    }

    private boolean nonAirNear(Location location) {
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    Block block = location.getBlock().getRelative(x, y, z);
                    if (!isAir(block.getType()))
                        return true;
                }
        return false;
    }

}
