package com.demo.race_condition_demo.controllers;

import com.demo.race_condition_demo.dto.HttpExceptionHandlerResponseDto;
import com.demo.race_condition_demo.exceptions.InsufficientBalanceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<HttpExceptionHandlerResponseDto> insufficientBalanceException(InsufficientBalanceException e, HttpServletRequest req) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        HttpExceptionHandlerResponseDto httpExceptionHandlerResponse = new HttpExceptionHandlerResponseDto(
                Instant.now(),
                httpStatus.value(),
                "Insufficient balance",
                e.getMessage(),
                req.getRequestURI()
        );

        return new ResponseEntity<>(httpExceptionHandlerResponse, httpStatus);
    }

}
