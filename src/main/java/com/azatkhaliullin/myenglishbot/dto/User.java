package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.EnglishTest;
import com.azatkhaliullin.myenglishbot.aws.Language;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private EnglishTest englishTest;
    private int inlineMessageId;

    public enum DialogueStep {
        WAIT_FOR_TRANSLATION
    }

}
