package ru.sergeew.entity.response.impl;

import lombok.AllArgsConstructor;
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
 * Ответ с информацией о странице с напоминаниями и кнопками пагинации.
 * Реализует интерфейс {@link EditableResponse}.
 */
@AllArgsConstructor
public class RemindersPageResponse implements EditableResponse {
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
        List<InlineKeyboardButton> paginationLine = new ArrayList<>();
        List<InlineKeyboardButton> removeLine = new ArrayList<>();
        if (hasPreviousPageReminders) {
            paginationLine.add(InlineKeyboardButton.builder()
                    .text("<")
                    .callbackData(CallbackData.builder()
                            .action(Action.PAGE)
                            .page(pageNumber - 1)
                            .build()
                            .toString())
                    .build());
        }
        if (hasNextPageReminders) {
            paginationLine.add(InlineKeyboardButton.builder()
                    .text(">")
                    .callbackData(CallbackData.builder()
                            .action(Action.PAGE)
                            .page(pageNumber + 1)
                            .build()
                            .toString())
                    .build());
        }
        InlineKeyboardButton removeButton = new InlineKeyboardButton();
        removeButton.setText("Select an item to remove");
        removeButton.setCallbackData(CallbackData.builder()
                .action(Action.PAGE_WITH_REMOVAL)
                .page(pageNumber)
                .build()
                .toString());
        removeLine.add(removeButton);

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(paginationLine);
        rowsInLine.add(removeLine);
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
}
