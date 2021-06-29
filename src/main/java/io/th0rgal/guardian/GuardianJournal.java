package io.th0rgal.guardian;

import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import io.th0rgal.guardian.storage.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuardianJournal {

    public enum Type {
        PUNISHER,
        NODE
    }

    private final Map<Type, Set<CommandSender>> subscribers;
    private final BukkitAudiences adventure;
    private final LanguageConfiguration lang;
    private final MiniMessage parser;

    public GuardianJournal(BukkitAudiences adventure, LanguageConfiguration lang, MiniMessage parser) {
        subscribers = new HashMap<>();
        this.adventure = adventure;
        this.lang = lang;
        this.parser = parser;
    }

    public boolean subscribe(CommandSender newSubscriber, Type journalType) {
        Set<CommandSender> journalSubscribers = subscribers.getOrDefault(journalType, new HashSet<>());
        boolean output = journalSubscribers.add(newSubscriber);
        subscribers.put(journalType, journalSubscribers);
        return output;
    }

    public boolean unsubscribe(CommandSender subscriber, Type journalType) {
        Set<CommandSender> journalSubscribers = subscribers.getOrDefault(journalType, new HashSet<>());
        boolean output = journalSubscribers.remove(subscriber);
        subscribers.put(journalType, journalSubscribers);
        return output;
    }

    public boolean isSubscribed(CommandSender subscriber, Type journalType) {
        return subscribers.getOrDefault(journalType, new HashSet<>()).contains(subscriber);
    }

    public void log(Type journalType, String message, MessageColor color, String... placeholders) {
        Component component = lang.getRich(Message.JOURNAL_PREFIX).color(color.get()).append(parser.parse(message, placeholders));
        for (CommandSender subscriber : subscribers.getOrDefault(journalType, new HashSet<>()))
            adventure.sender(subscriber).sendMessage(component);
    }

    public void log(Type journalType, Message message, String... placeholders) {
        Component component = lang.getRich(Message.JOURNAL_PREFIX).append(lang.getRich(message, placeholders));
        for (CommandSender subscriber : subscribers.getOrDefault(journalType, new HashSet<>()))
            adventure.sender(subscriber).sendMessage(component);
    }
}
