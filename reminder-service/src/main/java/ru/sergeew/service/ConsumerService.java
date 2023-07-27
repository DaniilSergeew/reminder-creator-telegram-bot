package ru.sergeew.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс сервиса-потребителя для обработки обновлений текстовых сообщений.
 */
public interface ConsumerService {
    void consumeTextMessageUpdate(Update update);

    void consumeCallbackQuery(Update update);
}
