package ru.sergeew.service.handlers.callback.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.service.handlers.callback.CallbackQueryHandler;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {
    private final PageActionHandler pageActionHandler;
    private final BackActionHandler backActionHandler;
    private final PageWithRemovalActionHandler pageWithRemovalActionHandler;
    private final RemoveActionHandler removeActionHandler;

    private Set<CallbackQueryHandler> getHandlers() {
        Set<CallbackQueryHandler> result = new HashSet<>();
        result.add(pageActionHandler);
        result.add(backActionHandler);
        result.add(pageWithRemovalActionHandler);
        result.add(removeActionHandler);
        return result;
    }

    @Override
    public boolean supports(Update update) {
        return true;
    }

    @Override
    public void handle(Update update) {
        getHandlers().stream()
                .filter(handler -> handler.supports(update))
                .findFirst()
                .ifPresent(handler -> handler.handle(update));
    }
}
