package io.th0rgal.guardian.storage.config;

import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

public class NodeConfig {

    private final Configuration nodesConfig;
    private final String prefix;

    public NodeConfig(Configuration nodesConfig, String name) {
        this.nodesConfig = nodesConfig;
        prefix = name + ".";
    }

    public Object get(String entry) {
        return nodesConfig.get(prefix + entry);
    }

    public long getLong(String entry) {
        return nodesConfig.getLong(prefix + entry);
    }

    public double getDouble(String entry) {
        return nodesConfig.getDouble(prefix + entry);
    }

    public String getString(String entry) {
        return nodesConfig.getString(prefix + entry);
    }

    public boolean getBoolean(String entry) {
        return nodesConfig.getBoolean(prefix + entry);
    }

    public boolean isTable(String entry) {
        return nodesConfig.isTable(prefix + entry);
    }

    public TomlTable getTable(String entry) {
        return nodesConfig.getTable(prefix + entry);
    }

    public boolean isArray(String entry) {
        return nodesConfig.isArray(prefix + entry);
    }

    public TomlArray getArray(String entry) {
        return nodesConfig.getArray(prefix + entry);
    }

}
