package io.th0rgal.guardian.events;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.commands.InspectData;
import io.th0rgal.guardian.commands.InspectMode;
import io.th0rgal.guardian.config.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InspectModeListener implements Listener {

    private final JavaPlugin plugin;
    private final InspectMode inspectMode;
    private final PlayersManager playersManager;
    private final Random random;

    public InspectModeListener(JavaPlugin plugin, InspectMode inspectMode, PlayersManager playersManager) {
        this.plugin = plugin;
        this.inspectMode = inspectMode;
        this.playersManager = playersManager;
        this.random = new Random();
    }

    @EventHandler
    public void onInspectingPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && playersManager.getPlayer(player).isInspecting())
            event.setCancelled(true);
    }

    @EventHandler
    public void onInspectingDropItem(PlayerDropItemEvent event) {
        if (playersManager.getPlayer(event.getPlayer()).isInspecting()) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (item != null && item.hasItemMeta()
                    && item.getItemMeta().getPersistentDataContainer()
                    .get(inspectMode.key, PersistentDataType.STRING) != null)
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClickTarget(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player targetPlayer) {
            Player player = event.getPlayer();
            if (!playersManager.getPlayer(event.getPlayer()).isInspecting())
                return;

            GuardianPlayer target = playersManager.getPlayer(targetPlayer);
            ItemStack item = player.getInventory().getItem(event.getHand());
            if (item == null || item.getItemMeta() == null)
                return;
            String type = item.getItemMeta().getPersistentDataContainer().get(inspectMode.key, PersistentDataType.STRING);
            apply_effect(type, target);

        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta())
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
        String type = item.getItemMeta().getPersistentDataContainer().get(inspectMode.key, PersistentDataType.STRING);
        if (type.equals("teleport") && player.isInspecting()) {
            InspectData data = player.getInspectData();
            event.setCancelled(true);
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            players.remove(player);
            if (players.size() == 0) {
                inspectMode.adventure.player(player.asBukkitPlayer()).sendMessage(inspectMode.language.getRich(Message.PREFIX)
                        .color(Message.NOT_ENOUGH_PLAYERS.color)
                        .append(inspectMode.language.getRich(Message.NOT_ENOUGH_PLAYERS)));
            } else {
                Player target = players.get(random.nextInt(players.size()));
                player.setInspectData(target,
                        target.getLocation(),
                        data.inventory(),
                        data.gameMode(),
                        data.invisible());
                player.asBukkitPlayer().teleport(target.getLocation());
            }
        }
    }

    private void apply_effect(String type, GuardianPlayer target) {
        switch (type) {

            case "freeze":
                target.switchFreeze();
                break;

            case "kill":
                target.kill();
                break;

            case "ban":
                target.ban();
                break;

            case "info":
                target.getScores();
                break;


            default:
        }
    }

}
