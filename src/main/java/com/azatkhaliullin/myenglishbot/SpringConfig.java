package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.aws.Aws;
import com.azatkhaliullin.myenglishbot.data.AnswerRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishLevelRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.domain.Bot;
import com.azatkhaliullin.myenglishbot.domain.BotCallbackQueryHandler;
import com.azatkhaliullin.myenglishbot.domain.BotCommandHandler;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class SpringConfig {

    @Bean
    public Bot bot(BotProperties botProperties,
                   BotCommandHandler botCommandHandler,
                   BotCallbackQueryHandler botCallbackQueryHandler,
                   UserRepository userRepo,
                   Aws aws) {
        return new Bot(botProperties,
                botCommandHandler,
                botCallbackQueryHandler,
                userRepo,
                aws);
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

    @Bean
    public Aws awsTranslator() {
        return new Aws();
    }

    @Bean
    public BotCommandHandler botCommandHandler() {
        return new BotCommandHandler();
    }

    @Bean
    public BotCallbackQueryHandler botCallbackQueryHandler(UserRepository userRepo,
                                                           EnglishTestRepository englishTestRepo,
                                                           EnglishLevelRepository englishLevelRepo,
                                                           AnswerRepository answerRepo) {
        return new BotCallbackQueryHandler(userRepo, englishTestRepo, englishLevelRepo, answerRepo);
    }

}
