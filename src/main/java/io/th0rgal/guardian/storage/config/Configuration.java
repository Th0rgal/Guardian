package io.th0rgal.guardian.storage.config;

import io.th0rgal.guardian.exceptions.ExceptionHandler;

import io.th0rgal.guardian.exceptions.ParsingException;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Configuration {

    private TomlParseResult result;
    private TomlParseResult original;

    public Configuration(JavaPlugin plugin, String fileName) {
        fileName += ".toml";
        Path configFile = Path.of(plugin.getDataFolder().getPath()).resolve(fileName);
        if (!Files.exists(configFile))
            plugin.saveResource(fileName, false);

        try {
            result = Toml.parse(configFile);
            for (TomlParseError error : result.errors())
                throw new ParsingException(error.toString());

            original = Toml.parse(getClass().getResourceAsStream("/" + fileName));
            for (TomlParseError error : original.errors())
                throw new ParsingException(error.toString());
        } catch (IOException | ParsingException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
    }

    public Object get(String entry) {
        return result.get(entry);
    }

    public long getLong(String entry) {
        return result.getLong(entry);
    }

    public double getDouble(String entry) {
        return result.getDouble(entry);
    }

    public String getString(String entry) {
        return result.getString(entry);
    }

    public boolean getBoolean(String entry) {
        return result.getBoolean(entry);
    }

    public boolean isArray(String entry) {
        return result.isArray(entry);
    }

    public TomlArray getArray(String entry) {
        return result.getArray(entry);
    }

    public boolean isTable(String entry) {
        return result.isTable(entry);
    }

    public TomlTable getTable(String entry) {
        return result.getTable(entry);
    }

    public Set<String> getKeys() {
        return result.keySet();
    }

}
