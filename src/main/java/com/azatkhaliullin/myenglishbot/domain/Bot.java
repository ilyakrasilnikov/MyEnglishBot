package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.AWSTranslator;
import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.dto.User.DialogueStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void sendMessage(User recipient,
                            String messageText) {
        SendMessage sm = SendMessage.builder()
                .chatId(recipient.getId())
                .text(messageText)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    public void sendVoice(User recipient,
                          String messageText) {
        byte[] voiceBytes = awsTranslator.getVoice(ITranslator.Language.EN, messageText);
        InputStream inputStream = new ByteArrayInputStream(voiceBytes);
        InputFile voiceFile = new InputFile()
                .setMedia(inputStream, messageText);
        SendVoice sv = SendVoice.builder()
                .chatId(recipient.getId())
                .voice(voiceFile)
                .build();
        try {
            execute(sv);
            sv.setDisableNotification(true);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке голового сообщения", e);
        }
    }

    public void sendInlineKeyboard(User recipient,
                                   String messageText,
                                   List<List<InlineKeyboardButton>> lists) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        SendMessage sm = SendMessage.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(recipient.getId())
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

        if (botCommandHandler.handleCommand(this, userFromMessage, msg)) {
            return;
        }

        if (userFromMessage.getDialogueStep() == DialogueStep.WAIT_FOR_TRANSLATION) {
            String translate = awsTranslator.getTranslate(
                    userFromMessage.getSource(),
                    userFromMessage.getTarget(),
                    msg.getText());
            List<String> singleton = List.of("Прослушать перевод");
            sendInlineKeyboard(
                    userFromMessage,
                    translate,
                    BotUtility.buildInlineKeyboardMarkup(singleton
                                    .stream().map(item -> Pair.of(
                                            item, BotUtility.KeyboardType.VOICE.name() + "/" + translate))
                                    .collect(Collectors.toList()),
                            1));
            userFromMessage.setDialogueStep(null);
            userRepo.save(userFromMessage);
        } else {
            sendMessage(userFromMessage, msg.getText());
        }
    }
}
