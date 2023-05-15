package com.azatkhaliullin.myenglishbot.data;

import com.azatkhaliullin.myenglishbot.dto.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository
        extends CrudRepository<User, Long> {
}