package com.demo.race_condition_demo.services;

import com.demo.race_condition_demo.dto.SourceAccountDetailsDto;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {

    private final AccountRepository accountRepository = mock();
    private final AccountService accountService = new AccountService(accountRepository);

    /*
    * The test below is used to simulate a concurrent scenario where multiple threads are trying to update the balance value of an Account. As an analogy, imagine that each thread represents independent mobile devices that has access to the same account, and they are trying to transfer money to another account simultaneously.
    * Our transfer() method from AccountService MUST be thread-safe. If not, when multiple threads try to transfer, they can see different balance values and update much more money than the correct.
    * To prevent this to happen, we need to use a Lock in our database transaction. In this case, we have used @Lock(LockType.PESSIMISTIC), which blocks the database table row for reads and writes, guaranteeing that only one thread can read/write
    *
    * To simulate the unit tests, I have created an ExecutorService with fixed thread pool of 10 (magic number, just to simulate :)  ). It means that the pool can execute 10 tasks at max in parallel
    * After that, I created a for loop to simulate the concurrent requests to the transfer() method.
    *
    * The desired result is: "It does not matter how many users are trying to transfer, the correct process MUST occur and the account balance must be correct
    * */


    @Test
    @DisplayName("Given sufficient balance, when transfer concurrently with multiple threads, then adjust both accounts")
    void givenSufficientBalance_whenTransferConcurrentlyWithMultipleThreads_thenAdjustsBothAccounts() throws Exception {
        try (ExecutorService service = Executors.newFixedThreadPool(3)) {
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

            for (int i = 0; i < 3; i++) {
                service.submit(() -> accountService.transfer(transferRequest));
            }

            service.awaitTermination(5, TimeUnit.SECONDS);

            assertEquals(0.0, sourceAccount.getBalance());
        }
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
        TransferRequestDto transferRequest = new TransferRequestDto(
                1000.0,
                sourceAccountId,
                targetAccountId);

        SourceAccountDetailsDto sourceAccountDetailsDto = accountService.transfer(transferRequest);

        assertEquals(4000.0, sourceAccountDetailsDto.balance());
        assertEquals(11000.0, targetAccount.getBalance());
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

        var insufficientBalanceException = assertThrows(InsufficientBalanceException.class, () -> accountService.transfer(transferRequest));

        assertEquals(5000.0, sourceAccount.getBalance());
        assertEquals(10000.0, targetAccount.getBalance());
        assertEquals("Insufficient balance on the source account", insufficientBalanceException.getMessage());
    }
}
