package io.th0rgal.guardian.storage.config.language;

import net.kyori.adventure.text.format.TextColor;

public enum Message {

    PREFIX("plugin.prefix"),
    JOURNAL_PREFIX("plugin.journal_prefix"),
    NODE_LOG("plugin.node_log"),

    HELP("commands.help"),
    NOT_A_PLAYER("commands.not_a_player", MessageColor.SEVERE),
    NOT_ENOUGH_PLAYERS("commands.not_enough_players", MessageColor.SEVERE),
    TARGET_FROZEN("commands.target_frozen", MessageColor.SUCCESS),
    TARGET_UNFROZEN("commands.target_unfrozen", MessageColor.SUCCESS),
    PLAYER_FROZEN("commands.player_frozen", MessageColor.SUCCESS),
    PLAYER_UNFROZEN("commands.player_unfrozen", MessageColor.SUCCESS),
    TARGET_KILLED("commands.target_killed", MessageColor.SUCCESS),
    TARGET_BANNED("commands.target_banned", MessageColor.SUCCESS),
    JOURNAL_SUBSCRIBED("commands.journal_subscribed", MessageColor.SUCCESS),
    JOURNAL_ALREADY_SUBSCRIBED("commands.journal_already_subscribed", MessageColor.SEVERE),
    JOURNAL_UNSUBSCRIBED("commands.journal_unsubscribed", MessageColor.SUCCESS),
    JOURNAL_NOT_SUBSCRIBED("commands.journal_not_subscribed", MessageColor.SEVERE),

    TELEPORT_ITEM_NAME("items.teleport_item_name"),
    FREEZE_ITEM_NAME("items.freeze_item_name"),
    KILL_ITEM_NAME("items.kill_item_name"),
    BAN_ITEM_NAME("items.ban_item_name"),
    INFO_ITEM_NAME("items.info_item_name"),

    INFOVIEW_TITLE("infoview.title"),
    INFOVIEW_AUTHOR("infoview.author"),
    INFOVIEW_PUNISHER_LINE("infoview.punisher_line");

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
