package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.aws.Language;
import com.azatkhaliullin.myenglishbot.data.AnswerRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishLevelRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
import com.azatkhaliullin.myenglishbot.data.UserRepository;
import com.azatkhaliullin.myenglishbot.dto.Answer;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.domain.BotUtility.InlineKeyboardType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A functional interface representing a callback query handler.
 * Provides a single method for handling callback queries with the given parameters.
 */
@FunctionalInterface
interface CallbackQueryHandler {
    void handleCallback(Bot bot,
                        User user,
                        String[] callbackSplit);
}

/**
 * A class that handles callback queries for the Telegram bot. It maps each keyboard type to a callback query handler
 * and handles the corresponding callback query accordingly.
 * <p>
 * It also interacts with the UserRepository, EnglishTestRepository,
 * EnglishLevelRepository, and AnswerRepository to store and retrieve data related to the user's test and answers.
 */
public class BotCallbackQueryHandler {
    private final Map<InlineKeyboardType, CallbackQueryHandler> callbackQueries;
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
        callbackQueries.put(InlineKeyboardType.LANGUAGE, this::handleTranslateCallback);
        callbackQueries.put(InlineKeyboardType.VOICE, this::handleVoiceCallback);
        callbackQueries.put(InlineKeyboardType.TEST, this::handleTestCallback);
        callbackQueries.put(InlineKeyboardType.ANSWERS, this::handleAnswersCallback);
    }

    /**
     * Handles the callback query by splitting it into an array of strings, getting the corresponding callback query handler
     * from the map, and calling the handler's handleCallback method.
     *
     * @param bot           the Telegram bot instance.
     * @param user          the user who sent the callback query.
     * @param callbackQuery the callback query.
     */
    public void handleCallback(Bot bot,
                               User user,
                               CallbackQuery callbackQuery) {
        String[] callbackSplit = callbackQuery.getData().split("/");
        CallbackQueryHandler handler = callbackQueries.get(InlineKeyboardType.valueOf(callbackSplit[0]));
        handler.handleCallback(bot, user, callbackSplit);
    }

    /**
     * Handles the language translation callback query by splitting it into an array of strings, setting the user's source
     * and target languages, setting the user's dialogue step to WAIT_FOR_TRANSLATION, and sending a message to the user
     * requesting the word or phrase to translate.
     *
     * @param bot           the Telegram bot instance.
     * @param user          the user who sent the callback query.
     * @param callbackSplit an array containing split callback data.
     */
    private void handleTranslateCallback(Bot bot,
                                         User user,
                                         String[] callbackSplit) {
        String[] split = callbackSplit[1].split("_");
        user.setSource(Language.valueOf(split[0]));
        user.setTarget(Language.valueOf(split[1]));
        user.setDialogueStep(User.DialogueStep.WAIT_FOR_TRANSLATION);
        userRepo.save(user);
        bot.sendMessage(user, "Введите слово либо фразу для перевода");
    }

    /**
     * Handles the voice callback by sending the specified voice message to the user.
     *
     * @param bot           the Telegram bot instance.
     * @param user          the user who sent the callback query.
     * @param callbackSplit an array containing split callback data.
     */
    private void handleVoiceCallback(Bot bot,
                                     User user,
                                     String[] callbackSplit) {
        bot.sendVoice(user, callbackSplit[1]);
    }

    /**
     * Handles the test callback by taking the appropriate action based on the data.
     * <p>Possible actions are:
     * <ul>
     * <li>0: If the user has not started the test, starts a new test and sends the first question;
     *     <p>If the user have started, they continue with the question where they left off;
     * <li>1: Starts a new test and sends the first question.
     * <li>2: Sends the result of the user's current test.
     * </ul>
     *
     * @param bot           the Telegram bot instance.
     * @param user          the user who sent the callback query.
     * @param callbackSplit an array containing split callback data.
     */
    private void handleTestCallback(Bot bot,
                                    User user,
                                    String[] callbackSplit) {
        switch (callbackSplit[1]) {
            case "0" -> {
                if (user.getEnglishTest() == null) {
                    user.setEnglishTest(new EnglishTest());
                    user = userRepo.save(user);
                }
                EnglishTestUtility.sendQuestion(bot, user, englishTestRepo, englishLevelRepo);
            }
            case "1" -> {
                user.setEnglishTest(new EnglishTest());
                user = userRepo.save(user);
                EnglishTestUtility.sendQuestion(bot, user, englishTestRepo, englishLevelRepo);
            }
            case "2" -> {
                EnglishTest englishTest = user.getEnglishTest();
                EnglishTestUtility.sendResult(bot, user, englishTest, englishLevelRepo);
            }
        }
    }

    /**
     * Handles callback queries related to user answers to English test questions.
     * Retrieves the answer from the database using the answer ID provided in the callback.
     * Calls the checkAnswer method from the EnglishTestUtility class to check if the answer is correct,
     * and updates the user's test result accordingly.
     * Sends the next question to the user using the sendQuestion method from the EnglishTestUtility class.
     *
     * @param bot           the Telegram bot instance.
     * @param user          the user who sent the callback query.
     * @param callbackSplit an array containing split callback data.
     */
    private void handleAnswersCallback(Bot bot,
                                       User user,
                                       String[] callbackSplit) {
        Optional<Answer> optionalAnswer = answerRepo.findById(Long.valueOf(callbackSplit[1]));
        optionalAnswer.ifPresent(answer -> EnglishTestUtility.checkAnswer(user.getEnglishTest(), answer));
        EnglishTestUtility.sendQuestion(bot, user, englishTestRepo, englishLevelRepo);
    }

}
