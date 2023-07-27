package ru.sergeew.service.enums;

import java.util.Optional;

/**
 * Перечисление, определяющее типы сервисных команд.
 */
public enum ServiceCommand {
    START("/start"),
    LIST("/list");
    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    public static Optional<ServiceCommand> fromValue(String v) {
        for (ServiceCommand c: ServiceCommand.values()) {
            if (c.value.equals(v)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
