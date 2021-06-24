package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.Permission;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.language.Message;
import io.th0rgal.guardian.events.PlayersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;

public record FreezePlayer(BukkitAudiences adventure, LanguageConfiguration language,
                           PlayersManager playersManager) {

    public CommandAPICommand getCommand() {
        return new CommandAPICommand("freeze")
                .withAliases("unfreeze")
                .withPermission(Permission.USE_COMMAND_FREEZE.toString())
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                    GuardianPlayer player = playersManager.getPlayer((Player) args[0]);
                    player.switchFreeze();
                    Message message = player.isFrozen() ? Message.PLAYER_FROZEN : Message.PLAYER_UNFROZEN;
                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(message.color)
                            .append(language.getRich(message)));
                });
    }

}