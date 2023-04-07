package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import com.azatkhaliullin.myenglishbot.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final BotCommandHandler botCommandHandler;
    private final BotCallbackQueryHandler botCallbackQueryHandler;
    private final UserRepository userRepo;

    public Bot(BotProperties botProperties,
               BotCommandHandler botCommandHandler,
               BotCallbackQueryHandler botCallbackQueryHandler,
               UserRepository userRepo) {
        this.botProperties = botProperties;
        this.botCommandHandler = botCommandHandler;
        this.botCallbackQueryHandler = botCallbackQueryHandler;
        this.userRepo = userRepo;
        BotUtility.registerBotCommands(this);
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (update.hasCallbackQuery()) {
            processingCallbackQuery(this, callbackQuery);
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            org.telegram.telegrambots.meta.api.objects.User userTG = update.getMessage().getFrom();
            User user = User.saveUser(userRepo, userTG);
            if (!processingBotCommands(this, user, msg)) {
                processingMessage(user, msg);
            }
        }
    }

    public void sendMessage(User who,
                            String messageText) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.getIdUser())
                .text(messageText)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    public void sendInlineKeyboard(User who,
                                   String messageText,
                                   List<List<InlineKeyboardButton>> lists) {
        SendMessage sm;
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        sm = SendMessage.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(who.getIdUser())
                .text(messageText)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке встроенной клавиатуре", e);
        }
    }

    public boolean processingBotCommands(Bot bot,
                                         User user,
                                         Message msg) {
        return botCommandHandler.handleCommand(bot, user, msg);
    }

    private void processingCallbackQuery(Bot bot,
                                         CallbackQuery callbackQuery) {
        org.telegram.telegrambots.meta.api.objects.User userTG = callbackQuery.getFrom();
        User user = User.saveUser(userRepo, userTG);
        botCallbackQueryHandler.handleCallback(bot, user, callbackQuery);
    }

    private void processingMessage(User user, Message message) {
        sendMessage(user, message.getText());
    }

}
