package ru.sergeew.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс главного сервиса (MainService) для обработки текстовых сообщений.
 */
public interface ReminderService {
    void processTextMessage(Update update);
    void processCallbackQuery(Update update);

}
