package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.data.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final BotCommandHandler botCommandHandler;
    private final UserRepository userRepo;

    public Bot(BotProperties botProperties,
               BotCommandHandler botCommandHandler,
               UserRepository userRepo) {
        this.botProperties = botProperties;
        this.botCommandHandler = botCommandHandler;
        this.userRepo = userRepo;
        BotMenu.registerBotCommands(this);
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            org.telegram.telegrambots.meta.api.objects.User userTG = update.getMessage().getFrom();
            User user = User.saveUser(userRepo, userTG);
            Message msg = update.getMessage();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (callbackQuery != null) {
                processingCallbackQuery(callbackQuery);
            } else if (!processingBotCommands(this, user, msg)) {
                processingMessage(user, msg);
            }
        }
    }

    public void sendMessage(User who,
                            String messageText) {
        SendMessage message = new SendMessage(
                String.valueOf(who.getIdUser()),
                messageText);
        try {
            execute(message);
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
            throw new RuntimeException(e);
        }
    }

    public static List<List<InlineKeyboardButton>> buildInlineKeyboardMarkup(List<String> value,
                                                                             int cntButtonInRow) {
        List<List<InlineKeyboardButton>> valueList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String string : value) {
            if (row.size() >= cntButtonInRow) {
                valueList.add(row);
                row = new ArrayList<>();
            }
            row.add(InlineKeyboardButton
                    .builder()
                    .text(string)
                    .callbackData(string)
                    .build());
        }
        valueList.add(row);
        return valueList;
    }

    public boolean processingBotCommands(Bot bot,
                                         User user,
                                         Message msg) {
        return botCommandHandler.handleCommand(bot, user, msg);
    }

    private void processingCallbackQuery(CallbackQuery callbackQuery) {
    }

    private void processingMessage(User user, Message message) {
        sendMessage(user, message.getText());
    }

}
