package io.th0rgal.guardian.config.language;

import net.kyori.adventure.text.format.TextColor;

public enum Message {

    PREFIX("plugin.prefix"),

    NOT_A_PLAYER("commands.not_a_player", MessageColor.SEVERE),
    NOT_ENOUGH_PLAYERS("commands.not_enough_players", MessageColor.SEVERE),
    PLAYER_FROZEN("commands.player_frozen", MessageColor.SUCCESS),
    PLAYER_UNFROZEN("commands.player_unfrozen", MessageColor.SUCCESS),

    TELEPORT_ITEM_NAME("items.teleport_item_name"),
    FREEZE_ITEM_NAME("items.freeze_item_name"),
    KILL_ITEM_NAME("items.kill_item_name"),
    BAN_ITEM_NAME("items.ban_item_name"),
    INFO_ITEM_NAME("items.info_item_name");

    private final String path;
    public final TextColor color;

    Message(String path) {
        this.path = path;
        color = MessageColor.INFO.get();
    }

    Message(String path, MessageColor messageColor) {
        this.path = path;
        color = messageColor.get();
    }

    @Override
    public String toString() {
        return path;
    }

}
