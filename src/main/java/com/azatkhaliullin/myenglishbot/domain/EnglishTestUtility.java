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

@UtilityClass
public class EnglishTestUtility {

    public void sendQuestion(Bot bot,
                             User user,
                             EnglishTestRepository englishTestRepo,
                             EnglishLevelRepository englishLevelRepo) {
        EnglishTest englishTest = user.getEnglishTest();
        Optional<Question> optionalQuestion = getNextQuestion(englishTest, englishTestRepo);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            List<Answer> answers = question.getAnswers();
            List<List<InlineKeyboardButton>> keyboardMarkup = BotUtility.buildInlineKeyboardMarkup(answers
                            .stream().map(answer -> Pair.of(
                                    answer.getValue(), BotUtility.KeyboardType.ANSWERS.name() + "/" + answer.getId()))
                            .collect(Collectors.toList()),
                    2);
            if (user.getInlineMessageId() == 0) {
                bot.sendInlineKeyboard(
                        user,
                        question.getQuestion(),
                        keyboardMarkup);
            } else {
                bot.editMessageWithInline(
                        user,
                        question.getQuestion(),
                        keyboardMarkup);
            }
        } else {
            sendResult(bot, user, englishTest, englishLevelRepo);
        }
    }

    private Optional<Question> getNextQuestion(EnglishTest englishTest,
                                               EnglishTestRepository englishTestRepo) {
        Optional<Question> optionalQuestion = englishTest.getNextQuestion();
        englishTestRepo.save(englishTest);
        return optionalQuestion;
    }

    public void checkAnswer(EnglishTest englishTest,
                            Answer answer) {
        if (answer.isRight()) {
            englishTest.calculateScore();
        }
        englishTest.incrementCurrentIndex();
    }

    public void sendResult(Bot bot,
                           User user,
                           EnglishTest englishTest,
                           EnglishLevelRepository englishLevelRepo) {
        int level = englishTest.calculateLevel();
        String message = String.format("У вас %d баллов из 162.%n%n%s",
                englishTest.getScore(),
                englishLevelRepo.getByLevel(level).getDescription());
        bot.sendMessage(user, message);
    }


}
