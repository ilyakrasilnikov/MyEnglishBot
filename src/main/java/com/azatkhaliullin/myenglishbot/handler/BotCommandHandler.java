package com.azatkhaliullin.myenglishbot.handler;

import com.azatkhaliullin.myenglishbot.aws.Language;
import com.azatkhaliullin.myenglishbot.Bot;
import com.azatkhaliullin.myenglishbot.utility.BotUtility;
import com.azatkhaliullin.myenglishbot.dto.User;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This interface represents a functional command handler, which is used to handle bot commands.
 */
@FunctionalInterface
interface CommandHandler {
    void handleCommand(Bot bot,
                       User user);
}

/**
 * This class handles bot commands and their relevant handlers.
 */
public class BotCommandHandler {

    private final Map<String, CommandHandler> commands;

    public BotCommandHandler() {
        commands = new HashMap<>();
        commands.put("/start", this::handleStartCommand);
        commands.put("/help", this::handleHelpCommand);
        commands.put("/translate", this::handleTranslateCommand);
        commands.put("/test", this::handleTestCommand);
    }

    /**
     * Processes the command by finding its handler and calling it.
     *
     * @param bot     the Telegram bot instance.
     * @param user    the user who sent the command.
     * @param message the message containing the command.
     * @return if the command was handled, false otherwise.
     */
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

    /**
     * Handles the /start command by sending a welcome message to the user.
     *
     * @param bot  the Telegram bot instance.
     * @param user the user who sent the command.
     */
    private void handleStartCommand(Bot bot,
                                    User user) {
        bot.sendMessage(user,
                BotUtility.loadResourceAsString("botCommands/start.txt"));
    }

    /**
     * Handles the /help command by sending a help message to the user.
     *
     * @param bot  the Telegram bot instance.
     * @param user the user who sent the command.
     */
    private void handleHelpCommand(Bot bot,
                                   User user) {
        bot.sendMessage(user,
                BotUtility.loadResourceAsString("botCommands/help.txt"));
    }

    /**
     * Handles the "/translate" command with the given bot and user by sending an inline keyboard with language pairs.
     *
     * @param bot  the Telegram bot instance.
     * @param user the user who sent the command.
     */
    private void handleTranslateCommand(Bot bot,
                                        User user) {
        List<String> languagePairs = Language.getLanguagePairs();
        bot.sendInlineKeyboard(
                user,
                "Выберите языковую пару для перевода",
                BotUtility.buildInlineKeyboardMarkup(languagePairs
                                .stream().map(languagePair -> Pair.of(
                                        languagePair, BotUtility.InlineKeyboardType.LANGUAGE.name() + "/" + languagePair))
                                .collect(Collectors.toList()),
                        2));
    }

    /**
     * Handles the "/test" command with the given bot and user by sending an inline keyboard with options for the user.
     *
     * @param bot  the Telegram bot instance.
     * @param user the user who sent the command.
     */
    private void handleTestCommand(Bot bot,
                                   User user) {
        List<String> valueButtons = new ArrayList<>();
        valueButtons.add("Начать/Продолжить тест");
        valueButtons.add("Пройти тест заново");
        valueButtons.add("Результаты теста");
        bot.sendInlineKeyboard(
                user,
                "Выберите дальнейшее действие",
                BotUtility.buildInlineKeyboardMarkup(valueButtons
                                .stream().map(valueButton -> Pair.of(
                                        valueButton, BotUtility.InlineKeyboardType.TEST.name() + "/" + valueButtons.indexOf(valueButton)))
                                .collect(Collectors.toList()),
                        1));
    }

}

