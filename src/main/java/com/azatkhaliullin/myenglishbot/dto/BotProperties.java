package com.azatkhaliullin.myenglishbot.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    private String username;
    private String token;
    private String chatId;

}
