package com.azatkhaliullin.myenglishbot;

import com.azatkhaliullin.myenglishbot.awsTranslate.AWSTranslator;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.Bot;
import com.azatkhaliullin.myenglishbot.dto.BotProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootConfiguration
public class SpringConfig {

    @Bean
    public Bot bot(UserRepository userRepo) {
        return new Bot(botProperties(), userRepo);
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

    @Bean
    public AWSTranslator awsTranslator(ThreadPoolExecutor executorService) {
        return new AWSTranslator(executorService);
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

}
