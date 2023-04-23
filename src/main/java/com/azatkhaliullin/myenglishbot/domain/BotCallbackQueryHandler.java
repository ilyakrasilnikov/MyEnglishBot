package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.awsTranslate.Language;
import com.azatkhaliullin.myenglishbot.data.AnswerRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishLevelRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.Answer;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.domain.BotUtility.KeyboardType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@FunctionalInterface
interface CallbackQueryHandler {
    void handleCallback(Bot bot,
                        User user,
                        String[] callbackSplit);
}

public class BotCallbackQueryHandler {
    private final Map<KeyboardType, CallbackQueryHandler> callbackQueries;
    private final UserRepository userRepo;
    private final EnglishTestRepository englishTestRepo;
    private final EnglishLevelRepository englishLevelRepo;
    private final AnswerRepository answerRepo;

    public BotCallbackQueryHandler(UserRepository userRepo,
                                   EnglishTestRepository englishTestRepo,
                                   EnglishLevelRepository englishLevelRepo,
                                   AnswerRepository answerRepo) {
        this.userRepo = userRepo;
        this.englishTestRepo = englishTestRepo;
        this.englishLevelRepo = englishLevelRepo;
        this.answerRepo = answerRepo;
        callbackQueries = new HashMap<>();
        callbackQueries.put(KeyboardType.LANGUAGE, this::handleTranslateCallback);
        callbackQueries.put(KeyboardType.VOICE, this::handleVoiceCallback);
        callbackQueries.put(KeyboardType.TEST, this::handleTestCallback);
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

    private void handleTestCallback(Bot bot,
                                    User user,
                                    String[] callbackSplit) {
        Optional<Answer> optionalAnswer = answerRepo.findById(Long.valueOf(callbackSplit[1]));
        optionalAnswer.ifPresent(answer -> EnglishTestUtility.checkAnswer(user.getEnglishTest(), answer));
        EnglishTestUtility.sendQuestion(bot, user, englishTestRepo, englishLevelRepo);
    }

}
