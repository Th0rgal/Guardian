package io.th0rgal.guardian.punisher;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.PunisherAction;
import io.th0rgal.guardian.config.PunisherConfig;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PunishersManager {

    private final Map<String, PunisherConfig> actionsMap;
    private final MiniMessage parser;
    private final BukkitAudiences adventure;

    public PunishersManager(JavaPlugin plugin, Configuration punishersConfiguration, MiniMessage parser, BukkitAudiences adventure) {
        actionsMap = new HashMap<>();
        this.parser = parser;
        this.adventure = adventure;
        for (String name : punishersConfiguration.getKeys())
            actionsMap.put(name, new PunisherConfig(punishersConfiguration, name));
    }

    /**
     * To update the punisher score of a Player
     *
     * @param player   The player to update
     * @param punisher The punisher's name
     * @param amount   Score to add (can be negative)
     */
    public void add(GuardianPlayer player, String punisher, double amount) {
        double newScore = Math.max(player.getScore(punisher) + amount, 0);
        player.setScore(punisher, newScore);
        performActions(punisher, player, newScore);
    }

    /**
     * To update the punisher score of a Player
     *
     * @param player   The player to update
     * @param punisher The punisher's name
     * @param amount   Scalar modifier
     */
    public void multiply(GuardianPlayer player, String punisher, double amount) {
        double newScore = Math.max(player.getScore(punisher) * amount, 0);
        player.setScore(punisher, newScore);
        performActions(punisher, player, newScore);
    }

    private void performActions(String punisher, GuardianPlayer player, double score) {
        boolean first = true;
        for (PunisherAction action : actionsMap.get(punisher).getActions()) {

            if (score < action.getThreshold())
                continue;

            if (first)
                first = false;
            else if (!action.concurrent)
                continue;

            if (action.hasAlert())
                adventure.sender(Bukkit.getConsoleSender()).sendMessage(
                        parser.parse(action.getAlert().replace("{player}", player.toBukkitPlayer().getName()))
                );

            if (action.hasCommands())
                for (String command : action.getCommands())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.toBukkitPlayer().getName()));
        }
    }

    public Set<String> getPunishers() {
        return actionsMap.keySet();
    }

    public Map<String, PunisherConfig> getPunishersConfig() {
        return actionsMap;
    }

}
