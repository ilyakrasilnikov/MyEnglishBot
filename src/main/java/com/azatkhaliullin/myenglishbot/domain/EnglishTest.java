package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.dto.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Slf4j
@Data
public class EnglishTest {

    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<Question> questions;
    private int currentIndex;
    @Transient
    private int level;
    private int score;

    public EnglishTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Question> questions = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("questions.json");
            questions = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("Не удалось загрузить вопросы для теста", e);
        }
        this.questions = questions;
        currentIndex = 0;
        level = 0;
        score = 0;
    }

    public Optional<Question> getNextQuestion() {
        if (currentIndex >= questions.size()) {
            return Optional.empty();
        }
        return Optional.of(questions.get(currentIndex));
    }

    public void incrementCurrentIndex() {
        currentIndex++;
    }

    public void calculateScore() {
        int[] scoreBoundaries = {6, 14, 29, 39, 50};
        int[] points = {1, 2, 3, 4, 5};
        for (int i = 0; i < scoreBoundaries.length; i++) {
            if (currentIndex <= scoreBoundaries[i]) {
                score += points[i];
                break;
            }
        }
    }

    public int calculateLevel() {
        int[] levelBoundaries = {10, 30, 50, 80, 100, 120, 140, 156, 162};
        int[] levels = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int i = 0;
        while (score >= levelBoundaries[i]) {
            i++;
        }
        return level = levels[i];
    }

}

