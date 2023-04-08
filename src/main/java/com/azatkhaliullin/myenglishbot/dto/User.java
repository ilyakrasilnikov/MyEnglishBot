package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator.Language;
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
@Builder(toBuilder=true)
public class User {

    @Id
    private Long id;
    private String username;
    private DialogueStep dialogueStep;
    private Language source;
    private Language target;

    public static User convertTGUserToUser(org.telegram.telegrambots.meta.api.objects.User userTG) {
        return new UserBuilder()
                .id(userTG.getId())
                .username(userTG.getUserName())
                .build();
    }

    public enum DialogueStep {
        WAIT_FOR_TRANSLATION
    }

}
