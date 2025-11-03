package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletTransactionResponse;
import com.library.librarymanagement.entity.WalletTransaction;

public interface WalletTransactionService {
    WalletTransactionResponse handleDeposit(Long readerId, Double amount, String transactionCode);

    WalletTransactionResponse getPendingTransaction(Long readerId);

    WalletTransactionResponse updateAmountInTransaction(Long transactionId, Double newAmount);

    void updateTransactionStatus(Long transactionId, String status);
}
