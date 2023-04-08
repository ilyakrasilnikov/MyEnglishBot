package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.AWSTranslator;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.dto.User.DialogueStep;
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
import java.util.Optional;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final BotCommandHandler botCommandHandler;
    private final BotCallbackQueryHandler botCallbackQueryHandler;
    private final UserRepository userRepo;
    private final AWSTranslator awsTranslator;

    public Bot(BotProperties botProperties,
               BotCommandHandler botCommandHandler,
               BotCallbackQueryHandler botCallbackQueryHandler,
               UserRepository userRepo,
               AWSTranslator awsTranslator) {
        this.botProperties = botProperties;
        this.botCommandHandler = botCommandHandler;
        this.botCallbackQueryHandler = botCallbackQueryHandler;
        this.userRepo = userRepo;
        this.awsTranslator = awsTranslator;
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
        if (update.hasCallbackQuery()) {
            processingCallbackQuery(this, update.getCallbackQuery());
        } else if (update.hasMessage()) {
            processingMessage(update.getMessage());
        }
    }

    public void sendMessage(User who,
                            String messageText) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.getId())
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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        SendMessage sm = SendMessage.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(who.getId())
                .text(messageText)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке встроенной клавиатуре", e);
        }
    }

    private void processingCallbackQuery(Bot bot,
                                         CallbackQuery callbackQuery) {
        org.telegram.telegrambots.meta.api.objects.User userTG = callbackQuery.getFrom();
        User user = User.convertTGUserToUser(userTG);
        userRepo.save(user);
        botCallbackQueryHandler.handleCallback(bot, user, callbackQuery);
    }

    private void processingMessage(Message msg) {

        User userFromMessage = User.convertTGUserToUser(msg.getFrom());
        Optional<User> userFromDbOptional = userRepo.findById(userFromMessage.getId());

        userFromMessage = userRepo.save(userFromDbOptional.isPresent()
                ? userFromDbOptional.get().toBuilder().username(userFromMessage.getUsername()).build()
                : userFromMessage);

        // Обработка команд бота
        if (botCommandHandler.handleCommand(this, userFromMessage, msg)) {
            // Если обработчик вернул true, значит команда обработана
            return;
        }

        if (userFromMessage.getDialogueStep() == DialogueStep.WAIT_FOR_TRANSLATION) {
            // Если пользователь находится в режиме ожидания перевода, то выполняем перевод текста
            String translate = awsTranslator.translate(
                    userFromMessage.getSource(),
                    userFromMessage.getTarget(),
                    msg.getText());
            sendMessage(userFromMessage, translate);
            userFromMessage.setDialogueStep(null);
            userRepo.save(userFromMessage);
        } else {
            // Если пользователь не находится в режиме ожидания перевода, то отправляем ему его сообщение без изменений
            sendMessage(userFromMessage, msg.getText());
        }
    }
}
