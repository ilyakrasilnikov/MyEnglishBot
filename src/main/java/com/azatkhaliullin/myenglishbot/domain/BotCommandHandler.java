package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator.Language;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
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
    private final UserRepository userRepo;
    private final EnglishTestRepository englishTestRepo;

    public BotCommandHandler(UserRepository userRepo,
                             EnglishTestRepository englishTestRepo) {
        this.userRepo = userRepo;
        this.englishTestRepo = englishTestRepo;
        commands = new HashMap<>();
        commands.put("/start", this::handleStartCommand);
        commands.put("/help", this::handleHelpCommand);
        commands.put("/translate", this::handleTranslateCommand);
        commands.put("/test", this::handleTestCommand);
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
                BotUtility.loadResourceAsString("botCommands/start.txt"));
    }

    private void handleHelpCommand(Bot bot,
                                   User user) {
        bot.sendMessage(user,
                BotUtility.loadResourceAsString("botCommands/help.txt"));
    }

    private void handleTranslateCommand(Bot bot,
                                        User user) {
        List<String> languagePairs = Language.getLanguagePairs();
        bot.sendInlineKeyboard(
                user,
                "Выберите языковую пару для перевода",
                BotUtility.buildInlineKeyboardMarkup(languagePairs
                                .stream().map(languagePair -> Pair.of(
                                        languagePair, BotUtility.KeyboardType.LANGUAGE.name() + "/" + languagePair))
                                .collect(Collectors.toList()),
                        2));
    }

    private void handleTestCommand(Bot bot,
                                   User user) {
        if (user.getEnglishTest() == null) {
            EnglishTest test = new EnglishTest();
            user.setEnglishTest(test);
            user = userRepo.save(user);
        }
        EnglishTestUtility.sendQuestion(bot, user, englishTestRepo);
    }
}

