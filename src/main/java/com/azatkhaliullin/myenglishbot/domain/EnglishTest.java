package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.dto.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Slf4j
public class EnglishTest {

    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private final List<Question> questions;
    private int currentIndex;
    private int numberCorrectAnswers;

    public EnglishTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Question> questions = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("questions.json");
            questions = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("YНе удалось загрузить вопросы для теста", e);
        }
        this.questions = questions;
        currentIndex = 0;
        numberCorrectAnswers = 0;
    }

    public Optional<Question> getNextQuestion() {
        if (currentIndex >= questions.size()) {
            return Optional.empty();
        }
        return Optional.of(questions.get(currentIndex));
    }

    public void incrementNumberCorrectAnswers() {
        numberCorrectAnswers++;
    }

    public void incrementCurrentIndex() {
        currentIndex++;
    }

    public Pair<Integer, Integer> getResult() {
        return Pair.of(numberCorrectAnswers, questions.size());
    }

}

