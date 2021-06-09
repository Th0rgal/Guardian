package io.th0rgal.guardian.config.language;

import net.kyori.adventure.text.format.TextColor;

public enum MessageColor {
    SUCCESS(0x55ffa4),
    INFO(0xD5D6D8),
    WARNING(0xfacc43),
    SEVERE(0xfa4943);

    private final TextColor color;

    MessageColor(int color) {
        this.color = TextColor.color(color);
    }

    public TextColor get() {
        return color;
    }
}