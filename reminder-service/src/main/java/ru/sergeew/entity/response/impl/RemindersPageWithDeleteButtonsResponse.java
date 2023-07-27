package ru.sergeew.entity.response.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sergeew.entity.Reminder;
import ru.sergeew.entity.response.EditableResponse;
import ru.sergeew.entity.enums.Action;
import ru.sergeew.entity.enums.Emoji;
import ru.sergeew.entity.utils.CallbackData;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Ответ с информацией о странице меню удаления напоминаний и кнопками пагинации.
 * Реализует интерфейс {@link EditableResponse}.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RemindersPageWithDeleteButtonsResponse implements EditableResponse {
    private String chatId;
    private long messageId;
    private List<Reminder> pageReminders;
    private int pageNumber;
    private boolean hasPreviousPageReminders;
    private boolean hasNextPageReminders;

    @Override
    public EditMessageText getEditMessageText() {
        if (pageReminders.isEmpty()) {
            String text = Emoji.ZAP.get() + " No active reminders.";
            return EditMessageText.builder()
                    .chatId(chatId)
                    .messageId((int) messageId)
                    .text(text)
                    .parseMode(ParseMode.HTML)
                    .build();
        }
        String text = generateReminderPageAnswerText();
        InlineKeyboardMarkup markupInLine = generatePaginationInlineKeyboardMarkup();
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(text)
                .replyMarkup(markupInLine)
                .parseMode(ParseMode.HTML)
                .build();
    }

    public String generateReminderPageAnswerText() {
        StringBuilder sb = new StringBuilder();
        sb.append(Emoji.ZAP.get())
                .append(" Active reminders")
                .append("\n\n");
        int pageSize = 10;
        int startIndex = (pageNumber - 1) * pageSize + 1;
        for (int i = 0; i < pageReminders.size(); i++) {

            Reminder reminder = pageReminders.get(i);
            String sendTimeFormatted = reminder.getSendTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String dayOfWeek = switch (reminder.getSendTime().getDayOfWeek()) {
                case MONDAY -> "(Mon)";
                case TUESDAY -> "(Tue)";
                case WEDNESDAY -> "(Wed)";
                case THURSDAY -> "(Thu)";
                case FRIDAY -> "(Fri)";
                case SATURDAY -> "(Sat)";
                default -> "(Sun)";
            };

            String s = String.format("%s <b>%s %s</b>\n%s %s",
                    Emoji.CLOCK.get(),
                    sendTimeFormatted,
                    dayOfWeek,
                    Emoji.WAVY_DASH.get(),
                    reminder.getMessage());

            int reminderNumber = startIndex + i;
            sb.append(reminderNumber).append(") ").append(s).append("\n\n");
        }
        return sb.toString();
    }

    private InlineKeyboardMarkup generatePaginationInlineKeyboardMarkup() {
        int pageSize = 10;
        int startIndex = (pageNumber - 1) * pageSize + 1;

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        for (int i = 0; i < pageReminders.size(); i++) {
            int reminderNumber = startIndex + i;
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(String.valueOf(reminderNumber))
                    .callbackData(CallbackData.builder()
                            .action(Action.REMOVE)
                            .page(pageNumber)
                            .id(pageReminders.get(i).getId())
                            .build()
                            .toString())
                    .build();
            if (i < 5) {
                row1.add(button);
            } else {
                row2.add(button);
            }
        }
        rowsInLine.add(row1);
        rowsInLine.add(row2);
        List<InlineKeyboardButton> paginationLine = new ArrayList<>();
        if (hasPreviousPageReminders) {
            paginationLine.add(InlineKeyboardButton.builder()
                    .text("<")
                    .callbackData(CallbackData.builder()
                            .action(Action.PAGE_WITH_REMOVAL)
                            .page(pageNumber - 1)
                            .build()
                            .toString())
                    .build());
        }
        if (hasNextPageReminders) {
            paginationLine.add(InlineKeyboardButton.builder()
                    .text(">")
                    .callbackData(CallbackData.builder()
                            .action(Action.PAGE_WITH_REMOVAL)
                            .page(pageNumber + 1)
                            .build()
                            .toString())
                    .build());
        }
        rowsInLine.add(paginationLine);
        List<InlineKeyboardButton> backLine = new ArrayList<>();
        backLine.add(InlineKeyboardButton.builder()
                .text("« Back")
                .callbackData(CallbackData.builder()
                        .action(Action.BACK)
                        .page(pageNumber)
                        .build()
                        .toString())
                .build());
        rowsInLine.add(backLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
}
