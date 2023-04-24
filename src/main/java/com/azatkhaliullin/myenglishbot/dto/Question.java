package com.azatkhaliullin.myenglishbot.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue
    private Long id;
    private String question;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Answer> answers;

}

