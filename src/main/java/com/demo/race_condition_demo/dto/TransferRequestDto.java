package com.demo.race_condition_demo.dto;

import java.util.UUID;

public record TransferRequestDto(Double amount, UUID sourceAccountId, UUID targetAccountId) {
}
