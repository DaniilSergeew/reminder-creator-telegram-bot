package ru.sergeew.service.handlers.response;

import ru.sergeew.entity.response.Response;

/**
 * Базовый интерфейс для отправки в очереди объектов, реализующих интерфейс Response.
 * Наследники этого интерфейса будут отправлять ответы в конкретные очереди.
 */
public interface ResponseHandler {
    /**
     * Проверяет поддерживает ли обработчик переданный объект `Response`.
     *
     * @param message Объект, реализующий интерфейс `Response`.
     * @return `true`, если обработчик поддерживает переданный объект `Response`, иначе `false`.
     */
    boolean supports(Response message);

    /**
     * Отправляет объект, реализующий интерфейс Response в нужную очередь.
     *
     * @param message Объект, реализующий интерфейс `Response`.
     */
    void handle(Response message);
}
