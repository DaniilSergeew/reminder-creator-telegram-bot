package ru.sergeew.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Интерфейс, представляющий слушателя RabbitMQ очереди для приема ответных сообщений от бота.
 */
public interface ConsumerService {
    /**
     * Метод для приема сообщения из бота.
     *
     * @param sendMessage Объект {@link SendMessage}, содержащий информацию о сообщении.
     */
    void consumeResponseMessage(SendMessage sendMessage);

    /**
     * Метод для приема измененного сообщения из бота.
     *
     * @param editMessageText Объект {@link EditMessageText}, содержащий информацию об измененном сообщении.
     */
    void consumeEditResponseMessage(EditMessageText editMessageText);

    void consumeDeleteResponseMessage(DeleteMessage deleteMessage);
}
