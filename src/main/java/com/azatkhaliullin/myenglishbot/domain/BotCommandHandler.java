package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator.Language;
import com.azatkhaliullin.myenglishbot.dto.User;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FunctionalInterface
interface CommandHandler {
    void handleCommand(Bot bot,
                       User user);
}

public class BotCommandHandler {
    private final Map<String, CommandHandler> commands;

    public BotCommandHandler() {
        commands = new HashMap<>();
        commands.put("/start", this::handleStartCommand);
        commands.put("/help", this::handleHelpCommand);
        commands.put("/translate", this::handleTranslateCommand);
    }

    public boolean handleCommand(Bot bot,
                                 User user,
                                 Message message) {
        CommandHandler handler = commands.get(message.getText());
        if (handler != null) {
            handler.handleCommand(bot, user);
            return true;
        }
        return false;
    }

    private void handleStartCommand(Bot bot,
                                    User user) {
        bot.sendMessage(user,
                BotUtility.loadResourceAsString("botMenuFiles/start.txt"));
    }

    private void handleHelpCommand(Bot bot,
                                   User user) {
        bot.sendMessage(user,
                BotUtility.loadResourceAsString("botMenuFiles/help.txt"));
    }

    private void handleTranslateCommand(Bot bot,
                                        User user) {
        List<String> languagePairs = Language.getLanguagePairs();
        bot.sendInlineKeyboard(user,
                "Выберите языковую пару для перевода",
                BotUtility.buildInlineKeyboardMarkup(languagePairs
                                .stream().map(item -> Pair.of(
                                        item, BotUtility.KeyboardType.LANGUAGE.name() + "/" + item))
                                .collect(Collectors.toList()),
                        2));
    }
}

