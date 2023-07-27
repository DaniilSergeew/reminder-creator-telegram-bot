package ru.sergeew.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.sergeew.entity.AppUser;
import ru.sergeew.entity.Reminder;
import ru.sergeew.entity.response.impl.*;
import ru.sergeew.repository.AppUserRepository;
import ru.sergeew.repository.ReminderRepository;
import ru.sergeew.service.ReminderService;
import ru.sergeew.service.enums.ServiceCommand;
import ru.sergeew.service.generators.impl.AnswerGeneratorImpl;
import ru.sergeew.service.handlers.callback.impl.CallbackQueryHandlerImpl;
import ru.sergeew.entity.utils.CallbackData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Этот класс реализует интерфейс {@link ReminderService} и предоставляет
 * основную логику для обработки текстовых сообщений в боте Telegram.
 * Обрабатывает команды бота, работает с напоминаниями и пользователями.
 */
@Log4j
@EnableScheduling
@Service
@AllArgsConstructor
public class ReminderServiceImpl implements ReminderService {
    private final AppUserRepository appUserRepository;
    private final ReminderRepository reminderRepository;
    private final CallbackQueryHandlerImpl callbackQueryHandler;
    private final AnswerGeneratorImpl answerGenerator;

    /**
     * Главный обработчик текстовых сообщений.
     *
     * @param update объект {@link Update}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processTextMessage(Update update) {
        AppUser appUser = findOrSaveAppUser(update);
        String text = update.getMessage().getText();
        Optional<ServiceCommand> optionalServiceCommand = ServiceCommand.fromValue(text);
        optionalServiceCommand.ifPresentOrElse(
                serviceCommand -> processServiceCommand(serviceCommand, update),
                () -> processCreateReminderRequest(update, appUser)
        );
    }

    /**
     * Метод - отправщик напоминаний
     */
    @Scheduled(fixedRateString = "${reminder.scheduler.check.interval}")
    @Transactional
    public void retrieveAndSendReminders() {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.systemDefault());
        List<Reminder> unsentReminders = reminderRepository.findUnsentRemindersBeforeDateTime(currentDateTime);
        for (Reminder reminder : unsentReminders) {
            ReminderResponse reminderResponse = new ReminderResponse(reminder);
            answerGenerator.sendResponse(reminderResponse);
            log.info("Sent reminder to user. Reminder ID: " + reminder.getId());
        }
        reminderRepository.markRemindersAsSent(unsentReminders);
    }

    /**
     * Главный обработчик CallbackQuery
     */
    @Override
    public void processCallbackQuery(Update update) {
        if (callbackQueryHandler.supports(update)) {
            callbackQueryHandler.handle(update);
        }
    }

    /**
     * Обрабатывает сервисные команды на основе переданного объекта {@link ServiceCommand} и обновления {@link Update}.
     *
     * @param serviceCommand объект {@link ServiceCommand}, представляющий сервисную команду для обработки.
     * @param update         объект {@link Update}, представляющий обновление от Telegram API, связанное с сервисной командой.
     */
    private void processServiceCommand(ServiceCommand serviceCommand, Update update) {
        switch (serviceCommand) {
            case START -> processStartServiceCommand(update);
            case LIST -> processListServiceCommand(update);
        }
    }

    /**
     * Обрабатывает запрос на создание нового напоминания на основе объекта обновления {@link Update} и объекта пользователя {@link AppUser}.
     *
     * @param update  объект {@link Update}, представляющий обновление от Telegram API, связанное с запросом на создание напоминания.
     * @param appUser объект {@link AppUser}, представляющий пользователя, для которого создается напоминание.
     */
    private void processCreateReminderRequest(Update update, AppUser appUser) {
        String textOfMessage = update.getMessage().getText();
        Optional<Reminder> optionalReminder = createReminderFromText(textOfMessage, appUser);
        String chatId = appUser.getChatId().toString();
        optionalReminder.ifPresentOrElse(
                reminder -> {
                    if (reminderRepository.canCreateNewReminder(appUser)) {
                        reminderRepository.save(reminder);
                        ReminderCreatedResponse response = new ReminderCreatedResponse(chatId, reminder);
                        answerGenerator.sendResponse(response);
                        log.info("Created new reminder for user. Reminder ID: " + reminder.getId());
                    } else {
                        ExceededReminderLimitResponse response = new ExceededReminderLimitResponse(chatId);
                        answerGenerator.sendResponse(response);
                        log.info("Failed to create reminder for user. Exceeded reminder limit.");
                    }
                },
                () -> {
                    UnrecognizedReminderResponse response = new UnrecognizedReminderResponse(chatId);
                    answerGenerator.sendResponse(response);
                    log.info("Failed to create reminder for user. Unrecognized reminder format.");
                }
        );
    }

    /**
     * Ищет пользователя в базе данных на основе объекта обновления {@link Update} и сохраняет его, если он отсутствует.
     * Если пользователь уже существует, и его chatId изменился, старые данные удаляются, и пользователь считается новым.
     *
     * @param update объект {@link Update}, представляющий обновление от Telegram API, связанное с пользователем.
     * @return объект {@link AppUser}, представляющий пользователя из базы данных или нового созданного пользователя.
     */
    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        long telegramUserId = telegramUser.getId();
        long chatId = update.getMessage().getChatId();
        Optional<AppUser> optionalAppUser = appUserRepository.findByTelegramUserId(telegramUserId);
        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            if (appUser.getChatId() != chatId) {
                // Если chatId изменился, то пользователь удалил бота ->
                // удаляем старые данные и работаем с пользователем, как будто он новый
                deleteAppUserByChatId(appUser.getChatId());
                log.info("Deleted user data for user with chatId: " + chatId);
                appUser.setChatId(chatId);
                appUserRepository.save(appUser);
                log.info("Updated chatId for existing user. User ID: " + appUser.getId());
            }
            return appUser;
        } else {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUserId)
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .chatId(chatId)
                    .build();
            AppUser savedAppUser = appUserRepository.save(transientAppUser);
            log.info("Created new user. User ID: " + savedAppUser.getId());
            return savedAppUser;
        }
    }

    /**
     * Ищет пользователя в базе данных на основе объекта {@link CallbackQuery}.
     *
     * @param callbackQuery объект {@link CallbackQuery}, представляющий запрос обратного вызова от Telegram API.
     * @return объект {@link AppUser}, представляющий пользователя из базы данных, если он найден, иначе возвращает null.
     */
    private Optional<AppUser> findAppUser(CallbackQuery callbackQuery) {
        User telegramUser = callbackQuery.getFrom();
        return appUserRepository.findByTelegramUserId(telegramUser.getId());
    }

    /**
     * Обрабатывает сервисную команду "/start" путем отправки ответа пользователю с помощью объекта {@link AnswerGeneratorImpl}.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    private void processStartServiceCommand(Update update) {
        StartPageResponse response = new StartPageResponse(update.getMessage().getChatId().toString());
        answerGenerator.sendResponse(response);
    }

    private final int PAGE_SIZE = 10;

    /**
     * Обрабатывает сервисную команду "/list" путем отправки пользователю списка напоминаний с помощью объекта {@link AnswerGeneratorImpl}.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    private void processListServiceCommand(Update update) {
        AppUser appUser = findOrSaveAppUser(update);
        String chatId = appUser.getChatId().toString();

        List<Reminder> reminders = reminderRepository.findUnsentRemindersByAppUser(appUser);
        int startIndex = 0;
        int endIndex = Math.min(startIndex + PAGE_SIZE, reminders.size());
        List<Reminder> pageReminders = reminders.subList(startIndex, endIndex);

        boolean hasNextPageReminders = endIndex < reminders.size();

        InitialRemindersPageResponse response = new InitialRemindersPageResponse(chatId, pageReminders, hasNextPageReminders);
        answerGenerator.sendResponse(response);
    }

    /**
     * Обрабатывает нажатие на кнопки пагинации путем листания списка напоминаний и отправки пользователю новой страницы
     * с помощью объекта {@link AnswerGeneratorImpl}.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    public void processPageAction(Update update) {
        Optional<AppUser> optionalAppUser = findAppUser(update.getCallbackQuery());
        if (optionalAppUser.isEmpty()) {
            return;
        }
        AppUser appUser = optionalAppUser.get();
        String chatId = appUser.getChatId().toString();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();

        Optional<CallbackData> optionalCallbackData = CallbackData.fromString(update.getCallbackQuery().getData());
        if (optionalCallbackData.isEmpty()) {
            return;
        }
        CallbackData callbackData = optionalCallbackData.get();
        int pageNumber = callbackData.getPage();

        List<Reminder> reminders = reminderRepository.findUnsentRemindersByAppUser(appUser);
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, reminders.size());
        List<Reminder> pageReminders = reminders.subList(startIndex, endIndex);

        RemindersPageResponse response = new RemindersPageResponse(chatId,
                messageId,
                pageReminders,
                pageNumber,
                startIndex > 0,
                endIndex < reminders.size());
        answerGenerator.sendResponse(response);
    }

    /**
     * Обрабатывает пагинацию в меню удаления напоминаний.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    public void processPageWithRemovalAction(Update update) {
        Optional<AppUser> optionalAppUser = findAppUser(update.getCallbackQuery());
        if (optionalAppUser.isEmpty()) {
            return;
        }
        AppUser appUser = optionalAppUser.get();
        String chatId = appUser.getChatId().toString();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();

        Optional<CallbackData> optionalCallbackData = CallbackData.fromString(update.getCallbackQuery().getData());
        if (optionalCallbackData.isEmpty()) {
            return;
        }
        CallbackData callbackData = optionalCallbackData.get();
        int pageNumber = callbackData.getPage();

        List<Reminder> reminders = reminderRepository.findUnsentRemindersByAppUser(appUser);
        int startIndex = (pageNumber - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, reminders.size());
        List<Reminder> pageReminders = reminders.subList(startIndex, endIndex);

        RemindersPageWithDeleteButtonsResponse response = new RemindersPageWithDeleteButtonsResponse(chatId,
                messageId,
                pageReminders,
                pageNumber,
                startIndex > 0,
                endIndex < reminders.size());
        answerGenerator.sendResponse(response);
    }

    /**
     * Обрабатывает удаление напоминаний.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    public void processRemoveAction(Update update) {
        Optional<AppUser> optionalAppUser = findAppUser(update.getCallbackQuery());
        if (optionalAppUser.isEmpty()) {
            return;
        }
        AppUser appUser = optionalAppUser.get();
        String chatId = appUser.getChatId().toString();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();

        Optional<CallbackData> optionalCallbackData = CallbackData.fromString(update.getCallbackQuery().getData());
        if (optionalCallbackData.isEmpty()) {
            return;
        }

        CallbackData callbackData = optionalCallbackData.get();
        Long id = callbackData.getId();
        // Если напоминание стало не активным за время, пока список висит в чате, то не удаляем его
        if (!reminderRepository.isReminderSent(id)) {
            reminderRepository.deleteById(id);
            log.info("Deleted reminder with ID " + id);
        }

        List<Reminder> reminders = reminderRepository.findUnsentRemindersByAppUser(appUser);

        int totalReminders = reminders.size();
        int currentPage = callbackData.getPage();
        int newPage = (totalReminders - 1) / PAGE_SIZE + 1;
        if (currentPage <= newPage) {
            newPage = currentPage;
        } else {
            newPage = Math.max(currentPage - 1, 1);
        }
        int startIndex = (newPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, reminders.size());
        List<Reminder> pageReminders = reminders.subList(startIndex, endIndex);
        RemindersPageWithDeleteButtonsResponse response = new RemindersPageWithDeleteButtonsResponse(chatId,
                messageId,
                pageReminders,
                newPage,
                startIndex > 0,
                endIndex < reminders.size());
        answerGenerator.sendResponse(response);
    }

    /**
     * Обрабатывает нажатие кнопки Back в меню удаления и возвращает пользователя в меню пагинации.
     *
     * @param update объект {@link Update}, представляющий полученное обновление от Telegram API.
     */
    public void processBackAction(Update update) {
        processPageAction(update);
    }

    /**
     * Создает {@link Reminder} на основе текста сообщения и объекта пользователя.
     *
     * @param textOfMessage Текст сообщения, из которого будет создано напоминание.
     * @param appUser       Объект пользователя, для которого создается напоминание.
     * @return Optional с созданным напоминанием, если дата была успешно распознана из текста сообщения, иначе пустой Optional.
     */
    public Optional<Reminder> createReminderFromText(String textOfMessage, AppUser appUser) {
        // Парсер плохо работает с тире, поэтому заменяем его на двоеточие
        List<java.util.Date> dates = new PrettyTimeParser().parse(textOfMessage.replace("-", ":"));
        return dates.stream()
                .findFirst()
                .map(date -> {
                    LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    return Reminder.builder()
                            .sendTime(localDateTime)
                            .message(textOfMessage)
                            .appUser(appUser)
                            .build();
                });
    }

    /**
     * Удаляет пользователя из базы данных на основе его chatId.
     *
     * @param chatId идентификатор чата пользователя, которого необходимо удалить.
     */
    private void deleteAppUserByChatId(long chatId) {
        Optional<AppUser> optionalAppUser = appUserRepository.findByChatId(chatId);
        optionalAppUser.ifPresent(appUser -> {
            reminderRepository.deleteAllByAppUser(appUser);
            appUserRepository.delete(appUser);
        });
    }
}
