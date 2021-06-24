package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.events.PlayersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;

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

    public void showMenu(Player player) {
        this.adventure.player(player);
    }

    public CommandAPICommand getCommand() {
        return null;
    }

}
