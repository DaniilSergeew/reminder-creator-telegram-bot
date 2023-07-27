package ru.sergeew.service.handlers.response.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sergeew.entity.response.SendableResponse;
import ru.sergeew.service.ProducerService;
import ru.sergeew.service.handlers.response.ResponseHandler;
import ru.sergeew.entity.response.Response;

@Component
@AllArgsConstructor
public class SendMessageResponseHandler implements ResponseHandler {
    private final ProducerService producerService;

    @Override
    public boolean supports(Response response) {
        return response instanceof SendableResponse;
    }

    @Override
    public void handle(Response response) {
        SendableResponse sendableResponse = (SendableResponse) response;
        SendMessage message = sendableResponse.getSendMessage();
        producerService.produceSendMessage(message);
    }
}
