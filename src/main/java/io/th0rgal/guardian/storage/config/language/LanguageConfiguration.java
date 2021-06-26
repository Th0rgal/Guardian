package io.th0rgal.guardian.storage.config.language;

import io.th0rgal.guardian.storage.config.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class LanguageConfiguration extends Configuration {

    private final MiniMessage parser;

    public LanguageConfiguration(JavaPlugin plugin, MiniMessage parser, String fileName) {
        super(plugin, fileName);
        this.parser = parser;
    }

    public String get(Message entry) {
        return super.getString(entry.toString());
    }

    public Component getRich(Message entry, String... placeholders) {
        return this.parser.parse(get(entry), placeholders);
    }

}
