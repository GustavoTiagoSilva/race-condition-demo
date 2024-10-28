package com.demo.race_condition_demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(BigDecimal amount, UUID sourceAccountId, UUID targetAccountId) {
}
