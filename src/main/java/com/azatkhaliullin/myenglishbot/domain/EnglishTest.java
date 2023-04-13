package com.azatkhaliullin.myenglishbot.domain;

import com.azatkhaliullin.myenglishbot.dto.Answer;
import com.azatkhaliullin.myenglishbot.dto.Question;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
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
        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("is", false));
        answers.add(new Answer("am", false));
        answers.add(new Answer("are", true));
        answers.add(new Answer("be", false));
        questions.add(new Question("This is a notebook. Those ___ notebooks.", answers));

        List<Answer> answers1 = new ArrayList<>();
        answers1.add(new Answer("You", false));
        answers1.add(new Answer("Are you", true));
        answers1.add(new Answer("Am I", false));
        answers1.add(new Answer("You’re", false));
        questions.add(new Question("I’m Russian. ___ English?", answers1));
        currentIndex = 0;
        numberCorrectAnswers = 0;
        this.questions = questions;
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

