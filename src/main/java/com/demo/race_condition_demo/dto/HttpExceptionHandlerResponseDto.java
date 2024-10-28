package com.demo.race_condition_demo.dto;

import java.time.Instant;

public record HttpExceptionHandlerResponseDto(Instant timestamp,
                                              Integer httpStatus,
                                              String error,
                                              String errorMessage,
                                              String path) {
}
