package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.data.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final UserRepository userRepo;

    public Bot(BotProperties botProperties,
               UserRepository userRepo) {
        this.botProperties = botProperties;
        this.userRepo = userRepo;
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
        org.telegram.telegrambots.meta.api.objects.User from = update.getMessage().getFrom();
        String text = update.getMessage().getText();
        User user = User.saveUser(userRepo, from);
        sendMessage(user, text);
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


}
