package com.azatkhaliullin.myenglishbot.domain;

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
                             EnglishTestRepository englishTestRepo) {
        EnglishTest englishTest = user.getEnglishTest();
        Optional<Question> optionalQuestion = getNextQuestion(englishTest, englishTestRepo);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            List<Answer> answers = question.getAnswers();
            List<List<InlineKeyboardButton>> keyboardMarkup = BotUtility.buildInlineKeyboardMarkup(answers
                            .stream().map(answer -> Pair.of(
                                    answer.getValue(), BotUtility.KeyboardType.TEST.name() + "/" + answer.getId()))
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
            Pair<Integer, Integer> result = englishTest.getResult();
            bot.sendMessage(user, "Результат: " + result.getFirst() + " из " + result.getSecond());
        }
    }

    public Optional<Question> getNextQuestion(EnglishTest englishTest,
                                              EnglishTestRepository englishTestRepo) {
        Optional<Question> optionalQuestion = englishTest.getNextQuestion();
        englishTestRepo.save(englishTest);
        return optionalQuestion;
    }

    public void checkAnswer(EnglishTest englishTest,
                            Answer answer) {
        if (answer.isRight()) {
            englishTest.incrementNumberCorrectAnswers();
        }
        englishTest.incrementCurrentIndex();
    }

}
