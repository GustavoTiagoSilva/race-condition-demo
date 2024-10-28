package com.demo.race_condition_demo.services;

import com.demo.race_condition_demo.dto.MessageResponseDto;
import com.demo.race_condition_demo.dto.TransferRequestDto;
import com.demo.race_condition_demo.entities.AccountEntity;
import com.demo.race_condition_demo.entities.UserEntity;
import com.demo.race_condition_demo.exceptions.InsufficientBalanceException;
import com.demo.race_condition_demo.faker.AccountEntityFaker;
import com.demo.race_condition_demo.faker.UserEntityFaker;
import com.demo.race_condition_demo.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {

    private AccountRepository accountRepository = mock();
    private AccountService accountService = new AccountService(accountRepository);

    @Test
    @DisplayName("Given sufficient balance, when transfer concurrently with multiple threads, then adjust both accounts")
    void givenSufficientBalance_whenTransferConcurrentlyWithMultipleThreads_thenAdjustsBothAccounts() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID sourceUserId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UserEntity sourceUser = UserEntityFaker.createSourceUser(sourceUserId);
        UserEntity targetUser = UserEntityFaker.createTargetUser(targetUserId);
        AccountEntity sourceAccount = AccountEntityFaker.createSourceAccount(sourceAccountId, sourceUser, 2000.0);
        AccountEntity targetAccount = AccountEntityFaker.createTargetAccount(targetAccountId, targetUser, 10000.0);
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        TransferRequestDto transferRequest = new TransferRequestDto(
                1000.0,
                sourceAccountId,
                targetAccountId);

        for (int i = 0; i < 10; i++) {
            service.execute(() -> accountService.transfer(transferRequest));
        }

        assertEquals(1000.0, sourceAccount.getBalance());
    }

    @Test
    @DisplayName("Given sufficient balance, when transfer, then adjust both accounts")
    void givenSufficientBalance_whenTransfer_thenAdjustsBothAccounts() {
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID sourceUserId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UserEntity sourceUser = UserEntityFaker.createSourceUser(sourceUserId);
        UserEntity targetUser = UserEntityFaker.createTargetUser(targetUserId);
        AccountEntity sourceAccount = AccountEntityFaker.createSourceAccount(sourceAccountId, sourceUser, 5000.0);
        AccountEntity targetAccount = AccountEntityFaker.createTargetAccount(targetAccountId, targetUser, 10000.0);
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        MessageResponseDto expectedMessage = new MessageResponseDto("Successfully transferred");
        TransferRequestDto transferRequest = new TransferRequestDto(
                1000.0,
                sourceAccountId,
                targetAccountId);

        MessageResponseDto messageResponseDto = accountService.transfer(transferRequest);

        assertEquals(4000.0, sourceAccount.getBalance());
        assertEquals(11000.0, targetAccount.getBalance());
        assertEquals(expectedMessage.message(), messageResponseDto.message());
    }

    @Test
    @DisplayName("Given insufficient balance, when transfer, then throws [InsufficientBalanceException]")
    void givenInsufficientBalance_whenTransfer_thenThrowsInsufficientBalanceException() {
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID sourceUserId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UserEntity sourceUser = UserEntityFaker.createSourceUser(sourceUserId);
        UserEntity targetUser = UserEntityFaker.createTargetUser(targetUserId);
        AccountEntity sourceAccount = AccountEntityFaker.createSourceAccount(sourceAccountId, sourceUser, 5000.0);
        AccountEntity targetAccount = AccountEntityFaker.createTargetAccount(targetAccountId, targetUser, 10000.0);
        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        TransferRequestDto transferRequest = new TransferRequestDto(
                10000.0,
                sourceAccountId,
                targetAccountId);

        var insufficientBalanceException = assertThrows(InsufficientBalanceException.class, () -> {
            accountService.transfer(transferRequest);
        });

        assertEquals(5000.0, sourceAccount.getBalance());
        assertEquals(10000.0, targetAccount.getBalance());
        assertEquals("Insufficient balance on the source account", insufficientBalanceException.getMessage());
    }
}
