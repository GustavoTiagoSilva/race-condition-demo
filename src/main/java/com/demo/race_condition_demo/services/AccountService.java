package com.demo.race_condition_demo.services;

import com.demo.race_condition_demo.dto.MessageResponseDto;
import com.demo.race_condition_demo.dto.TransferRequestDto;
import com.demo.race_condition_demo.entities.AccountEntity;
import com.demo.race_condition_demo.exceptions.InsufficientBalanceException;
import com.demo.race_condition_demo.exceptions.ResourceNotFoundException;
import com.demo.race_condition_demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public MessageResponseDto transfer(TransferRequestDto transferRequest) {

        AccountEntity sourceAccount = accountRepository.findById(transferRequest.sourceAccountId()).orElseThrow(() -> new ResourceNotFoundException("Source account with id " + transferRequest.targetAccountId() + " not found"));
        AccountEntity targetAccount = accountRepository.findById(transferRequest.targetAccountId()).orElseThrow(() -> new ResourceNotFoundException("Target account with id " + transferRequest.targetAccountId() + " not found"));

        if (sourceAccount.getBalance() < transferRequest.amount()) {
            logger.info("Exception occurs");
            throw new InsufficientBalanceException("Insufficient balance on the source account");
        }

        double sourceAccountBalance = sourceAccount.getBalance() - transferRequest.amount();
        double targetAccountBalance = targetAccount.getBalance() + transferRequest.amount();

        logger.info("Source account balance before update: {}", sourceAccount.getBalance());
        logger.info("Target account balance before update: {}", targetAccount.getBalance());

        sourceAccount.setBalance(sourceAccountBalance);
        targetAccount.setBalance(targetAccountBalance);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        logger.info("Source account balance after update: {}", sourceAccount.getBalance());
        logger.info("Target account balance after update: {}", targetAccount.getBalance());

        return new MessageResponseDto("Successfully transferred");
    }
}
