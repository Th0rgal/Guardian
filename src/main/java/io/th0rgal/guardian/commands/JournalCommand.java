package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.Permission;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public record JournalCommand(GuardianJournal journal, BukkitAudiences adventure,
                             LanguageConfiguration language) {

    public CommandAPICommand getCommand() {
        return new CommandAPICommand("journal")
                .withAliases("logs")
                .withArguments(new BooleanArgument("subscribe"))
                .withPermission(Permission.USE_COMMAND_LOGS.toString())
                .executes((sender, args) -> {
                    boolean enable = (boolean) args[0];
                    Message message;
                    if (enable)
                        message = journal.subscribe(sender) ? Message.LOGS_SUBSCRIBED : Message.LOGS_ALREADY_SUBSCRIBED;
                    else
                        message = journal.unsubscribe(sender) ? Message.LOGS_UNSUBSCRIBED : Message.LOGS_NOT_SUBSCRIBED;
                    adventure.sender(sender).sendMessage(language.getRich(message));
                });
    }
}
