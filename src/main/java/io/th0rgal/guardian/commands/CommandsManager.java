package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.language.Message;
import io.th0rgal.guardian.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsManager {

    private final JavaPlugin plugin;
    private final BukkitAudiences adventure;
    private final LanguageConfiguration language;
    private final PlayersManager playersManager;

    public CommandsManager(JavaPlugin plugin, BukkitAudiences adventure, LanguageConfiguration language, PlayersManager playersManager) {
        this.plugin = plugin;
        this.adventure = adventure;
        this.language = language;
        this.playersManager = playersManager;
    }

    public void register() {
        InspectMode inspectMode = new InspectMode(plugin, adventure, language, playersManager);
        new CommandAPICommand("guardian")
                .withAliases("guard", "g") // Command aliases
                .withSubcommand(inspectMode.getInspectCommand())
                .withSubcommand(inspectMode.getInspectPlayerCommand())
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

}
