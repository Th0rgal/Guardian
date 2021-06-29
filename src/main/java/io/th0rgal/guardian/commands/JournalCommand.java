package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import io.th0rgal.guardian.GuardianJournal;
import io.th0rgal.guardian.Permission;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;

public record JournalCommand(GuardianJournal journal, BukkitAudiences adventure,
                             LanguageConfiguration language) {


    public CommandAPICommand getCommand() {
        return new CommandAPICommand("journal")
                .withPermission(Permission.USE_COMMAND_JOURNAL.toString())
                .withSubcommand(getPunisherCommand())
                .withSubcommand(getPunisherToggleCommand())
                .withSubcommand(getNodeCommand())
                .withSubcommand(getNodeToggleCommand())
                .withSubcommand(getAllCommand())
                .withSubcommand(getAllToggleCommand());
    }

    private void handleBaseCommand(boolean subscribe, GuardianJournal.Type journalType, String journalName, CommandSender sender) {
        Message message;
        if (subscribe)
            message = journal.subscribe(sender, GuardianJournal.Type.PUNISHER) ? Message.JOURNAL_SUBSCRIBED : Message.JOURNAL_ALREADY_SUBSCRIBED;
        else
            message = journal.unsubscribe(sender, GuardianJournal.Type.PUNISHER) ? Message.JOURNAL_UNSUBSCRIBED : Message.JOURNAL_NOT_SUBSCRIBED;
        adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX).append(language.getRich(message, "name", journalName)));
    }

    private void handleToggleCommand(GuardianJournal.Type journalType, String journalName, CommandSender sender) {
        Message message;
        if (journal().isSubscribed(sender, journalType)) {
            journal.unsubscribe(sender, journalType);
            message = Message.JOURNAL_UNSUBSCRIBED;
        } else {
            journal.subscribe(sender, journalType);
            message = Message.JOURNAL_SUBSCRIBED;
        }
        adventure.sender(sender).sendMessage(language.getRich(Message.PREFIX).append(language.getRich(message, "name", journalName)));
    }

    private CommandAPICommand getPunisherCommand() {
        return new CommandAPICommand("punisher")
                .withArguments(new BooleanArgument("subscribe"))
                .executes((sender, args) -> {
                    handleBaseCommand((boolean) args[0], GuardianJournal.Type.PUNISHER, "punisher", sender);
                });
    }

    private CommandAPICommand getPunisherToggleCommand() {
        return new CommandAPICommand("punisher")
                .executes((sender, args) -> {
                    handleToggleCommand(GuardianJournal.Type.PUNISHER, "punisher", sender);
                });
    }


    private CommandAPICommand getNodeCommand() {
        return new CommandAPICommand("node")
                .withArguments(new BooleanArgument("subscribe"))
                .executes((sender, args) -> {
                    handleBaseCommand((boolean) args[0], GuardianJournal.Type.NODE, "node", sender);
                });
    }

    private CommandAPICommand getNodeToggleCommand() {
        return new CommandAPICommand("node")
                .executes((sender, args) -> {
                    handleToggleCommand(GuardianJournal.Type.NODE, "node", sender);
                });
    }


    private CommandAPICommand getAllCommand() {
        return new CommandAPICommand("all")
                .withArguments(new BooleanArgument("subscribe"))
                .executes((sender, args) -> {
                    boolean enable = (boolean) args[0];
                    handleBaseCommand(enable, GuardianJournal.Type.PUNISHER, "punisher", sender);
                    handleBaseCommand(enable, GuardianJournal.Type.NODE, "node", sender);
                });
    }

    private CommandAPICommand getAllToggleCommand() {
        return new CommandAPICommand("all")
                .withAliases("logs")
                .withPermission(Permission.USE_COMMAND_JOURNAL.toString())
                .executes((sender, args) -> {
                    handleToggleCommand(GuardianJournal.Type.PUNISHER, "punisher", sender);
                    handleToggleCommand(GuardianJournal.Type.NODE, "node", sender);
                });
    }
}
