package ru.sergeew.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.sergeew.service.ProducerService;

import static ru.sergeew.model.RabbitQueue.*;

/**
 * Этот класс реализует интерфейс {@link ProducerService} и предоставляет
 * реализацию для отправки сообщений в RabbitMQ. Он принимает на вход
 * текстовые сообщения и отправляет их в соответствующую очередь RabbitMQ
 * для дальнейшей отправки в чат.
 */
@Service
@Log4j
@AllArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceSendMessage(SendMessage sendMessage) {
        log.info("Sent SendMessage to RabbitMQ: " + sendMessage);
        rabbitTemplate.convertAndSend(RESPONSE_MESSAGE, sendMessage);
    }

    @Override
    public void produceEditMessageText(EditMessageText editMessageText) {
        log.info("Sent EditMessageText to RabbitMQ: " + editMessageText);
        rabbitTemplate.convertAndSend(EDIT_RESPONSE_MESSAGE, editMessageText);
    }

    @Override
    public void produceDeleteResponseMessage(DeleteMessage deleteMessage) {
        log.info("Sent DeleteMessage to RabbitMQ: " + deleteMessage);
        rabbitTemplate.convertAndSend(DELETE_RESPONSE_MASSAGE, deleteMessage);
    }
}
