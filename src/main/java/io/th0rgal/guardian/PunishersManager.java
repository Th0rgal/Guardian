package io.th0rgal.guardian;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.PunisherAction;
import io.th0rgal.guardian.config.PunisherConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PunishersManager {

    private final Map<String, PunisherConfig> actionsMap;

    public PunishersManager(JavaPlugin plugin, Configuration punishersConfiguration) {
        actionsMap = new HashMap<>();
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
                Bukkit.broadcastMessage(action.getAlert());

            if (action.hasCommands())
                for (String command : action.getCommands())
                    Bukkit.broadcastMessage(command);
        }
    }

    public Set<String> getPunishers() {
        return actionsMap.keySet();
    }

}
