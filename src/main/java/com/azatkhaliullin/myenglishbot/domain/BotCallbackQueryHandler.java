package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator.Language;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.domain.BotUtility.KeyboardType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface CallbackQueryHandler {
    void handleCallback(Bot bot,
                        User user,
                        String[] callbackSplit);
}

public class BotCallbackQueryHandler {
    private final Map<KeyboardType, CallbackQueryHandler> callbackQueries;
    private final UserRepository userRepo;

    public BotCallbackQueryHandler(UserRepository userRepo) {
        this.userRepo = userRepo;
        callbackQueries = new HashMap<>();
        callbackQueries.put(KeyboardType.LANGUAGE, this::handleTranslateCallback);
        callbackQueries.put(KeyboardType.VOICE, this::handleVoiceCallback);
    }

    public void handleCallback(Bot bot,
                               User user,
                               CallbackQuery callbackQuery) {
        String[] callbackSplit = callbackQuery.getData().split("/");
        CallbackQueryHandler handler = callbackQueries.get(KeyboardType.valueOf(callbackSplit[0]));
        handler.handleCallback(bot, user, callbackSplit);
    }

    private void handleTranslateCallback(Bot bot,
                                         User user,
                                         String[] callbackSplit) {
        String[] split = callbackSplit[1].split("_");
        Language source = Language.valueOf(split[0]);
        Language target = Language.valueOf(split[1]);
        user = userRepo.getUserById(user.getId());
        user.setDialogueStep(User.DialogueStep.WAIT_FOR_TRANSLATION);
        user.setSource(source);
        user.setTarget(target);
        userRepo.save(user);
        bot.sendMessage(user, "Введите слово либо фразу для перевода");
    }

    private void handleVoiceCallback(Bot bot,
                                     User user,
                                     String[] callbackSplit) {
        bot.sendVoice(user, callbackSplit[1]);

    }
}
