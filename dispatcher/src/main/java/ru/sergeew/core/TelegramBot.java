package ru.sergeew.core;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sergeew.handlers.impl.UpdateHandler;


/**
 * Класс Telegram-бота, расширяющий TelegramLongPollingBot.
 */
@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private final UpdateHandler updateHandler;

    public TelegramBot(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (updateHandler.supports(update)) {
            updateHandler.handle(update);
        }
    }

    /**
     * Отправляет ответное сообщение от бота.
     *
     * @param message объект {@link SendMessage} сообщения для отправки
     */
    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending the response: " + e.getMessage());
            }
        }
    }

    /**
     * Изменяет ответное сообщение от бота.
     *
     * @param message объект {@link SendMessage} сообщения для отправки
     */
    public void sendAnswerMessage(EditMessageText message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending the response: " + e.getMessage());
            }
        }
    }

    /**
     * Удаляет сообщение из чата.
     *
     * @param message объект {@link DeleteMessage} сообщения для отправки
     */
    public void sendAnswerMessage(DeleteMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error sending the response: " + e.getMessage());
            }
        }
    }
}
