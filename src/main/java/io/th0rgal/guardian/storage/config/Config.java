package io.th0rgal.guardian.storage.config;

public enum Config {

    SETTINGS_LANGUAGE("settings.language");

    private String path;

    Config(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

}
