package com.azatkhaliullin.myenglishbot.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class Question {

    private String question;
    private List<Answer> answers;

}