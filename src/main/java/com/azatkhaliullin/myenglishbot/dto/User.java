package com.azatkhaliullin.myenglishbot.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {

    @Id
    private Long id;
    private String username;
    private DialogueStep dialogueStep;
    private Language source;
    private Language target;
    private Long englishTestId;
    private Integer inlineMessageId;

    public enum DialogueStep {
        WAIT_FOR_TRANSLATION
    }

}