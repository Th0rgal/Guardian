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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClickTarget(PlayerInteractAtEntityEvent event) {
        Bukkit.broadcastMessage("RIGHTCLICKED");
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() != Action.PHYSICAL
                && item == null
                || item.getItemMeta() == null)
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
        String type = item.getItemMeta().getPersistentDataContainer().get(inspectMode.key, PersistentDataType.STRING);
        if (type == null || !player.isInspecting())
            return;
        event.setCancelled(true);
        InspectData data = player.getInspectData();

        if (type.equals("teleport")) {

            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            players.remove(player);
            if (players.size() == 0) {
                inspectMode.adventure.player(player.toBukkitPlayer()).sendMessage(inspectMode.language.getRich(Message.PREFIX)
                        .color(Message.NOT_ENOUGH_PLAYERS.color)
                        .append(inspectMode.language.getRich(Message.NOT_ENOUGH_PLAYERS)));
            } else {
                Player target = players.get(random.nextInt(players.size()));
                player.setInspectData(target,
                        target.getLocation(),
                        data.inventory(),
                        data.gameMode(),
                        data.invisible());
                player.toBukkitPlayer().teleport(target.getLocation());
            }
            return;
        }

        GuardianPlayer target = playersManager.getPlayer(data.target());
        apply_effect(type, target);

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