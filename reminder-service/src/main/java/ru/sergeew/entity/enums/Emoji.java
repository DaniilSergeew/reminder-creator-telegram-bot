package ru.sergeew.entity.enums;

import com.vdurmont.emoji.EmojiParser;

/**
 * Перечисление, определяющее типы эможди.
 */
public enum Emoji {
    CLOCK("\u23F0"),
    PUSHPIN("\uD83D\uDCCC"),
    DATE("\uD83D\uDCC5"),
    WAVY_DASH("\u3030"),
    ORANGE_CIRCLE("\uD83D\uDFE0"),
    ZAP("\u26A1");

    private final String value;

    Emoji(String value) {
        this.value = value;
    }

    /**
     * Возвращает Unicode для отправки эмоджи пользователю.
     */
    public String get() {
        return value;
    }
}
