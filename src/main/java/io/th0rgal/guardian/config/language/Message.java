package io.th0rgal.guardian.config.language;

import net.kyori.adventure.text.format.TextColor;

public enum Message {

    PREFIX("plugin.prefix");

    private final String path;
    public final TextColor color;

    Message(String path) {
        this.path = path;
        color = MessageColor.INFO.get();
    }

    Message(String path, MessageColor messageColor) {
        this.path = path;
        color = messageColor.get();
    }

    @Override
    public String toString() {
        return path;
    }

}
