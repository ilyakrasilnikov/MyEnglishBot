package com.azatkhaliullin.myenglishbot.dto;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;

interface CommandHandler {
    void handleCommand(Bot bot,
                       User user,
                       Message message);
}

public class BotCommandHandler {
    private final Map<String, CommandHandler> commands;

    public BotCommandHandler() {
        commands = new HashMap<>();
        commands.put("/start", this::handleStartCommand);
        commands.put("/help", this::handleHelpCommand);
        commands.put("/translate", this::handleTranslateCommand);
    }

    public boolean handleCommand(Bot bot, User user, Message message) {
        CommandHandler handler = commands.get(message.getText());
        if (handler != null) {
            handler.handleCommand(bot, user, message);
            return true;
        }
        return false;
    }

    private void handleStartCommand(Bot bot, User user, Message message) {
        bot.sendMessage(user, BotMenu.loadResourceAsString("botMenuFiles/start.txt"));
    }

    private void handleHelpCommand(Bot bot, User user, Message message) {
        bot.sendMessage(user, BotMenu.loadResourceAsString("botMenuFiles/help.txt"));
    }

    private void handleTranslateCommand(Bot bot, User user, Message message) {
        bot.sendInlineKeyboard(user, "Выберите языковую пару для перевода",
                Bot.buildInlineKeyboardMarkup(BotMenu.processingTranslateCommand(), 2));
    }

    // Остальные методы
}

