package com.demo.race_condition_demo.faker;

import com.demo.race_condition_demo.entities.AccountEntity;
import com.demo.race_condition_demo.entities.UserEntity;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountEntityFaker {

    public static AccountEntity createSourceAccount(UUID accountId, UserEntity sourceUser, BigDecimal balance) {
        return new AccountEntity(
                accountId,
                "03412-9",
                "8130",
                balance,
                sourceUser);
    }

    public static AccountEntity createTargetAccount(UUID accountId, UserEntity targetUser, BigDecimal balance) {
        return new AccountEntity(accountId,
                "02325-1",
                "7743",
                balance,
                targetUser);
    }

}
