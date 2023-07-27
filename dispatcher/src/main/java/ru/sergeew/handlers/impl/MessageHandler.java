package ru.sergeew.handlers.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.handlers.Handler;
import ru.sergeew.service.impl.ProducerServiceImpl;

import static ru.sergeew.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@AllArgsConstructor
public class MessageHandler implements Handler {
    private final ProducerServiceImpl updateProducer;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public void handle(Update update) {
        processTextMessage(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }
}
