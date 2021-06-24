package io.th0rgal.guardian;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.th0rgal.guardian.commands.CommandsManager;
import io.th0rgal.guardian.storage.config.Config;
import io.th0rgal.guardian.storage.config.Configuration;
import io.th0rgal.guardian.storage.config.MainConfig;
import io.th0rgal.guardian.storage.config.language.LanguageConfiguration;
import io.th0rgal.guardian.events.PlayersManager;
import io.th0rgal.guardian.nodes.NodesManager;
import io.th0rgal.guardian.punishers.PunishersManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GuardianPlugin extends JavaPlugin {

    private PlayersManager playersManager;

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
        playersManager = new PlayersManager(this, punisher, adventure, lang);
        new CommandsManager(this, adventure, lang, playersManager).register();
        new NodesManager(this, new Configuration(this, "nodes"), playersManager, punisher).enableAll();
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GuardianPlayer guardianPlayer = playersManager.getPlayer(player);
            if (guardianPlayer.isFrozen())
                guardianPlayer.switchFreeze();
            if (guardianPlayer.isInspecting())
                guardianPlayer.leaveInspectMode();
        }
    }
}
