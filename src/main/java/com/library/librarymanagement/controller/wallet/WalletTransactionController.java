package com.library.librarymanagement.controller.wallet;


import com.library.librarymanagement.dto.request.AmountRequest;
import com.library.librarymanagement.dto.request.DepositRequest;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.Wallet;
import com.library.librarymanagement.entity.WalletTransaction;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.wallet.WalletRepository;
import com.library.librarymanagement.repository.wallet.WalletTransactionRepository;
import com.library.librarymanagement.service.wallet.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/wallet-transactions")
public class WalletTransactionController {
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;
    private final ReaderRepository readerRepository;

    @GetMapping("/{userId}/pending")
    public ResponseEntity<?> getPending(@PathVariable Long userId) {
        Reader reader= readerRepository.findByAccountId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok().body(walletTransactionService.getPendingTransaction(reader.getId()));
    }

    @PostMapping("/handle")
    public ResponseEntity<?> handle(@RequestBody DepositRequest depositRequest) {
        Reader reader= readerRepository.findByAccountId(depositRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok().body(walletTransactionService.handleDeposit(reader.getId(), depositRequest.getAmount(), depositRequest.getTransactionCode()));
    }


    @PatchMapping("/{transactionId}")
    public ResponseEntity<?> updateAmount(@PathVariable Long transactionId,
                                          @RequestBody AmountRequest amount) {
        System.out.println("Updating transaction ID: " + transactionId + " with amount: " + amount.getAmount());
        return ResponseEntity.ok().body(walletTransactionService.updateAmountInTransaction(transactionId, amount.getAmount()));
    }

    @PostMapping("/{transactionId}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long transactionId) {
        walletTransactionService.updateTransactionStatus(transactionId, "CANCELED");
        return ResponseEntity.ok().build();
    }
}
