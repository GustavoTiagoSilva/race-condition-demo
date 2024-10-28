package com.demo.race_condition_demo.services;

import com.demo.race_condition_demo.dto.AccountDetailsDto;
import com.demo.race_condition_demo.dto.TransferRequestDto;
import com.demo.race_condition_demo.entities.AccountEntity;
import com.demo.race_condition_demo.exceptions.InsufficientBalanceException;
import com.demo.race_condition_demo.exceptions.ResourceNotFoundException;
import com.demo.race_condition_demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountDetailsDto transfer(TransferRequestDto transferRequest) {

        AccountEntity sourceAccount = accountRepository.findById(transferRequest.sourceAccountId()).orElseThrow(() -> new ResourceNotFoundException("Source account with id " + transferRequest.targetAccountId() + " not found"));
        AccountEntity targetAccount = accountRepository.findById(transferRequest.targetAccountId()).orElseThrow(() -> new ResourceNotFoundException("Target account with id " + transferRequest.targetAccountId() + " not found"));

        if (sourceAccount.getBalance().compareTo(transferRequest.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance on the source account");
        }

        BigDecimal sourceAccountBalance = sourceAccount.getBalance().subtract(transferRequest.amount());
        BigDecimal targetAccountBalance = targetAccount.getBalance().add(transferRequest.amount());

        sourceAccount.setBalance(sourceAccountBalance);
        targetAccount.setBalance(targetAccountBalance);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        return new AccountDetailsDto(sourceAccount.getBalance());
    }
}
