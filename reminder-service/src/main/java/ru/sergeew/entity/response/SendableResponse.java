package ru.sergeew.entity.response;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Интерфейс для ответов, которые будут отправляться новым сообщением в чат.
 */
public interface SendableResponse extends Response {
    SendMessage getSendMessage();
}
