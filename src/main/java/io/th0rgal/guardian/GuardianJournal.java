package io.th0rgal.guardian;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class GuardianJournal {

    private final Set<CommandSender> subscribers;
    private final BukkitAudiences adventure;

    public GuardianJournal(BukkitAudiences adventure) {
        subscribers = new HashSet<>();
        this.adventure = adventure;
    }

    public boolean subscribe(CommandSender newSubscriber) {
        return subscribers.add(newSubscriber);
    }

    public boolean unsubscribe(CommandSender subscriber) {
        return subscribers.remove(subscriber);
    }

    public void log(Component component) {
        for (CommandSender subscriber : subscribers)
            adventure.sender(subscriber).sendMessage(component);
    }
}
