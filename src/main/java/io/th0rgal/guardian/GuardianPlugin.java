package io.th0rgal.guardian;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.th0rgal.guardian.commands.CommandsManager;
import io.th0rgal.guardian.config.Config;
import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.language.LanguageConfiguration;
import io.th0rgal.guardian.config.MainConfig;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.NodesManager;
import io.th0rgal.guardian.punisher.PunishersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class GuardianPlugin extends JavaPlugin {

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().silentLogs(true).verboseOutput(false));
    }

    public void onEnable() {
        CommandAPI.onEnable(this);
        MiniMessage parser = MiniMessage.markdown();
        MainConfig config = new MainConfig(this, "config");
        LanguageConfiguration lang = new LanguageConfiguration(this, parser,
                "languages/" + config.getString(Config.SETTINGS_LANGUAGE));
        BukkitAudiences adventure = BukkitAudiences.create(this);
        PunishersManager punisher = new PunishersManager(this, new Configuration(this, "punishers"), parser, adventure);
        PlayersManager playersManager = new PlayersManager(this, punisher);
        new CommandsManager(this, adventure, lang, playersManager).register();
        new NodesManager(this, new Configuration(this, "nodes"), playersManager, punisher).enableAll();
    }

}
