package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.language.Message;
import io.th0rgal.guardian.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CommandsManager {

    private final BukkitAudiences adventure;
    private final LanguageConfiguration language;

    public CommandsManager(BukkitAudiences adventure, LanguageConfiguration language) {
        this.adventure = adventure;
        this.language = language;
    }

    public void register() {
        new CommandAPICommand("guardian")
                .withAliases("guard", "g") // Command aliases
                .withSubcommand(getInspectCommand())
                .executes((sender, args) -> {
                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(Message.PREFIX.color)
                            .append(Component.text("This is an example message of type [INFO]")));

                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(MessageColor.SUCCESS.get())
                            .append(Component.text("This is an example message of type [SUCCESS]")));

                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(MessageColor.WARNING.get())
                            .append(Component.text("This is an example message of type [WARNING]")));

                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(MessageColor.SEVERE.get())
                            .append(Component.text("This is an example message of type [SEVERE]")));

                }).register();
    }

    private CommandAPICommand getInspectCommand() {
        return new CommandAPICommand("inspect")
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                                .color(Message.NOT_A_PLAYER.color)
                                .append(language.getRich(Message.NOT_A_PLAYER)));
                    }

                    Player target = (Player) args[0];

                });
    }


}
