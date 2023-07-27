package ru.sergeew.service.generators;

import ru.sergeew.entity.response.Response;

/**
 * Интерфейс для объекта-генератора ответов, который будет отправлять сообщения в очередь для дальнейшей отправки пользователям.
 */
public interface AnswerGenerator {
    /**
     * Отправить ответ в очередь для дальнейшей отправки пользователю.
     *
     * @param message ответ, который нужно отправить
     */
    void sendResponse(Response message);
}
