package com.azatkhaliullin.myenglishbot.data;

import com.azatkhaliullin.myenglishbot.EnglishTest;
import org.springframework.data.repository.CrudRepository;

public interface EnglishTestRepository
        extends CrudRepository<EnglishTest, Long> {
}
