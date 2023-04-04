package com.azatkhaliullin.myenglishbot.dto;

import com.azatkhaliullin.myenglishbot.data.UserRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long idUser;
    private String username;

    public static User saveUser(UserRepository userRepo,
                                org.telegram.telegrambots.meta.api.objects.User userTG) {
        User user = new UserBuilder()
                .idUser(userTG.getId())
                .username(userTG.getUserName())
                .build();
        userRepo.save(user);
        return user;
    }
}
