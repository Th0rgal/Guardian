package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.th0rgal.guardian.PlayersManager;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.language.Message;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InspectMode {

    private final BukkitAudiences adventure;
    private final LanguageConfiguration language;
    private final PlayersManager playersManager;
    private final Map<UUID, InspectData> map;
    private final LegacyComponentSerializer displayNameSerializer;
    private final ItemStack[] inspectInventory;

    public InspectMode(BukkitAudiences adventure, LanguageConfiguration language, PlayersManager playersManager) {
        this.adventure = adventure;
        this.language = language;
        this.playersManager = playersManager;
        this.map = new HashMap<>();
        this.displayNameSerializer = LegacyComponentSerializer.builder()
                .character('§')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        this.inspectInventory = new ItemStack[]{
                getItem(Material.ENDER_EYE, Message.TELEPORT_ITEM_NAME),
                getItem(Material.TRIDENT, Message.FREEZE_ITEM_NAME),
                getItem(Material.NETHERITE_SWORD, Message.KILL_ITEM_NAME),
                getItem(Material.DAMAGED_ANVIL, Message.BAN_ITEM_NAME),
                getItem(Material.GLOBE_BANNER_PATTERN, Message.INFO_ITEM_NAME)
        };
    }

    private ItemStack getItem(Material material, Message name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.displayNameSerializer.serialize(language.getRich(name)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public CommandAPICommand getInspectPlayerCommand() {
        return new CommandAPICommand("inspect")
                .withArguments(new PlayerArgument("player"))
                .withPermission("guardian.inspect")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player admin)) {
                        this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                                .color(Message.NOT_A_PLAYER.color)
                                .append(language.getRich(Message.NOT_A_PLAYER)));
                        return;
                    }

                    Player target = (Player) args[0];

                    if (map.get(admin.getUniqueId()) == null)
                        changeInspectMode(admin);
                    admin.teleport(target);
                });
    }

    public CommandAPICommand getInspectCommand() {
        return new CommandAPICommand("inspect")
                .withPermission("guardian.inspect")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                                .color(Message.NOT_A_PLAYER.color)
                                .append(language.getRich(Message.NOT_A_PLAYER)));
                        return;
                    }

                    changeInspectMode((Player) sender);
                });
    }

    public void changeInspectMode(Player player) {
        InspectData data = map.get(player.getUniqueId());
        if (data == null) {
            map.put(player.getUniqueId(), new InspectData(player.getLocation(),
                    player.getInventory().getContents(),
                    player.getGameMode(),
                    player.isInvisible()));
            player.setGameMode(GameMode.CREATIVE);
            player.setInvisible(true);
            for (int i = 0; i < inspectInventory.length; i++)
                player.getInventory().setItem(i, inspectInventory[i]);
        } else {
            player.teleport(data.location());
            player.getInventory().setContents(data.inventory());
            player.setGameMode(data.gameMode());
            player.setInvisible(data.invisible());
            map.put(player.getUniqueId(), null);
        }
    }

    private record InspectData(Location location, ItemStack[] inventory, GameMode gameMode, boolean invisible) {

    }

}
