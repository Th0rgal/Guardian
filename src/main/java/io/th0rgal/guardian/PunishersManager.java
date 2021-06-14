package io.th0rgal.guardian;

import io.th0rgal.guardian.config.Configuration;
import io.th0rgal.guardian.config.PunisherAction;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.util.*;

public class PunishersManager {

    private final Map<String, List<PunisherAction>> actionsMap;

    public PunishersManager(Configuration punishersConfiguration) {
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
    }

    public void performActions(String punisher, GuardianPlayer player) {
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
