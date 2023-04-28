package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.data.EnglishLevelRepository;
import com.azatkhaliullin.myenglishbot.data.EnglishTestRepository;
import com.azatkhaliullin.myenglishbot.dto.Answer;
import com.azatkhaliullin.myenglishbot.dto.Question;
import com.azatkhaliullin.myenglishbot.dto.User;
import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A utility class for handling EnglishTest related functionalities.
 */
@UtilityClass
public class EnglishTestUtility {

    /**
     * Sends the next question to the user through a Telegram bot.
     *
     * @param bot              the Telegram bot instance.
     * @param user             the user object.
     * @param englishTestRepo  the repository object for EnglishTest.
     * @param englishLevelRepo the repository object for EnglishLevel.
     */
    public void sendQuestion(Bot bot,
                             User user,
                             EnglishTestRepository englishTestRepo,
                             EnglishLevelRepository englishLevelRepo) {
        // Get the next question from the EnglishTest object of the user.
        EnglishTest englishTest = user.getEnglishTest();
        Optional<Question> optionalQuestion = getNextQuestion(englishTest, englishTestRepo);
        // If there is a question available, send it to the user.
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            List<Answer> answers = question.getAnswers();
            // Build the inline keyboard markup for the question's answers.
            List<List<InlineKeyboardButton>> keyboardMarkup = BotUtility.buildInlineKeyboardMarkup(answers
                            .stream().map(answer -> Pair.of(
                                    answer.getValue(), BotUtility.InlineKeyboardType.ANSWERS.name() + "/" + answer.getId()))
                            .collect(Collectors.toList()),
                    2);
            // If the user does not have an inline message ID yet, send a new message.
            if (user.getInlineMessageId() == 0) {
                bot.sendInlineKeyboard(
                        user,
                        question.getQuestion(),
                        keyboardMarkup);
            } else {
                // If the user already has an inline message ID, edit the existing message.
                bot.editMessageWithInline(
                        user,
                        question.getQuestion(),
                        keyboardMarkup);
            }
        } else {
            // If there are no more questions, send the test result to the user.
            sendResult(bot, user, englishTest, englishLevelRepo);
        }
    }

    /**
     * Returns the next question from the given EnglishTest object and saves the object to the repository.
     *
     * @param englishTest     the EnglishTest object.
     * @param englishTestRepo the repository object for EnglishTest.
     * @return the next question from the given EnglishTest object as an Optional object.
     */
    private Optional<Question> getNextQuestion(EnglishTest englishTest,
                                               EnglishTestRepository englishTestRepo) {
        Optional<Question> optionalQuestion = englishTest.getNextQuestion();
        englishTestRepo.save(englishTest);
        return optionalQuestion;
    }

    /**
     * Checks if the given answer is correct, updates the score and moves on to the next question.
     *
     * @param englishTest the EnglishTest object.
     * @param answer      the answer object.
     */
    public void checkAnswer(EnglishTest englishTest,
                            Answer answer) {
        if (answer.isRight()) {
            // If the answer is correct, increment the score.
            englishTest.calculateScore();
        }
        // Move on to the next question.
        englishTest.incrementCurrentIndex();
    }

    /**
     * Sends the final result of the English test to the user through a Telegram bot.
     *
     * @param bot              the Telegram bot instance.
     * @param user             the user object.
     * @param englishTest      the EnglishTest object.
     * @param englishLevelRepo the repository object for EnglishLevel.
     */
    public void sendResult(Bot bot,
                           User user,
                           EnglishTest englishTest,
                           EnglishLevelRepository englishLevelRepo) {
        if (englishTest != null) {
            // Calculate the final score and level.
            int level = englishTest.calculateLevel();
            String message = String.format("У вас %d баллов из 162.%n%n%s", // велика вероятность забыть поменять это число в случае, если алгоритм подсчёта баллов изменится, лучше вынести 162 в константы класса EnglishTest или высчитывать её динамически на основе банка текущих вопросов и цены оответов на них в том же классе
                    englishTest.getScore(),
                    englishLevelRepo.getByLevel(level).getDescription());
            bot.sendMessage(user, message);
        } else {
            // If the user has not yet taken the test
            bot.sendMessage(user, "Упс, кажется вы еще не проходили тест");
        }
    }


}
