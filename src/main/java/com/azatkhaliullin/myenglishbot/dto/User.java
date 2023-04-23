package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.domain.EnglishTest;
import com.azatkhaliullin.myenglishbot.aws.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
    @OneToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private EnglishTest englishTest;
    private int InlineMessageId;

    public enum DialogueStep {
        WAIT_FOR_TRANSLATION
    }

}
