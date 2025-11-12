package com.library.librarymanagement.controller.wallet;


import com.library.librarymanagement.dto.response.WalletResponse;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.service.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/wallets")
public class WalletController {
    private final WalletService walletService;
    private final ReaderRepository readerRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<?> getWallet(@PathVariable Long id) {
        Reader reader = readerRepository.findByAccountId(id).orElse(null);
        WalletResponse walletResponse = walletService.getWalletByUserId(reader.getId());
        return ResponseEntity.ok(walletResponse);
    }
}
