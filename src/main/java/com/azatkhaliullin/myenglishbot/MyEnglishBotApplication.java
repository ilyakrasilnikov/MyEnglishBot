package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.domain.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Slf4j
public class MyEnglishBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MyEnglishBotApplication.class, args);
        try {
            new TelegramBotsApi(DefaultBotSession.class).
                    registerBot(context.getBean(Bot.class));
        } catch (TelegramApiException e) {
            log.error("Ошибка при регистрации бота", e);
        }
    }

}
