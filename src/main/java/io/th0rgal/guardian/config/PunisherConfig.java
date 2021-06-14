package io.th0rgal.guardian.config;

import org.tomlj.TomlTable;

import java.util.ArrayList;
import java.util.List;

public class PunisherConfig {

    private final Configuration punishersConfig;
    private final String prefix;

    public PunisherConfig(Configuration punishersConfig, String name) {
        this.punishersConfig = punishersConfig;
        prefix = name + ".";
    }

    public double getDecrease() {
        return punishersConfig.getDouble(prefix + "decrease");
    }

    @SuppressWarnings("unchecked")
    public List<PunisherAction> getActions() {
        List<PunisherAction> actions = new ArrayList<>();
        for (Object object : punishersConfig.getArray(prefix + "action").toList()) {
            TomlTable table = (TomlTable) object;
            PunisherAction action = new PunisherAction(table.getDouble("threshold"));
            if (table.isString("alert"))
                action.setAlert(table.getString("alert"));
            if (table.isArray("commands"))
                action.setCommands((List<String>) (List<?>) table.getArray("commands").toList());
            actions.add(action);
        }
        return actions;
    }

}