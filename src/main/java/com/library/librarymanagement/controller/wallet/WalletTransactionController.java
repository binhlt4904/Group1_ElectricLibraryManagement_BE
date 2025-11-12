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
    private final RestTemplate restTemplate;

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

    @Transactional
    @Scheduled(fixedRate = 30000) // 1 phút
    public void checkPendingTransactions() {
        System.out.println("Checking pending transactions with Sepay API...");
          List<WalletTransaction> pendingTransactions = walletTransactionRepository.findByStatus("PENDING");

          boolean matched = false;
        for (WalletTransaction t : pendingTransactions) {
            System.out.println("Checking transaction: " + t.getTransactionCode() + " with amount: " + t.getAmount());
             matched = checkWithSepayAPI(t.getTransactionCode(), t.getAmount());
            System.out.println("matched: "+ matched);
            if (matched) {
                System.out.println("matched");
                t.setStatus("DONE");
                t.setConfirmedDate(new Date());
                t.setTransactionCode(t.getTransactionCode());
                t.getWallet().setBalance(t.getWallet().getBalance().add(t.getAmount()));
                t.getWallet().setLastUpdated(new Date());
                t.setType("INCREASE");
                walletTransactionRepository.save(t);

            }

        }

    }

    public boolean checkWithSepayAPI(String transactionCode, BigDecimal amount) {
        String url = "https://my.sepay.vn/userapi/transactions/list";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("GSA6INY97MR4URKVRAVHR3VOQGQAL20CS1BBG5PQI4KKFFECCB9NFYT3Z27ZXXNU");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("🔎 JSON Sepay trả về: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode transactions = root.get("transactions");

                if (transactions != null && transactions.isArray()) {
                    for (JsonNode txn : transactions) {
                        String content = txn.get("transaction_content").asText();
                        String amountInStr = txn.get("amount_in").asText();

                        System.out.println("➡️ Nội dung: " + content);
                        System.out.println("➡️ Tiền vào: " + amountInStr);
                        String sanitizedTransactionCode = transactionCode.replace("_", "");

                        System.out.println("sanitizedTransactionCode: " + sanitizedTransactionCode);


                        if (content != null && content.contains(sanitizedTransactionCode)) {
                            try {
                                BigDecimal parsedAmount = new BigDecimal(amountInStr);
                                if ( parsedAmount.compareTo(amount) == 0 ) {
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("⚠️ Không parse được amount_in: " + amountInStr);
                            }
                        }
                    }
                } else {
                    System.out.println("⚠️ Không tìm thấy trường 'transactions' hoặc không phải mảng.");
                }
            }

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
