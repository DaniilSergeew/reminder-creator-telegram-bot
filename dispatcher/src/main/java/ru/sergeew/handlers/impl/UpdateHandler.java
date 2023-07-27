package ru.sergeew.handlers.impl;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sergeew.core.TelegramBot;
import ru.sergeew.handlers.Handler;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Класс, отвечающий за обработку входящих обновлений от Telegram бота.
 */
@Component
public class UpdateHandler implements Handler {
    private final TelegramBot telegramBot;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    public UpdateHandler(@Lazy TelegramBot telegramBot,
                         MessageHandler messageHandler,
                         CallbackHandler callbackHandler) {
        this.telegramBot = telegramBot;
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
    }

    private Set<Handler> getHandlers() {
        Set<Handler> result = new LinkedHashSet<>();
        result.add(messageHandler);
        result.add(callbackHandler);
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
                .ifPresentOrElse(
                        handler -> handler.handle(update),
                        () -> setUnsupportedMessageTypeView(update));
    }

    private static final String UNSUPPORTED_MESSAGE_TYPE = "Unsupported message type";

    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = generateSendMessageWithText(update, UNSUPPORTED_MESSAGE_TYPE);
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setView(EditMessageText editMessageText) {
        telegramBot.sendAnswerMessage(editMessageText);
    }

    public void setView(DeleteMessage deleteMessage) {
        telegramBot.sendAnswerMessage(deleteMessage);
    }

    /**
     * Генерирует объект {@link SendMessage} с указанным текстом для отправки в Telegram чат на основе объекта
     * {@link Update}.
     *
     * @param update объект {@link Update} из Telegram API
     * @param text   текст сообщения для отправки
     * @return объект {@link SendMessage} с настроенными параметрами
     */
    private SendMessage generateSendMessageWithText(Update update, String text) {
        Message message = update.getMessage();
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
