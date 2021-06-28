package io.th0rgal.guardian.storage.config;

import org.tomlj.TomlTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PunisherConfig {

    private final Configuration punishersConfig;
    private final String prefix;
    private final List<PunisherAction> actions = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public PunisherConfig(Configuration punishersConfig, String name) {
        this.punishersConfig = punishersConfig;
        prefix = name + ".";
        for (Object object : punishersConfig.getArray(prefix + "action").toList()) {
            TomlTable table = (TomlTable) object;
            PunisherAction action = new PunisherAction(table.getDouble("threshold"), table.getBoolean("concurrent"));
            if (table.isString("log"))
                action.setLog(table.getString("log"));
            if (table.isArray("commands"))
                action.setCommands((List<String>) (List<?>) table.getArray("commands").toList());
            actions.add(action);
        }
        actions.sort(Comparator.comparing(PunisherAction::getThreshold).reversed());
    }

    public double getDecrease() {
        return punishersConfig.getDouble(prefix + "decrease");
    }

    public List<PunisherAction> getActions() {
        return actions;
    }

}