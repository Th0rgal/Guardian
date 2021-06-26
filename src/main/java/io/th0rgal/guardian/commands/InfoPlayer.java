package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.Permission;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

public class InfoPlayer {

    private final BukkitAudiences adventure;
    private final LanguageConfiguration language;
    private final PlayersManager playersManager;

    public InfoPlayer(BukkitAudiences adventure, LanguageConfiguration language,
                      PlayersManager playersManager) {
        this.adventure = adventure;
        this.language = language;
        this.playersManager = playersManager;
    }

    public Component getReport(GuardianPlayer target) {
        Component output = language.getRich(Message.INFOVIEW_TITLE, "player", target.asBukkitPlayer().getName());
        for (Map.Entry<String, Double> node : target.getScores().entrySet())
            output = output.append(Component.text("\n")).append(language.getRich(Message.INFOVIEW_PUNISHER_LINE,
                    "punisher", node.getKey(), "score", node.getValue().toString()));
        return output;
    }

    public void showMenu(GuardianPlayer player, GuardianPlayer target) {
        player.audience.openBook(Book.book(
                language.getRich(Message.INFOVIEW_TITLE, "player", target.asBukkitPlayer().getName()),
                language.getRich(Message.INFOVIEW_AUTHOR),
                getReport(target)));
    }

    public CommandAPICommand getCommand() {
        return new CommandAPICommand("info")
                .withPermission(Permission.USE_COMMAND_INFO.toString())
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                    GuardianPlayer target = playersManager.getPlayer((Player) args[0]);

                    if (sender instanceof Player player)
                        showMenu(playersManager.getPlayer(player), target);
                });
    }

}
