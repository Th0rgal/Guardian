package io.th0rgal.guardian.config;

import io.th0rgal.guardian.exceptions.ExceptionHandler;

import io.th0rgal.guardian.exceptions.ParsingException;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.Toml;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public String getString(String entry) {
        return result.getString(entry);
    }

    public boolean getBoolean(String entry) {
        return result.getBoolean(entry);
    }


}
