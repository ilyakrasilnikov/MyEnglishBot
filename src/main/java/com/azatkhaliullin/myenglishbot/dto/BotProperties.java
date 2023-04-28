package com.azatkhaliullin.myenglishbot.dto;

import lombok.Getter;

//@Data
@Getter
public class BotProperties {

    private final String username = System.getenv("BOT_USERNAME");
    private final String token = System.getenv("BOT_TOKEN");

}
