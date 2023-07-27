package ru.sergeew.entity.response;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

/**
 * Интерфейс для ответов (пустых), которые будут удалять уже отправленными сообщениями.
 */
public interface DeletableResponse extends Response{
    DeleteMessage getDeleteMessage();
}
