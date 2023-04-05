package com.azatkhaliullin.myenglishbot.dto;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BotMenu {

    private static final List<BotCommand> listOfCommands = new ArrayList<>();

    public static void registerBotCommands(Bot bot) {
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/translate", "translate your word"));
        try {
            bot.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка при создание меню бота", e);
        }
    }

    public static String loadResourceAsString(String fileName) {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Ошибка при создание меню бота", e);
        }
        return "Команда временно недоступна";
    }

    public static List<String> processingTranslateCommand() {
        return Arrays.asList("EN/RUS", "RUS/EN");
    }

}
