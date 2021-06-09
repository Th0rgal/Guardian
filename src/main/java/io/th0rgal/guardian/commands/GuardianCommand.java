package io.th0rgal.guardian.commands;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.command.CommandSender;

@Command("guardian")
@Alias({"g", "guard"})
public class GuardianCommand {

    @Default
    public static void guardian(CommandSender sender) {
        sender.sendMessage("Hello");
    }

}
