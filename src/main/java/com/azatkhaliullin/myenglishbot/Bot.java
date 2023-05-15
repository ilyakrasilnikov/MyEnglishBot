package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.service.AwsService;
import com.azatkhaliullin.myenglishbot.dto.Language;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.handler.BotCallbackQueryHandler;
import com.azatkhaliullin.myenglishbot.handler.BotCommandHandler;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.dto.User.DialogueStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

/**
 * This class represents a Telegram bot that extends TelegramLongPollingBot class. It is used to interact with users through Telegram and perform actions based on user input.
 * <p>
 * It requires BotProperties, BotCommandHandler, BotCallbackQueryHandler, UserRepository and Aws objects as input parameters for its constructor.
 */
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final BotCommandHandler botCommandHandler;
    private final BotCallbackQueryHandler botCallbackQueryHandler;
    private final UserRepository userRepo;
    private final AwsService awsService;

    public Bot(BotProperties botProperties,
               BotCommandHandler botCommandHandler,
               BotCallbackQueryHandler botCallbackQueryHandler,
               UserRepository userRepo,
               AwsService awsService) {
        this.botProperties = botProperties;
        this.botCommandHandler = botCommandHandler;
        this.botCallbackQueryHandler = botCallbackQueryHandler;
        this.userRepo = userRepo;
        this.awsService = awsService;
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

    /**
     * This method is called whenever an update is received by the bot.
     * It checks whether the update contains a callback query or a message and performs the corresponding action.
     *
     * @param update an Update object representing the update received by the bot.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            processingCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            processingMessage(update.getMessage());
        }
    }

    /**
     * Sends a message to the target user.
     *
     * @param user        a User object representing the user to whom the message will be sent.
     * @param messageText a String representing the text of the message to be sent.
     */
    public void sendMessage(User user,
                            String messageText) {
        SendMessage sm = SendMessage.builder()
                .chatId(user.getId())
                .text(messageText)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Error when sending a message", e);
        }
    }

    /**
     * Sends a voice message to the target user.
     *
     * @param user        a User object representing the user to whom the voice message will be sent.
     * @param messageText a String representing the text of the voice message.
     */
    public void sendVoice(User user,
                          String messageText) {
        byte[] voiceBytes = awsService.polly(
                user.getTarget(),
                messageText);
        InputStream inputStream = new ByteArrayInputStream(voiceBytes);
        InputFile voiceFile = new InputFile()
                .setMedia(inputStream, messageText);
        SendVoice sv = SendVoice.builder()
                .chatId(user.getId())
                .voice(voiceFile)
                .build();
        try {
            execute(sv);
        } catch (TelegramApiException e) {
            log.error("Error when sending a voice message", e);
        }
    }

    /**
     * Sends a message with an inline keyboard to the target user.
     *
     * @param user        the user to send the message to.
     * @param messageText the text of the message to send.
     * @param lists       a list of lists of inline keyboard buttons to display.
     */
    public void sendInlineKeyboard(User user,
                                   String messageText,
                                   List<List<InlineKeyboardButton>> lists) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        SendMessage sm = SendMessage.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(user.getId())
                .text(messageText)
                .build();
        try {
            user.setInlineMessageId(execute(sm).getMessageId());
            userRepo.save(user);
        } catch (TelegramApiException e) {
            log.error("Error when sending a inline keyboard", e);
        }
    }

    /**
     * Edits the text and inline keyboard of a message previously sent to the target user.
     *
     * @param user        the user that received the original message.
     * @param messageText the new text for the message.
     * @param lists       a list of lists of inline keyboard buttons to display.
     */
    public void editMessageWithInline(User user,
                                      String messageText,
                                      List<List<InlineKeyboardButton>> lists) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        EditMessageText editMessageText = EditMessageText.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(user.getId())
                .text(messageText)
                .messageId(user.getInlineMessageId())
                .build();
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Error when sending a modified inline keyboard", e);
        }
    }

    /**
     * Handles a callback query received from Telegram.
     *
     * @param callbackQuery the callback query to handle.
     */
    private void processingCallbackQuery(CallbackQuery callbackQuery) {
        User user = toUser(callbackQuery.getFrom());
        botCallbackQueryHandler.handleCallback(this, user, callbackQuery);
    }

    /**
     * Handles a regular text message received from Telegram.
     *
     * @param msg the message to handle.
     */
    private void processingMessage(Message msg) {
        User userFromMessage = toUser(msg.getFrom());

        if (botCommandHandler.handleCommand(this, userFromMessage, msg)) {
            return;
        }

        if (userFromMessage.getDialogueStep() == DialogueStep.WAIT_FOR_TRANSLATION) {
            Language target = userFromMessage.getTarget();
            String translate = awsService.translate(
                    userFromMessage.getSource(),
                    target,
                    msg.getText());
            List<String> list = List.of("Прослушать перевод");
            sendInlineKeyboard(
                    userFromMessage,
                    translate,
                    BotUtility.buildInlineKeyboardMarkup(list
                                    .stream().map(item -> Pair.of(
                                            item, BotUtility.InlineKeyboardType.VOICE.name() + "/" + translate))
                                    .collect(Collectors.toList()),
                            1));
            userFromMessage.setDialogueStep(null);
            userRepo.save(userFromMessage);
        } else {
            sendMessage(userFromMessage, msg.getText());
        }
    }

    /**
     * Converts a Telegram User object to an application User object, saving it to the database if necessary.
     *
     * @param userTG the Telegram User object to convert.
     * @return the relevant application User object.
     */
    private User toUser(org.telegram.telegrambots.meta.api.objects.User userTG) {
        User userFromMessage = User.builder()
                .id(userTG.getId())
                .username(userTG.getUserName())
                .build();
        Optional<User> userFromDbOptional = userRepo.findById(userFromMessage.getId());
        if (userFromDbOptional.isPresent()) {
            if (userFromDbOptional.get().getUsername().equals(userFromMessage.getUsername())) {
                return userFromDbOptional.get();
            } else {
                return userRepo.save(
                        userFromDbOptional.get().toBuilder().username(userFromMessage.getUsername()).build());
            }
        }
        return userRepo.save(userFromMessage);
    }
}
