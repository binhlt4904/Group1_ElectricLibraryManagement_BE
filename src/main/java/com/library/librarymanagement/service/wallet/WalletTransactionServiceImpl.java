package com.library.librarymanagement.service.wallet;

import com.library.librarymanagement.dto.response.WalletTransactionResponse;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.Wallet;
import com.library.librarymanagement.entity.WalletTransaction;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.wallet.WalletRepository;
import com.library.librarymanagement.repository.wallet.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ReaderRepository readerRepository;


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
}
