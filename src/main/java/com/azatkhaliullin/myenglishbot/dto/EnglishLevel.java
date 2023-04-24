package com.azatkhaliullin.myenglishbot.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EnglishLevel {

    @Id
    private int level;
    @Column(columnDefinition = "text")
    private String description;

}

