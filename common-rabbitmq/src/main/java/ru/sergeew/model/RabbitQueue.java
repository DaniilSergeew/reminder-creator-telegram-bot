package ru.sergeew.model;

/**
 * Класс, содержащий константы имен очередей RabbitMQ.
 * Определяет имена очередей для различных типов сообщений.
 */
public class RabbitQueue {
    public static final String TEXT_MESSAGE_UPDATE = "text_message_update";
    public static final String RESPONSE_MESSAGE = "response_message";
    public static final String CALLBACK_QUERY = "callback_query";
    public static final String EDIT_RESPONSE_MESSAGE = "edit_response_message";
    public static final String DELETE_RESPONSE_MASSAGE = "delete_response_message";
}
