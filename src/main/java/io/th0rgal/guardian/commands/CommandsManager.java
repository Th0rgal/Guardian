package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import io.th0rgal.guardian.storage.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public record CommandsManager(JavaPlugin plugin, GuardianJournal journal, BukkitAudiences adventure,
                              LanguageConfiguration language, PlayersManager playersManager) {

    public void register() {
        InfoPlayer infoPlayer = new InfoPlayer(adventure, language, playersManager);
        InspectMode inspectMode = new InspectMode(plugin, adventure, language, playersManager, infoPlayer);
        FreezePlayer freezePlayer = new FreezePlayer(adventure, language, playersManager);
        JournalCommand journalCommand = new JournalCommand(journal, adventure, language);
        new CommandAPICommand("guardian")
                .withAliases("guard", "g") // Command aliases
                .withSubcommand(freezePlayer.getCommand())
                .withSubcommand(infoPlayer.getCommand())
                .withSubcommand(inspectMode.getInspectCommand())
                .withSubcommand(inspectMode.getInspectPlayerCommand())
                .withSubcommand(journalCommand.getCommand())
                .withSubcommand(journalCommand.getToggleCommand())
                .executes((sender, args) -> {
                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .color(MessageColor.INFO.get())
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
