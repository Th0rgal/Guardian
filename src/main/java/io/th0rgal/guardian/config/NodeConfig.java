package io.th0rgal.guardian.config;

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

    public String getString(String entry) {
        return nodesConfig.getString(prefix + entry);
    }

    public boolean getBoolean(String entry) {
        return nodesConfig.getBoolean(prefix + entry);
    }

}
