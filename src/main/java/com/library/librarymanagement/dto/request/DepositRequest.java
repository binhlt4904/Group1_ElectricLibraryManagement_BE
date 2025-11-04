package com.library.librarymanagement.dto.request;

import lombok.Data;

@Data
public class DepositRequest {
    private Long userId;
    private Double amount;
    private String transactionCode;
}
