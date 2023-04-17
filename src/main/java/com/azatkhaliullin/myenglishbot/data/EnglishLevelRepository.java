package com.azatkhaliullin.myenglishbot.data;

import com.azatkhaliullin.myenglishbot.dto.EnglishLevel;
import org.springframework.data.repository.CrudRepository;

public interface EnglishLevelRepository
        extends CrudRepository<EnglishLevel, Long> {

    EnglishLevel getByLevel(int level);
}
