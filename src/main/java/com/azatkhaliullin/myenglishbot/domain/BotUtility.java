package com.azatkhaliullin.myenglishbot.domain;

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

@UtilityClass
@Slf4j
public class BotUtility {

    public void registerBotCommands(Bot bot) {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/translate", "translate your word"));
        listOfCommands.add(new BotCommand("/test", "check your english level"));
        try {
            bot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка при создание меню бота", e);
        }
    }

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

    public String loadResourceAsString(String fileName) {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Ошибка при создание меню бота", e);
        }
        return "Команда временно недоступна";
    }

    public enum KeyboardType {
        LANGUAGE, VOICE, TEST, ANSWERS
    }

}
