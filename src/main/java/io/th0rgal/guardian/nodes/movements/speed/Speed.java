package io.th0rgal.guardian.nodes.movements.speed;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.nodes.Node;
import io.th0rgal.guardian.punishers.SerializedPunisher;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.tomlj.TomlTable;

import java.util.*;

public class Speed extends Node implements Listener {

    public final Map<UUID, Long> lastJump = new HashMap<>();
    private final SerializedPunisher serializedPunisher;
    private final boolean rollback;
    private final double tolerance;

    public Speed(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, punishersManager, name, configuration);
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
        Location to = event.getTo().clone();
        Player player = event.getPlayer();
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        World world = player.getWorld();

        Location location = player.getLocation();
        Location blockLocation = new Location(world, location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        Location headLocation = blockLocation.clone().add(0, 2, 0);
        Location above = blockLocation.clone().add(0, 3, 0);
        long lastHitDiff = Math.abs(System.currentTimeMillis() - guardianPlayer.getLastHit());

        if ((event.getTo().getX() == event.getFrom().getX()) && (event.getTo().getZ() == event.getFrom().getZ())
                && (event.getTo().getY() == event.getFrom().getY())
                || lastHitDiff < 1500L
                || player.getNoDamageTicks() != 0
                || player.getVehicle() != null
                || player.getGameMode().equals(GameMode.CREATIVE)
                || player.getAllowFlight()
                || player.isSwimming()
                || player.isGliding()
                || player.isFlying()
                || isDisabledFor(guardianPlayer)) {
            return;
        }

        if (location.getY() != location.getBlockY())
            lastJump.put(player.getUniqueId(), System.currentTimeMillis());

        double maxSpeed = player.getWalkSpeed() * 1.4;
        SpeedData speedData = (SpeedData) guardianPlayer.getData(this.getClass());
        if (speedData == null) {
            speedData = new SpeedData();
            guardianPlayer.setData(this.getClass(), speedData);
        }

        boolean onGround = false;
        if (to.getY() - from.getY() == 0) {
            if (speedData.isOnGround())
                onGround = true;
        } else
            speedData.setLastJump();
        if (!onGround) {
            maxSpeed *= 1.3;
        }
        if (location.getBlock().getType() == Material.DIRT_PATH)
            maxSpeed *= 1.6;

        if (isOnIce(player)) {
            speedData.setOnIce();
            maxSpeed *= 1.875;
        } else if (speedData.wasOnIce())
            maxSpeed *= 1.875;

        if (!isAir(headLocation.getBlock().getType()))
            maxSpeed *= 1.1;

        double speed = to.toVector().subtract(from.toVector()).setY(0).length();
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(player, PotionEffectType.SPEED);
            if (level > 0)
                maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
        }

        maxSpeed = maxSpeed * (1D + ((double) player.getPing()) / 2000D);
        maxSpeed *= tolerance;

        if ((location.getY() != location.getBlockY() || onGround)
                && speed > maxSpeed && player.getFallDistance() < 0.4
                && !blockLocation.getBlock().isLiquid()
                && !nonCubicBlocksNear(location)
                && isAir(above.getBlock().getType())) {
            if (rollback)
                event.setCancelled(true);
            punish(guardianPlayer,
                    serializedPunisher.name(),
                    serializedPunisher.addition(),
                    serializedPunisher.multiply());
        }

    }

    private boolean isOnIce(final Player player) {
        final Location location = player.getLocation();
        location.setY(location.getY() - 1.0);
        if (location.getBlock().getType() == Material.ICE)
            return true;
        location.setY(location.getY() - 1.0);
        return location.getBlock().getType() == Material.ICE;
    }

    private int getPotionEffectLevel(Player player, PotionEffectType potionEffectType) {
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            if (potionEffect.getType() == potionEffectType)
                return potionEffect.getAmplifier() + 1;
        return 0;
    }

    private boolean nonCubicBlocksNear(Location location) {

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++) {
                    Block block = location.getBlock().getRelative(x, y, z);
                    if (block.getBlockData() instanceof Slab
                            || block.getBlockData() instanceof Stairs
                            || block.getBlockData() instanceof Bed
                            || block.getBlockData() instanceof Fence
                            || block.getBlockData() instanceof TrapDoor
                            || block.getBlockData() instanceof Farmland
                            || block.getBlockData() instanceof SeaPickle)
                        return true;
                }
        return false;
    }

    private boolean isAir(Material material) {
        List<Material> airs = Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
        return airs.contains(material);
    }

}
