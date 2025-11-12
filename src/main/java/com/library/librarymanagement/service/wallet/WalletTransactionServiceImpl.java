package com.library.librarymanagement.service.wallet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.librarymanagement.dto.response.WalletTransactionResponse;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.Wallet;
import com.library.librarymanagement.entity.WalletTransaction;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.wallet.WalletRepository;
import com.library.librarymanagement.repository.wallet.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ReaderRepository readerRepository;
    private final RestTemplate restTemplate;

    @Value("${sepayapi.token}")
    private String sepayApiToken;


    @Override
    public WalletTransactionResponse getPendingTransaction(Long readerId) {
        Wallet wallet = walletRepository.findByReaderId(readerId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        WalletTransaction tx= walletTransactionRepository.findFirstByWalletIdAndStatusOrderByCreatedDate(wallet.getId(), "PENDING")
                .orElse(null);
        if (tx == null) {
            return null;
        }
        WalletTransactionResponse response = convert(tx);
        return response;
    }

    @Override
    public WalletTransactionResponse updateAmountInTransaction(Long transactionId, Double newAmount) {
        WalletTransaction walletTransaction= walletTransactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        walletTransaction.setAmount(BigDecimal.valueOf(newAmount));
        walletTransactionRepository.save(walletTransaction);
        WalletTransactionResponse response = convert(walletTransaction);
        return response;
    }

    @Override
    public void updateTransactionStatus(Long transactionId, String status) {
        WalletTransaction walletTransaction= walletTransactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        walletTransaction.setStatus(status);
        walletTransactionRepository.save(walletTransaction);
    }


    @Override
    public Page<WalletTransactionResponse> findAllTransactions(Pageable pageable, Long userId, String type, String search) {
        Specification<WalletTransaction> spec = Specification.allOf();
        Reader reader = readerRepository.findByAccountId(userId).orElse(null);
         spec = spec.and( (root,query,cb) ->
            cb.equal(root.get("wallet").get("reader").get("id"), reader.getId())
        );
        if(type != null && !type.isEmpty()) {
            spec = spec.and((root,query,cb) ->
                    cb.equal(root.get("type"), type)
            );
        }
        if(search != null && !search.isEmpty()) {
            spec = spec.and((root,query,cb) ->
                    cb.like(cb.lower(root.get("transactionCode")),"%" + search.toLowerCase() + "%")
            );
        }
        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findAll(spec, pageable);

        return walletTransactions.map(this::convert);

    }

    @Override
    public WalletTransactionResponse handleDeposit(Long userId, Double amount, String transactionCode) {
        Wallet wallet = walletRepository.findByReaderId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        boolean exiting = walletTransactionRepository.findFirstByWalletIdAndStatusOrderByCreatedDate(wallet.getId(),"PENDING").orElse(null) != null;
        if (exiting) {
            throw new RuntimeException("Transaction already exists");
        }
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(BigDecimal.valueOf(amount));
        tx.setStatus("PENDING");
        tx.setTransactionCode(transactionCode);
        tx.setCreatedDate(new Date());
        tx.setType("IN PROGRESS");
        WalletTransaction w= walletTransactionRepository.save(tx);
        WalletTransactionResponse response = convert(w);
        return response;
    }

    @Transactional
    @Scheduled(fixedRate = 30000)
    @Override
    public void checkingPendingTransactions() {
        List<WalletTransaction> pendingTransactions = walletTransactionRepository.findByStatus("PENDING");

        boolean matched = false;
        for (WalletTransaction t : pendingTransactions) {
            System.out.println("Checking transaction: " + t.getTransactionCode() + " with amount: " + t.getAmount());
            matched = checkWithSepayAPI(t.getTransactionCode(), t.getAmount());
            if (matched) {
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

    private WalletTransactionResponse convert(WalletTransaction walletTransaction) {
        WalletTransactionResponse walletTransactionResponse = new WalletTransactionResponse();
        walletTransactionResponse.setId(walletTransaction.getId());
        walletTransactionResponse.setTransactionCode(walletTransaction.getTransactionCode());
        walletTransactionResponse.setAmount(Double.parseDouble(walletTransaction.getAmount().toString()));
        walletTransactionResponse.setStatus(walletTransaction.getStatus());
        walletTransactionResponse.setCreatedDate(walletTransaction.getCreatedDate());
        walletTransactionResponse.setType(walletTransaction.getType());
        return walletTransactionResponse;
    }

    public boolean checkWithSepayAPI(String transactionCode, BigDecimal amount) {
        String url = "https://my.sepay.vn/userapi/transactions/list";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(sepayApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

//            System.out.println("JSON Sepay trả về: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode transactions = root.get("transactions");

                if (transactions != null && transactions.isArray()) {
                    for (JsonNode txn : transactions) {
                        String content = txn.get("transaction_content").asText();
                        String amountInStr = txn.get("amount_in").asText();

                        System.out.println("Nội dung: " + content);
                        System.out.println("Tiền vào: " + amountInStr);
                        String clearTransactionCode = transactionCode.replace("_", "");

//                        System.out.println("clearTransactionCode: " + clearTransactionCode);


                        if (content != null && content.contains(clearTransactionCode)) {
                            try {
                                BigDecimal parsedAmount = new BigDecimal(amountInStr);
                                if ( parsedAmount.compareTo(amount) == 0 ) {
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                System.err.println(" Không parse được amount_in: " + amountInStr);
                            }
                        }
                    }
                } else {
                    System.out.println(" Không tìm thấy trường 'transactions' hoặc không phải mảng.");
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
