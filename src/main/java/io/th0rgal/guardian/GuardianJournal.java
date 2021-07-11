package io.th0rgal.guardian;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import io.th0rgal.guardian.storage.JournalDatabase;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.storage.config.language.Message;
import io.th0rgal.guardian.storage.config.language.MessageColor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GuardianJournal {

    public enum Type {
        PUNISHER,
        NODE
    }

    private final Plugin plugin;
    private final Map<Type, Set<CommandSender>> subscribers;
    private final BukkitAudiences adventure;
    private final LanguageConfiguration lang;
    private final MiniMessage parser;
    private PrintWriter out;
    private final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
    private final File latestLogs;

    public GuardianJournal(JavaPlugin plugin, BukkitAudiences adventure, LanguageConfiguration lang, MiniMessage parser, boolean write) {
        this.plugin = plugin;
        subscribers = new HashMap<>();
        this.adventure = adventure;
        this.lang = lang;
        this.parser = parser;
        latestLogs = new File(plugin.getDataFolder(), "latest.log");
        if (write) try {
            out = new PrintWriter(latestLogs);
        } catch (FileNotFoundException e) {
            new ExceptionHandler(e).fire(plugin.getLogger(), "Unable to find file");
        }
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
        Component log = parser.parse(message, placeholders);
        Component component = lang.getRich(Message.JOURNAL_PREFIX).color(color.get()).append(log);
        for (CommandSender subscriber : subscribers.getOrDefault(journalType, new HashSet<>()))
            adventure.sender(subscriber).sendMessage(component);
        out.println(dateFormat.format(System.currentTimeMillis()) + " | " + serializer.serialize(log));
    }

    public void log(Type journalType, Message message, String... placeholders) {
        Component log = lang.getRich(message, placeholders);
        Component component = lang.getRich(Message.JOURNAL_PREFIX).append(log);
        for (CommandSender subscriber : subscribers.getOrDefault(journalType, new HashSet<>()))
            adventure.sender(subscriber).sendMessage(component);
        out.println(dateFormat.format(System.currentTimeMillis()) + " | " + serializer.serialize(log));
    }

    public void close() {
        if (out != null) {
            out.close();
            File journalFolder = new File(plugin.getDataFolder(), "journal");
            journalFolder.mkdir();
            latestLogs.renameTo(new File(journalFolder, dateFormat.format(System.currentTimeMillis()) + ".log"));
        }
    }
}
