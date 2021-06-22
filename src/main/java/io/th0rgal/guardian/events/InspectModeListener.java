package io.th0rgal.guardian.events;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.commands.InspectMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class InspectModeListener implements Listener {

    private final JavaPlugin plugin;
    private final InspectMode inspectMode;
    private final PlayersManager playersManager;

    public InspectModeListener(JavaPlugin plugin, InspectMode inspectMode, PlayersManager playersManager) {
        this.plugin = plugin;
        this.inspectMode = inspectMode;
        this.playersManager = playersManager;
    }

    @EventHandler
    public void onRightClickTarget(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (!inspectMode.map.containsKey(player.getUniqueId()))
            return;
        if (event.getRightClicked() instanceof Player targetPlayer) {
            GuardianPlayer target = playersManager.getPlayer(targetPlayer);
            ItemStack item = player.getInventory().getItem(event.getHand());
            if (item == null || item.getItemMeta() == null)
                return;
            String type = item.getItemMeta().getPersistentDataContainer().get(inspectMode.key, PersistentDataType.STRING);
            switch (type) {

                case "freeze":
                    target.freeze();
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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if ((event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_AIR)
                || !inspectMode.map.containsKey(player.getUniqueId())
                || item == null
                || item.getItemMeta() == null)
            return;
        String type = item.getItemMeta().getPersistentDataContainer().get(inspectMode.key, PersistentDataType.STRING);

    }


    @EventHandler
    public void onCreativeInventoryClick(InventoryCreativeEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null
                && item.getItemMeta() != null
                && event.getSlot() < 5
                && inspectMode.map.containsKey(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
