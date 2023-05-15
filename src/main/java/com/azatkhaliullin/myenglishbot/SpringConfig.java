package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import com.azatkhaliullin.myenglishbot.handler.BotCallbackQueryHandler;
import com.azatkhaliullin.myenglishbot.handler.BotCommandHandler;
import com.azatkhaliullin.myenglishbot.service.AwsService;
import com.azatkhaliullin.myenglishbot.service.EnglishTestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpringConfig {

    @Bean
    public Bot bot(BotProperties botProperties,
                   BotCommandHandler botCommandHandler,
                   BotCallbackQueryHandler botCallbackQueryHandler,
                   UserRepository userRepo,
                   AwsService awsService) {
        return new Bot(botProperties,
                botCommandHandler,
                botCallbackQueryHandler,
                userRepo,
                awsService);
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

    @Bean
    public AwsService awsService(RestTemplate restTemplate) {
        return new AwsService(restTemplate);
    }

    @Bean
    public EnglishTestService englishTestService(RestTemplate restTemplate) {
        return new EnglishTestService(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BotCommandHandler botCommandHandler() {
        return new BotCommandHandler();
    }

    @Bean
    public BotCallbackQueryHandler botCallbackQueryHandler(UserRepository userRepo,
                                                           EnglishTestService englishTestService) {
        return new BotCallbackQueryHandler(userRepo, englishTestService);
    }

}