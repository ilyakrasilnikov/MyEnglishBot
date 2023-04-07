package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.domain.BotUtility.KeyboardType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface CallbackQueryHandler {
    void handleCallback(Bot bot,
                        User user,
                        String callbackData);
}

public class BotCallbackQueryHandler {
    private final Map<KeyboardType, CallbackQueryHandler> callbackQueries;

    public BotCallbackQueryHandler() {
        callbackQueries = new HashMap<>();
        callbackQueries.put(KeyboardType.LANGUAGE, this::handleTranslateCallback);
    }

    public void handleCallback(Bot bot,
                               User user,
                               CallbackQuery callbackQuery) {
        String[] split = callbackQuery.getData().split("/");
        CallbackQueryHandler handler = callbackQueries.get(KeyboardType.valueOf(split[0]));
        handler.handleCallback(bot, user, split[1]);
    }

    private void handleTranslateCallback(Bot bot,
                                         User user,
                                         String callbackData) {
        bot.sendMessage(user, callbackData);
    }
}
