package com.library.librarymanagement.controller.wallet;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllTransactions(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "4") int size,
                                                @RequestParam (required = false) String type,
                                                @RequestParam (required = false) String search) {
        System.out.println("userId: "+ userId + ", page: " + page + ", size: " + size + ", type: " + type + ", search: " + search);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(walletTransactionService.findAllTransactions(pageable, userId, type, search));
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
        walletTransactionService.updateTransactionStatus(transactionId, "CANCELLED");
        return ResponseEntity.ok().build();
    }


}
