package com.azatkhaliullin.myenglishbot.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue
    private Long id;
    private String value;
    private boolean isRight;

}
