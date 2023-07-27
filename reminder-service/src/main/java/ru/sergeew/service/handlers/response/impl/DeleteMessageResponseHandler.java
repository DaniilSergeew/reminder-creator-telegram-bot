package ru.sergeew.service.handlers.response.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.sergeew.entity.response.DeletableResponse;
import ru.sergeew.service.ProducerService;
import ru.sergeew.service.handlers.response.ResponseHandler;
import ru.sergeew.entity.response.Response;

@Component
@AllArgsConstructor
public class DeleteMessageResponseHandler implements ResponseHandler {
    private final ProducerService producerService;

    @Override
    public boolean supports(Response response) {
        return response instanceof DeletableResponse;
    }

    @Override
    public void handle(Response response) {
        DeletableResponse deletableResponse = (DeletableResponse) response;
        DeleteMessage message = deletableResponse.getDeleteMessage();
        producerService.produceDeleteResponseMessage(message);
    }
}
