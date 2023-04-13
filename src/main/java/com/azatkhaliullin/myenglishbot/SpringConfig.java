package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.awsTranslate.AWSTranslator;
import com.azatkhaliullin.myenglishbot.data.AnswerRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
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
    public Bot bot(BotProperties botProperties,
                   BotCommandHandler botCommandHandler,
                   BotCallbackQueryHandler botCallbackQueryHandler,
                   UserRepository userRepo,
                   AWSTranslator awsTranslator) {
        return new Bot(botProperties,
                botCommandHandler,
                botCallbackQueryHandler,
                userRepo,
                awsTranslator);
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

    @Bean
    public AWSTranslator awsTranslator(ThreadPoolExecutor threadPoolExecutor) {
        return new AWSTranslator(threadPoolExecutor);
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    @Bean
    public BotCommandHandler botCommandHandler(UserRepository userRepo,
                                               EnglishTestRepository englishTestRepo) {
        return new BotCommandHandler(userRepo, englishTestRepo);
    }

    @Bean
    public BotCallbackQueryHandler botCallbackQueryHandler(UserRepository userRepo,
                                                           EnglishTestRepository englishTestRepo,
                                                           AnswerRepository answerRepo) {
        return new BotCallbackQueryHandler(userRepo, englishTestRepo, answerRepo);
    }

}
