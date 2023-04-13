package com.azatkhaliullin.myenglishbot.data;

import com.azatkhaliullin.myenglishbot.dto.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository
        extends CrudRepository<Answer, Long> {
}
