package io.th0rgal.guardian;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.PunisherAction;
import io.th0rgal.guardian.storage.Database;
import io.th0rgal.guardian.storage.SQLite;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.util.*;

public class PunishersManager {

    private final Map<String, List<PunisherAction>> actionsMap;
    private final Database database;

    public PunishersManager(JavaPlugin plugin, Configuration punishersConfiguration) {
        actionsMap = new HashMap<>();
        for (String name : punishersConfiguration.getKeys()) {
            TomlTable punisher = punishersConfiguration.getTable(name);
            double decrease = punisher.getDouble("decrease");
            TomlArray array = punisher.getArray("action");
            List<PunisherAction> actions = new ArrayList<>();
            for (Object actionObject : array.toList()) {
                TomlTable actionTable = (TomlTable) actionObject;
                PunisherAction action = new PunisherAction(actionTable.getDouble("threshold"));
                if (actionTable.isString("alert"))
                    action.setAlert(actionTable.getString("alert"));
                if (actionTable.isString("commands"))
                    action.setAlert(actionTable.getString("alert"));
                actions.add(action);
            }
            actionsMap.put(name, actions);
        }

        database = new SQLite(plugin, getPunishers(), "punishers");
        database.load();
    }

    /**
     * To update the punisher score of a Player
     *
     * @param player   The player to update
     * @param punisher The punisher's name
     * @param amount   Score to add (can be negative)
     */
    public void add(GuardianPlayer player, String punisher, int amount) {
        database.setScore(player.getId(),
                punisher,
                Math.max(database.getScore(player.getId(), punisher) + amount, 0)
        );
    }

    /**
     * To update the punisher score of a Player
     *
     * @param player   The player to update
     * @param punisher The punisher's name
     * @param amount   Scalar modifier
     */
    public void multiply(GuardianPlayer player, String punisher, int amount) {
        database.setScore(player.getId(),
                punisher,
                Math.max(database.getScore(player.getId(), punisher) * amount, 0)
        );
    }

    private void performActions(String punisher, GuardianPlayer player) {
        for (PunisherAction action : actionsMap.get(punisher)) {
            if (action.hasAlert())
                action.getAlert();

            if (action.hasCommands())
                action.getCommands();
        }
    }

    public Set<String> getPunishers() {
        return actionsMap.keySet();
    }

}
