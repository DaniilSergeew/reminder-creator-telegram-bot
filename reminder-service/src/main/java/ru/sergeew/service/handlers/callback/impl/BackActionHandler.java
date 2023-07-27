package ru.sergeew.service.handlers.callback.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.service.handlers.callback.CallbackQueryHandler;
import ru.sergeew.entity.enums.Action;
import ru.sergeew.service.impl.ReminderServiceImpl;
import ru.sergeew.entity.utils.CallbackData;

import java.util.Optional;

@Component
public class BackActionHandler implements CallbackQueryHandler {
    private final ReminderServiceImpl reminderService;

    public BackActionHandler(@Lazy ReminderServiceImpl reminderService) {
        this.reminderService = reminderService;
    }

    @Override
    public boolean supports(Update update) {
        Optional<CallbackData> optionalCallbackData = CallbackData.fromString(update.getCallbackQuery().getData());
        if (optionalCallbackData.isEmpty()) {
            return false;
        }
        CallbackData callbackData = optionalCallbackData.get();
        return callbackData.getAction() == Action.BACK;
    }

    @Override
    public void handle(Update update) {
        reminderService.processBackAction(update);
    }
}
