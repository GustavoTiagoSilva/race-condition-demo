package com.demo.race_condition_demo.faker;

import com.demo.race_condition_demo.entities.UserEntity;

import java.util.UUID;

public class UserEntityFaker {

    public static UserEntity createSourceUser(UUID userId) {
        return new UserEntity(userId,
                "Gustavo",
                "Silva",
                "gustavo@gmail.com",
                "123.456.789-1",
                null);
    }

    public static UserEntity createTargetUser(UUID userId) {
        return new UserEntity(userId,
                "Henrique",
                "Nunes",
                "henrique@gmail.com",
                "987.654.321-0",
                null);
    }

}
