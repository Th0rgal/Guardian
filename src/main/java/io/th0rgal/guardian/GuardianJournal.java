package io.th0rgal.guardian;

import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import io.th0rgal.guardian.storage.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class GuardianJournal {

    private final Set<CommandSender> subscribers;
    private final BukkitAudiences adventure;
    private final LanguageConfiguration lang;
    private final MiniMessage parser;

    public GuardianJournal(BukkitAudiences adventure, LanguageConfiguration lang, MiniMessage parser) {
        subscribers = new HashSet<>();
        this.adventure = adventure;
        this.lang = lang;
        this.parser = parser;
    }

    public boolean subscribe(CommandSender newSubscriber) {
        return subscribers.add(newSubscriber);
    }

    public boolean unsubscribe(CommandSender subscriber) {
        return subscribers.remove(subscriber);
    }

    public boolean isSubscribed(CommandSender subscriber) {
        return subscribers.contains(subscriber);
    }

    public void log(String message, MessageColor color, String... placeholders) {
        Component component = lang.getRich(Message.JOURNAL_PREFIX).color(color.get()).append(parser.parse(message, placeholders));
        for (CommandSender subscriber : subscribers)
            adventure.sender(subscriber).sendMessage(component);
    }

    public void log(Message message, String... placeholders) {
        Component component = lang.getRich(Message.JOURNAL_PREFIX).append(lang.getRich(message, placeholders));
        for (CommandSender subscriber : subscribers)
            adventure.sender(subscriber).sendMessage(component);
    }
}
