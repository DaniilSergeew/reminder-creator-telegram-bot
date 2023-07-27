package ru.sergeew.service.handlers.response.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sergeew.service.handlers.response.ResponseHandler;
import ru.sergeew.entity.response.Response;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class ResponseHandlerImpl implements ResponseHandler {
    private final DeleteMessageResponseHandler deleteMessageResponseHandler;
    private final EditMessageTextResponseHandler editMessageTextResponseHandler;
    private final SendMessageResponseHandler sendMessageResponseHandler;


    private Set<ResponseHandler> getHandlers() {
        Set<ResponseHandler> result = new HashSet<>();
        result.add(deleteMessageResponseHandler);
        result.add(editMessageTextResponseHandler);
        result.add(sendMessageResponseHandler);
        return result;
    }

    @Override
    public boolean supports(Response message) {
        return true;
    }

    @Override
    public void handle(Response message) {
        getHandlers().stream()
                .filter(handler -> handler.supports(message))
                .findFirst()
                .ifPresentOrElse(handler -> handler.handle(message),
                        () -> System.out.println("ResponseHandlerImpl: Хендлера не нашлось"));
    }
}
