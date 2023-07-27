package ru.sergeew.entity.response.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sergeew.entity.Reminder;
import ru.sergeew.entity.enums.Emoji;
import ru.sergeew.entity.response.SendableResponse;

/**
 * Ответ с информацией о напоминании.
 * Реализует интерфейс {@link SendableResponse}.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReminderResponse implements SendableResponse {
    private String chatId;
    private Reminder reminder;

    public ReminderResponse(Reminder reminder) {
        this.reminder = reminder;
        this.chatId = reminder.getAppUser().getChatId().toString();
    }

    @Override
    public SendMessage getSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(Emoji.ORANGE_CIRCLE.get() + "   " + reminder.getMessage())
                .parseMode(ParseMode.HTML)
                .build();
    }
}
