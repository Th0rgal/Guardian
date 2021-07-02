package io.th0rgal.guardian.nodes.render;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.punishers.PunishersManager;
import io.th0rgal.guardian.storage.config.NodeConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class HealthBarNode extends Node implements Listener {

    public HealthBarNode(JavaPlugin plugin, GuardianJournal journal, PlayersManager playersManager,
                         PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, journal, playersManager, punishersManager, name, configuration);
    }

    @Override
    public void enable() {
        registerPackets();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    private void registerPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.isPlayerTemporary())
                            return;
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        Entity entity = null;
                        int entityId = packet.getIntegers().read(0);
                        for (Entity worldEntity : player.getWorld().getLivingEntities())
                            if (worldEntity.getEntityId() == entityId) {
                                entity = worldEntity;
                                break;
                            }
                        if (isDisabledFor(playersManager.getPlayer(player))
                                || player.getEntityId() == entityId
                                || !(entity instanceof LivingEntity)
                                || entity instanceof Wither
                                || entity instanceof EnderDragon
                                || entity.getPassengers().contains(player))
                            return;
                        for (WrappedWatchableObject watch : packet.getWatchableCollectionModifier().read(0))
                            if (watch.getIndex() == 9 && (float) watch.getValue() != 0f){
                                double value = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH)
                                        .getValue();
                                watch.setValue(value);
                            }

                    }
                });
    }

    @EventHandler
    public void onMount(final VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player))
            return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (event.getVehicle().isValid() && event.getEntered().isValid())
                ProtocolLibrary.getProtocolManager().updateEntity(event.getVehicle(), Collections.singletonList((Player) event.getEntered()));
        });
    }
}
