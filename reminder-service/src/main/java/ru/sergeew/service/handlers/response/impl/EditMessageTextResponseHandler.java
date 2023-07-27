package ru.sergeew.service.handlers.response.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.sergeew.entity.response.EditableResponse;
import ru.sergeew.service.ProducerService;
import ru.sergeew.service.handlers.response.ResponseHandler;
import ru.sergeew.entity.response.Response;

@Component
@AllArgsConstructor
public class EditMessageTextResponseHandler implements ResponseHandler {
    private final ProducerService producerService;

    @Override
    public boolean supports(Response response) {
        return response instanceof EditableResponse;
    }

    @Override
    public void handle(Response response) {
        EditableResponse editableResponse = (EditableResponse) response;
        EditMessageText message = editableResponse.getEditMessageText();
        producerService.produceEditMessageText(message);
    }
}
