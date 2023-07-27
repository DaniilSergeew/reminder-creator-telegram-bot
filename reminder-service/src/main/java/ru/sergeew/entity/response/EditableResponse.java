package ru.sergeew.entity.response;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Интерфейс для ответов, которые будут редактировать уже отправленные сообщения в чате.
 */
public interface EditableResponse extends Response {
    EditMessageText getEditMessageText();
}
