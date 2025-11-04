package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletTransactionResponse;
import com.library.librarymanagement.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletTransactionService {

    Page<WalletTransactionResponse> findAllTransactions(Pageable pageable,Long userId, String type, String search);

    WalletTransactionResponse handleDeposit(Long readerId, Double amount, String transactionCode);

    WalletTransactionResponse getPendingTransaction(Long readerId);

    WalletTransactionResponse updateAmountInTransaction(Long transactionId, Double newAmount);

    void updateTransactionStatus(Long transactionId, String status);
}
