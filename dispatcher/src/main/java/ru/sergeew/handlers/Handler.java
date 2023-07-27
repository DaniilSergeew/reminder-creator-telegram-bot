package ru.sergeew.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {
    boolean supports(Update update);

    void handle(Update update);
}
