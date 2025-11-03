package com.library.librarymanagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletResponse {
    private Long id;
    private BigDecimal balance;
    private String status;
    private Double totalPaid;

}
