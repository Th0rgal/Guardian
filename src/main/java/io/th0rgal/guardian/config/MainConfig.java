package io.th0rgal.guardian.config;

import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends Configuration {

    public MainConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public Object get(Config entry) {
        return super.get(entry.toString());
    }

    public String getString(Config entry) {
        return super.getString(entry.toString());
    }

    public boolean getBoolean(Config entry) {
        return super.getBoolean(entry.toString());
    }

}
