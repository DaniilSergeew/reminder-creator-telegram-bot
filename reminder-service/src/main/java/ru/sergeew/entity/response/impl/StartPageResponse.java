package ru.sergeew.entity.response.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sergeew.entity.response.SendableResponse;
import ru.sergeew.entity.enums.Emoji;

/**
 * Ответ на команду /start.
 * Реализует интерфейс {@link SendableResponse}.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StartPageResponse implements SendableResponse {
    private final String START_SERVICE_COMMAND_ANSWER_TEXT = String.format("""
            %s Set reminders in your own words!
            Just send a text message.

            For example:
            - call the boss in 20 minutes
            - dentist on Monday at 18
            - 13-30 lunch
            - tax office tomorrow at 14
            - on September 16 at 10.20 post office
            - 04/17/2017 at 9:15 to congratulate a colleague on his birthday
            - draw up the documents wednesday morning
            - May 9 at 7 pm buy tickets

            Enter /list to view or delete reminders by number.

            Good luck!
            """, Emoji.DATE.get());

    private String chatId;

    @Override
    public SendMessage getSendMessage() {
        return SendMessage.builder()
                .chatId(chatId)
                .text(START_SERVICE_COMMAND_ANSWER_TEXT)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
