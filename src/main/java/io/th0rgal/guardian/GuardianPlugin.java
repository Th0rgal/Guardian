package io.th0rgal.guardian;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.th0rgal.guardian.commands.CommandsManager;
import io.th0rgal.guardian.config.Config;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.MainConfig;
import io.th0rgal.guardian.events.PlayersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GuardianPlugin extends JavaPlugin {

    public void onLoad() {
        CommandAPIConfig commandAPIConfig = new CommandAPIConfig();
        commandAPIConfig.setVerboseOutput(false);
        CommandAPI.onLoad(commandAPIConfig);
    }

    public void onEnable() {
        CommandAPI.onEnable(this);
        MiniMessage parser = MiniMessage.markdown();
        MainConfig config = new MainConfig(this, "config");
        LanguageConfiguration lang = new LanguageConfiguration(this, parser,
                "languages/" + config.getString(Config.SETTINGS_LANGUAGE));
        BukkitAudiences adventure = BukkitAudiences.create(this);
        new CommandsManager(adventure, lang).register();
        PlayersManager playersManager = new PlayersManager(this);
    }

}
