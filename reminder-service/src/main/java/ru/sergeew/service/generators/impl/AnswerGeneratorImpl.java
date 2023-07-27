package ru.sergeew.service.generators.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sergeew.service.generators.AnswerGenerator;
import ru.sergeew.service.handlers.response.impl.ResponseHandlerImpl;
import ru.sergeew.entity.response.Response;

/**
 * Реализация интерфейса AnswerGenerator, отвечающая за отправку ответов пользователям.
 */
@Component
@AllArgsConstructor
public class AnswerGeneratorImpl implements AnswerGenerator {
    private final ResponseHandlerImpl responseHandler;

    @Override
    public void sendResponse(Response message) {
        if (responseHandler.supports(message)) {
            responseHandler.handle(message);
        }
    }
}
