package com.azatkhaliullin.myenglishbot.service;

import com.azatkhaliullin.myenglishbot.Bot;
import com.azatkhaliullin.myenglishbot.dto.Answer;
import com.azatkhaliullin.myenglishbot.dto.EnglishTest;
import com.azatkhaliullin.myenglishbot.dto.Question;
import com.azatkhaliullin.myenglishbot.dto.User;
import com.azatkhaliullin.myenglishbot.BotUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class EnglishTestService {

    @Value("${BASE_URL}")
    private String BASE_URL;
    private final RestTemplate restTemplate;

    public EnglishTestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EnglishTest getEnglishTest() {
        try {
            URI url = new URIBuilder(BASE_URL)
                    .setPort(90)
                    .setPath("/test")
                    .build();
            return restTemplate.postForObject(url, null, EnglishTest.class);
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteEnglishTest(Long englishTestId) {
        try {
            if (englishTestId != null) {
                URI url = new URIBuilder(BASE_URL)
                        .setPort(90)
                        .setPath("/delete")
                        .addParameter("englishTestId", String.valueOf(englishTestId))
                        .build();
                restTemplate.postForObject(url, null, Void.class);
            }
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<Question> getQuestion(Long englishTestId) {
        try {
            URI url = new URIBuilder(BASE_URL)
                    .setPort(90)
                    .setPath("/question")
                    .addParameter("englishTestId", String.valueOf(englishTestId))
                    .build();
            return Optional.ofNullable(restTemplate.postForObject(url, null, Question.class));
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

    public void checkAnswer(Long englishTestId,
                            String answerId) {
        try {
            URI url = new URIBuilder(BASE_URL)
                    .setPort(90)
                    .setPath("/answer")
                    .addParameter("englishTestId", String.valueOf(englishTestId))
                    .addParameter("answerId", answerId)
                    .build();
            restTemplate.postForObject(url, null, Void.class);
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

    public String getResult(Long englishTestId) {
        try {
            if (englishTestId != null) {
                URI url = new URIBuilder(BASE_URL)
                        .setPort(90)
                        .setPath("/result")
                        .addParameter("englishTestId", String.valueOf(englishTestId))
                        .build();
                return restTemplate.postForObject(url, null, String.class);
            }
            return "Упс, кажется вы еще не проходили тест";
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

    public void sendQuestion(Bot bot,
                             User user) {
        Optional<Question> optionalQuestion = getQuestion(user.getEnglishTestId());
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            List<Answer> answers = question.getAnswers();
            List<List<InlineKeyboardButton>> keyboardMarkup = BotUtility.buildInlineKeyboardMarkup(answers
                            .stream().map(answer -> Pair.of(
                                    answer.getValue(), BotUtility.InlineKeyboardType.ANSWER.name() + "/" + answer.getId()))
                            .collect(Collectors.toList()),
                    2);
            if (user.getInlineMessageId() == null) {
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
            bot.sendMessage(user, getResult(user.getEnglishTestId()));
        }
    }

}