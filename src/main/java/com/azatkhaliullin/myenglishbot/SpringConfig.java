package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.awsTranslate.AWSTranslator;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.domain.Bot;
import com.azatkhaliullin.myenglishbot.domain.BotCallbackQueryHandler;
import com.azatkhaliullin.myenglishbot.domain.BotCommandHandler;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootConfiguration
public class SpringConfig {

    @Bean
    public Bot bot(UserRepository userRepo) {
        return new Bot(botProperties(),
                botCommandHandler(),
                botCallbackQueryHandler(userRepo),
                userRepo,
                awsTranslator());
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

    @Bean
    public AWSTranslator awsTranslator() {
        return new AWSTranslator(threadPoolExecutor());
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    @Bean
    public BotCommandHandler botCommandHandler() {
        return new BotCommandHandler();
    }

    @Bean
    public BotCallbackQueryHandler botCallbackQueryHandler(UserRepository userRepo) {
        return new BotCallbackQueryHandler(userRepo);
    }

}
