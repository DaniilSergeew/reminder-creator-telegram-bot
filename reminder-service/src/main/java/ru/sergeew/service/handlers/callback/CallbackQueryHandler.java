package ru.sergeew.service.handlers.callback;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Базовый интерфейс для обработки CallbackQuery из Telegram API.
 */
public interface CallbackQueryHandler {
    /**
     * Проверяет поддерживает ли обработчик переданный объект `Update`.
     *
     * @param update Объект `Update`, содержащий информацию о CallbackQuery.
     * @return `true`, если обработчик поддерживает переданный объект `Update`, иначе `false`.
     */
    boolean supports(Update update);

    /**
     * Обрабатывает CallbackQuery, содержащийся в переданном объекте `Update`.
     *
     * @param update Объект `Update`, содержащий информацию о CallbackQuery.
     */
    void handle(Update update);
}
