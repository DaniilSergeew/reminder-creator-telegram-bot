package ru.sergeew.handlers.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.handlers.Handler;
import ru.sergeew.service.impl.ProducerServiceImpl;

import static ru.sergeew.model.RabbitQueue.CALLBACK_QUERY;

@Component
@AllArgsConstructor
public class CallbackHandler implements Handler {
    private final ProducerServiceImpl updateProducer;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public void handle(Update update) {
        processCallbackQuery(update);
    }

    private void processCallbackQuery(Update update) {
        updateProducer.produce(CALLBACK_QUERY, update);
    }
}
