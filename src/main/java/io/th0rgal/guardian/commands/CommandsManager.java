package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
                .executes((sender, args) -> {
                    this.adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX)
                            .append(language.getRich(Message.HELP)));
                }).register();
    }

}
