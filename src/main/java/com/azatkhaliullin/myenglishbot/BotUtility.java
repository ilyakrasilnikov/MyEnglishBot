package com.azatkhaliullin.myenglishbot;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.util.StreamUtils;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides various methods to support bot development.
 */
@UtilityClass
@Slf4j
public class BotUtility {

    /**
     * Registers a set of bot commands for the given bot.
     *
     * @param bot the bot to register the commands for.
     */
    public void registerBotCommands(Bot bot) {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/translate", "translate your word"));
        listOfCommands.add(new BotCommand("/test", "check your english level"));
        try {
            bot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error in creating the bot menu", e);
        }
    }

    /**
     * Builds an inline keyboard markup based on a list of values.
     *
     * @param values         the list of values to display on the keyboard.
     * @param cntButtonInRow the number of buttons to display in each row.
     * @return a list of rows, where each row contains a list of inline keyboard buttons.
     */
    public List<List<InlineKeyboardButton>> buildInlineKeyboardMarkup(List<Pair<String, String>> values,
                                                                      int cntButtonInRow) {
        List<List<InlineKeyboardButton>> valueList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Pair<String, String> value : values) {
            if (row.size() >= cntButtonInRow) {
                valueList.add(row);
                row = new ArrayList<>();
            }
            row.add(InlineKeyboardButton
                    .builder()
                    .text(value.getFirst())
                    .callbackData(value.getSecond())
                    .build());
        }
        valueList.add(row);
        return valueList;
    }

    /**
     * Loads the contents of a resource file as a string.
     *
     * @param fileName the name of the resource file to load.
     * @return the contents of the resource file as a string.
     */
    public String loadResourceAsString(String fileName) {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load the contents of the resource file ", e);
        }
        return "Команда временно недоступна";
    }

    public enum InlineKeyboardType {
        LANGUAGE, VOICE, TEST, ANSWER
    }

}
