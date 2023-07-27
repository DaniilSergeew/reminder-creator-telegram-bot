package ru.sergeew.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.sergeew.handlers.impl.UpdateHandler;
import ru.sergeew.service.ConsumerService;

import static ru.sergeew.model.RabbitQueue.*;

/**
 * Реализация интерфейса {@link ConsumerService}, представляющая методы для потребления ответных сообщений из RabbitMQ очереди.
 */
@Service
@Log4j
@AllArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final UpdateHandler updateHandler;

    @Override
    @RabbitListener(queues = RESPONSE_MESSAGE)
    public void consumeResponseMessage(SendMessage sendMessage) {
        log.info("Received SendMessage: " + sendMessage);
        updateHandler.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = EDIT_RESPONSE_MESSAGE)
    public void consumeEditResponseMessage(EditMessageText editMessageText) {
        log.info("Received EditMessageText: " + editMessageText);
        updateHandler.setView(editMessageText);
    }

    @Override
    @RabbitListener(queues = DELETE_RESPONSE_MASSAGE)
    public void consumeDeleteResponseMessage(DeleteMessage deleteMessage) {
        log.info("Received DeleteMessage: " + deleteMessage);
        updateHandler.setView(deleteMessage);
    }
}
