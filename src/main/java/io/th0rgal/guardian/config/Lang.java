package io.th0rgal.guardian.config;

public enum Lang {

    PLUGIN_NAME("plugin.name");

    private String path;

    Lang(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

}
