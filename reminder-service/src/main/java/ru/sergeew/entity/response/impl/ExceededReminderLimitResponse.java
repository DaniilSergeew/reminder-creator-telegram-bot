package ru.sergeew.entity.response.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sergeew.entity.response.SendableResponse;

/**
 * Ответ, отправляемый пользователю в случае превышения лимита на количество активных напоминаний.
 * Реализует интерфейс {@link SendableResponse}.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExceededReminderLimitResponse implements SendableResponse {
    private String chatId;
    private static final String response = "Sorry, you have exceeded the maximum limit of active reminders. You can have up to 30 active reminders at a time.";

    @Override
    public SendMessage getSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(response)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
