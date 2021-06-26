package io.th0rgal.guardian.events;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.commands.InfoPlayer;
import io.th0rgal.guardian.commands.InspectData;
import io.th0rgal.guardian.storage.config.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InspectModeListener implements Listener {

    private final PlayersManager playersManager;
    private final NamespacedKey key;
    private final Random random;
    private final InfoPlayer infoPlayer;

    public InspectModeListener(PlayersManager playersManager, NamespacedKey key, InfoPlayer infoPlayer) {
        this.playersManager = playersManager;
        this.key = key;
        this.random = new Random();
        this.infoPlayer = infoPlayer;
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
                    .get(key, PersistentDataType.STRING) != null)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClickTarget(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player targetPlayer) {
            Player player = event.getPlayer();
            if (!playersManager.getPlayer(event.getPlayer()).isInspecting())
                return;

            GuardianPlayer target = playersManager.getPlayer(targetPlayer);
            ItemStack item = player.getInventory().getItem(event.getHand());
            if (item == null || item.getItemMeta() == null)
                return;
            String type = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            apply_effect(type, playersManager.getPlayer(player), target);

        }
    }

    @EventHandler
    public void onLeftClickTarget(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player targetPlayer && event.getDamager() instanceof Player player) {
            if (!playersManager.getPlayer(player).isInspecting())
                return;

            GuardianPlayer target = playersManager.getPlayer(targetPlayer);
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getItemMeta() == null)
                return;
            String type = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            apply_effect(type, playersManager.getPlayer(player), target);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta())
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
        String type = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (type != null && type.equals("teleport") && player.isInspecting()) {
            InspectData data = player.getInspectData();
            event.setCancelled(true);
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            players.remove(player);
            if (players.size() == 0)
                player.message(Message.NOT_ENOUGH_PLAYERS);
            else {
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

    private void apply_effect(String type, GuardianPlayer player, GuardianPlayer target) {
        switch (type) {
            case "freeze" -> {
                target.switchFreeze();
                if (target.isFrozen()) {
                    player.message(Message.PLAYER_FROZEN);
                } else {
                    player.message(Message.PLAYER_UNFROZEN);
                }
            }
            case "kill" -> {
                target.kill();
                player.message(Message.TARGET_KILLED);
            }
            case "ban" -> {
                target.ban();
                player.message(Message.TARGET_BANNED);
            }
            case "info" -> {
                infoPlayer.showMenu(player, target);
                target.getScores();
            }
        }

    }

}
