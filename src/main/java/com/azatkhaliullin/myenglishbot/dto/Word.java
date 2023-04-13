package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.awsTranslate.ITranslator.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Word {

    @Id
    @GeneratedValue
    private Long id;
    private String value;
    private Language language;
    @OneToMany
    private Set<Word> translate;

}
