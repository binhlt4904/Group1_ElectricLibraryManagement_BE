package com.library.librarymanagement.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class WalletTransactionResponse {
    private Long id;
    private String transactionCode;
    private String type;
    private Double amount;
    private String status;
    private Date createdDate;
}
