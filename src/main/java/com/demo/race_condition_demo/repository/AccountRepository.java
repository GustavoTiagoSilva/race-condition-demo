package com.demo.race_condition_demo.repository;

import com.demo.race_condition_demo.entities.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findById(UUID id);
}
