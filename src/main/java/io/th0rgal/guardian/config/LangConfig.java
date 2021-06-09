package io.th0rgal.guardian.config;

import org.bukkit.plugin.java.JavaPlugin;

public class LangConfig extends Configuration {

    public LangConfig(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    public Object get(Lang entry) {
        return super.get(entry.toString());
    }

    public String getString(Lang entry) {
        return super.getString(entry.toString());
    }

    public boolean getBoolean(Lang entry) {
        return super.getBoolean(entry.toString());
    }

}
