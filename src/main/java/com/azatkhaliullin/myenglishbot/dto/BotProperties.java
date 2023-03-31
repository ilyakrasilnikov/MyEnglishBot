package com.azatkhaliullin.myenglishbot.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BotProperties {
    private String username = System.getenv("BOT_USERNAME");
    private String token = System.getenv("BOT_TOKEN");
    private String chatId;

}
