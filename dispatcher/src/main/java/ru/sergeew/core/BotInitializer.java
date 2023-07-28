package ru.sergeew.core;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Класс-инициализатор бота.
 * Регистрирует бота при событии запуска контекста приложения.
 */
@Log4j
@Component
@AllArgsConstructor
public class BotInitializer {
    private final TelegramBot bot;

    /**
     * Метод-обработчик события запуска контекста приложения.
     * Регистрирует бота при запуске контекста приложения.
     *
     * @throws TelegramApiException в случае ошибки при регистрации бота
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
            log.info("The bot has been successfully registered.");
        } catch (TelegramApiException e) {
            log.error("Error to register bot: " + e.getMessage());
        }
    }
}
