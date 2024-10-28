package com.demo.race_condition_demo.controllers;

import com.demo.race_condition_demo.dto.AccountDetailsDto;
import com.demo.race_condition_demo.dto.TransferRequestDto;
import com.demo.race_condition_demo.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public AccountDetailsDto transfer(@RequestBody TransferRequestDto transferRequest) {
        return accountService.transfer(transferRequest);
    }
}
