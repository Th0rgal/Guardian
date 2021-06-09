package io.th0rgal.guardian;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.th0rgal.guardian.commands.GuardianCommand;
import io.th0rgal.guardian.config.Config;
import io.th0rgal.guardian.config.LangConfig;
import io.th0rgal.guardian.config.MainConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class GuardianPlugin extends JavaPlugin {

    public void onLoad() {
        CommandAPIConfig commandAPIConfig = new CommandAPIConfig();
        commandAPIConfig.setVerboseOutput(false);
        CommandAPI.onLoad(commandAPIConfig);
        CommandAPI.registerCommand(GuardianCommand.class);
    }

    public void onEnable() {
        CommandAPI.onEnable(this);
        MainConfig config = new MainConfig(this, "config");
        LangConfig lang = new LangConfig(this,
                "languages/" + config.getString(Config.SETTINGS_LANGUAGE));
    }

}
