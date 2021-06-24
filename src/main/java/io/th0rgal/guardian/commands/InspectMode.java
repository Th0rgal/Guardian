package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.Permission;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.language.Message;
import io.th0rgal.guardian.events.InspectModeListener;
import io.th0rgal.guardian.events.PlayersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class InspectMode {

    private final JavaPlugin plugin;
    public final BukkitAudiences adventure;
    public final LanguageConfiguration language;
    private final PlayersManager playersManager;
    public final NamespacedKey key;

    private final LegacyComponentSerializer displayNameSerializer;
    private final ItemStack[] inspectInventory;

    public InspectMode(JavaPlugin plugin, BukkitAudiences adventure, LanguageConfiguration language, PlayersManager playersManager) {
        this.plugin = plugin;
        this.adventure = adventure;
        this.language = language;
        this.playersManager = playersManager;
        this.key = new NamespacedKey(plugin, "data");
        this.displayNameSerializer = LegacyComponentSerializer.builder()
                .character('§')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        this.inspectInventory = new ItemStack[]{
                getItem(Material.ENDER_EYE, Message.TELEPORT_ITEM_NAME, "teleport"),
                getItem(Material.TRIDENT, Message.FREEZE_ITEM_NAME, "freeze"),
                getItem(Material.NETHERITE_SWORD, Message.KILL_ITEM_NAME, "kill"),
                getItem(Material.NETHERITE_AXE, Message.BAN_ITEM_NAME, "ban"),
                getItem(Material.GLOBE_BANNER_PATTERN, Message.INFO_ITEM_NAME, "info")
        };
        Bukkit.getPluginManager().registerEvents(
                new InspectModeListener(plugin, this, playersManager),
                plugin);
    }

    private ItemStack getItem(Material material, Message name, String data) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.displayNameSerializer.serialize(language.getRich(name)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
        item.setItemMeta(meta);
        return item;
    }

    public CommandAPICommand getInspectPlayerCommand() {
        return new CommandAPICommand("inspect")
                .withArguments(new PlayerArgument("player"))
                .withPermission(Permission.USE_COMMAND_INSPECT.toString())
                .executes((sender, args) -> {
                    if (!(sender instanceof Player admin)) {
                        this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                                .color(Message.NOT_A_PLAYER.color)
                                .append(language.getRich(Message.NOT_A_PLAYER)));
                        return;
                    }

                    GuardianPlayer target = playersManager.getPlayer((Player) args[0]);
                    if (!target.isInspecting())
                        changeInspectMode(admin, target.asBukkitPlayer());
                    admin.teleport(target.asBukkitPlayer());
                });
    }

    public CommandAPICommand getInspectCommand() {
        return new CommandAPICommand("inspect")
                .withPermission(Permission.USE_COMMAND_INSPECT.toString())
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                                .color(Message.NOT_A_PLAYER.color)
                                .append(language.getRich(Message.NOT_A_PLAYER)));
                        return;
                    }

                    changeInspectMode((Player) sender, null);
                });
    }

    public void changeInspectMode(Player player, Player target) {
        GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
        if (!guardianPlayer.isInspecting()) {
            guardianPlayer.setInspectMode(target);
            for (int i = 0; i < inspectInventory.length; i++)
                player.getInventory().setItem(i, inspectInventory[i]);
        } else
            guardianPlayer.leaveInspectMode();
    }

}
